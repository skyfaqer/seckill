package com.cgy.seckill.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SeckillGoods {

	private Long id;

	private Long goodsId;

	// Seckill stock only, not total stock
	private Integer stockCount;

	private Date startDate;

	private Date endDate;
}
