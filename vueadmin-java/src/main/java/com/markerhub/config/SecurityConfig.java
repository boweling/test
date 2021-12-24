package com.markerhub.config;

import com.markerhub.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity//该注解作用：加载security相关的安全策略的配置
@EnableGlobalMethodSecurity(prePostEnabled = true) // 即哪些方法需要权限。  ()里 设置在 所有的post请求 之前进行 权限的校验
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    CaptchaFilter captchaFilter;

    @Autowired
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Bean
//将自定义Jwt认证过滤器注入容器
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }

    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    //告诉Security密码的加密形式
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    UserDetailServiceImpl userDetailService;

    //设置白名单
    private static final String[] URL_WHITELIST = {
            "/login",
            "/logout",
            "/captcha",
            "/favicon.ico",
    };

    public SecurityConfig() throws Exception {
    }

    //这个配置方法用于配置静态资源的处理方式，可使用 Ant 匹配规则。
    protected void configure(HttpSecurity http) throws Exception {

        //处理跨域问题
        http.cors().and().csrf().disable()  //关闭csrf防护

                // 自定义登录表单页面,使用form表单的形式
                .formLogin()
                .successHandler(loginSuccessHandler)  //跳转登录成功处理器
                .failureHandler(loginFailureHandler)  //跳转登录失败处理器

                //退出
                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)

                //禁用session
                .and()
                .sessionManagement()
                //配置生成规则，这里使用 不生成session的策略 stateless
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //配置拦截规则
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()      //请求与 白名单 进行匹配,如果请求属于白名单里的就让它跳过
                                                            //permitAll()代表任意用户可访问。
                .anyRequest().authenticated()//除了白名单，其他 所有请求链接 都要进行 登录认证 才可进行 访问(即已登录用户才可访问)

                //异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                //配置自定义的过滤器
                .and()
                .addFilter(jwtAuthenticationFilter())  //这里添加自定义的Jwt认证过滤器
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);// 登录验证码校验过滤器
                                                                         //设定验证码过滤器在登录过滤器之前。
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("对用户名密码进行校验");
        auth.userDetailsService(userDetailService);
    }
}
