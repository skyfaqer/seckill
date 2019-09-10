package com.cgy.seckill.dao;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.domain.Goods;
import com.cgy.seckill.domain.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_date, sg.end_date from seckill_goods sg left join goods g on sg.goods_id = g.id")
    List<GoodsVO> getGoodsVOList();

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_date, sg.end_date from seckill_goods sg left join goods g on sg.goods_id = g.id where g.id = #{goodsId}")
    GoodsVO getGoodsVOByGoodsId(@Param("goodsId") long goodsId);

    @Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    int decreaseStock(SeckillGoods g);
}
