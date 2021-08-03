package com.learn.tcp.encoder;

import com.learn.tcp.pojo.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 11:18 下午
 */
public class Msg2String extends MessageToMessageEncoder<Msg> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, List<Object> out) throws Exception {
        out.add(msg.asStringLightweight());
    }
}
