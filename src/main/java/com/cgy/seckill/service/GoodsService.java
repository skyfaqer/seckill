package com.cgy.seckill.service;

import com.cgy.seckill.VO.GoodsVO;
import com.cgy.seckill.dao.GoodsDao;
import com.cgy.seckill.domain.Goods;
import com.cgy.seckill.domain.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVO> getGoodsVOList() {
        return goodsDao.getGoodsVOList();
    }

    public GoodsVO getGoodsVOByGoodsId(long goodsId) {
        return goodsDao.getGoodsVOByGoodsId(goodsId);
    }

    public boolean decreaseStock(GoodsVO goods) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
        int result = goodsDao.decreaseStock(seckillGoods);
        return result > 0;
    }
}
