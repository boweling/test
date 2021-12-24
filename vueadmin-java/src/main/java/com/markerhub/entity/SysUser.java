package com.markerhub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 *    
 * @since 2021-12-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")  //controller中@Validated注解会对此字段进行校验
    private String username;

    private String password;
    //=======
    private String phone;
    //=======

    private String avatar;

    @NotBlank(message = "用户名不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    private String city;

    private LocalDateTime lastLogin;

    //告诉mybatisPlus，查表时忽略此字段
    @TableField(exist = false)
    private List<SysRole> sysRoles = new ArrayList<>();


}
