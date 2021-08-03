package com.learn.tcp;

import com.learn.tcp.codec.Byte2StringCodec;
import com.learn.tcp.handler2.HandlerIn21;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/7/30 16:42
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture sync = bootstrap
                .group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Byte2StringCodec());
                        pipeline.addLast(new HandlerIn21());
                    }
                })
                .connect("127.0.0.1", 8190)
                .sync();
        Channel channel = sync.channel();
        channel.writeAndFlush(Unpooled.buffer().writeBytes("test".getBytes()));
        LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
        channel.flush();
        channel.writeAndFlush(Unpooled.buffer().writeBytes("text".getBytes()));
    }
}
