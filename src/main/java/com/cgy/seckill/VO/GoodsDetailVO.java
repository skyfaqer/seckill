package com.cgy.seckill.VO;

import com.cgy.seckill.domain.SeckillUser;
import lombok.Data;

@Data
public class GoodsDetailVO {

    private GoodsVO goods;

    private SeckillUser user;

    private int seckillStatus;

    private long remainSeconds;
}
