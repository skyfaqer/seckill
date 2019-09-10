package com.cgy.seckill.dao;

import com.cgy.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillUserDao {

    @Select("select * from seckill_user where id = #{id}")
    SeckillUser getById(@Param("id") Long id);

    @Update("update seckill_user set password = #{password} where id = #{id}")
    void update(SeckillUser updatedUser);
}
