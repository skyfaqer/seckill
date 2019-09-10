package com.cgy.seckill.access;

import com.cgy.seckill.domain.SeckillUser;

public class UserContext {

    // Store the user of one thread
    private static ThreadLocal<SeckillUser> seckillUserHolder = new ThreadLocal<>();

    public static SeckillUser getUser() {
        return seckillUserHolder.get();
    }

    public static void setUser(SeckillUser user) {
        seckillUserHolder.set(user);
    }
}
