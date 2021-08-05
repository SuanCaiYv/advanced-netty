package com.learn.ws.server1;

import com.learn.common.transport.Msg;
import com.learn.common.util.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 13:38
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
                        WebSocketClientProtocolConfig config = WebSocketClientProtocolConfig.newBuilder()
                                .webSocketUri("/ws")
                                .handleCloseFrames(true)
                                .version(WebSocketVersion.V13)
                                .build();
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 512));
                        pipeline.addLast(new WebSocketClientProtocolHandler(config));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("客户端读到了: 🤟{}型数据: {}🤏", msg.getClass().getName(), msg);
                                ReferenceCountUtil.release(msg);
                            }
                        });
                    }
                })
                .connect("127.0.0.1", 10190)
                .syncUninterruptibly();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(Msg.withPlainText("hello"));
    }
}
