package demo.ocean.rpc.client.connector.handler;

import demo.ocean.rpc.client.runner.RequestFuture;
import demo.ocean.rpc.client.runner.RpcRequestManager;
import demo.ocean.rpc.client.runner.RpcRequestPool;
import demo.ocean.rpc.client.runner.SyncRequestFuture;
import demo.ocean.rpc.common.data.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

/**
 * Rpc数据接收响应处理器
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Autowired
    private RpcRequestPool requestPool;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse znsResponse) {
        // 获取请求回调信息
        RequestFuture requestFuture = SyncRequestFuture.syncRequest.get(znsResponse.getRequestId());
        if(null != requestFuture) {
            // 设置回调结果
            requestFuture.setResponse(znsResponse);
        }else {
            requestPool.notifyRequest(znsResponse.getRequestId(), znsResponse);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.error("远程服务：{}关闭了，请查看！！！", socketAddress.toString());
        String socketAddressStr = socketAddress.toString().replace("/", "");
        RpcRequestManager.destroyChannelHolder(socketAddressStr);
        ctx.channel().close();
    }
}
