package com.learn.tcp.decoder;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/5 14:24
 */
public class Byte2Msg extends LengthFieldBasedFrameDecoder {

    public Byte2Msg(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
