package tech.kotlin.common.tcp;


import tech.kotlin.common.encode.BytesEncoder;
import tech.kotlin.common.os.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class TcpPackager {

    private static final int BUFF_SIZE = 64 * 1024;

    private final BufferRing mBuffer = new BufferRing(BUFF_SIZE);
    private final SocketChannel channel;
    private final Tagger mTagger;

    private final BytesEncoder encoder;

    public TcpPackager(SocketChannel channel, BytesEncoder encoder) {
        this.channel = channel;
        this.encoder = encoder;
        mTagger = new Tagger();
    }

    /***
     * 读取管道中的TCP包
     */
    public int read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
        int result = channel.read(buffer);

        if (result <= 0)
            return result;

        if (encoder != null) {
            mBuffer.push(encoder.decode(buffer.array(), 0, buffer.position()));
        } else {
            mBuffer.push(buffer.array(), 0, buffer.position());
        }
        return result;
    }

    /**
     * 向管道中写TCP包
     */
    public int write(int type, long packageId, byte[] data) throws IOException {
        Tagger tagger = new Tagger();
        byte[] sent = new byte[data.length + 16];
        tagger.writeInt(sent, data.length, 0);
        tagger.writeInt(sent, type, 4);
        tagger.writeInt(sent, (int) (packageId >> 32), 8);
        tagger.writeInt(sent, (int) packageId, 12);

        System.arraycopy(data, 0, sent, 16, data.length);
        if (encoder != null) {
            return channel.write(ByteBuffer.wrap(encoder.encode(sent, 0, sent.length)));
        } else {
            return channel.write(ByteBuffer.wrap(sent));
        }
    }

    /***
     * 取出已经解析成功的包
     */
    public List<TcpPackage> fetch() throws IOException {
        List<TcpPackage> dataList = new ArrayList<>();
        while (mBuffer.getLen() > 0) {
            if (mBuffer.getLen() < 16)
                break;//包头不完整

            mBuffer.peek(mTagger.mTagBuffer, 16);//取出包头
            int length = mTagger.readInt(mTagger.mTagBuffer, 0);
            int type = mTagger.readInt(mTagger.mTagBuffer, 4);
            int high = mTagger.readInt(mTagger.mTagBuffer, 8);
            int low = mTagger.readInt(mTagger.mTagBuffer, 12);
            long packageId = (long) high << 32 | low & 0xFFFFFFFFL;

            if (mBuffer.getLen() < length + 16)
                break;//包内容未解析完整

            mBuffer.shift(16);//移位到包数据体开始
            if (length < 0 || length >= BUFF_SIZE) {
                throw new IOException("illegal length:" + length);
            }
            byte[] data = new byte[length];
            mBuffer.pull(data, length);//读包

            dataList.add(new TcpPackage(type, packageId, data));
        }
        return dataList;
    }

    /***
     * 包头读写工具类
     */
    private static class Tagger {

        private byte[] mTagBuffer = new byte[16];

        /***
         * 从byte[]中读取int
         * @param in 读取数组
         * @param offset 起始地址
         */
        private int readInt(byte[] in, int offset) {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                int shift = (3 - i) * 8;
                value += (in[i + offset] & 0xff) << shift;
            }
            return value;
        }

        /***
         * 写入int至byte[]
         * @param data 写入数组
         * @param value 写入数据
         * @param offset 起始地址
         */
        private void writeInt(byte[] data, int value, int offset) {
            assert offset + 4 < data.length;
            for (int i = offset; i < offset + 4; i++) {
                data[i] = (byte) (0xff & value >> ((3 - i) * 8));
            }
        }
    }

    /***
     * 环形队列缓存
     */
    private static class BufferRing {

        private final int RING_BUF_SIZE;
        private final byte[] buff;
        volatile private int start, end, length;

        BufferRing(int size) {
            RING_BUF_SIZE = size;
            buff = new byte[size];
            start = end = length = 0;
        }

        //写入方法
        void push(byte[] data) {
            if (length + data.length >= RING_BUF_SIZE) {
                //当前环形队列已满，不能继续读包，当丢包处理
                Log.e("buffer is full");
                return;
            }

            for (byte aData : data) {
                buff[end] = aData;
                end = (end + 1) % RING_BUF_SIZE;
                ++length;
            }
        }

        //分片写入
        void push(byte[] data, int pos, int len) {
            if (length + len >= RING_BUF_SIZE) {//当前环形队列已满，不能继续读包，当丢包处理
                Log.e("buffer is full");
                return;
            }

            for (int i = pos; i < pos + len; i++) {
                byte aData = data[i];
                buff[end] = aData;
                end = (end + 1) % RING_BUF_SIZE;
                ++length;
            }
        }

        //尝试读取但不移位
        int peek(byte[] buf, int len) {
            int count = 0;
            int _start = start;
            for (int i = 0; i < len; ++i) {
                buf[i] = buff[_start];
                _start = (_start + 1) % RING_BUF_SIZE;
                ++count;
            }
            return count;
        }

        //执行移位
        void shift(int len) {
            length -= len;
            start = (start + len) % RING_BUF_SIZE;
        }

        //读取并移位
        int pull(byte[] buf, int len) {
            int count = peek(buf, len);
            shift(count);
            return count;
        }

        //当前缓存长度
        int getLen() {
            return length;
        }
    }
}
