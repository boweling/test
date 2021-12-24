package com.markerhub.security;

import cn.hutool.json.JSONUtil;
import com.markerhub.common.lang.Result;
import com.markerhub.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        response.setContentType("application/json;charset=UTF-8");
        //获取响应输出流
        ServletOutputStream outputStream = response.getOutputStream();

        //利用用户名，生成jwt，
        String username = authentication.getName();
        String jwt = jwtUtils.generateToken(username);
        //将jwt放到响应请求头中
        response.setHeader(jwtUtils.getHeader(), jwt);


        Result result = Result.succ("");

        //将result转化成json字符串后获取字节码(采用utf-8编码)，再写入输出流对象
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        //将输出流推出去？
        outputStream.flush();
        //关闭输出流
        outputStream.close();
    }
}
