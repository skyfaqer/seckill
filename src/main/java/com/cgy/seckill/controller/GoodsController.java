package com.cgy.seckill.controller;

import com.cgy.seckill.VO.GoodsDetailVO;
import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.redis.GoodsKey;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static final String LOGIN_URL = "/login/to_login";

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private ApplicationContext applicationContext;

    // Use redis to store the page in cache
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user) {
        if (user == null) {
            return "You are not logged in or your session has ended. Please log in again.";
        }
        model.addAttribute("user", user);
        // Get cache
        String html = redisService.get(GoodsKey.LIST, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        // Query goods list
        List<GoodsVO> goodsList = goodsService.getGoodsVOList();
        model.addAttribute("goodsList", goodsList);
        // If cache not exist, manually render the page
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.LIST, "", html);
        }
        return html;
    }

    // Get goods detail using static page, in use
    // Result used in /static/goods_detail.htm
    @RequestMapping("/to_detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVO> toDetail(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user,
                                          @PathVariable("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.NOT_LOGGED_IN);
        }
        // Get goods info
        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
        // Compute seckill activity status and countdown time
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus = -1;
        long remainSeconds = -2;
        if (now < startTime) {
            seckillStatus = 0;  // Seckill not started, need countdown
            remainSeconds = (startTime - now) / 1000;
        } else if (now > endTime) {
            seckillStatus = 2;  // Seckill ended
            remainSeconds = -1;
        } else {
            seckillStatus = 1;  // Seckill in progress
            remainSeconds = 0;
        }
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        goodsDetailVO.setGoods(goods);
        goodsDetailVO.setUser(user);
        goodsDetailVO.setSeckillStatus(seckillStatus);
        goodsDetailVO.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVO);
    }

    // Original method for getting goods detail page, not used
//    @RequestMapping("/to_detail/{goodsId}")
//    public String toDetail_original(Model model, SeckillUser user,
//                                    @PathVariable("goodsId") long goodsId) {
//        if (user == null) {
//            return "redirect:" + LOGIN_URL;
//        }
//        // Get goods info
//        model.addAttribute("user", user);
//        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
//        // Compute seckill activity status and countdown time
//        long startTime = goods.getStartDate().getTime();
//        long endTime = goods.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//        int seckillStatus = -1;
//        long remainSeconds = -2;
//        if (now < startTime) {
//            seckillStatus = 0;  // Seckill not started, need countdown
//            remainSeconds = (startTime - now) / 1000;
//        } else if (now > endTime) {
//            seckillStatus = 2;  // Seckill ended
//            remainSeconds = -1;
//        } else {
//            seckillStatus = 1;  // Seckill in progress
//            remainSeconds = 0;
//        }
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";
//    }

    // Get goods detail page using cache, not used because of errors of countdown and stock
//    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
//    @ResponseBody
//    public String toDetail_cache(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user,
//                                                                                         @PathVariable("goodsId") long goodsId) {
//        if (user == null) {
//            return "redirect:" + LOGIN_URL;
//        }
//        // Get cache
//        String html = redisService.get(GoodsKey.DETAIL, "" + goodsId, String.class);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        // Get goods info
//        model.addAttribute("user", user);
//        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
//        // Compute seckill activity status and countdown time
//        long startTime = goods.getStartDate().getTime();
//        long endTime = goods.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//        int seckillStatus = -1;
//        long remainSeconds = -2;
//        if (now < startTime) {
//            seckillStatus = 0;  // Seckill not started, need countdown
//            remainSeconds = (startTime - now) / 1000;
//        } else if (now > endTime) {
//            seckillStatus = 2;  // Seckill ended
//            remainSeconds = -1;
//        } else {
//            seckillStatus = 1;  // Seckill in progress
//            remainSeconds = 0;
//        }
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        // If cache not exist, manually render the page
//        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(),
//                request.getLocale(), model.asMap(), applicationContext);
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", springWebContext);
//        if (!StringUtils.isEmpty(html)) {
//            redisService.set(GoodsKey.DETAIL, "" + goodsId, html);
//        }
//        return html;
//    }
}
