package com.seckill.controller;


import com.alibaba.druid.util.StringUtils;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonResponse;
import com.seckill.controller.view.UserVO;
import com.seckill.service.IUserService;
import com.seckill.service.model.UserModel;
import com.seckill.util.Md5Util;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RequestMapping("/user")
@Controller("userController")
public class UserController extends BaseController{

    @Autowired
    IUserService userService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonResponse login(@RequestParam("telphone") String telphone,
                                @RequestParam("password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 1、入参校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR);
        }

        // 2、用户登录服务，用来校验用户登录是否合法
        String encryptPassword = Md5Util.encode(password);
        UserModel userModel = userService.validateLogin(telphone, encryptPassword);

        UserVO userVO = convertFromModel(userModel);
        // 3、将登录凭证加入到用户登录成功的Session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userVO);

        return new CommonResponse("success", "登录成功！");
    }

    @RequestMapping("/register")
    @ResponseBody
    public CommonResponse register(@RequestParam("telphone") String telphone, @RequestParam("otpCode") String optCode,
                                   @RequestParam("name")String name, @RequestParam("gender")Integer gender,
                                   @RequestParam("age")Integer age, @RequestParam("password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 1、验证手机号和对应得到OTP code 是否符合
        String sessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        // 两个OTP Code 不相等
        if (! StringUtils.equals(optCode, sessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMATER_VALIDATION_ERROR, "短信验证码输入错误！");
        }
        // 2、开启用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("phone");
        userModel.setEncryptPasswoed(Md5Util.encode(password));

        userService.register(userModel);
        return new CommonResponse("success", "注册成功！");
    }

    /**
     * 通过ID获取用户信息
     * @param id
     * @return
     * @throws BusinessException
     */
    @RequestMapping("/get")
    @ResponseBody
    public CommonResponse getUser(@RequestParam("id") Integer id) throws BusinessException {
        // 调用service服务获取对应ID的User对象
        UserModel userModel = userService.getUserByID(id);

        if(userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        // 将核心领域模型用户转化为可供UI使用的ViewObject
        UserVO userVO = convertFromModel(userModel);

        return new CommonResponse("success", userVO);
    }

    /**
     * 用户获取OTP短信接口
     * @param telphone
     * @return
     */
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonResponse getOTP(@RequestParam("telphone") String telphone){
        // 1、按照一定规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999) + 10000;
        String otpCode = String.valueOf(randomInt);

        // 2、将OTP验证码与用户手机号关联(使用httpSession的方式绑定手机号与OPT验证码)
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        // 3、将OTP验证码通过短信发送给用户（省略）
        System.out.println("telphone=" + telphone + "&optCode=" + otpCode);

        return new CommonResponse("success", null);
    }


    /**
     * 将 UserModel 转换成 UserView
     * @param model
     * @return
     */
    private UserVO convertFromModel(UserModel model){
        if (model == null)
            return null;

        UserVO userView = new UserVO();
        BeanUtils.copyProperties(model, userView);

        return userView;
    }
}
