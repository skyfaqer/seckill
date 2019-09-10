package com.cgy.seckill.dao;

import com.cgy.seckill.domain.OrderInfo;
import com.cgy.seckill.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {

    @Select("select * from seckill_order where user_id = #{userId} and goods_id = #{goodsId}")
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info (user_id, goods_id, delivery_addr_id, goods_name, goods_count, goods_price, order_channel, status, create_date)" +
            " values (#{userId}, #{goodsId}, #{deliveryAddrId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    int insertOrder(OrderInfo orderInfo);

    @Insert("insert into seckill_order (user_id, goods_id, order_id) values (#{userId}, #{goodsId}, #{orderId})")
    int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);

    @Select("select * from seckill_order where goods_id = #{goodsId}")
    List<SeckillOrder> getAllSeckillOrdersByGoodsId(@Param("goodsId") long goodsId);
}
