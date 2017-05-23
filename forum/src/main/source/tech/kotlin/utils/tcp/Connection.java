package tech.kotlin.utils.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Connection implements Comparable<Connection> {

    private static final Logger log = LogManager.getLogger(Connection.class.getSimpleName());

    private final InetSocketAddress remoteAddress;
    private final MultiConnPool workThread;
    /*package*/ final SocketChannel channel;

    /*package*/ volatile TcpPackager packager;
    /*package*/ volatile private Object tag;

    /*package*/ Connection(InetSocketAddress address, MultiConnPool workThread, SocketChannel channel) {
        this.remoteAddress = address;
        this.workThread = workThread;
        this.channel = channel;
        this.packager = new TcpPackager(channel, null);
    }

    public void updatePackager(final PackageFactory factory) {
        this.packager = factory.onCreate(channel);
    }

    public void send(final int type, final byte[] data) {
        //Log.d("send message type=" + type + ";length=" + data.length);
        try {
            if (channel.isConnected()) {
                packager.write(type, data);
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void close() {
        //Log.d("close connection");
        try {
            workThread.close(this);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void attach(Object tag) {
        this.tag = tag;
    }

    public Object attachment() {
        return tag;
    }

    @Override
    public int compareTo(Connection o) {
        if (o.hashCode() > hashCode()) return 1;
        if (o.hashCode() < hashCode()) return -1;
        return 0;
    }

    public interface PackageFactory {
        TcpPackager onCreate(SocketChannel channel);
    }
}