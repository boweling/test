package com.markerhub.security;

import cn.hutool.json.JSONUtil;
import com.markerhub.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义Jwt认证失败处理器
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//设置权限不足状态码 unAuthorized 401  未认证(即未登录)

        //获取响应输出流
        ServletOutputStream outputStream = response.getOutputStream();

        Result result = Result.fail("请先登录");

        //将result转化成json字符串后获取字节码(采用utf-8编码)
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        //将输出流推出去？
        outputStream.flush();
        //关闭输出流
        outputStream.close();
    }
}
