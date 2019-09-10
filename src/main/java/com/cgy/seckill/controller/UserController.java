package com.cgy.seckill.controller;

import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(SeckillUser user) {
        return Result.success(user);
    }
}
