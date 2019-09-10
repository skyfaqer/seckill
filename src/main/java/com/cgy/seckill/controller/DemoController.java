package com.cgy.seckill.controller;

import com.cgy.seckill.domain.User;
import com.cgy.seckill.rabbitmq.MQSender;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.redis.UserKey;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/trans")
    @ResponseBody
    public Result<Boolean> dbTrans() {
        userService.trans();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.ID, "555", User.class);  // Format: "UserKey:id555"
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(555);
        user.setName("bbbbb");
        boolean result = redisService.set(UserKey.ID, "555", user); // Format: "UserKey:id555"
        return Result.success(result);
    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq() {
//        mqSender.send("hello");
//        return Result.success("success");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> mqTopic() {
//        mqSender.sendTopic("hello");
//        return Result.success("success");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> mqFanout() {
//        mqSender.sendFanout("hello");
//        return Result.success("success");
//    }
//
//    @RequestMapping("/mq/headers")
//    @ResponseBody
//    public Result<String> mqHeaders() {
//        mqSender.sendHeaders("hello");
//        return Result.success("success");
//    }
}
