package com.learn.tcp;

import com.learn.tcp.transport.Msg;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/3 11:03 下午
 */
public class ServerTest {

    @Test
    public void test() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ctx.writeAndFlush(Msg.withPlainText("hi"));
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("我们读到了: " + msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        System.out.println(cause.getMessage());
                    }
                });
            }
        });
        Msg msg = Msg.withPlainText("hello");
        Msg read = embeddedChannel.readOutbound();
        System.out.println(read);
    }
}
