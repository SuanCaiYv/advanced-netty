package com.learn.tcp.server6.handler;

import com.learn.common.transport.Msg;
import com.learn.tcp.server6.event.OfflineEvent;
import com.learn.tcp.server6.system.SystemConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 15:47
 */
public class MsgPushHandler extends ChannelInboundHandlerAdapter {

    static final ConcurrentHashMap<Long, Channel> CHANNELS = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Long, Channel> channels() {
        return CHANNELS;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg input = (Msg) msg;
        if (input.getHead().getType() == Msg.Head.TYPE_INIT) {
            ctx.channel().attr(AttributeKey.valueOf(SystemConstant.CHANNEL_ID)).set(input.getHead().getSenderId());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof OfflineEvent) {
            ;
        }
    }
}
