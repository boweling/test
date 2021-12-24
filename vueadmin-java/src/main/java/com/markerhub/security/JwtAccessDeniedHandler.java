package com.markerhub.security;

import cn.hutool.json.JSONUtil;
import com.markerhub.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt权限异常处理器
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info("权限不够！！");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);//设置权限不足状态码 forbidden 403 禁止

        //获取响应输出流
        ServletOutputStream outputStream = response.getOutputStream();

        Result result = Result.fail(accessDeniedException.getMessage());

        //将result转化成json字符串后获取字节码(采用utf-8编码)
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        //将输出流推出去？
        outputStream.flush();
        //关闭输出流
        outputStream.close();
    }
}
