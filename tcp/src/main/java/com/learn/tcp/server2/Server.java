package com.learn.tcp.server2;

import com.learn.common.util.CommonUtils;
import com.learn.tcp.codec.Byte2StringCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/3 15:27
 * <br/>
 * 做为服务器1远程请求的服务器存在
 */
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap
                .channel(CommonUtils.serverChannel())
                .group(CommonUtils.bossEventLoopGroup(), CommonUtils.workerEventLoopGroup())
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Byte2StringCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ctx.writeAndFlush("server2 get: " + msg);
                            }
                        });
                    }
                })
                .bind(new InetSocketAddress("127.0.0.1", 8290))
                .addListener((ChannelFutureListener) future -> {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) future.channel().localAddress();
                    LOGGER.info("服务端监听: {}:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                });
        channelFuture.syncUninterruptibly();
    }
}
