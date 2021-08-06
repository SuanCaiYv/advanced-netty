package com.learn.tcp.server6;

import com.learn.common.transport.Msg;
import com.learn.common.util.CommonUtils;
import com.learn.tcp.codec.Byte2MsgCodec;
import com.learn.tcp.server6.handler.KeepAliveHandler;
import com.learn.tcp.server6.handler.MsgPushHandler;
import com.learn.tcp.server6.system.SystemConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 14:23
 */
public class Server {
    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap
                .group(CommonUtils.bossEventLoopGroup(), CommonUtils.workerEventLoopGroup())
                .channel(CommonUtils.serverChannel())
                .childAttr(AttributeKey.newInstance(SystemConstant.CONTINUATION), true)
                .childAttr(AttributeKey.newInstance(SystemConstant.REMAINS), SystemConstant.INIT_COUNT)
                .childAttr(AttributeKey.newInstance(SystemConstant.CHANNEL_ID), 0L)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 根据Msg的Size属性抽取出Msg整体，这里的整体包含Head，Body
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 4, 8, Msg.Head.HEAD_SIZE - 12, 0));
                        pipeline.addLast(new Byte2MsgCodec());
                        pipeline.addLast(new IdleStateHandler(SystemConstant.READ_TIMEOUT, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new KeepAliveHandler());
                        pipeline.addLast(new MsgPushHandler());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("服务器读取到: {}", msg);
                            }
                        });
                    }
                })
                .bind("127.0.0.1", 8690)
                .syncUninterruptibly();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            Msg msg = Msg.withPlainText(s);
            for (Channel channel : MsgPushHandler.channels().values()) {
                channel.writeAndFlush(msg);
            }
        }
    }
}
