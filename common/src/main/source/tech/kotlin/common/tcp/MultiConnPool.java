package tech.kotlin.common.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public interface MultiConnPool {

    void listen(InetSocketAddress address) throws IOException;

    void connect(InetSocketAddress address) throws IOException;

    void connect(InetSocketAddress address, Object tag) throws IOException;

    void close(Connection conn) throws IOException;

    void terminate();

}
