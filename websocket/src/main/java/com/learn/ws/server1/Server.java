package com.learn.ws.server1;

import com.learn.common.util.CommonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/4 11:36 下午
 * <br/>
 * WebSocket版
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
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof HttpRequest) {
                                    FullHttpRequest request = (FullHttpRequest) msg;
                                    LOGGER.info("我们读到了一个HTTP请求");
                                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                    response.headers().set("Content-Length", 0);
                                    ctx.writeAndFlush(response);
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
                                    String content = text.text();
                                    LOGGER.info("我们读到了Text: 🤜{}🤛", content);
                                    TextWebSocketFrame response = new TextWebSocketFrame("ok");
                                    ctx.writeAndFlush(response);
                                } else if (msg instanceof BinaryWebSocketFrame) {
                                    BinaryWebSocketFrame binary = (BinaryWebSocketFrame) msg;
                                    ByteBuf content = binary.content();
                                    byte[] bytes = new byte[content.readableBytes()];
                                    content.readBytes(bytes);
                                    LOGGER.info("我们读到了Binary: 🤜{}🤛", new String(bytes));
                                } else if (msg instanceof ContinuationWebSocketFrame) {
                                    ContinuationWebSocketFrame continuation = (ContinuationWebSocketFrame) msg;
                                    ByteBuf content = continuation.content();
                                    byte[] bytes = new byte[content.readableBytes()];
                                    content.readBytes(bytes);
                                    LOGGER.info("我们读到了Continuation: 🤜{}🤛", new String(bytes));
                                } else if (msg instanceof CloseWebSocketFrame) {
                                    CloseWebSocketFrame close = (CloseWebSocketFrame) msg;
                                    LOGGER.info("我们读到了关闭请求: {}, {}", close.reasonText(), close.statusCode());
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
