package com.cgy.seckill.controller;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.access.AccessLimit;
import com.cgy.seckill.domain.SeckillOrder;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.rabbitmq.MQSender;
import com.cgy.seckill.rabbitmq.SeckillMessage;
import com.cgy.seckill.redis.GoodsKey;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.redis.SeckillKey;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.GoodsService;
import com.cgy.seckill.service.OrderService;
import com.cgy.seckill.service.SeckillService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    private static final String LOGIN_URL = "/login/to_login";

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> localNoStockMap = new HashMap<>();

    // Do seckill and create order, using static page
    // Using redis to cache goods' stock, rabbitMQ to manage requests in queue
    @RequestMapping(value = "/{path}/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill(SeckillUser user,
                                     @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_INVALID);
        }
        // Verify path
        boolean isRightPath = seckillService.checkSeckillPath(user, goodsId, path);
        if (!isRightPath) {
            return Result.error(CodeMsg.ILLEGAL_REQUEST);
        }
        // Check if the user has bought this product in seckill
        // A user is allowed to buy only one item of a product
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATED_PURCHASE);
        }
        // Check if stock is available
        // Check local map first to reduce load for redis
        boolean noStock = localNoStockMap.get(goodsId);
        if (noStock) {
            return Result.error(CodeMsg.STOCK_UNAVAILABLE);
        }
        // Decrease stock in advance
        long stock = redisService.decr(GoodsKey.SECKILL_STOCK, "" + goodsId);
        if (stock < 0) {
            localNoStockMap.put(goodsId, true);
            return Result.error(CodeMsg.STOCK_UNAVAILABLE);
        }
        // Enqueue, send message
        SeckillMessage sm = new SeckillMessage();
        sm.setSeckillUser(user);
        sm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(sm);
        // Return: waiting in queue
        return Result.success(0);
        // Original, no redis and rabbitMQ optimizations
//        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
//        // Check if goods exists
//        if (goods == null) {
//            return Result.error(CodeMsg.GOODS_NOT_EXIST);
//        }
//        // Check if seckill has ended or not
//        if (goods.getEndDate().getTime() <= System.currentTimeMillis()) {
//            return Result.error(CodeMsg.SECKILL_ENDED);
//        }
//        // Check if stock is available
//        int stock = goods.getStockCount();
//        if (stock <= 0) {
//            return Result.error(CodeMsg.STOCK_UNAVAILABLE);
//        }
//        // Check if the user has bought this product in seckill
//        // A user is allowed to buy only one item of a product
//        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
//        if (seckillOrder != null) {
//            return Result.error(CodeMsg.REPEATED_PURCHASE);
//        }
//        // No error, then do seckill
//        OrderInfo orderInfo = seckillService.seckill(user, goods);
//        return Result.success(orderInfo);
    }

    // Called when initializing the application
    @Override
    public void afterPropertiesSet() throws Exception {
        // Load all goods' stock and end_time into redis
        List<GoodsVO> goodsVOList = goodsService.getGoodsVOList();
        if (goodsVOList == null) {
            return;
        }
        for (GoodsVO goodsVO : goodsVOList) {
            redisService.set(GoodsKey.SECKILL_STOCK, "" + goodsVO.getId(), goodsVO.getStockCount());
            // redisService.set(GoodsKey.SECKILL_END_TIME, "" + goodsVO.getId(), goodsVO.getEndDate());
            localNoStockMap.put(goodsVO.getId(), false);
        }
    }

    // Return:
    // orderId: success
    // 0: waiting
    // -1: seckill failed (no stock)
    // -2: seckill has ended
    // -3: goods not exist
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> result(SeckillUser user,
                               @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_INVALID);
        }
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)   // Limit user's access count
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> path(HttpServletRequest request, SeckillUser user,
                               @RequestParam("goodsId") long goodsId,
                               @RequestParam("captchaInput") String captchaInput) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_INVALID);
        }
        // Check if captcha input is correct
        try {
            int input = Integer.parseInt(captchaInput);
            boolean isCorrect = seckillService.checkCaptchaInput(user, goodsId, input);
            if (!isCorrect) {
                return Result.error(CodeMsg.CAPTCHA_INCORRECT);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return Result.error(CodeMsg.CAPTCHA_INCORRECT);
        }
        // Generate path
        String path = seckillService.generateSeckillPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> captcha(HttpServletResponse response, SeckillUser user,
                                  @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_INVALID);
        }
        BufferedImage image = seckillService.generateSeckillCaptcha(user, goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.CAPTCHA_ERROR);
        }
    }

    // Original method of do_seckill
//    @RequestMapping("/do_seckill")
//    public String doSeckill(Model model, SeckillUser user,
//                            @RequestParam("goodsId") long goodsId) {
//        if (user == null) {
//            return "redirect:" + LOGIN_URL;
//        }
//        model.addAttribute("user", user);
//        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
//        // Check if goods exists
//        if (goods == null) {
//            model.addAttribute("errorMsg", CodeMsg.GOODS_NOT_EXIST.getMsg());
//            return "seckill_fail";
//        }
//        // Check if seckill has ended or not
//        if (goods.getEndDate().getTime() <= System.currentTimeMillis()) {
//            model.addAttribute("errorMsg", CodeMsg.SECKILL_ENDED.getMsg());
//            return "seckill_fail";
//        }
//        // Check if stock is available
//        int stock = goods.getStockCount();
//        if (stock <= 0) {
//            model.addAttribute("errorMsg", CodeMsg.STOCK_UNAVAILABLE.getMsg());
//            return "seckill_fail";
//        }
//        // Check if the user has bought this product in seckill
//        // A user is allowed to buy only one item of a product
//        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
//        if (seckillOrder != null) {
//            model.addAttribute("errorMsg", CodeMsg.REPEATED_PURCHASE.getMsg());
//            return "seckill_fail";
//        }
//        // No error, then do seckill
//        OrderInfo orderInfo = seckillService.seckill(user, goods);
//        model.addAttribute("orderInfo", orderInfo);
//        model.addAttribute("goods", goods);
//        return "order_detail";
//    }
}
