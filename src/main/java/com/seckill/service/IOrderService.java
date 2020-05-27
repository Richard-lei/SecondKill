package com.seckill.service;

import com.seckill.error.BusinessException;
import com.seckill.service.model.OrderModel;

public interface IOrderService {

    OrderModel createOrder(Integer userID, Integer itemID, Integer amount) throws BusinessException;



}
