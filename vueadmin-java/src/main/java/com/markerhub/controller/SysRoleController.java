package com.markerhub.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.lang.Const;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysRole;
import com.markerhub.entity.SysRoleMenu;
import com.markerhub.entity.SysUserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 *    
 * @since 2021-12-18
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    /**
     * 当点击分配权限或者编辑时，对对话框里的数据进行回显
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result info(@PathVariable("id") Long id){
        //根据id获取角色实体
        SysRole sysRole = sysRoleService.getById(id);
        //获取角色相关联的菜单id
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        //通过  流的形式，从查找出来的关联表中获取所有的menuId
        List<Long> menuIds = roleMenus.stream().map(p ->
                p.getMenuId()).collect(Collectors.toList());
        sysRole.setMenuIds(menuIds);//menuIds设置进sysRole里
        return Result.succ(sysRole);
    }

    /**
     * 查询
     * @param name
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(String name){

        //分页，
        Page<SysRole> pageData = sysRoleService.page(getPage(), //设置分页参数
                                                 //判断查询输入框的name是否有参数，是的话true，根据name查询角色信息
                                                                            //false,就忽略该like条件查询，查询全部记录
                new QueryWrapper<SysRole>().like(StrUtil.isNotBlank(name), "name", name));
        return Result.succ(pageData);
    }

    //保存
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result save(@Validated @RequestBody SysRole sysRole){
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatu(Const.STATUS_ON);  // 状态为 1 表示正常 0表示禁用
        sysRoleService.save(sysRole);
        return Result.succ(sysRole);
    }

    /**
     * 更新
     * @param sysRole
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody SysRole sysRole){
        sysRole.setCreated(LocalDateTime.now());
        sysRoleService.updateById(sysRole);//根据sysRole实体来更新

        //更新缓存
        sysUserService.clearUserAuthorityInfoByRoleId(sysRole.getId());
        return Result.succ(sysRole);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Transactional
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result delete(@RequestBody Long[] ids){  //批量删除

        sysRoleService.removeByIds(Arrays.asList(ids));//删除角色

        // 根据ids中的id 删除中间表的关联记录
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", ids));//这里ids是数组，用 .in
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", ids));//如果是的单个id， 用 .eq

        // 缓存同步删除
        Arrays.stream(ids).forEach(id -> {
            // 更新缓存,角色对应权限，角色删除了，相关的权限也一起删除掉
            sysUserService.clearUserAuthorityInfoByRoleId(id);
        });
        return Result.succ("");
    }

    /**
     * 分配权限,可以选多个Id
     * @param roleId
     * @param menuIds
     * @return
     */
    @Transactional
    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result info(@PathVariable("roleId") Long roleId, @RequestBody Long[] menuIds){

        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();

        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);

            sysRoleMenus.add(roleMenu);
        });

        // 先删除原来的记录，再保存新的
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        sysRoleMenuService.saveBatch(sysRoleMenus);

        // 删除缓存
        sysUserService.clearUserAuthorityInfoByRoleId(roleId);

        return Result.succ(menuIds);
//        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
//
//        Arrays.stream(menuIds).forEach(menuId -> {
//            SysRoleMenu roleMenu = new SysRoleMenu();
//            roleMenu.setMenuId(menuId);
//            roleMenu.setRoleId(roleId);
//
//            sysRoleMenus.add(roleMenu);
//        });
//
//        //先删除原来的记录，再保存新的
//        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",roleId));
//        sysRoleMenuService.saveBatch(sysRoleMenus);
//
//        //删除缓存
//        sysUserService.clearUserAuthorityInfoByRoleId(roleId);
//        return Result.succ(menuIds);
    }

}
