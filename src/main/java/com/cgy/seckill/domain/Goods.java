package com.cgy.seckill.domain;

import lombok.Data;

@Data
public class Goods {

	private Long id;

	private String goodsName;

	private String goodsTitle;

	private String goodsImg;

	private String goodsDetail;

	private Double goodsPrice;

	// Total stock of goods including seckill stock
	private Integer goodsStock;
}
