package tech.kotlin.common.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class IOThread extends Thread implements MultiConnPool {

    private static final Logger log = LogManager.getLogger(IOThread.class.getSimpleName());
    protected final TcpHandler messenger;

    private final ReentrantLock selectorLock = new ReentrantLock();
    private final CountDownLatch initLatch = new CountDownLatch(1);
    private final ConcurrentSkipListSet<Connection> connPool = new ConcurrentSkipListSet<>();

    private Selector selector;
    private volatile boolean isClose;

    public IOThread(TcpHandler messenger) throws IOException {
        super("utils-thread");
        this.messenger = messenger;
        messenger.workThread = this;
    }

    public final void listen(InetSocketAddress localAddress) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(localAddress);
        serverChannel.configureBlocking(false);
        selectorLock.lock();
        try {
            initLatch.await();
            selector.wakeup();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            selectorLock.unlock();
        }
    }

    @Override
    public void register(Connection conn) throws IOException {
        if (isClose || !connPool.add(conn)) return;
        conn.channel.configureBlocking(false);
        messenger.post(() -> messenger.onConnect(conn));
        selectorLock.lock();
        try {
            initLatch.await();
            selector.wakeup();
            conn.channel.register(selector, SelectionKey.OP_READ, conn);
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            selectorLock.unlock();
        }
    }

    @Override
    public void close(Connection conn) throws IOException {
        try {
            conn.channel.close();
        } finally {
            if (connPool.remove(conn)) {
                messenger.post(() -> messenger.onDisconnected(conn));
            }
        }
    }

    @Override
    public void terminate() {
        isClose = true;
    }

    @Override
    public final void run() {
        try (Selector selector = Selector.open()) {
            this.selector = selector;
            initLatch.countDown();
            while (true) {
                if (isClose) return;
                try {
                    selectorLock.lock();
                    selectorLock.unlock();
                    int selectSize = selector.select();
                    if (selectSize == 0) continue;
                } catch (IOException error) {
                    log.error(error);
                    continue;
                }

                try {
                    for (SelectionKey it : selector.selectedKeys()) {
                        if (it.isAcceptable()) {
                            try {
                                SocketChannel channel = ((ServerSocketChannel) it.channel()).accept();
                                register(new Connection(null, IOThread.this, channel));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (it.isReadable()) {
                            final Connection conn = (Connection) it.attachment();
                            try {
                                int flag = conn.packager.read();
                                if (flag == -1) {
                                    throw new IOException("read EOF");
                                } else {
                                    for (final TcpPackage p : conn.packager.fetch()) {
                                        messenger.post(() -> messenger.onData(conn, p));
                                    }
                                }
                            } catch (IOException error) {
                                close(conn);
                            }
                        }
                    }
                } finally {
                    selector.selectedKeys().clear();
                }
            }
        } catch (Exception error) {
            log.error(error);
        } finally {
            isClose = true;
            connPool.forEach(it -> {
                try {
                    it.channel.close();
                } catch (IOException ignore) {
                } finally {
                    messenger.post(() -> messenger.onDisconnected(it));
                }
            });
            connPool.clear();
            onTerminate();
        }
    }

    protected void onTerminate() {
    }
}