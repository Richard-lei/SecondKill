package com.seckill.service;

import com.seckill.error.BusinessException;
import com.seckill.service.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface IUserService {

    UserModel getUserByID(Integer id);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String telphone, String emcryptPassword) throws BusinessException;
}
