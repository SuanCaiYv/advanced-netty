package com.learn.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/7/30 16:41
 */
public class Server {

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .bind(new InetSocketAddress("127.0.0.1", 8190))
                .addListeners((ChannelFutureListener) channelFuture -> {
                    ;
                });
    }
}
