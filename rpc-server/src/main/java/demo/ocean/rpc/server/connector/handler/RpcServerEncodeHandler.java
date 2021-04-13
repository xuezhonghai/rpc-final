package demo.ocean.rpc.server.connector.handler;


import demo.ocean.rpc.common.data.RpcResponse;
import demo.ocean.rpc.common.utils.ProtoSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 服务端编码器
 */
public class RpcServerEncodeHandler extends MessageToByteEncoder<RpcResponse> {

    /**
     * 编码接口
     * @param ctx
     * @param znsResponse
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse znsResponse, ByteBuf byteBuf)
            throws Exception {
        // 通过Protostuff实现编码接口
        byte[] bytes = ProtoSerializerUtil.serialize(znsResponse);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
