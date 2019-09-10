package com.cgy.seckill.service;

import com.cgy.seckill.VO.LoginVO;
import com.cgy.seckill.dao.SeckillUserDao;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.exception.GlobalException;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.redis.SeckillUserKey;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.utils.MD5Util;
import com.cgy.seckill.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private SeckillUserDao seckillUserDao;

    @Autowired
    private RedisService redisService;

    public SeckillUser getById(Long id) {
        // Get cache
        SeckillUser user = redisService.get(SeckillUserKey.ID, "" + id, SeckillUser.class);
        if (user != null) {
            return user;
        }
        // No cache, then get from database
        user = seckillUserDao.getById(id);
        if (user != null) {
            redisService.set(SeckillUserKey.ID, "" + id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, Long id, String newPassword) {
        SeckillUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // Update database, only set the attribute that needs to be updated
        SeckillUser updatedUser = new SeckillUser();
        updatedUser.setId(id);
        updatedUser.setPassword(MD5Util.formPassword2DatabasePassword(newPassword, user.getSalt()));
        seckillUserDao.update(updatedUser);
        // Update cache
        redisService.delete(SeckillUserKey.ID, "" + id);
        user.setPassword(updatedUser.getPassword());
        redisService.set(SeckillUserKey.TOKEN, token, user);
        return true;
    }

    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserKey.TOKEN, token, SeckillUser.class);
        // Renew expiration time
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVO.getMobile();
        String formPassword = loginVO.getPassword();
        // Check if mobile number exists
        System.out.println(Long.parseLong(mobile));
        SeckillUser user = seckillUserDao.getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // Check if password (encrypted) is correct
        String databasePassword = user.getPassword();
        String databaseSalt = user.getSalt();
        String password = MD5Util.formPassword2DatabasePassword(formPassword, databaseSalt);
        if (!password.equals(databasePassword)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // Generate cookie for user session
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, SeckillUser user) {
        redisService.set(SeckillUserKey.TOKEN, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.TOKEN.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
