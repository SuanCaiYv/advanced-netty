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

    public static final int DEFAULT_SIZE = 76;

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Head {
        public static final int TYPE_HEARTBEAT = 1 << 1;

        public static final int TYPE_TEXT      = 1 << 2;

        public static final int TYPE_IMG       = 1 << 3;

        public static final int TYPE_VIDEO     = 1 << 4;

        public static final int TYPE_FILE      = 1 << 5;

        public static final int TYPE_INIT      = 1 << 6;

        public static final int TYPE_ERROR     = 1 << 7;

        public static final int HEAD_SIZE      = 60;

        private int type;

        private long size;

        private long[] id;

        private long createdTime;

        private long arrivedTime;

        private long senderId;

        private long receiverId;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        private byte[] body;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tail {
        public static final int TAIL_SIZE = 16;

        // 16字节的占位符
        private byte[] tail;
    }

    private Head head;

    private Body body;

    private Tail tail;

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
        out.writeBytes(this.body.body);
        out.writeBytes(this.tail.tail);
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
        byte[] body = new byte[(int) (size - DEFAULT_SIZE)];
        src.readBytes(body, 0, body.length);
        byte[] tail = new byte[Tail.TAIL_SIZE];
        src.readBytes(tail, 0, tail.length);
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
        Tail t = msg.tail;
        t.setTail(tail);
        return msg;
    }

    private static Msg withEmpty() {
        Head h = Head.builder()
                .type(Head.TYPE_HEARTBEAT)
                .size(DEFAULT_SIZE)
                .id(new long[] {0, 0})
                .createdTime(System.currentTimeMillis())
                .arrivedTime(System.currentTimeMillis())
                .senderId(0)
                .receiverId(0)
                .build();
        Body b = Body.builder()
                .body(new byte[0])
                .build();
        Tail t = Tail.builder()
                .tail(new byte[Tail.TAIL_SIZE])
                .build();
        Msg msg = Msg.builder()
                .head(h)
                .body(b)
                .tail(t)
                .build();
        return msg;
    }

    public static Msg withHeartbeat() {
        Msg msg = withEmpty();
        Head h = msg.head;
        h.setType(Head.TYPE_HEARTBEAT);
        return msg;
    }

    public static Msg withPlainText(String plainText) {
        byte[] src = plainText.getBytes();
        Msg msg = withEmpty();
        Head h = msg.head;
        h.setType(Head.TYPE_TEXT);
        h.setSize(h.getSize() + src.length);
        Body b = msg.body;
        b.setBody(src);
        return msg;
    }
}
