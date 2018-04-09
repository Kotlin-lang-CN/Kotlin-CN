package cn.kotliner.forum.component;

import cn.kotliner.forum.domain.annotation.Login;
import cn.kotliner.forum.domain.annotation.User;
import cn.kotliner.forum.domain.model.Account;
import cn.kotliner.forum.domain.model.Device;
import cn.kotliner.forum.domain.model.UserInfo;
import cn.kotliner.forum.exceptions.Abort;
import cn.kotliner.forum.service.Err;
import cn.kotliner.forum.service.account.impl.SessionService;
import cn.kotliner.forum.service.account.req.CheckTokenReq;
import cn.kotliner.forum.service.account.resp.CheckTokenResp;
import cn.kotliner.forum.utils.Ins;
import cn.kotliner.forum.utils.algorithm.Json;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 登录用户的会话校验:
 * 对于需要登录会话鉴权的接口需要添加 {@link Login} 来声明该接口所需要的登录会话权限
 */
@Aspect
@Order(10)
public class SessionAspect {

    private static final Logger LOG = LoggerFactory.getLogger(SessionAspect.class);
    private static final Map<Method, CheckTokenAspect> lazyCache = new ConcurrentHashMap<>();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource
    @Lazy
    private HttpServletRequest request;

    @Resource
    private SessionService sessionService;

    //handler entry point definition
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void handlerFunction() {
    }

    @Around("handlerFunction()")
    public Object doHandle(ProceedingJoinPoint pjp) throws Throwable {
        //log basic request info
        LOG.debug(printRequest());
        Device device = getDevice();

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        //valid request session and autowired session
        final Object[] args = pjp.getArgs();
        CheckTokenResp session = CheckTokenAspect
                .of(method, sessionService)
                .process(request, device);

        for (int i = 0; i < method.getParameterCount(); i++) {
            if (!method.getParameters()[i].isAnnotationPresent(User.class))
                continue;

            if (method.getParameterTypes()[i] == Device.class) {
                args[i] = device;
            } else if (method.getParameterTypes()[i] == UserInfo.class) {
                args[i] = session == null ? null : session.getUser();
            } else if (method.getParameterTypes()[i] == Account.class) {
                args[i] = session == null ? null : session.getAccount();
            }
        }
        return pjp.proceed(args);
    }

    private Device getDevice() {
        String deviceToken = request.getHeader("X-App-Device");
        String platform = request.getHeader("X-App-Platform");
        String vendor = request.getHeader("X-App-Vendor");
        String system = request.getHeader("X-App-System");
        if (StringUtils.isEmpty(deviceToken)
                || StringUtils.isEmpty(platform)
                || StringUtils.isEmpty(vendor)
                || StringUtils.isEmpty(system)) {
            throw new Abort(Err.PARAMETER.getCode(), "缺少设备信息");
        }
        return Ins.apply(new Device(), d -> {
            d.setPlatform(Integer.parseInt(platform));
            d.setVendor(vendor);
            d.setSystem(system);
            d.setToken(deviceToken);
        });
    }

    private String printRequest() {
        return Json.dumps(Ins.dict(m -> {
            m.put("url", String.format("%s [%s]", request.getRequestURI(), request.getMethod()));
            m.put("headers", Ins.dict(h -> {
                Enumeration<String> names = request.getHeaderNames();
                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    h.put(name, request.getHeader(name));
                }
            }));
            m.put("params", Ins.dict(p -> {
                Enumeration<String> names = request.getParameterNames();
                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    p.put(name, request.getParameter(name));
                }
            }));
        }));
    }


    static class CheckTokenAspect {

        private final Login require;
        private final SessionService service;

        static CheckTokenAspect of(Method method, SessionService service) {
            CheckTokenAspect entry;
            if (!lazyCache.containsKey(method)) {
                synchronized (CheckTokenAspect.class) {//min sync lock
                    if (lazyCache.containsKey(method))//multi-thread condition
                        return lazyCache.get(method);

                    entry = new CheckTokenAspect(method, service);
                    lazyCache.put(method, entry);
                }
            } else {
                entry = lazyCache.get(method);
            }
            return entry;
        }

        @Nullable
        CheckTokenResp process(HttpServletRequest req, Device device) {
            if (require == null) return null;
            String token = req.getHeader("X-App-Token");
            if (StringUtils.isEmpty(token)) throw new Abort(Err.UNAUTHORIZED.getCode(), "缺少登录信息");
            return service.checkToken(Ins.apply(new CheckTokenReq(), it -> {
                it.setToken(token);
                it.setDevice(device);
            }));
        }

        private CheckTokenAspect(Method method, SessionService service) {
            this.service = service;
            if (method.isAnnotationPresent(Login.class)) {
                require = method.getAnnotation(Login.class);
            } else {
                require = null;
            }
        }
    }
}