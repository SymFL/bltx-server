package com.csk2024.bltx.service;

import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.query.UserQuery;
import com.csk2024.bltx.result.R;
import com.github.pagehelper.PageInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    PageInfo<TUser> getUsersByPage(Integer current,String name);

    TUser getUserDetail(Integer id);

    int saveUser(UserQuery userQuery);

    int editUser(UserQuery userQuery);

    int delUser(Integer id);

    int batchDel(List<String> ids);

    R changePwd(UserQuery userQuery);
}
