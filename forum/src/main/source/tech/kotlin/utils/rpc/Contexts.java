package tech.kotlin.utils.rpc;

import org.reflections.Reflections;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class Contexts {

    /***
     * rpc node for consumer mode only
     */
    public static ServiceContext create(String name) {
        return create(name, null);
    }

    /***
     * rpc node for consumer/provider mode
     * @param name rpc node thread name
     * @param service service executor
     * @param packageNames service package name
     */
    public static ServiceContext create(String name, ExecutorService service, String... packageNames) {
        CountDownLatch latch = new CountDownLatch(1);
        ServiceContext rpc = new ServiceContext(name, service, latch);
        Stream.of(packageNames).map(packageName -> new Reflections(packageName)
        ).forEach(reflection -> reflection.getTypesAnnotatedWith(RpcService.class).forEach(impl -> {
            try {
                rpc.internalRegister(impl.getInterfaces()[0], impl.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }));
        rpc.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("create interrupted!");
        }
        return rpc;
    }
}
