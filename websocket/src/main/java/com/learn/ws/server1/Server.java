package com.learn.ws.server1;

import com.learn.common.util.CommonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/4 11:36 下午
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
                        pipeline.addLast(new HttpObjectAggregator(1025 * 512));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/wss"));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof HttpRequest) {
                                    FullHttpRequest request = (FullHttpRequest) msg;
                                } else {
                                    // 需要给WebSocket来处理
                                    ctx.fireChannelRead(msg);
                                }
                            }
                        });
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof TextWebSocketFrame) {
                                    TextWebSocketFrame text = (TextWebSocketFrame) msg;
                                } else if (msg instanceof BinaryWebSocketFrame) {
                                    BinaryWebSocketFrame binary = (BinaryWebSocketFrame) msg;
                                } else if (msg instanceof ContinuationWebSocketFrame) {
                                    ContinuationWebSocketFrame continuation = (ContinuationWebSocketFrame) msg;
                                } else if (msg instanceof CloseWebSocketFrame) {
                                    CloseWebSocketFrame close = (CloseWebSocketFrame) msg;
                                } else {
                                    ctx.fireChannelRead(msg);
                                }
                            }
                        });
                    }
                })
                .bind("127.0.0.1", 10190)
                .syncUninterruptibly();
    }
}
