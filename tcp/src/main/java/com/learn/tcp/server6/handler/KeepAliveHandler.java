package com.learn.tcp.server6.handler;

import com.learn.common.transport.Msg;
import com.learn.tcp.server6.event.OfflineEvent;
import com.learn.tcp.server6.system.SystemConstant;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 15:46
 */
public class KeepAliveHandler extends ChannelInboundHandlerAdapter {
    static final Logger LOGGER = LoggerFactory.getLogger(KeepAliveHandler.class);

    static final Msg HEARTBEAT_MSG = Msg.withHeartbeat();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg input = (Msg) msg;
        if (input.getHead().getType() == Msg.Head.TYPE_HEARTBEAT) {
            Channel channel = ctx.channel();
            channel.attr(AttributeKey.valueOf(SystemConstant.CONTINUATION)).set(false);
            channel.attr(AttributeKey.valueOf(SystemConstant.REMAINS)).set(SystemConstant.INIT_COUNT);
            ctx.writeAndFlush(Msg.withAck());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            LOGGER.warn("Channel({})长时间闲置，我们决定发送心跳包进行探活", (address.getHostName() + ":" + address.getPort()));
            heartbeatTask(ctx);
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    static void heartbeatTask(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        int count = (int) channel.attr(AttributeKey.valueOf(SystemConstant.REMAINS)).get();
        boolean continuation = (boolean) channel.attr(AttributeKey.valueOf(SystemConstant.CONTINUATION)).get();
        if (count > 0 && continuation) {
            channel.attr(AttributeKey.valueOf(SystemConstant.REMAINS)).set(count - 1);
            ctx.writeAndFlush(HEARTBEAT_MSG);
            channel.eventLoop().schedule(() -> heartbeatTask(ctx), SystemConstant.DEFAULT_INTERVAL, TimeUnit.SECONDS);
        } else if (count <= 0) {
            InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
            LOGGER.info("远程节点({})已离线", (address.getHostName() + ":" + address.getPort()));
            channel.close().addListeners(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LOGGER.info("远程节点({})连接已关闭", (address.getHostName() + ":" + address.getPort()));
                }
            });
            ctx.fireUserEventTriggered(new OfflineEvent());
        } else {
            InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
            LOGGER.info("远程节点({})已重新上线", (address.getHostName() + ":" + address.getPort()));
        }
    }
}
