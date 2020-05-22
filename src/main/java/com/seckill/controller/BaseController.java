package com.seckill.controller;

import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";

    /**
     * 异常拦截处理
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResponse handlerException(HttpServletRequest request, Exception ex){
        Map<String, Object> repnseData = new HashMap<>();
        if (ex instanceof BusinessException) {
            BusinessException be = (BusinessException) ex;
            repnseData.put("errCode", be.getErrCode());
            repnseData.put("errMsg", be.getErrMsg());
        }
        else {
            repnseData.put("errCode", EmBusinessError.UNKOWN_ERROR.getErrCode());
            repnseData.put("errMsg", EmBusinessError.UNKOWN_ERROR.getErrMsg());
        }

        return new CommonResponse("fail", repnseData);
    }

}
