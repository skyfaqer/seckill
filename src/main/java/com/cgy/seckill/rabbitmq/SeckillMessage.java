package com.cgy.seckill.rabbitmq;

import com.cgy.seckill.domain.SeckillUser;
import lombok.Data;

@Data
public class SeckillMessage {

    private SeckillUser seckillUser;

    private long goodsId;
}
