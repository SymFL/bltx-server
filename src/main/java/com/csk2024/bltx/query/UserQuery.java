package com.csk2024.bltx.query;

import lombok.Data;

/**
 * 接受前端传来的用户信息
 */
@Data
public class UserQuery extends BaseQuery {
    /**
     * 主键，自动增长，用户ID
     */
    private Integer id;

    /**
     * 登录账号
     */
    private String loginAct;

    /**
     * 登录密码
     */
    private String loginPwd;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 账号是否启用，0禁用 1启用
     */
    private Integer accountEnabled;

    /**
     * 账号角色
     */
    private Integer role;

    /**
     * 原密码
     */
    private String oldLoginPwd;

    /**
     * 新密码
     */
    private String newLoginPwd;
}
