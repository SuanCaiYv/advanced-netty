package com.learn.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/7/30 16:41
 */
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .channel(NioServerSocketChannel.class)
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()))
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                    }
                });
        ChannelFuture sync = serverBootstrap
                .bind(new InetSocketAddress("127.0.0.1", 8190))
                .addListener((ChannelFutureListener) channelFuture -> {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) channelFuture.channel().localAddress();
                    LOGGER.info("服务端监听: {},{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                });
        ChannelFuture channelFuture = sync.channel().closeFuture();
        channelFuture.sync();
    }
}
