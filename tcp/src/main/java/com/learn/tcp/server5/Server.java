package com.learn.tcp.server5;

import com.learn.common.transport.Msg;
import com.learn.common.util.CommonUtils;
import com.learn.tcp.codec.Byte2MsgCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/4 14:43
 * <br/>
 * 添加了心跳保活机制
 */
public class Server {

    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final ConcurrentHashMap<Channel, Integer> HEARTBEAT_RETRIES = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap
                .group(CommonUtils.bossEventLoopGroup(), CommonUtils.workerEventLoopGroup())
                .channel(CommonUtils.serverChannel())
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                if (evt instanceof IdleStateEvent) {
                                    LOGGER.info("似乎有一段时间没有操作了，我们决定发送心跳进行测试。");
                                    heartbeat(ctx);
                                }
                            }
                        });
                        pipeline.addLast(new Byte2MsgCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                // 默认尝试十次
                                HEARTBEAT_RETRIES.put(ctx.channel(), 10);
                                ctx.channel().attr(AttributeKey.newInstance("IS_CONTINUE")).setIfAbsent(true);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof Msg) {
                                    Msg msg0 = (Msg) msg;
                                    if (msg0.getHead().getType() == Msg.Head.TYPE_HEARTBEAT) {
                                        // 收到了心跳回执
                                        ctx.channel().attr(AttributeKey.valueOf("IS_CONTINUE")).set(false);
                                    }
                                }
                                LOGGER.info("我们读到了: 🤜{}型数据: {}🤛", msg.getClass().getName(), msg);
                                ctx.writeAndFlush("hello client: " + msg);
                            }
                        });
                    }
                })
                .bind("127.0.0.1", 8590)
                .syncUninterruptibly();
        Client.main(args);
    }

    private static void heartbeat(ChannelHandlerContext ctx) {
        int count = HEARTBEAT_RETRIES.get(ctx.channel());
        if (count < 1) {
            LOGGER.warn("远程服务已下线!");
        } else {
            -- count;
            HEARTBEAT_RETRIES.put(ctx.channel(), count);
            boolean isContinue = (boolean) ctx.channel().attr(AttributeKey.valueOf("IS_CONTINUE")).get();
            if (!isContinue) {
                HEARTBEAT_RETRIES.put(ctx.channel(), 10);
            } else {
                ByteBuf byteBuf = Unpooled.directBuffer();
                Msg msg = Msg.withHeartbeat();
                msg.serialize(byteBuf);
                ctx.writeAndFlush(byteBuf);
                LOGGER.info("发送一个心跳包，还剩{}次机会", count);
                if (count > 0) {
                    // 每10s发送一个心跳包，超过10次没有应答，说明下线
                    ctx.channel().eventLoop().schedule(() -> heartbeat(ctx), 1, TimeUnit.SECONDS);
                }
            }
        }
    }
}
