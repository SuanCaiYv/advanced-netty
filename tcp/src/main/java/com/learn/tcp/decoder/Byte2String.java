package com.learn.tcp.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 9:06 下午
 */
public class Byte2String extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.hasArray()) {
            out.add(new String(in.array(), in.arrayOffset(), in.readableBytes()));
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes, 0, bytes.length);
            out.add(new String((bytes)));
        }
    }
}
