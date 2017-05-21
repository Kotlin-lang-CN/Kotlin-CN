package tech.kotlin.common.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface MultiConnPool {

    void listen(InetSocketAddress address) throws IOException;

    void register(Connection conn) throws IOException;

    void close(Connection conn) throws IOException;

    void terminate();

}
