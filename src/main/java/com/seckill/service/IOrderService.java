package com.seckill.service;

import com.seckill.error.BusinessException;
import com.seckill.service.model.OrderModel;

public interface IOrderService {

    // 1、通过前端Url上传秒杀活动ID，然后下单接口校验对应ID是否属于对应商品且活动已经开始

    OrderModel createOrder(Integer userID, Integer itemID, Integer promoID, Integer amount) throws BusinessException;

    // 2、直接在下单接口内判断对应商品是否存在秒杀活动，若存在进行中活动，则以秒杀价格下单
    //OrderModel createOrder(Integer userID, Integer itemID, Integer amount) throws BusinessException;



}
