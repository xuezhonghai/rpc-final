package demo.ocean.rpc.client.runner;


import demo.ocean.rpc.client.cache.ServiceRouteCache;
import demo.ocean.rpc.client.channel.ChannelHolder;
import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.cluster.ClusterStrategy;
import demo.ocean.rpc.client.cluster.engine.ClusterEngine;
import demo.ocean.rpc.client.config.RpcClientConfiguration;
import demo.ocean.rpc.client.connector.RpcClientConnector;
import demo.ocean.rpc.client.connector.RpcClientInitializer;
import demo.ocean.rpc.common.data.RpcRequest;
import demo.ocean.rpc.common.data.RpcResponse;
import demo.ocean.rpc.common.utils.RpcException;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import demo.ocean.rpc.common.utils.StatusEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.*;

/**
 * Rpc请求管理器
 */
@Component
@Slf4j
public class RpcRequestManager {

    private static RpcClientConfiguration configuration;

    private static ServiceRouteCache SERVICE_ROUTE_CACHE;

    private static RpcClientInitializer rpcClientInitializer;

    private static RpcRequestPool REQUEST_POOL;

    private final static ConcurrentHashMap<String, ChannelHolder> channelHolderMap = new ConcurrentHashMap<>();

    private final static ThreadPoolExecutor REQUEST_EXECUTOR = new ThreadPoolExecutor(4, 6,
            30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512),
            new BasicThreadFactory.Builder().namingPattern("request-service-connector-%d").build());

    public static void startRpcRequestManager(ServiceRouteCache serviceRouteCache, RpcClientConfiguration clientConfiguration, RpcRequestPool requestPool) {
        SERVICE_ROUTE_CACHE = serviceRouteCache;
        rpcClientInitializer = SpringBeanFactory.getBean(RpcClientInitializer.class);
        configuration = clientConfiguration;
        REQUEST_POOL = requestPool;
    }



    /**
     * 发送客户端请求
     * @param rpcRequest
     * @throws InterruptedException
     * @throws RpcException
     */
    public static RpcResponse sendRequest(RpcRequest rpcRequest) throws InterruptedException, RpcException {
        // 1. 从缓存中获取RPC服务列表信息
        List<ProviderService> providerServices = SERVICE_ROUTE_CACHE.getServiceRoutes(rpcRequest.getClassName());
        // 2. 获取负载均衡算法,从服务列表中获取服务信息
        ClusterStrategy strategy = ClusterEngine.queryClusterStrategy(configuration.getRpcClientClusterStrategy());
        ProviderService targetServiceProvider = strategy.select(providerServices);
        if (targetServiceProvider != null) {
            String requestId = rpcRequest.getRequestId();
            // 3. 发起远程调用
            RpcResponse response = requestByNetty(rpcRequest, targetServiceProvider);
            log.info("Send request[{}:{}] to service provider successfully", requestId, rpcRequest.toString());
            return response;
        } else {
            throw new RpcException(StatusEnum.NOT_FOUND_SERVICE_PROVIDER);
        }
    }

    public static void sendRequestAsync(RpcRequest rpcRequest) throws Exception {
        // 1. 从缓存中获取RPC服务列表信息
        List<ProviderService> providerServices = SERVICE_ROUTE_CACHE.getServiceRoutes(rpcRequest.getClassName());
        // 2. 获取负载均衡算法,从服务列表中获取服务信息
        ClusterStrategy strategy = ClusterEngine.queryClusterStrategy(configuration.getRpcClientClusterStrategy());
        ProviderService targetServiceProvider = strategy.select(providerServices);
        if (targetServiceProvider != null) {
            //获取连接
            ChannelHolder channelHolder = getChannelHolderByProvider(targetServiceProvider);
            if(null != channelHolder){
                REQUEST_POOL.submitRequest(rpcRequest.getRequestId(), channelHolder.getChannel().eventLoop());
                channelHolder.getChannel().writeAndFlush(rpcRequest);
            }else {
                throw new RpcException(StatusEnum.NOT_FOUND_SERVICE_PROVIDER);
            }

        } else {
            throw new RpcException(StatusEnum.NOT_FOUND_SERVICE_PROVIDER);
        }
    }

    private static ChannelHolder getChannelHolderByProvider(ProviderService targetServiceProvider) throws InterruptedException {
        String providerAddress = targetServiceProvider.getServerIp() + ":" + targetServiceProvider.getNetworkPort();
        if(!channelHolderMap.containsKey(providerAddress)){
            CountDownLatch latch = new CountDownLatch(1);
            REQUEST_EXECUTOR.submit(new RpcClientConnector(targetServiceProvider, latch));
            latch.await();
        }

        return channelHolderMap.get(providerAddress);
    }



    //绑定对应会话的ChannelHolder对象
    public static void registerChannelHolder(@NotNull String providerAddress, @NotNull ChannelHolder channelHolder) {

        // 1. 缓存记录连接通道
        channelHolderMap.put(providerAddress, channelHolder);
        log.info("Register ChannelHolder[{}:{}] successfully", providerAddress,
                channelHolder.toString());
        // 2. 缓存记录请求信息， 用于回调结果处理
        //REQUEST_POOL.submitRequest(requestId, channelHolder.getChannel().eventLoop());
        //log.info("Submit request into RpcRequestPool successfully");
    }

    //销毁对应会话的ChannelHolder对象
    public static void destroyChannelHolder(String providerAddress){
        ChannelHolder channelHolder = channelHolderMap.remove(providerAddress);
        if(null != channelHolder){
            channelHolder.getChannel().closeFuture();
            channelHolder.getEventLoopGroup().shutdownGracefully();
        }
    }



    /**
     * 采用Netty进行远程调用
     */
    public static RpcResponse requestByNetty(RpcRequest rpcRequest, ProviderService providerService) {

        // 1. 创建Netty连接配置
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(providerService.getServerIp(), providerService.getNetworkPort())
                .handler(rpcClientInitializer);
        try {
            // 2. 建立连接
            ChannelFuture future = bootstrap.connect().sync();
            if (future.isSuccess()) {
                ChannelHolder channelHolder = ChannelHolder.builder()
                        .channel(future.channel())
                        .eventLoopGroup(worker)
                        .build();
                log.info("Construct a connector with service provider[{}:{}] successfully",
                        providerService.getServerIp(),
                        providerService.getNetworkPort()
                );

                // 3. 创建请求回调对象
                final RequestFuture<RpcResponse> responseFuture = new SyncRequestFuture(rpcRequest.getRequestId());
                // 4. 将请求回调放置缓存
                SyncRequestFuture.syncRequest.put(rpcRequest.getRequestId(), responseFuture);
                // 5. 根据连接通道， 下发请求信息
                ChannelFuture channelFuture = channelHolder.getChannel().writeAndFlush(rpcRequest);
                // 6. 建立回调监听
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        // 7. 设置是否成功的标记
                        responseFuture.setWriteResult(future.isSuccess());
                        if(!future.isSuccess()) {
                            // 调用失败，清除连接缓存
                            SyncRequestFuture.syncRequest.remove(responseFuture.requestId());
                        }
                    }
                });
                // 8. 阻塞等待30秒
                RpcResponse result = responseFuture.get(30, TimeUnit.SECONDS);
                // 9. 移除连接缓存
                SyncRequestFuture.syncRequest.remove(rpcRequest.getRequestId());
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
