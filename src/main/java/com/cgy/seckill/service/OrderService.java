package com.cgy.seckill.service;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.dao.OrderDao;
import com.cgy.seckill.domain.OrderInfo;
import com.cgy.seckill.domain.SeckillOrder;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.redis.OrderKey;
import com.cgy.seckill.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long userId, long goodsId) {
        // return orderDao.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        // Query redis cache, not database
        // Key: userId and goodsId (identifying unique order)
        return redisService.get(OrderKey.SECKILL_UID_GID, "" + userId + "_" + goodsId, SeckillOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVO goods) {
        // Create order info
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        // Insert into order_info table
        int result1 = orderDao.insertOrder(orderInfo);
        // System.out.println(result1);
        // Insert into seckill_order table
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setUserId(user.getId());
        int result2 = orderDao.insertSeckillOrder(seckillOrder);
        // System.out.println(result2);
        // Write to redis cache
        redisService.set(OrderKey.SECKILL_UID_GID, "" + user.getId() + "_" + goods.getId(), seckillOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    public List<SeckillOrder> getAllSeckillOrdersByGoodsId(long goodsId) {
        return orderDao.getAllSeckillOrdersByGoodsId(goodsId);
    }
}
