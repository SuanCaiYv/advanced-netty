package com.learn.http.server1;

import com.learn.common.util.CommonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/4 11:26 下午
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
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof HttpRequest) {
                                    LOGGER.info("我们读到了一个HTTP请求头");
                                    HttpRequest request = (HttpRequest) msg;
                                    LOGGER.info("它的URI: {}, 它的METHOD: {}", request.uri(), request.method());
                                    LOGGER.info("它拥有如下Headers:");
                                    for (Map.Entry<String, String> header : request.headers()) {
                                        LOGGER.info("Key: {} = Value: {}", header.getKey(), header.getValue());
                                    }
                                } else if (msg instanceof HttpContent) {
                                    HttpContent content = (HttpContent) msg;
                                }
                            }
                        });
                    }
                })
                .bind("127.0.0.1", 9190)
                .syncUninterruptibly();
    }
}
