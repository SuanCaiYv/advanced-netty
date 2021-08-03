package com.learn.tcp;

import com.learn.tcp.codec.Byte2MsgCodec;
import com.learn.tcp.decoder.Byte2String;
import com.learn.tcp.decoder.String2Msg;
import com.learn.tcp.pojo.Msg;
import io.netty.buffer.Unpooled;
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
                pipeline.addLast(new Byte2String());
                pipeline.addLast(new String2Msg());
                pipeline.addLast(new ChannelInboundHandlerAdapter() {
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
        byte[] bytes = msg.asByteArrayLightweight();
        embeddedChannel.writeInbound(Unpooled.buffer().writeBytes(bytes));
    }
}
