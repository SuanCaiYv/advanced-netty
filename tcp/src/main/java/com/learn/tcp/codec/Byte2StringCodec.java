package com.learn.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/2 16:10
 */
public class Byte2StringCodec extends ByteToMessageCodec<String> {

    static final Logger LOGGER = LoggerFactory.getLogger(Byte2StringCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getBytes());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.hasArray()) {
            out.add(new String(in.array(), in.arrayOffset(), in.readableBytes()));
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            out.add(new String(bytes));
        }
    }
}
