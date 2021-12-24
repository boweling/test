package com.markerhub.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.markerhub.common.exception.CaptchaException;
import com.markerhub.common.lang.Const;
import com.markerhub.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//自定义验证码过滤器

/**
 * 图片验证码校验过滤器，在登录过滤器前
 */
@Slf4j
@Component
public class CaptchaFilter extends OncePerRequestFilter {    //因为验证码过滤器是一次性校验规则的，所以让它继承该抽象类

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LoginFailureHandler loginFailureHandler;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {


        String url = httpServletRequest.getRequestURI();
        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {//如果是post的登录请求

            try {
                //就先校验验证码
                validate(httpServletRequest);
            }catch (CaptchaException e){//如果不正确，捕获到异常，就跳转到认证失败处理器进行处理

                //交给登录失败处理器处理
                loginFailureHandler.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
            }

        }
        //验证码校验成功(即验证码正确)的话就交给过滤器链接着往后处理
        filterChain.doFilter(httpServletRequest,httpServletResponse);


    }

    private void validate(HttpServletRequest httpServletRequest) {

        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("token"); //从用户提交的表单中获取key(uuid)值和验证码code

        if(StringUtils.isBlank(key) || StringUtils.isBlank(code)){   //判断key和code是否为空
            throw new CaptchaException("验证码不能为空");  //抛出异常
        }

        //从redis中根据 缓存名称 及key(uuid) 来获取 正确的验证码 和 用户输入的验证码 对比
        if(!code.equals(redisUtil.hget(Const.CAPTCHA_KEY,key))){
            throw new CaptchaException("验证码不正确");
        }

        //因为验证码是一次性使用，所以处理完就删除掉
        redisUtil.hdel(Const.CAPTCHA_KEY,key);

    }
}
