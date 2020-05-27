package com.seckill.controller;


import com.seckill.controller.view.OrderVO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonResponse;
import com.seckill.service.IOrderService;
import com.seckill.service.model.OrderModel;
import com.seckill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("orderController")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class OrderController extends BaseController{

    @Autowired
    private IOrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/createOrder", method = RequestMethod.POST, consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonResponse createOrder(@RequestParam("itemId") Integer itemID,
                                      @RequestParam("amount") Integer amount) throws BusinessException {

        // 获取用户的登录信息
        Boolean isLogin = (Boolean) this.httpServletRequest.getSession().getAttribute("IS_LOGIN");

        if (isLogin == null || ! isLogin.booleanValue()) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) this.httpServletRequest.getSession().getAttribute("LOGIN_USER");

        // 创建订单
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemID, amount);

        OrderVO orderVO = convertOrderVoFromModel(orderModel);
        return new CommonResponse("success", orderVO);
    }

    private OrderVO convertOrderVoFromModel(OrderModel model){
        if (model == null)
            return null;

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(model, orderVO);

        return orderVO;
    }

}
