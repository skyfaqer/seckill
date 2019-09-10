package com.cgy.seckill.rabbitmq;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.domain.OrderInfo;
import com.cgy.seckill.domain.SeckillOrder;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.redis.RedisService;
import com.cgy.seckill.redis.SeckillKey;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.GoodsService;
import com.cgy.seckill.service.OrderService;
import com.cgy.seckill.service.SeckillService;
import com.cgy.seckill.utils.StringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

//    @RabbitListener(queues = MQConfig.QUEUE_NAME)
//    public void receive(String message) {
//        log.info("Receive message: " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.QUEUE_NAME1)
//    public void receiveQueue1(String message) {
//        log.info("Receive queue1 message: " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.QUEUE_NAME2)
//    public void receiveQueue2(String message) {
//        log.info("Receive queue2 message: " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE_NAME)
//    public void receiveHeadersQueue(byte[] message) {
//        log.info("Receive headers queue message: " + new String(message));
//    }

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE_NAME)
    public void receiveSeckillMessage(String message) {
        log.info("Receive seckill message: " + message);
        SeckillMessage sm = StringBeanUtil.stringToBean(message, SeckillMessage.class);
        SeckillUser user = sm.getSeckillUser();
        long goodsId = sm.getGoodsId();
        // After receiving the message:
        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
        // Check if goods exists
        if (goods == null) {
            seckillService.setSeckillStatus(goodsId, SeckillKey.GOODS_NOT_EXIST);
            return;
        }
        // Check if seckill has ended or not
        if (goods.getEndDate().getTime() <= System.currentTimeMillis()) {
            seckillService.setSeckillStatus(goodsId, SeckillKey.SECKILL_END);
            return;
        }
        // Check if stock is available
        int stock = goods.getStockCount();
        if (stock <= 0) {
            seckillService.setSeckillStatus(goodsId, SeckillKey.NO_STOCK);
            return;
        }
        // Check if the user has bought this product in seckill
        // A user is allowed to buy only one item of a product
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return;
        }
        // No error, then do seckill
        OrderInfo orderInfo = seckillService.seckill(user, goods);
    }
}
