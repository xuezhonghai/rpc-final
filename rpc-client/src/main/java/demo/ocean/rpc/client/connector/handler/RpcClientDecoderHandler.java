package demo.ocean.rpc.client.connector.handler;

import demo.ocean.rpc.common.data.RpcResponse;
import demo.ocean.rpc.common.utils.ProtoSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcClientDecoderHandler extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        // 长度不够， 直接退出
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
            // 通过protoStuff封装组件， 实现反序列化操作
            RpcResponse znsResponse = ProtoSerializerUtil.deserialize(bytes, RpcResponse.class);
            list.add(znsResponse);
        }
    }
}
