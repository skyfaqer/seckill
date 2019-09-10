package com.cgy.seckill.VO;

import com.cgy.seckill.domain.Goods;
import lombok.Data;

import java.util.Date;

@Data
public class GoodsVO extends Goods {

    private Double seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;
}
