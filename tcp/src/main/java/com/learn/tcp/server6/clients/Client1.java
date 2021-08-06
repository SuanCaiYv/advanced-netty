package com.learn.tcp.server6.clients;

import com.learn.common.transport.Msg;
import com.learn.common.util.CommonUtils;
import com.learn.tcp.codec.Byte2MsgCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 15:48
 */
public class Client1 {
    static final Logger LOGGER = LoggerFactory.getLogger(Client1.class);

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap
                .channel(CommonUtils.clientChannel())
                .group(CommonUtils.bossEventLoopGroup())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Byte2MsgCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                Msg input = (Msg) msg;
                                if (input.getHead().getType() == Msg.Head.TYPE_HEARTBEAT) {
                                    ctx.writeAndFlush(Msg.withHeartbeat());
                                } else {
                                    ctx.fireChannelRead(msg);
                                }
                                LOGGER.info("客户端读到了: 🤟{}型数据: {}🤏", msg.getClass().getName(), msg);
                                ReferenceCountUtil.release(msg);
                            }
                        });
                    }
                })
                .connect("127.0.0.1", 8690)
                .syncUninterruptibly();
        Channel channel = channelFuture.channel();
        Msg init = Msg.withPlainText("init");
        init.getHead().setType(Msg.Head.TYPE_INIT);
        init.getHead().setSenderId(1L);
        channel.writeAndFlush(init);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            Msg msg = Msg.withPlainText(s);
            msg.getHead().setSenderId(1L);
            channel.writeAndFlush(msg);
        }
    }
}
