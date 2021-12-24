package com.markerhub.service;

import com.markerhub.common.dto.SysMenuDto;
import com.markerhub.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 *    
 * @since 2021-12-18
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取当前用户的导航栏和权限信息
     * @return
     */
    List<SysMenuDto> getCurrentNav();

    /**
     * 获取菜单树状结构
     * @return
     */
    List<SysMenu> tree();
}
