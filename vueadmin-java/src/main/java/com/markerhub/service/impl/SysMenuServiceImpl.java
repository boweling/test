package com.markerhub.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.common.dto.SysMenuDto;
import com.markerhub.entity.SysMenu;
import com.markerhub.entity.SysUser;
import com.markerhub.mapper.SysMenuMapper;
import com.markerhub.mapper.SysUserMapper;
import com.markerhub.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markerhub.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 *    
 * @since 2021-12-18
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public List<SysMenuDto> getCurrentNav() {

        //从全局中获取用户名
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser sysUser = sysUserService.getByUsername(username);
        List<Long> menuIds = sysUserMapper.getNavMenuIds(sysUser.getId());//根据用户id获取对应的菜单id
        List<SysMenu> menus = this.listByIds(menuIds);//调用最终父类的查询方法，查出菜单实体对象

        //转树状结构
        List<SysMenu> menuTree = buildTreeMenu(menus);

        //菜单实体转我们自定义DTO对象
        return convert(menuTree);
    }

    /**
     * 获取 菜单管理 里的 树状结构信息
     * @return
     */
    @Override
    public List<SysMenu> tree() {
//获取所有菜单信息                                                     升序排序
        List<SysMenu> menus = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));
        //转成树状结构
        List<SysMenu> treeMenu = buildTreeMenu(menus);//调用下面的方法

        return treeMenu;
    }

    /**
     * SysMenu实体转换我们自定义SysMenuDto实体
     */
    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos = new ArrayList<>();

        menuTree.forEach(m -> {
            SysMenuDto dto = new SysMenuDto();

            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());

            if (m.getChildren().size() > 0) {

                // 子节点调用当前方法进行再次转换,递归调用
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtos.add(dto);
        });

        return menuDtos;
    }

    /**
     * 构建侧边栏树状结构菜单
     * @param menus
     * @return
     */
    private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {

        List<SysMenu> finalMenus = new ArrayList<>();

        // 先各自寻找到各自的孩子节点
        for (SysMenu menu : menus) {

            for (SysMenu e : menus) {
                if (menu.getId() == e.getParentId()) {  //判断e的父Id是否和menu的Id一样
                    menu.getChildren().add(e);     //是，说明e是menu的孩子节点，把e加到menu的Children中
                }
            }

            // 提取出父节点
            if (menu.getParentId() == 0L) { //表示没有上级节点了，那就是父节点
                finalMenus.add(menu);
            }
        }

        System.out.println("导航栏："+JSONUtil.toJsonStr(finalMenus));
        return finalMenus;
    }
}
