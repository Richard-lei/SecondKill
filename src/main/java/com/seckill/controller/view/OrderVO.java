package com.seckill.controller.view;

import java.math.BigDecimal;

public class OrderVO {

    // 订单号
    private String id;

    // 用户ID
    private Integer userID;

    // 商品ID
    private Integer itemID;

    // 商品的单价
    private BigDecimal itemPrice;

    // 购买数量
    private Integer amount;

    // 订单金额
    private BigDecimal orderPrice;

}
