package demo.ocean.rpc.server.connector;

import demo.ocean.rpc.common.utils.SpringBeanFactory;
import demo.ocean.rpc.server.config.RpcServerConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServerAcceptor  implements  Runnable{

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();

    private RpcServerConfiguration znsServerConfiguration;

    private RpcServerInitializer rpcServerInitializer;

    public RpcServerAcceptor() {
        this.znsServerConfiguration = SpringBeanFactory.getBean(RpcServerConfiguration.class);
        this.rpcServerInitializer = SpringBeanFactory.getBean(RpcServerInitializer.class);
    }



    @Override
    public void run() {
        log.warn("netty server start");

        // 1. Netty服务配置
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(rpcServerInitializer);

        try {
            log.info("ZnsServer acceptor startup at port[{}] successfully", znsServerConfiguration.getNetworkPort());
            // 2. 绑定端口， 启动服务
            ChannelFuture future = bootstrap.bind(znsServerConfiguration.getNetworkPort()).sync();
            // 3. 服务同步阻塞方式运行
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("ZnsServer acceptor startup failure!", e);
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully().syncUninterruptibly();
            worker.shutdownGracefully().syncUninterruptibly();
        }


    }
}
