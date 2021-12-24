package com.markerhub.service;

import com.markerhub.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 *    
 * @since 2021-12-18
 */
public interface SysUserService extends IService<SysUser> {

    //通过用户名获取用户实体
    SysUser getByUsername(String username);

    //根据 用户Id  获取  用户权限信息
    String getUserAuthorityInfo(Long userId);

    void clearUserAuthorityInfo(String username);

    void clearUserAuthorityInfoByRoleId(Long roleId);

    void clearUserAuthorityInfoByMenuId(Long menuId);
}
