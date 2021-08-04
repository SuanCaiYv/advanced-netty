package com.learn.tcp.server3;

import com.learn.tcp.decoder.Byte2Integer;
import com.learn.tcp.encoder.String2Byte;
import com.learn.tcp.util.CommonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/3 16:12
 * <br/>
 * 介绍了编解码器的使用
 */
public class Server {

    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelOption<Long> id = ChannelOption.valueOf("id");
        ChannelFuture channelFuture = serverBootstrap
                .childOption(id, 0L)
                .group(CommonUtils.bossEventLoopGroup(), CommonUtils.workerEventLoopGroup())
                .channel(CommonUtils.serverChannel())
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Byte2Integer());
                        pipeline.addLast(new String2Byte());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("我们读到了: 🤜{}型数据: {}🤛", msg.getClass().getName(), msg);
                                ctx.writeAndFlush("hello client: " + msg);
                            }
                        });
                    }
                })
                .bind("127.0.0.1", 8390)
                .syncUninterruptibly();
        Client.main(args);
        Channel channel = channelFuture.channel();
    }
}
