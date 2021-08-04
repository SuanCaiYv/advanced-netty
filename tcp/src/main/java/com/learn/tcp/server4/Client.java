package com.learn.tcp.server4;

import com.learn.tcp.codec.Byte2MsgCodec;
import com.learn.tcp.decoder.Byte2String;
import com.learn.tcp.encoder.Integer2Byte;
import com.learn.tcp.pojo.Msg;
import com.learn.tcp.util.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/4 10:41
 */
public class Client {

    static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .keyManager(CommonUtils.clientCrtChainFile(), CommonUtils.clientKeyFile())
                .trustManager(CommonUtils.caFile())
                .build();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap
                .channel(CommonUtils.clientChannel())
                .group(CommonUtils.bossEventLoopGroup())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new SslHandler(sslContext.newEngine(ch.alloc())));
                        pipeline.addLast(new Byte2MsgCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("客户端读到了: 🤟{}型数据: {}🤏", msg.getClass().getName(), msg);
                                ReferenceCountUtil.release(msg);
                            }
                        });
                    }
                })
                .connect("127.0.0.1", 8490)
                .syncUninterruptibly();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(Msg.withPlainText("hello"));
    }
}
