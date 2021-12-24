package com.markerhub.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.dto.PassDto;
import com.markerhub.common.lang.Const;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysRole;
import com.markerhub.entity.SysUser;
import com.markerhub.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 *    
 * @since 2021-12-18
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    //点击 分配角色或编辑 按钮时，对弹出的 对话框 中与 用户信息 相关的数据进行 回显(回调显示)
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result info(@PathVariable("id") Long id){

        SysUser sysUser = sysUserService.getById(id);
        Assert.notNull(sysUser,"找不到该管理员");//对sysUser中的id进行非空校验，如果为空，就抛出异常，

        List<SysRole> roles = sysRoleService.listRolesByUserId(id);

        sysUser.setSysRoles(roles);
        return Result.succ(sysUser);
    }

    //page  查询
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result list(String username){
        Page<SysUser> pageData = sysUserService.page(getPage(), new QueryWrapper<SysUser>()
                        //条件判断， 如果非空， true  说明查询框里有用户输入的数据，根据该数据进行查询  SysUser实体
                .like(StrUtil.isNotBlank(username), "username", username));

        pageData.getRecords().forEach(u -> {  //循环
            u.setSysRoles(sysRoleService.listRolesByUserId(u.getId()));//根据用户Id获取角色对象
        });
        return Result.succ(pageData);
    }

    //保存
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result save(@Validated @RequestBody SysUser sysUser){
        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(Const.STATUS_ON);

        //默认密码 888888
        String password = bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD);
        sysUser.setPassword(password);

        //默认头像
        sysUser.setAvatar(Const.DEFAULT_AVATAR);

        sysUserService.save(sysUser);//保存到数据库
        return Result.succ(sysUser);
    }

    /**
     * 更新
     * @param sysUser
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@Validated @RequestBody SysUser sysUser){
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);

        return Result.succ(sysUser);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result delete(@RequestBody Long[] ids){
        sysUserService.removeByIds(Arrays.asList(ids));//数组转集合，再根据ids中的id删除用户
        //删除关联表的数据
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>()
                .in("user_id",ids));
        //删除操作，没有返回数据
        return Result.succ("");
    }

    /**
     * 分配角色
     * @param userId
     * @return
     */
    @Transactional//事务
    @PostMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('sys:user:role')")
    public Result rolePerm(@PathVariable("userId") Long userId, @RequestBody Long[] roleIds){

        List<SysUserRole> userRoles = new ArrayList<>();

        Arrays.stream(roleIds).forEach(r -> {//遍历
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);

            userRoles.add(sysUserRole);
        });
        //批量删除
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        //批量保存
        sysUserRoleService.saveBatch(userRoles);

        //删除缓存(角色改变了，对应的权限也改变了，所以清除权限缓存)
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearUserAuthorityInfo(sysUser.getUsername());
        return Result.succ("");
    }

    /**
     * 重置密码  888888
     * @param userId
     * @return
     */
    @PostMapping("/repass")
    @PreAuthorize("hasAuthority('sys:user:repass')")
    public Result repass(@RequestBody Long userId){
        SysUser sysUser = sysUserService.getById(userId);
        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return Result.succ("");
    }

    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal){

        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        boolean matches = bCryptPasswordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if(!matches){
            return Result.fail("旧密码不正确");
        }

        sysUser.setPassword(bCryptPasswordEncoder.encode(passDto.getPassword()));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return Result.succ("");
    }
}
