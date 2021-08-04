package com.learn.tcp.server4;

import com.learn.common.util.CommonUtils;
import com.learn.tcp.codec.Byte2MsgCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 11:40 下午
 * <br/>
 * 添加了SSL加密策略
 */
public class Server {

    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forServer(CommonUtils.serverCrtChainFile(), CommonUtils.serverKeyFile())
                .trustManager(CommonUtils.caFile())
                .clientAuth(ClientAuth.REQUIRE)
                .build();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap
                .group(CommonUtils.bossEventLoopGroup(), CommonUtils.workerEventLoopGroup())
                .channel(CommonUtils.serverChannel())
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new SslHandler(sslContext.newEngine(ch.alloc())));
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
                .bind("127.0.0.1", 8490)
                .syncUninterruptibly();
        Client.main(args);
    }
}
