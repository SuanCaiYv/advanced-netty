package com.learn.tcp.codec;

import com.learn.tcp.decoder.Byte2String;
import com.learn.tcp.encoder.String2Byte;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device iMacPro
 * @time 2021/8/4 12:39 上午
 */
public class CombineByteMsgCodec extends CombinedChannelDuplexHandler<Byte2String, String2Byte> {
}
