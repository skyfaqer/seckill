package com.cgy.seckill.redis;

public interface KeyPrefix {

    int getExpireSeconds();

    String getPrefix();

    int getValue();
}
