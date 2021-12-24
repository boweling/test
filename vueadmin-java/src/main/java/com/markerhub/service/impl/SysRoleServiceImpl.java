package com.markerhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.entity.SysRole;
import com.markerhub.mapper.SysRoleMapper;
import com.markerhub.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        List<SysRole> sysRoles = this.list(new QueryWrapper<SysRole>()
                //从关联表查出与userId相关的记录，再根据记录中的role_id查出SysRole实体对象集合
                .inSql("id", "select role_id from sys_user_role where user_id = " + userId));
        return sysRoles;
    }
}
