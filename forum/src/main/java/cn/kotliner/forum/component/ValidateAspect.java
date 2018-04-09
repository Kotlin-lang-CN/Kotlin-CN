package cn.kotliner.forum.component;

import cn.kotliner.forum.exceptions.Abort;
import cn.kotliner.forum.service.Err;
import cn.kotliner.forum.utils.algorithm.Json;
import cn.kotliner.forum.utils.validate.Checker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Aspect
@Order(1)
@Component
public class ValidateAspect {

    private static final Logger log = LoggerFactory.getLogger(ValidateAspect.class);
    private static final Map<Method, CheckerAspect> lazyCache = new ConcurrentHashMap<>();

    @Pointcut("@annotation(org.springframework.validation.annotation.Validated)")
    public void formCheckFunction() {
    }

    @Around("formCheckFunction()")
    public Object doHandle(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        //validate request body
        CheckerAspect.of(method).process(pjp.getArgs());
        return pjp.proceed();
    }

    static class CheckerAspect {

        private final HashSet<Integer> objTask = new HashSet<>();
        private final HashSet<Integer> collectionTask = new HashSet<>();

        static CheckerAspect of(Method method) {
            CheckerAspect validator;
            if (!lazyCache.containsKey(method)) {
                synchronized (CheckerAspect.class) {//min sync lock
                    if (lazyCache.containsKey(method))//multi-thread condition
                        return lazyCache.get(method);

                    validator = new CheckerAspect(method);
                    lazyCache.put(method, validator);
                }
            } else {
                validator = lazyCache.get(method);
            }
            return validator;
        }

        void process(Object[] args) {
            Object current = null;
            try {
                for (Integer i : objTask) {
                    current = args[i];
                    ((Checker) current).check();
                }
                for (Integer i : collectionTask) {
                    current = args[i];
                    //noinspection unchecked
                    for (Checker checker : (Collection<Checker>) current) {
                        checker.check();
                    }
                }
            } catch (IllegalArgumentException | AssertionError err) {
                throw new Abort(Err.PARAMETER.getCode(), err.getMessage()) {
                    @Override
                    public StackTraceElement[] getStackTrace() {
                        return err.getStackTrace();
                    }
                };
            } catch (Abort abort) {
                log.info(String.format("[Err]%s", Json.dumps(current)));
                throw new Abort(abort.model.code, abort.model.msg) {
                    @Override
                    public StackTraceElement[] getStackTrace() {
                        return abort.getStackTrace();
                    }
                };
            }
        }

        private CheckerAspect(Method method) {
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter parameter = method.getParameters()[i];
                if (Checker.class.isAssignableFrom(parameter.getType())) {
                    objTask.add(i);
                } else if (Collection.class.isAssignableFrom(parameter.getType())//collection type
                        && Stream.of(parameter.getType().getGenericInterfaces())
                        .allMatch(it -> it == Checker.class)) {//generic type is checker
                    collectionTask.add(i);
                }
            }
        }
    }

}