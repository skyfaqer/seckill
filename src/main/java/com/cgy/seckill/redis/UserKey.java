package com.cgy.seckill.redis;

public class UserKey extends BasePrefix {

    public static UserKey ID = new UserKey("id");

    public static UserKey NAME = new UserKey("name");

    private UserKey(String prefix) {
        super(prefix);
    }
}
