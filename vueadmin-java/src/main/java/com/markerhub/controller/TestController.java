package com.markerhub.controller;

import com.markerhub.common.lang.Result;
import com.markerhub.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {


    @Autowired
    private SysUserService sysUserService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public Result test(){
        return Result.succ(sysUserService.list());
    }

    // 普通用户、超级管理员
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/test/password")
    public Result password(){
        String encodePassword = bCryptPasswordEncoder.encode("111111");
        boolean matches = bCryptPasswordEncoder.matches("111111", encodePassword);

        System.out.println("匹配结果:"+matches);
        return Result.succ(encodePassword);
    }
}
