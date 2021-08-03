package com.learn.tcp.util;

import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/3 15:36
 */
public class CommonUtils {

    private static String osname = System.getProperty("os.name");

    private static int cpuNums = Runtime.getRuntime().availableProcessors();

    static {
        osname = osname.replaceAll(" ", "").toLowerCase();
    }

    public static EventLoopGroup bossEventLoopGroup() {
        if (osname.contains("macos")) {
            return new KQueueEventLoopGroup(cpuNums >= 16 ? cpuNums >> 2 : 1);
        } else if (osname.contains("linux")) {
            return new EpollEventLoopGroup(cpuNums >= 16 ? cpuNums >> 2 : 1);
        } else {
            return new NioEventLoopGroup(cpuNums >= 16 ? cpuNums >> 2 : 1);
        }
    }

    public static EventLoopGroup workerEventLoopGroup() {
        if (osname.contains("macos")) {
            return new KQueueEventLoopGroup(cpuNums >= 32 ? cpuNums << 2 : cpuNums);
        } else if (osname.contains("linux")) {
            return new EpollEventLoopGroup(cpuNums >= 32 ? cpuNums << 2 : cpuNums);
        } else {
            return new NioEventLoopGroup(cpuNums >= 32 ? cpuNums << 2 : cpuNums);
        }
    }

    public static Class<? extends ServerChannel> serverChannel() {
        if (osname.contains("macos")) {
            return KQueueServerSocketChannel.class;
        } else if (osname.contains("linux")) {
            return EpollServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public static Class<? extends Channel> clientChannel() {
        if (osname.contains("macos")) {
            return KQueueSocketChannel.class;
        } else if (osname.contains("linux")) {
            return EpollSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static EventLoopGroup executors() {
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(cpuNums, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread();
            }
        });
        return defaultEventLoopGroup;
    }
}
