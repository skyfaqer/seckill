package com.cgy.seckill.redis;

public class GoodsKey extends BasePrefix {

    public static GoodsKey LIST = new GoodsKey(60, "list");

    public static GoodsKey DETAIL = new GoodsKey(60, "detail");

    public static GoodsKey SECKILL_STOCK = new GoodsKey(0, "seckill_stock");

    public static GoodsKey SECKILL_END_TIME = new GoodsKey(0, "seckill_end_time");

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
