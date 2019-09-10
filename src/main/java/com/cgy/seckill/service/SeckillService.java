package com.cgy.seckill.service;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.dao.GoodsDao;
import com.cgy.seckill.domain.Goods;
import com.cgy.seckill.domain.OrderInfo;
import com.cgy.seckill.domain.SeckillOrder;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.redis.SeckillKey;
import com.cgy.seckill.utils.MD5Util;
import com.cgy.seckill.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class SeckillService {

    private static char[] OPS = new char[]{'+', '-'};

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVO goods) {
        // Decrease stock
        boolean success = goodsService.decreaseStock(goods);
        if (success) {  // If decreased successfully, create order and seckill order
            return orderService.createOrder(user, goods);
        } else {    // Or decreasing failed (no stock)
            setSeckillStatus(goods.getId(), SeckillKey.NO_STOCK);
            return null;
        }
    }

    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        if (seckillOrder != null) { // Order has been created
            return seckillOrder.getOrderId();
        } else if (!existsSeckillStatus(goodsId, SeckillKey.NO_STOCK)) {
            if (existsSeckillStatus(goodsId, SeckillKey.SECKILL_END)) {
                return SeckillKey.SECKILL_END.getValue();
            } else if (existsSeckillStatus(goodsId, SeckillKey.GOODS_NOT_EXIST)) {
                return SeckillKey.GOODS_NOT_EXIST.getValue();
            } else {    // Waiting
                return 0;
            }
        } else {
            // Possible extreme case:
            // NO_STOCK has been set because stock was decreased to 0 by others, but the order is still being generated
            // So when NO_STOCK is found in redis, still need to check if the order has been generated
            List<SeckillOrder> orders = orderService.getAllSeckillOrdersByGoodsId(goodsId);
            if (orders == null || orders.size() <= 0) {   // No order of this goods exists
                return 0;
            } else {
                // Try to find the order which has the user's id
                SeckillOrder order = findSeckillOrderByUserId(orders, userId);
                if (order != null) {    // Order is there
                    return order.getOrderId();
                } else {    // No such order, then can make sure that the problem is NO_STOCK
                    return SeckillKey.NO_STOCK.getValue();
                }
            }
        }
    }

    private boolean existsSeckillStatus(long goodsId, SeckillKey seckillKey) {
        return redisService.exists(seckillKey, "" + goodsId);
    }

    public void setSeckillStatus(Long goodsId, SeckillKey seckillKey) {
        redisService.set(seckillKey, "" + goodsId, seckillKey.getValue());
    }

    private SeckillOrder findSeckillOrderByUserId(List<SeckillOrder> orders, Long userId) {
        if (orders == null || orders.size() <= 0) {
            return null;
        }
        for (SeckillOrder order : orders) {
            if (order.getUserId().equals(userId)) {
                return order;
            }
        }
        return null;
    }

    public String generateSeckillPath(SeckillUser user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        // Generate random string, using MD5 to make it safer
        String path = MD5Util.generateMD5(UUIDUtil.uuid() + "123456");
        // Store in redis for future verification
        redisService.set(SeckillKey.SECKILL_PATH, "" + user.getId() + "_" + goodsId, path);
        return path;
    }

    public boolean checkSeckillPath(SeckillUser user, long goodsId, String path) {
        if (user == null || goodsId <= 0 || path == null) {
            return false;
        }
        String realPath = redisService.get(SeckillKey.SECKILL_PATH, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(realPath);
    }


    public BufferedImage generateSeckillCaptcha(SeckillUser user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        // Create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // Set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // Draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // Create a random instance to generate the codes
        Random rdm = new Random();
        // Make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // Generate a random code
        String exp = generateExp(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(exp, 8, 24);
        g.dispose();
        // Save captcha into redis
        int ans = calculate(exp);
        redisService.set(SeckillKey.SECKILL_CAPTCHA, user.getId() + "_" + goodsId, ans);
        return image;
    }

    // Generate random math expression
    // Format: "a op1 b op2 c"
    // Numbers: 0 ~ 9
    // Operators: + or -
    private String generateExp(Random rdm) {
        int a = rdm.nextInt(10);
        int b = rdm.nextInt(10);
        int c = rdm.nextInt(10);
        char op1 = OPS[rdm.nextInt(2)];
        char op2 = OPS[rdm.nextInt(2)];
        return "" + a + op1 + b + op2 + c;
    }

    private int calculate(String exp) {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
            Object result = scriptEngine.eval(exp);
            if (result instanceof Double) {
                return (int) Double.parseDouble(result.toString());
            }
            return (Integer) result;
        } catch (Exception e) {
            e.printStackTrace();
            return 65535;
        }
    }

    public boolean checkCaptchaInput(SeckillUser user, long goodsId, int inputAns) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer realAns = redisService.get(SeckillKey.SECKILL_CAPTCHA, user.getId() + "_" + goodsId, Integer.class);
        if (realAns == null || realAns - inputAns != 0) {
            return false;
        }
        redisService.delete(SeckillKey.SECKILL_CAPTCHA, user.getId() + "_" + goodsId);
        return true;
    }
}
