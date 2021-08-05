package com.learn.common.util;

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
import io.netty.util.NettyRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/3 15:36
 */
public class CommonUtils {

    static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    private static final String osName = System.getProperty("os.name").replaceAll(" ", "").toLowerCase();

    private static final int cpuNums = NettyRuntime.availableProcessors();

    private static final File serverCrtChainFile;

    private static final File serverKeyFile;

    private static final File clientCrtChainFile;

    private static final File clientKeyFile;

    private static final File caFile;

    private static final AtomicInteger count = new AtomicInteger();

    static {
        URL crtChainResource1 = Thread.currentThread().getContextClassLoader().getResource("ssl/server.crt");
        URL keyResource1 = Thread.currentThread().getContextClassLoader().getResource("ssl/pkcs8_server.key");
        URL crtChainResource2 = Thread.currentThread().getContextClassLoader().getResource("ssl/client.crt");
        URL keyResource2 = Thread.currentThread().getContextClassLoader().getResource("ssl/pkcs8_client.key");
        URL caCrtResource = Thread.currentThread().getContextClassLoader().getResource("ssl/ca.crt");
        assert crtChainResource1 != null;
        serverCrtChainFile = new File(crtChainResource1.getPath());
        assert keyResource1 != null;
        serverKeyFile = new File(keyResource1.getPath());
        assert crtChainResource2 != null;
        clientCrtChainFile = new File(crtChainResource2.getPath());
        assert keyResource2 != null;
        clientKeyFile = new File(keyResource2.getPath());
        assert caCrtResource != null;
        caFile = new File(caCrtResource.getPath());
    }

    public static EventLoopGroup bossEventLoopGroup() {
        if (osName.contains("macos") || osName.contains("osx")) {
            return new KQueueEventLoopGroup(cpuNums >= 16 ? cpuNums >> 2 : 1);
        } else if (osName.contains("linux")) {
            return new EpollEventLoopGroup(cpuNums >= 16 ? cpuNums >> 2 : 1);
        } else {
            return new NioEventLoopGroup(cpuNums >= 16 ? cpuNums >> 2 : 1);
        }
    }

    public static EventLoopGroup workerEventLoopGroup() {
        if (osName.contains("macos") || osName.contains("osx")) {
            return new KQueueEventLoopGroup(cpuNums >= 32 ? cpuNums << 2 : cpuNums);
        } else if (osName.contains("linux")) {
            return new EpollEventLoopGroup(cpuNums >= 32 ? cpuNums << 2 : cpuNums);
        } else {
            return new NioEventLoopGroup(cpuNums >= 32 ? cpuNums << 2 : cpuNums);
        }
    }

    public static Class<? extends ServerChannel> serverChannel() {
        if (osName.contains("macos") || osName.contains("osx")) {
            return KQueueServerSocketChannel.class;
        } else if (osName.contains("linux")) {
            return EpollServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public static Class<? extends Channel> clientChannel() {
        if (osName.contains("macos")) {
            return KQueueSocketChannel.class;
        } else if (osName.contains("linux")) {
            return EpollSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static EventLoopGroup executors() {
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(cpuNums, r -> {
            Thread thread = new Thread(r);
            thread.setName("business-thread-" + count.getAndIncrement());
            return thread;
        });
        return defaultEventLoopGroup;
    }

    public static File serverCrtChainFile() {
        return serverCrtChainFile;
    }

    public static File serverKeyFile() {
        return serverKeyFile;
    }

    public static File clientCrtChainFile() {
        return clientCrtChainFile;
    }

    public static File clientKeyFile() {
        return clientKeyFile;
    }

    public static File caFile() {
        return caFile;
    }

    public static byte[] md5(byte[] src) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src);
            byte[] digest = md.digest();
            return digest;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("加密失败");
            return new byte[16];
        }
    }
}
