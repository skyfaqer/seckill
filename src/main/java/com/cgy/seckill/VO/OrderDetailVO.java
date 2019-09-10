package com.cgy.seckill.VO;

import com.cgy.seckill.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVO {

    private GoodsVO goods;

    private OrderInfo order;
}
