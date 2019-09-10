package com.cgy.seckill.redis;

public class SeckillUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24;

    public static SeckillUserKey TOKEN = new SeckillUserKey(TOKEN_EXPIRE, "token");

    public static SeckillUserKey ID = new SeckillUserKey(0, "id");

    private SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
