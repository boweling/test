package com.markerhub.security;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.markerhub.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//登录失败处理器
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        //因为该方法是void修饰，不能使用return的形式返回数据
        //所以这里使用  流  的形式

        //设置响应数据为 json格式数据 utf-8编码
        response.setContentType("application/json;charset=UTF-8");
        //获取响应输出流对象
        ServletOutputStream outputStream = response.getOutputStream();
        //封装失败信息返回对象
        Result result = Result.fail("用户名或密码错误");

        //将result转化成json字符串后获取字节码(采用utf-8编码)后，再写入输出流对象
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        //将输出流推出去？
        outputStream.flush();
        //关闭输出流
        outputStream.close();
    }
}
