package demo.ocean.rpc.server.connector.handler;

import demo.ocean.rpc.common.data.RpcRequest;
import demo.ocean.rpc.common.data.RpcResponse;
import demo.ocean.rpc.common.utils.SpringBeanFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.SocketAddress;

@Component
@ChannelHandler.Sharable
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.warn("有人连我啦！他是:{}", socketAddress.toString());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            Class<?> clazz = Class.forName(request.getClassName());
            Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
            Object bean = SpringBeanFactory.getBean(Class.forName(request.getClassName()));
            Object result = method.invoke(bean, request.getParameters());
            response.setResult(result);
        } catch (Exception e) {
            response.setCause(e);
        }

        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
    }
}
