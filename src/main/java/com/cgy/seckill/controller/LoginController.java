package com.cgy.seckill.controller;

import com.cgy.seckill.VO.LoginVO;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.SeckillUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private SeckillUserService seckillUserService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVO loginVO) {
        if (loginVO == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        log.info(loginVO.toString());
        // Login
        String token = seckillUserService.login(response, loginVO);
        return Result.success(token);
    }
}
