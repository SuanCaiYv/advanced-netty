package com.learn.tcp.server5;

import com.learn.common.transport.Msg;
import com.learn.common.util.CommonUtils;
import com.learn.tcp.codec.Byte2MsgCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/4 14:43
 */
public class Server {

    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap
                .group(CommonUtils.bossEventLoopGroup(), CommonUtils.workerEventLoopGroup())
                .channel(CommonUtils.serverChannel())
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                if (evt instanceof IdleStateEvent) {
                                    LOGGER.info("似乎有一段时间没有操作了，我们决定发送心跳进行测试。");
                                    ByteBuf byteBuf = Unpooled.directBuffer();
                                    Msg msg = Msg.withHeartbeat();
                                    msg.serialize(byteBuf);
                                    ctx.writeAndFlush(byteBuf);
                                }
                            }
                        });
                        pipeline.addLast(new Byte2MsgCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("我们读到了: 🤜{}型数据: {}🤛", msg.getClass().getName(), msg);
                                ctx.writeAndFlush("hello client: " + msg);
                            }
                        });
                    }
                })
                .bind("127.0.0.1", 8590)
                .syncUninterruptibly();
        Client.main(args);
    }
}
