package com.cgy.seckill.access;

import com.alibaba.fastjson.JSON;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.redis.SeckillKey;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SeckillUserService seckillUserService;

    @Autowired
    private RedisService redisService;

    // Conducted before argument resolver
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            SeckillUser user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    // Put error message into response
                    render(response, CodeMsg.SESSION_INVALID);
                    return false;
                }
                key += "_" + user.getId();
            }
            // Check user's access count of this uri
            SeckillKey ACCESS_COUNT = SeckillKey.withExpireSeconds(seconds);
            Integer count = redisService.get(ACCESS_COUNT, key, Integer.class);
            // Allow at most {maxCount} accesses in {seconds} seconds
            if (count == null) {
                redisService.set(ACCESS_COUNT, key, 1);
            } else if (count < maxCount) {
                redisService.incr(ACCESS_COUNT, key);
            } else {
                render(response, CodeMsg.ACCESS_OVERFLOW);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return seckillUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
