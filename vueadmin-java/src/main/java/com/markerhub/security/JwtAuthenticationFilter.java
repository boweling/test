package com.markerhub.security;

import cn.hutool.core.util.StrUtil;
import com.markerhub.entity.SysUser;
import com.markerhub.service.SysUserService;
import com.markerhub.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简单来说，前端的每个请求的请求头中会携带jwt
 * 自定义Jwt认证过滤器，来识别这个jwt是哪个用户，以及判断用户是否有权限等操作
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    SysUserService sysUserService;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //根据工具类里的header值，从请求中获取jwt
        String jwt = request.getHeader(jwtUtils.getHeader());
        if (StrUtil.isBlankOrUndefined(jwt)) {//判断是否为空
            chain.doFilter(request, response);//为空，交给后面的过滤器进行处理，如果后续的请求需要某些权限，会直接跳转登录页进行登录
            //获取相关权限后才可进行一些请求操作
            return;
        }

        //根据jwt获取claims实体
        Claims claims = jwtUtils.getClaimsByToken(jwt);
        if (claims == null) {
            throw new JwtException("token不存在异常");
        }
        if (jwtUtils.IsTokenExpired(claims)) {
            throw new JwtException("token 已过期");
        }

        //获取用户名
        String username = claims.getSubject();

        //获取用户的权限信息
        SysUser sysUser = sysUserService.getByUsername(username);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(username, null, userDetailService.getUserAuthority(sysUser.getId()));

        //将用户权限信息设置到Security的上下文中
        SecurityContextHolder.getContext().setAuthentication(token);

        //让 过滤器链 继续往后走
        chain.doFilter(request, response);
    }
}
