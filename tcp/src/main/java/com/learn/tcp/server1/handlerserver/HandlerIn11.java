package com.learn.tcp.server1.handlerserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/2 14:33
 */
public class HandlerIn11 extends ChannelInboundHandlerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(HandlerIn11.class);

    // 远程连接建立=>new一个Channel=>注册到EventLoop=>变为活跃状态。
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("此Channel: {}已经变为活跃状态。", ctx.channel().id());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            LOGGER.debug("接收到一个新连接: {}, {}", address.getHostName(), address.getPort());
            LOGGER.debug("此连接之上的Channel已经被注册到了EventLoop: {}上", ctx.channel().eventLoop().hashCode());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("此Channel: {}发生了读事件，但是我们决定把它送到下流处理。", ctx.channel().id());
        }
        ctx.fireChannelRead(msg);
    }
}
