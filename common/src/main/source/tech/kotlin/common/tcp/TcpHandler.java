package tech.kotlin.common.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.kotlin.common.os.Handler;
import tech.kotlin.common.os.Looper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public abstract class TcpHandler extends Handler {

    private static final Logger log = LogManager.getLogger(TcpHandler.class.getSimpleName());
    public static int CONN_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors() + 1;
    /*package*/ MultiConnPool workThread = null;
    private ExecutorService connService;

    protected TcpHandler() {
        this(Looper.myLooper());
    }

    protected TcpHandler(Looper looper) {
        super(looper);
    }

    public final void connect(InetSocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(address);
        workThread.register(new Connection(address, workThread, channel));
    }

    public final void close() {
        connService.shutdown();
        workThread.terminate();
    }

    public final void changeWorkThread(MultiConnPool workThread) {
        this.workThread = workThread;
        onChangeWorkThread(workThread);
    }

    /***
     * Connection is create.
     * @param connection connection instance
     */
    public abstract void onConnect(Connection connection);

    /***
     * Receive data by remote
     * @param connection connection instance
     * @param data package
     */
    public abstract void onData(Connection connection, TcpPackage data);

    /***
     * Connection is close
     * @param connection connection instance
     */
    public abstract void onDisconnected(Connection connection);

    /***
     * On ChangeWorkThread
     */
    protected void onChangeWorkThread(MultiConnPool workThread) {
    }

}