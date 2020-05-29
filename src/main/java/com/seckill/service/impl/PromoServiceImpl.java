package com.seckill.service.impl;

import com.seckill.dao.PromoMapper;
import com.seckill.dataobject.PromoDO;
import com.seckill.service.IPromoService;
import com.seckill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements IPromoService {

    @Autowired
    private PromoMapper promoMapper;

    // 根据Item ID获取即将或正在进行的秒杀或动
    @Override
    public PromoModel getPromoByItemID(Integer itemID) {
        // 获取对应商品的秒杀活动信息
        PromoDO promoDO = promoMapper.selectPromoByItemID(itemID);

        // 转换成Model
        PromoModel promoModel = convertPromoModelFromPromoDO(promoDO);
        if (promoModel == null) {
            return null;
        }

        // 设置秒杀状态
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        }
        else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        }
        else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    private PromoModel convertPromoModelFromPromoDO(PromoDO promoDO){
        if (promoDO == null) {
            return null;
        }

        PromoModel promoModel = new PromoModel();

        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setPromoPrice(new BigDecimal(promoDO.getPromoPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));

        return promoModel;
    }
}
