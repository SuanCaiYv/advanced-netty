package com.learn.tcp.server6.handler;

import com.learn.common.transport.Msg;
import com.learn.tcp.server6.event.OfflineEvent;
import com.learn.tcp.server6.system.SystemConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 15:47
 */
public class MsgPushHandler extends ChannelInboundHandlerAdapter {
    static final Logger LOGGER = LoggerFactory.getLogger(MsgPushHandler.class);

    static final ConcurrentHashMap<Long, Channel> CHANNELS = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Long, Channel> channels() {
        return CHANNELS;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg input = (Msg) msg;
        if (input.getHead().getType() == Msg.Head.TYPE_INIT) {
            ctx.channel().attr(AttributeKey.valueOf(SystemConstant.CHANNEL_ID)).set(input.getHead().getSenderId());
            CHANNELS.put(input.getHead().getSenderId(), ctx.channel());
        } else if (input.getHead().getType() == Msg.Head.TYPE_HEARTBEAT && !CHANNELS.containsKey(input.getHead().getSenderId())) {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            LOGGER.warn("远程节点: ({}) 已重连", (address.getHostName() + ":" + address.getPort()));
            // 此时需要完全重连才行
            ctx.channel().close();
        } else {
            LOGGER.info("我们读到了: {}", input);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("run");
        if (evt instanceof OfflineEvent) {
            long userId = (long) ctx.channel().attr(AttributeKey.valueOf(SystemConstant.CHANNEL_ID)).get();
            CHANNELS.remove(userId);
        }
    }
}
