package com.markerhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.entity.SysMenu;
import com.markerhub.entity.SysRole;
import com.markerhub.entity.SysUser;
import com.markerhub.mapper.SysUserMapper;
import com.markerhub.service.SysMenuService;
import com.markerhub.service.SysRoleService;
import com.markerhub.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markerhub.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2021-12-18
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {


    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    SysMenuService sysMenuService;

    @Override
    public SysUser getByUsername(String username) {

        //使用mybatisPlus的查询方法，new一个条件查询构造器，返回类型为SysUser，匹配字段为"username"，值为username的用户实体
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    /**
     *根据用户Id获取用户权限信息(角色权限，菜单操作权限)
     * @param userId
     * @return
     */
    @Override
    public String getUserAuthorityInfo(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);

        //  最终拼接效果应该是这样的: "ROLE_admin,ROLE_normal,... ,sys:user:list,...."
        String authority = "";

        //先判断redis中有没有缓存的用户信息，有就取出来直接用
        if (redisUtil.hasKey("GrantedAuthority:" + sysUser.getUsername())) {
            authority = (String) redisUtil.get("GrantedAuthority:" + sysUser.getUsername());

        } else {//如果redis中没有用户的缓存信息，就重新从数据库中获取

            // 获取角色编码
            List<SysRole> roles = sysRoleService.list(new QueryWrapper<SysRole>()
                    //子查询,先根据userId查出对应的role_id，再根据role_id查出 角色对象
                    .inSql("id", "select role_id from sys_user_role where user_id = " + userId));

            if (roles.size() > 0) {
                //  转化成流，以ROLE_为前缀拼接code后，再通过 "," 分隔开来     eg: "ROLE_admin,ROLE_normal"
                String roleCodes = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authority = roleCodes.concat(",");  //拼接一个  ,  下面还要拼接 菜单权限字符串列表
            }

            // 获取菜单操作编码
            List<Long> menuIds = sysUserMapper.getNavMenuIds(userId);//根据 用户Id 获取 菜单Id
            if (menuIds.size() > 0) {

                List<SysMenu> menus = sysMenuService.listByIds(menuIds);//根据 菜单Id  查找出对应的 菜单操作权限列表
                //与上面同理的操作
                String menuPerms = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));

                authority = authority.concat(menuPerms);    //拼接菜单权限字符串列表  将角色权限和菜单权限信息拼接在一起
            }

            //将 用户名、用户权限信息存到redis中便于获取，就不需要   每次请求进行多次数据库查表操作      缓存时间 1小时 time单位是秒
            redisUtil.set("GrantedAuthority:" + sysUser.getUsername(), authority, 60 * 60);
        }
        return authority;
    }

    //删除某个用户的权限信息
    @Override
    public void clearUserAuthorityInfo(String username) {
        redisUtil.del("GrantedAuthority:"+ username);

    }

    //删除所有与该角色关联的用户的权限信息
    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        List<SysUser> sysUsers = this.list(new QueryWrapper<SysUser>()
                .inSql("id", "select user_id from sys_user_role where role_id = " + roleId));
        sysUsers.forEach(u -> { //遍历
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }

    //删除所有与该菜单关联的所有用户的权限信息
    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<SysUser> sysUsers = sysUserMapper.listByMenuId(menuId);

        sysUsers.forEach(u -> { //遍历
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }
}
