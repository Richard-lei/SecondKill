package com.seckill.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    // 校验结果是否有错
    private boolean hasErrors = false;

    // 存放错误信息的Map
    private Map<String, String> errorMsgMap = new HashMap<>();

    // 通过通用格式化字符串获取错误信息
    public String getErrMsg(){
        return StringUtils.join(errorMsgMap.values().toArray(), ",");
    }

    public boolean isHasErrors() {
        return hasErrors;
    }


    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }
}
