package com.seckill.error;

public enum EmBusinessError implements CommonError {
    //通用错误类型 1000
    PARAMATER_VALIDATION_ERROR(10001, "非法参数！"),

    // 未知错误信息（非BusinessException异常）
    UNKOWN_ERROR(10002, "未知错误！"),

    // 2000 开头为用户相关错误信息
    USER_NOT_EXIST(20001, "用户不存在！"),

    // 用户名或密码输入错误
    USER_LOGIN_FAIL(20002, "用户名或密码错误！"),

    USER_NOT_LOGIN(20003, "用户未登录！"),

    // 3000 开头为交易信息错误
    STOCK_NOT_ENOUGH(30001, "库存不足！")
    ;

    EmBusinessError(int errCode, String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
