package com.learn.common.transport;

import io.netty.buffer.ByteBuf;
import lombok.*;

/**
 * @author CodeWithBuff(给代码来点Buff)
 * @device MacBookPro
 * @time 2021/8/4 15:27
 */
@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Msg {

    public static final int EMPTY_SIZE = 76;

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Head {
        public static final int TYPE_HEARTBEAT   = 1 << 1;

        public static final int TYPE_TEXT        = 1 << 2;

        public static final int TYPE_IMG         = 1 << 3;

        public static final int TYPE_VIDEO       = 1 << 4;

        public static final int TYPE_FILE        = 1 << 5;

        public static final int TYPE_INIT        = 1 << 6;

        public static final int TYPE_ACK         = 1 << 7;

        public static final int TYPE_ERROR       = 1 << 8;

        private static final int AUTH_TOKEN_SIZE = 32;

        public static final int HEAD_SIZE        = 60 + AUTH_TOKEN_SIZE;

        private int type;

        // 这里把size定义为消息体的大小，即Body的大小。
        private long size;

        private long[] id;

        private long createdTime;

        private long arrivedTime;

        private long senderId;

        private long receiverId;

        private byte[] authToken;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        private byte[] body;
    }

    private Head head;

    private Body body;

    public void serialize(ByteBuf out) {
        // 硬核编码
        out.writeInt(this.head.type);
        out.writeLong(this.head.size);
        out.writeLong(this.head.id[0]);
        out.writeLong(this.head.id[1]);
        out.writeLong(this.head.createdTime);
        out.writeLong(this.head.arrivedTime);
        out.writeLong(this.head.senderId);
        out.writeLong(this.head.receiverId);
        out.writeBytes(this.head.authToken);
        out.writeBytes(this.body.body);
    }

    public static Msg deserialize(ByteBuf src) {
        int type = src.readInt();
        long size = src.readLong();
        long[] id = new long[2];
        id[0] = src.readLong();
        id[1] = src.readLong();
        long createdTime = src.readLong();
        long arrivedTime = src.readLong();
        long senderId = src.readLong();
        long receiverId = src.readLong();
        byte[] authToken = new byte[Head.AUTH_TOKEN_SIZE];
        src.readBytes(authToken, 0, authToken.length);
        byte[] body = new byte[(int) size];
        src.readBytes(body, 0, body.length);
        Msg msg = withEmpty();
        Head h = msg.head;
        h.setType(type);
        h.setSize(size);
        h.setId(id);
        h.setCreatedTime(createdTime);
        h.setArrivedTime(arrivedTime);
        h.setSenderId(senderId);
        h.setReceiverId(receiverId);
        Body b = msg.body;
        b.setBody(body);
        return msg;
    }

    private static Msg withEmpty() {
        Head h = Head.builder()
                .type(Head.TYPE_HEARTBEAT)
                .size(0)
                .id(new long[] {0, 0})
                .createdTime(System.currentTimeMillis())
                .arrivedTime(System.currentTimeMillis())
                .senderId(0)
                .receiverId(0)
                .authToken(new byte[Head.AUTH_TOKEN_SIZE])
                .build();
        Body b = Body.builder()
                .body(new byte[0])
                .build();
        Msg msg = Msg.builder()
                .head(h)
                .body(b)
                .build();
        return msg;
    }

    public static Msg withHeartbeat() {
        Msg msg = withEmpty();
        Head h = msg.head;
        h.setCreatedTime(0L);
        h.setArrivedTime(0L);
        h.setType(Head.TYPE_HEARTBEAT);
        return msg;
    }

    public static Msg withPlainText(String plainText) {
        byte[] src = plainText.getBytes();
        Msg msg = withEmpty();
        Head h = msg.head;
        h.setType(Head.TYPE_TEXT);
        h.setSize(src.length);
        Body b = msg.body;
        b.setBody(src);
        return msg;
    }

    public static Msg withAck() {
        Msg msg = withEmpty();
        Head h = msg.head;
        h.setType(Head.TYPE_ACK);
        return msg;
    }
}
