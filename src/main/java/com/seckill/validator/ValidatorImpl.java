package com.seckill.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


/**
 * 校验参数加上判断注解，参考UserModel类
 */
@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    // 实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        final ValidationResult result = new ValidationResult();

        Set<ConstraintViolation<Object>> validateSet = validator.validate(bean);

        // 有错误
        if (validateSet.size() > 0) {
            result.setHasErrors(true);
            validateSet.forEach(valition -> {
                String errMsg = valition.getMessage();
                String propertyName = valition.getPropertyPath().toString();

                result.getErrorMsgMap().put(propertyName, errMsg);
            });
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将hibernate validator 通过工厂得到初始化方式实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
