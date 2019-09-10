package com.cgy.seckill.redis;

public class OrderKey extends BasePrefix {

    public static final OrderKey SECKILL_UID_GID = new OrderKey("seckilluidgid");

    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    private OrderKey(String prefix) {
        super(prefix);
    }
}
