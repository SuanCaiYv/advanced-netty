package com.learn.tcp.server1;

import com.learn.tcp.codec.Byte2StringCodec;
import com.learn.tcp.server1.handlerserver.HandlerIn11;
import com.learn.tcp.server1.handlerserver.HandlerIn12;
import com.learn.tcp.server1.handlerserver.HandlerOut11;
import com.learn.tcp.util.CommonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/7/30 16:41
 */
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup boss = CommonUtils.bossEventLoopGroup();
        EventLoopGroup worker = CommonUtils.workerEventLoopGroup();
        ChannelFuture channelFuture = serverBootstrap
                .channel(CommonUtils.serverChannel())
                .group(boss, worker)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 这里有一个小bug，也可能不是bug，我之前遇到过，就是Out必须不能放在最后一个，否则不起作用。
                        pipeline.addLast(new HandlerIn11());
                        pipeline.addLast(new Byte2StringCodec());
                        pipeline.addLast(new HandlerOut11());
                        pipeline.addLast(new HandlerIn12());
                    }
                })
                .bind(new InetSocketAddress("127.0.0.1", 8190))
                .addListener((ChannelFutureListener) future -> {
                    if (LOGGER.isDebugEnabled()) {
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) future.channel().localAddress();
                        LOGGER.info("服务端监听: {}:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                    }
                });
        Client.main(args);
        channelFuture.syncUninterruptibly();
        LockSupport.parkNanos(Duration.ofDays(1).toNanos());
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
