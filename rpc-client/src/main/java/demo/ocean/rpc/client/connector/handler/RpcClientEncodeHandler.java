package demo.ocean.rpc.client.connector.handler;

import demo.ocean.rpc.common.data.RpcRequest;
import demo.ocean.rpc.common.utils.ProtoSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcClientEncodeHandler extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        byte[] bytes = ProtoSerializerUtil.serialize(rpcRequest);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }
}
