package tech.kotlin.common.tcp;

import tech.kotlin.common.os.Handler;
import tech.kotlin.common.os.Looper;

import javax.annotation.Nonnull;

public abstract class TcpHandler extends Handler {

    /***
     * Connection is create.
     * @param connection connection instance
     */
    public abstract void onConnect(@Nonnull Connection connection);

    /***
     * Receive data by remote
     * @param connection connection instance
     * @param data package
     */
    public abstract void onData(@Nonnull Connection connection, @Nonnull TcpPackage data);

    /***
     * Connection is close
     * @param connection connection instance
     */
    public abstract void onDisconnected(@Nonnull Connection connection);

}