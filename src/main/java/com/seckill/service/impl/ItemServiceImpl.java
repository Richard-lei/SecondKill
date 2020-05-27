package com.seckill.service.impl;

import com.seckill.dao.ItemMapper;
import com.seckill.dao.ItemStockMapper;
import com.seckill.dataobject.ItemDO;
import com.seckill.dataobject.ItemStockDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.ItemService;
import com.seckill.service.model.ItemModel;
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

        ItemModel model = convertModelFromDataObj(itemDO, stockDO);
        return model;
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
