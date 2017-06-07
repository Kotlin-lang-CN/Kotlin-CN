package tech.kotlin.common.tcp;


import org.jetbrains.annotations.NotNull;
import tech.kotlin.common.os.Log;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Connection implements Comparable<Connection> {


    private final MultiConnPool workThread;
    /*package*/ final SocketChannel channel;

    /*package*/ volatile TcpPackager packager;
    /*package*/ volatile private Object tag;

    /*package*/ Connection(MultiConnPool workThread, SocketChannel channel) {
        this.workThread = workThread;
        this.channel = channel;
        this.packager = new TcpPackager(channel, null);
    }

    public void updatePackager(final PackageFactory factory) {
        this.packager = factory.onCreate(channel);
    }

    public int send(int type, long packageId, final byte[] data) {
        try {
            return packager.write(type, packageId, data);
        } catch (IOException e) {
            Log.e(e);
            return 0;
        }
    }

    public void close() {
        try {
            workThread.close(this);
        } catch (IOException e) {
            Log.e(e);
        }
    }

    public void attach(Object tag) {
        this.tag = tag;
    }

    public Object attachment() {
        return tag;
    }

    @Override
    public int compareTo(@NotNull Connection o) {
        if (o.hashCode() > hashCode()) return 1;
        if (o.hashCode() < hashCode()) return -1;
        return 0;
    }

    public interface PackageFactory {
        TcpPackager onCreate(SocketChannel channel);
    }
}