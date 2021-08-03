package com.learn.tcp.server1.handlerclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/2 16:31
 */
public class HandlerIn21 extends ChannelInboundHandlerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(HandlerIn21.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("客户端读取到: " + msg);
        ReferenceCountUtil.release(msg);
    }
}
