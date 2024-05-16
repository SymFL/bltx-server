package com.csk2024.bltx.service.impl;

import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.mapper.TUserMapper;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.query.UserQuery;
import com.csk2024.bltx.service.UserService;
import com.csk2024.bltx.utils.JWTUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private TUserMapper tUserMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 通过账号查询用户
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        TUser tUser = tUserMapper.selectByLoginAct(username);

        if(tUser == null){
            throw new UsernameNotFoundException("登录账号不存在");
        }

        return tUser;
    }

    /**
     * 分页查询所有用户
     */
    @Override
    public PageInfo<TUser> getUsersByPage(Integer current) {
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        List<TUser> list = tUserMapper.selectUsersByPage();
        return new PageInfo<>(list);
    }

    @Override
    public TUser getUserDetail(Integer id) {
        return tUserMapper.selectUserDetailById(id);
    }

    @Override
    public int saveUser(UserQuery userQuery) {
        TUser user = new TUser();
        BeanUtils.copyProperties(userQuery,user);

        user.setCreateBy(JWTUtils.parseUserFromJWT(userQuery.getToken()).getId());
        user.setLoginPwd(passwordEncoder.encode(userQuery.getLoginPwd()));
        user.setCreateTime(new Date());

        return tUserMapper.insertSelective(user);
    }

    @Override
    public int editUser(UserQuery userQuery) {
        TUser user = new TUser();
        BeanUtils.copyProperties(userQuery,user);

        user.setEditBy(JWTUtils.parseUserFromJWT(userQuery.getToken()).getId());
        if(!user.getLoginPwd().isEmpty()){
            user.setLoginPwd(passwordEncoder.encode(userQuery.getLoginPwd()));
        }
        user.setEditTime(new Date());

        return tUserMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public int delUser(Integer id) {
        return tUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int batchDel(List<String> ids) {
        return tUserMapper.batchDelete(ids);
    }
}
