package demo.ocean.rpc.server.connector;

import demo.ocean.rpc.server.connector.handler.RpcRequestHandler;
import demo.ocean.rpc.server.connector.handler.RpcServerDecodeHandler;
import demo.ocean.rpc.server.connector.handler.RpcServerEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class RpcServerInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcRequestHandler requestHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new RpcServerDecodeHandler())
                .addLast(new RpcServerEncodeHandler())
                .addLast(requestHandler);
    }
}
