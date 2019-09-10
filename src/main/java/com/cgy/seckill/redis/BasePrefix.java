package com.cgy.seckill.redis;

public abstract class BasePrefix implements KeyPrefix {

    // Expiration time (seconds) of key, default: 0
    private int expireSeconds;

    // Prefix of key, corresponding to different scenes with the actual class
    private String prefix;

    // Used for denoting seckill status stored in redis
    private int value;

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
        this.value = 0;
    }

    public BasePrefix(String prefix) {
        this.expireSeconds = 0;
        this.prefix = prefix;
        this.value = 0;
    }

    public BasePrefix(String prefix, int value) {
        this.expireSeconds = 0;
        this.prefix = prefix;
        this.value = value;
    }

    public BasePrefix(int expireSeconds, String prefix, int value) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
        this.value = value;
    }

    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }

    // Prefix format: className + ":" + prefix
    // Example: "UserKey:name", "OrderKey:id"
    // The rest of the key string will be concatenated to prefix to form the whole key, e.g., "UserKey:id123"
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

    @Override
    public int getValue() {
        return value;
    }
}
