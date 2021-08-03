package com.learn.tcp.codec;

import com.learn.tcp.pojo.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 11:31 下午
 */
public class Byte2MsgCodec extends ByteToMessageCodec<Msg> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String input;
        if (in.hasArray()) {
            byte[] bytes = in.array();
            input = new String(bytes, in.arrayOffset(), in.readableBytes());
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            input = new String(bytes);
        }
        Msg msg = Msg.parseLightweight(input);
        out.add(msg);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, ByteBuf out) throws Exception {
        String str = msg.asStringLightweight();
        out.writeBytes(str.getBytes());
    }
}
