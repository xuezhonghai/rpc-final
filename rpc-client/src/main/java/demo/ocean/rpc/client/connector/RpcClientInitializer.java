package demo.ocean.rpc.client.connector;

import demo.ocean.rpc.client.connector.handler.RpcClientDecoderHandler;
import demo.ocean.rpc.client.connector.handler.RpcClientEncodeHandler;
import demo.ocean.rpc.client.connector.handler.RpcResponseHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RpcClientInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcResponseHandler responseHandler;


    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new RpcClientEncodeHandler())
                .addLast(new RpcClientDecoderHandler())
                .addLast(responseHandler);
    }
}
