package com.learn.tcp.server1.handlerserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/2 16:20
 */
public class HandlerOut11 extends ChannelOutboundHandlerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(HandlerOut11.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("实际的写出操作是交给Pipeline的TailHandler处理的，本质调用了Unsafe写出，因此我们无法控制，只能表示通知: 👉{}👈被写出了", msg);
        }
        ctx.write(msg);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("实际的刷新操作也不受我们管控，在这里我们仅做一个通知: 消息被刷新出Channel");
        }
        ctx.flush();
    }
}
