package com.seckill.service.impl;

import com.seckill.dao.OrderInfoMapper;
import com.seckill.dao.SequenceMapper;
import com.seckill.dataobject.OrderInfoDO;
import com.seckill.dataobject.SequenceDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.IOrderService;
import com.seckill.service.IUserService;
import com.seckill.service.ItemService;
import com.seckill.service.model.ItemModel;
import com.seckill.service.model.OrderModel;
import com.seckill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderInfoMapper orderMapper;

    @Autowired
    private SequenceMapper sequenceMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userID, Integer itemID, Integer amount) throws BusinessException {
        // 1、校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemID);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, "商品信息不存在！");
        }

        UserModel userModel = userService.getUserByID(userID);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, "用户信息不存在！");
        }

        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, "数量信息不正确！");
        }

        // 2、落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemID, amount);
        if (! result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 3、订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserID(userID);
        orderModel.setItemID(itemID);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
        // 生成交易流水号
        orderModel.setId(this.generateOrderNo());

        OrderInfoDO orderInfoDO = convertFromOrderModel(orderModel);
        orderMapper.insertSelective(orderInfoDO);

        // 4、商品销量增加
        itemService.increaseSales(itemID, amount);

        // 5、返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected String generateOrderNo() {
        // 订单号有 16 位
        StringBuilder sb = new StringBuilder();
        // 1、前八位为时间信息（年月日）
        LocalDateTime now = LocalDateTime.now();
        String front = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");

        sb.append(front);
        // 2、中间6位为自增序列
        int sequence = 0;
        // 通过数据库表获取自增序列信息
        SequenceDO sequenceDO = sequenceMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();

        // 更新当前值
        sequenceDO.setCurrentValue(sequence + sequenceDO.getStep());
        sequenceMapper.updateByPrimaryKeySelective(sequenceDO);

        // 拼接成6为序列
        for (int i = 0; i < 6 - sequence; i++) {
            sb.append("0");
        }
        sb.append(String.valueOf(sequence));
        // 3、最后两位为分库分表位(暂时写死为00)
        sb.append("00");


        return sb.toString();
    }

    private OrderInfoDO convertFromOrderModel(OrderModel orderModel){
        if (orderModel == null) {
            return null;
        }

        OrderInfoDO orderDO = new OrderInfoDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());

        return orderDO;
    }

}
