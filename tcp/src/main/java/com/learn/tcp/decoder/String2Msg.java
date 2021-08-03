package com.learn.tcp.decoder;

import com.learn.tcp.pojo.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.util.List;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 11:17 下午
 */
public class String2Msg extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String input, List<Object> out) throws Exception {
        Msg msg = Msg.parseLightweight(input);
        if (msg == null) {
            ctx.writeAndFlush(Msg.withError(new IOException("格式错误")));
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
