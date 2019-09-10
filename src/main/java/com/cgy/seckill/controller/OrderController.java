package com.cgy.seckill.controller;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.VO.OrderDetailVO;
import com.cgy.seckill.domain.OrderInfo;
import com.cgy.seckill.domain.SeckillUser;
import com.cgy.seckill.result.CodeMsg;
import com.cgy.seckill.result.Result;
import com.cgy.seckill.service.GoodsService;
import com.cgy.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVO> detail(SeckillUser user,
                                        @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_INVALID);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrder(orderInfo);
        orderDetailVO.setGoods(goodsVO);
        return Result.success(orderDetailVO);
    }
}
