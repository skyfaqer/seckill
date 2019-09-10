package com.cgy.seckill.result;

import lombok.Getter;

@Getter
public class CodeMsg {

    private int code;
    private String msg;

    // Common
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "form validation error: %s");
    public static CodeMsg USER_EMPTY = new CodeMsg(500102, "user empty");
    public static CodeMsg ILLEGAL_REQUEST = new CodeMsg(500103, "illegal request");
    public static CodeMsg CAPTCHA_ERROR = new CodeMsg(500104, "error when creating captcha, please try again");
    public static CodeMsg CAPTCHA_INCORRECT = new CodeMsg(500105, "your answer is wrong");
    // Login: 5002XX
    public static CodeMsg SESSION_INVALID = new CodeMsg(500210, "your session is over, please log in again");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "password can not be empty");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "mobile number can not be empty");
    public static CodeMsg MOBILE_FORMAT_ERROR = new CodeMsg(500213, "mobile number format error");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "mobile number not exist");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "password error");
    public static CodeMsg NOT_LOGGED_IN = new CodeMsg(500216, "you are not logged in, please log in and try again");
    // Product: 5003XX
    public static CodeMsg GOODS_NOT_EXIST = new CodeMsg(500300, "goods not exist");
    // Order: 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "order not exist");
    // Seckill: 5005XX
    public static CodeMsg STOCK_UNAVAILABLE = new CodeMsg(500500, "this product has been sold out");
    public static CodeMsg REPEATED_PURCHASE = new CodeMsg(500501, "you are allowed to buy only one item of this product");
    public static CodeMsg SECKILL_ENDED = new CodeMsg(500502, "seckill has ended, good luck next time");
    public static CodeMsg ACCESS_OVERFLOW = new CodeMsg(500503, "you accessed too frequently, please wait");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String msg = String.format(this.msg, args);
        return new CodeMsg(code, msg);
    }
}
