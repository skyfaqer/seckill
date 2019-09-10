package com.cgy.seckill.redis;

public class SeckillKey extends BasePrefix {

    public static SeckillKey NO_STOCK = new SeckillKey(0, "no_stock", -1);

    public static SeckillKey SECKILL_END = new SeckillKey(0, "seckill_end", -2);

    public static SeckillKey GOODS_NOT_EXIST = new SeckillKey(0, "goods_not_exist", -3);

    public static SeckillKey SECKILL_PATH = new SeckillKey(60, "seckill_path", 1);

    public static SeckillKey SECKILL_CAPTCHA = new SeckillKey(300, "seckill_captcha", 2);

    // public static SeckillKey ACCESS_COUNT = new SeckillKey(5, "access_count", 3);

    private SeckillKey(int expireSeconds, String prefix, int value) {
        super(expireSeconds, prefix, value);
    }

    // Get ACCESS_COUNT key with given expire seconds
    public static SeckillKey withExpireSeconds(int expireSeconds) {
        return new SeckillKey(expireSeconds, "access_count", 3);
    }
}
