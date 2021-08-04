package com.learn.tcp.codec;

import com.learn.common.transport.Msg;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 11:32 下午
 */
public class String2MsgCodec extends MessageToMessageCodec<String, Msg> {

    @Override
    protected void decode(ChannelHandlerContext ctx, String input, List<Object> out) throws Exception {
        out.add(Msg.deserialize(Unpooled.buffer().readBytes(input.getBytes())));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, List<Object> out) throws Exception {
        ;
    }
}
