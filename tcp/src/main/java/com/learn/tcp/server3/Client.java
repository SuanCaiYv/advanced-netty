package com.learn.tcp.server3;

import com.learn.common.util.CommonUtils;
import com.learn.tcp.decoder.Byte2String;
import com.learn.tcp.encoder.Integer2Byte;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 9:02 下午
 */
public class Client {

    static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap
                .channel(CommonUtils.clientChannel())
                .group(CommonUtils.bossEventLoopGroup())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Integer2Byte());
                        pipeline.addLast(new Byte2String());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("客户端读到了: 🤟{}型数据: {}🤏", msg.getClass().getName(), msg);
                                ReferenceCountUtil.release(msg);
                            }
                        });
                    }
                })
                .connect("127.0.0.1", 8390)
                .syncUninterruptibly();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(121);
        channel.writeAndFlush(456);
        channel.writeAndFlush(769);
    }
}
