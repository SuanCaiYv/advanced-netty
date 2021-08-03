package com.learn.tcp.server3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/3 16:12
 */
public class Server {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 12, 3000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                System.out.println(r.hashCode());
                thread.setName(atomicInteger.incrementAndGet()+"");
                return thread;
            }
        }, new ThreadPoolExecutor.DiscardPolicy());
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName());
        };
        threadPoolExecutor.execute(runnable);
    }
}
