package demo.ocean.rpc.client.connector;


import demo.ocean.rpc.client.channel.ChannelHolder;
import demo.ocean.rpc.client.channel.ProviderService;
import demo.ocean.rpc.client.runner.RpcRequestManager;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class RpcClientConnector implements Runnable {


    private ProviderService providerService;
    private CountDownLatch latch;
    private RpcClientInitializer znsClientInitializer;

    public RpcClientConnector(ProviderService providerService, CountDownLatch latch) {
        this.providerService = providerService;
        this.latch = latch;
        this.znsClientInitializer = SpringBeanFactory.getBean(RpcClientInitializer.class);
    }

    @Override
    public void run() {
        // 1. 创建Netty连接配置
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(providerService.getServerIp(), providerService.getNetworkPort())
                .handler(znsClientInitializer);

        try {
            // 2. 建立连接
            ChannelFuture future = bootstrap.connect().sync();
            if (future.isSuccess()) {
                //封装当前链接对应的Channel对象和EventLoopGroup
                ChannelHolder channelHolder = ChannelHolder.builder()
                        .channel(future.channel())
                        .eventLoopGroup(worker)
                        .build();

                //、将对应的请求(requestId)的ChannelHolder对象传递到发送请求对象中
                //向RpcRequestPool中注册一个当前请求对应的消息回调对象Future(Promise)
                String providerAddress = providerService.getServerIp() + ":" + providerService.getNetworkPort();
                RpcRequestManager.registerChannelHolder(providerAddress, channelHolder);
            }else {
                log.error("connect fail ip: {}, port:{}", providerService.getServerIp(), providerService.getNetworkPort());
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            latch.countDown();
        }
    }
}