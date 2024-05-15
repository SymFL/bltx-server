package com.csk2024.bltx.controller;

import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.query.UserQuery;
import com.csk2024.bltx.result.R;
import com.csk2024.bltx.service.UserService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 返回登录信息
     */
    @GetMapping("/api/login/info")
    public R loginInfo(Authentication authentication){
        TUser tUser = (TUser) authentication.getPrincipal();
        return R.OK(tUser);
    }

    /**
     * 免登录
     */
    @GetMapping("/api/login/free")
    public R freeLogin(){
        return R.OK();
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/api/users")
    public R userList(@RequestParam(value = "current",required = false) Integer current){
        if (current == null){
            current = 1;
        }
        PageInfo<TUser> pageInfo = userService.getUsersByPage(current);
        return R.OK(pageInfo);
    }

    /**
     * 查询用户详细信息
     */
    @GetMapping("/api/users/{id}")
    public R userDetail(@PathVariable("id") Integer id){
        TUser user = userService.getUserDetail(id);
        System.out.println(user.getCreateTime());
        return R.OK(user);
    }

    /**
     * 添加用户
     */
    @PostMapping("/api/users/add")
    public R addUser(UserQuery userQuery, @RequestHeader(value="Authorization") String token){
        userQuery.setToken(token);
        int save = userService.saveUser(userQuery);
        return save >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 编辑用户
     */
    @PutMapping("/api/users/edit")
    public R editUser(UserQuery userQuery, @RequestHeader(value="Authorization") String token){
        userQuery.setToken(token);
        int edit = userService.editUser(userQuery);
        return edit >= 1 ? R.OK() : R.FAIL();
    }
}