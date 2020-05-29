package com.seckill.service.model;


import java.math.BigDecimal;

// 用户下单的交易模型
public class OrderModel {

    // 订单号
    private String id;

    // 用户ID
    private Integer userID;

    // 商品ID
    private Integer itemID;

    // 若非空， 表示以秒杀商品方式下单
    private Integer promoID;

    // 商品的单价，若PromoID非空，表示以秒杀商品价格
    private BigDecimal itemPrice;

    // 购买数量
    private Integer amount;

    // 订单金额，若PromoID非空，表示以秒杀商品价格
    private BigDecimal orderPrice;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Integer getPromoID() {
        return promoID;
    }

    public void setPromoID(Integer promoID) {
        this.promoID = promoID;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}
