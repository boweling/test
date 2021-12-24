package com.markerhub.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.common.dto.SysMenuDto;
import com.markerhub.common.lang.Const;
import com.markerhub.entity.SysMenu;
import com.markerhub.entity.SysRoleMenu;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


/**
 *菜单管理
 * @since 2021-12-18
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    /**
     * 获取当前用户的菜单栏以及权限信息
     *
     * @param principal
     * @return
     */
    @GetMapping("/nav")
    public Result nav(Principal principal) {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        //获取权限信息
        String authorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());//获取到的是一串字符串，以 , 连接
        //把字符串通过逗号分开组成数组形式。
        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");

        //获取导航栏信息
        List<SysMenuDto> navs = sysMenuService.getCurrentNav();


        return Result.succ(MapUtil.builder()
                .put("authoritys", authorityInfoArray)
                .put("nav", navs)
                .map()
        );
    }

    /**
     * 根据菜单Id获取菜单信息
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable(name = "id") Long id) {
        return Result.succ(sysMenuService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list() {
        List<SysMenu> menus = sysMenuService.tree();
        return Result.succ(menus);
    }

    /**
     * 新增按钮
     * @param sysMenu
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody SysMenu sysMenu) {//@Validated对SysMenu里的 一些字段 进行 校验 是否为空
                                                 //@RequestBody: 因为菜单数据是存到Body里的，通过该注解进行自动注入Body数据
                                                //点编辑按钮时，菜单数据自动注入对话框表单
        sysMenu.setCreated(LocalDateTime.now());//设置  创建时间
//        sysMenu.setStatu(Const.STATUS_ON);
        sysMenuService.save(sysMenu);         //保存到数据库
        return Result.succ(sysMenu);        //返回操作完信息
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody SysMenu sysMenu) {
        sysMenu.setUpdated(LocalDateTime.now());

        sysMenuService.updateById(sysMenu);//传入实体，根据实体来更新

        // 清除所有与该菜单相关的权限缓存
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());
        return Result.succ(sysMenu);
    }


    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id") Long id) {
        //从所以表里查找parent_id 和当前id相等的表，如果有，说明当前表下还有子表
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if (count > 0) {
            return Result.fail("请先删除子菜单");
        }

        // 清除所有与该菜单相关的权限缓存
        sysUserService.clearUserAuthorityInfoByMenuId(id);

        sysMenuService.removeById(id);

        // 同步删除中间关联表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));
        return Result.succ("");
    }

}
