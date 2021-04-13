package demo.ocean.rpc.server.connector.handler;

import demo.ocean.rpc.common.data.RpcRequest;
import demo.ocean.rpc.common.utils.ProtoSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 服务端解码器
 */
public class RpcServerDecodeHandler extends ByteToMessageDecoder {

    /**
     * 解码接口实现
     * @param ctx
     * @param in
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws
            Exception {
        if (in.readableBytes() <= 4) {
            return;
        }

        int length = in.readInt();
        in.markReaderIndex();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            // 通过Protostuff实现解码
            RpcRequest znsRequest = ProtoSerializerUtil.deserialize(bytes, RpcRequest.class);
            list.add(znsRequest);
        }
    }
}
