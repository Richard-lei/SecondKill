package com.seckill.service.impl;

import com.seckill.dao.ItemMapper;
import com.seckill.dao.ItemStockMapper;
import com.seckill.dataobject.ItemDO;
import com.seckill.dataobject.ItemStockDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.IPromoService;
import com.seckill.service.ItemService;
import com.seckill.service.model.ItemModel;
import com.seckill.service.model.PromoModel;
import com.seckill.validator.ValidationResult;
import com.seckill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ValidatorImpl validator;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemStockMapper stockMapper;

    @Autowired
    IPromoService promoService;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel model) throws BusinessException {
        // 1、校验入参
        ValidationResult result = validator.validate(model);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, result.getErrMsg());
        }

        // 2、转换ItemModel 为DataObject
        ItemDO itemDO = convertItemDoFromItemModel(model);


        // 3、写入数据库
        itemMapper.insertSelective(itemDO);
        model.setId(itemDO.getId()); // 获取自动生成的ID，设置给Model,用以关联库存表

        ItemStockDO stockDO = convertItemStockDoFromItemModel(model);
        stockMapper.insertSelective(stockDO);

        // 4、返回创建完成的对象

        return this.getItemById(model.getId());
    }


    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOS = itemMapper.listItem();

        List<ItemModel> modelList = itemDOS.stream().map(itemDO -> {
            ItemStockDO stockDO = stockMapper.selectByItemID(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObj(itemDO, stockDO);

            return itemModel;
        }).collect(Collectors.toList());

        return modelList;
    }


    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemMapper.selectByPrimaryKey(id);

        if (itemDO == null) {
            return null;
        }

        // 获取库存数量
        ItemStockDO stockDO = stockMapper.selectByItemID(itemDO.getId());

        ItemModel itemModel = convertModelFromDataObj(itemDO, stockDO);

        // 获取商品的活动信息
        PromoModel promoModel = promoService.getPromoByItemID(itemModel.getId());
        // 没有秒杀活动或秒杀活动已停止
        if (promoModel == null || promoModel.getStatus() == 3) {
            return itemModel;
        }

        itemModel.setPromoModel(promoModel);
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemID, Integer amount) {
        int affectedRow = stockMapper.decreaseStock(itemID, amount);
        if (affectedRow > 0) {
            // 更新库存成功
            return true;
        }
        else {
            // 更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemID, Integer amount){

        itemMapper.increaseSales(itemID, amount);
    }


    private ItemDO convertItemDoFromItemModel(ItemModel model){
        if (model == null) {
            return null;
        }

        ItemDO itemDo = new ItemDO();
        BeanUtils.copyProperties(model, itemDo);
        // BeanUtils 不会copy类型不一致的属性
        itemDo.setPrice(model.getPrice().doubleValue());

        return itemDo;
    }

    private ItemStockDO convertItemStockDoFromItemModel(ItemModel model){
        if (model == null) {
            return null;
        }

        ItemStockDO stockDO = new ItemStockDO();

        stockDO.setItemId(model.getId());
        stockDO.setStock(model.getStock());
        
        return stockDO;
    }

    private ItemModel convertModelFromDataObj(ItemDO itemDO, ItemStockDO stockDO){
        ItemModel model = new ItemModel();

        BeanUtils.copyProperties(itemDO, model);
        model.setPrice(new BigDecimal(itemDO.getPrice()));
        model.setStock(stockDO.getStock());

        return model;
    }
}
