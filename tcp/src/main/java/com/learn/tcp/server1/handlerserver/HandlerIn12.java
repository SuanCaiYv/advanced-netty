package com.learn.tcp.server1.handlerserver;

import com.learn.tcp.server1.codec.Byte2StringCodec;
import com.learn.tcp.util.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/2 16:03
 */
public class HandlerIn12 extends ChannelInboundHandlerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(HandlerIn12.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.info("我们读到了: 🤜{}🤛", msg);
        if (msg.equals("forward")) {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture sync = bootstrap
                    // 在当前eventloop中发起远程请求
                    .group(ctx.channel().eventLoop())
                    .channel(CommonUtils.clientChannel())
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new Byte2StringCodec());
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx0) throws Exception {
                                    ctx0.writeAndFlush(msg);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx0, Object msg0) throws Exception {
                                    ctx.write("hi client: " + msg0);
                                    ctx.flush();
                                }
                            });
                        }
                    })
                    .connect("127.0.0.1", 8290);
        } else {
            ctx.write("hello client: " + msg);
            ctx.flush();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        LOGGER.error(cause.getMessage());
    }
}
