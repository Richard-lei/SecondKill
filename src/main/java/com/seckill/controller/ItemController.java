package com.seckill.controller;


import com.seckill.controller.view.ItemVO;
import com.seckill.error.BusinessException;
import com.seckill.response.CommonResponse;
import com.seckill.service.impl.ItemServiceImpl;
import com.seckill.service.model.ItemModel;
import com.seckill.service.model.PromoModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("itemController")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController extends BaseController {

    @Autowired
    ItemServiceImpl itemService;


    // 创建商品页
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonResponse createItem(@RequestParam("title")String title,
                                     @RequestParam("description")String description,
                                     @RequestParam("price")BigDecimal price,
                                     @RequestParam("stock")Integer stock,
                                     @RequestParam("imgUrl")String imgUrl) throws BusinessException {

        // 封装Service 请求
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel model = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVoFromModel(model);

        return new CommonResponse("success", itemVO);
    }

    // 商品详情页浏览
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse getItem(@RequestParam("id")Integer id){

        ItemModel model = itemService.getItemById(id);

        ItemVO itemVO = convertItemVoFromModel(model);

        return new CommonResponse("success", itemVO);
    }

    // 商品列表页浏览
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse listItem(){

        List<ItemModel> itemModels = itemService.listItem();

        List<ItemVO> itemVOList = itemModels.stream().map(model -> {
            ItemVO itemVO = this.convertItemVoFromModel(model);

            return itemVO;
        }).collect(Collectors.toList());

        return new CommonResponse("success", itemVOList);
    }

    private ItemVO convertItemVoFromModel(ItemModel model) {
        if (model == null)
            return null;

        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(model, itemVO);
        PromoModel promoModel = model.getPromoModel();
        if (promoModel != null) {
            itemVO.setPromoID(promoModel.getId());
            itemVO.setPromoStatus(promoModel.getStatus());
            itemVO.setPromoPrice(promoModel.getPromoPrice());
            itemVO.setStartDate(promoModel.getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }
        else {
            itemVO.setPromoStatus(0);
        }


        return itemVO;
    }
}
