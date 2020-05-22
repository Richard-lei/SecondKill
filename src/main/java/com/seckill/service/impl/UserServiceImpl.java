package com.seckill.service.impl;

import com.seckill.dataobject.UserInfoDO;
import com.seckill.dataobject.UserPasswordDO;
import com.seckill.dao.UserInfoMapper;
import com.seckill.dao.UserPasswordMapper;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.IUserService;
import com.seckill.service.model.UserModel;
import com.seckill.util.Md5Util;
import com.seckill.validator.ValidationResult;
import com.seckill.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    UserInfoMapper userMapper;

    @Autowired
    UserPasswordMapper passwordMapper;

    @Autowired
    ValidatorImpl validator;

    @Override
    public UserModel getUserByID(Integer id) {
        UserInfoDO userInfoDO = userMapper.selectByPrimaryKey(id);
        if (userInfoDO == null)
            return null;
        // 通过用户ID获取加密密码
        UserPasswordDO password = passwordMapper.selectByUserID(userInfoDO.getId());

        return convertFromUserData(userInfoDO, password);
    }

    @Override
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR);
        }

        // 校验模型参数
        ValidationResult validate = validator.validate(userModel);
        if (validate.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, validate.getErrMsg());
        }

        UserInfoDO userDO = convertFromUserModel(userModel);
        try {
            userMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, "手机号已注册！");
        }


        userModel.setId(userDO.getId());

        UserPasswordDO passwordDO = convertPasswordFromUserModel(userModel);
        passwordMapper.insertSelective(passwordDO);
    }

    @Override
    public UserModel validateLogin(String telphone, String emcryptPassword) throws BusinessException {
        // 1、通过用户的手机获取用户信息
        UserInfoDO userInfoDO = userMapper.selectByTelphone(telphone);
        if (userInfoDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO passwordDO = passwordMapper.selectByUserID(userInfoDO.getId());

        UserModel userModel = convertFromUserData(userInfoDO, passwordDO);

        // 2、比对用户信息内加密的密码
        if (StringUtils.equals(emcryptPassword, userModel.getEncryptPasswoed())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        return userModel;
    }

    /**
     * UserModel 转换成 UserInfo
     * @param userModel
     * @return
     */
    private UserInfoDO convertFromUserModel(UserModel userModel){
        if (userModel == null)
            return null;

        UserInfoDO userDO = new UserInfoDO();

        BeanUtils.copyProperties(userModel, userDO);

        return userDO;
    }

    /**
     * UserModel 转换成 UserPasswordDO
     * @param userModel
     * @return
     */
    private UserPasswordDO convertPasswordFromUserModel(UserModel userModel){
        if (userModel == null)
            return null;

        UserPasswordDO passwordDO = new UserPasswordDO();
        passwordDO.setEncryptPasswoed(userModel.getEncryptPasswoed());
        passwordDO.setUserId(userModel.getId());

        return passwordDO;
    }

    /**
     * UserInfo 和 UserPassword 构造成UserModel
     * @param userInfoDO
     * @param password
     * @return
     */
    private UserModel convertFromUserData(UserInfoDO userInfoDO, UserPasswordDO password){
        if (userInfoDO == null)
            return null;

        UserModel model = new UserModel();
        BeanUtils.copyProperties(userInfoDO, model);
        // UserInfo 与 UserPassword 有重名的属性，不能再次调用copy 方法
        if (password != null)
            model.setEncryptPasswoed(password.getEncryptPasswoed());

        return model;
    }
}
