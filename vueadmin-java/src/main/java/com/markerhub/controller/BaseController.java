package com.markerhub.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.service.*;
import com.markerhub.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

//定义 所有controler类的父类
public class BaseController {

    @Autowired
    HttpServletRequest req;

    @Autowired
    RedisUtil redisUtil;  //redis工具类

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    /**
     * 获取页码
     * @return
     */
    //   设置分页 信息
    public Page getPage() {
        //从request请求中获取当前 默认页码 以及每页 默认页数
        int current = ServletRequestUtils.getIntParameter(req, "cuurent", 1);//默认第一页
        int size = ServletRequestUtils.getIntParameter(req, "size", 10);//默认每页10条记录

        return new Page(current, size);
    }

}
