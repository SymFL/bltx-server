package com.csk2024.bltx.service.impl;

import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.mapper.TPermissionMapper;
import com.csk2024.bltx.mapper.TRoleMapper;
import com.csk2024.bltx.mapper.TUserMapper;
import com.csk2024.bltx.mapper.TUserRoleMapper;
import com.csk2024.bltx.model.TPermission;
import com.csk2024.bltx.model.TRole;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.model.TUserRole;
import com.csk2024.bltx.query.UserQuery;
import com.csk2024.bltx.result.R;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private TUserMapper tUserMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private TPermissionMapper tPermissionMapper;
    @Resource
    private TUserRoleMapper tUserRoleMapper;

    /**
     * 通过账号查询用户
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        TUser tUser = tUserMapper.selectByLoginAct(username);

        if(tUser == null){
            throw new UsernameNotFoundException("登录账号不存在");
        }

        /*//查询当前用户的角色
        List<TRole> tRoleList = tRoleMapper.selectByUserId(tUser.getId());
        //字符串的角色列表
        List<String> stringRoleList = new ArrayList<>();
        tRoleList.forEach(tRole -> {
            stringRoleList.add(tRole.getRole());
        });

        //设置用户角色
        tUser.setRoleList(stringRoleList);*/

        //查询该用户有哪些菜单权限
        List<TPermission> menuPermissionList = tPermissionMapper.selectMenuPermissionByUserId(tUser.getId());

        //设置用户菜单权限
        tUser.setMenuPermissionList(menuPermissionList);

        return tUser;
    }

    /**
     * 分页查询所有用户
     */
    @Override
    public PageInfo<TUser> getUsersByPage(Integer current,String name) {
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        List<TUser> list = tUserMapper.selectUsersByPage(name);
        return new PageInfo<>(list);
    }

    @Override
    public TUser getUserDetail(Integer id) {
        TUser tUser = tUserMapper.selectUserDetailById(id);
        TUserRole tUserRole = tUserRoleMapper.selectByUserId(id);
        if(tUserRole != null){
            tUser.setRole(tUserRole.getRoleId());
        }
        return tUser;
    }

    @Override
    public int saveUser(UserQuery userQuery) {
        TUser user = new TUser();
        BeanUtils.copyProperties(userQuery,user);

        user.setCreateBy(JWTUtils.parseUserFromJWT(userQuery.getToken()).getId());
        user.setLoginPwd(passwordEncoder.encode(userQuery.getLoginPwd()));
        user.setCreateTime(new Date());

        int i = tUserMapper.insertSelective(user);

        TUserRole tUserRole = new TUserRole();
        tUserRole.setUserId(user.getId());
        tUserRole.setRoleId(userQuery.getRole());
        int i1 = tUserRoleMapper.insertSelective(tUserRole);

        return i+i1;
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

        TUserRole tUserRole = tUserRoleMapper.selectByUserId(userQuery.getId());
        int i = 0;
        if(tUserRole == null){
            tUserRole.setUserId(user.getId());
            tUserRole.setRoleId(userQuery.getRole());
            tUserRoleMapper.insertSelective(tUserRole);
        }else{
            tUserRole.setRoleId(userQuery.getRole());
            i += tUserRoleMapper.updateByPrimaryKeySelective(tUserRole);
        }

        i += tUserMapper.updateByPrimaryKeySelective(user);
        return i;
    }

    @Override
    public int delUser(Integer id) {
        return tUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int batchDel(List<String> ids) {
        return tUserMapper.batchDelete(ids);
    }

    @Override
    public R changePwd(UserQuery userQuery) {
        TUser tUser = JWTUtils.parseUserFromJWT(userQuery.getToken());
        if(!passwordEncoder.matches(userQuery.getOldLoginPwd(), tUser.getLoginPwd())){
            return R.FAIL("原密码错误！");
        }
        if(passwordEncoder.matches(userQuery.getNewLoginPwd(), tUser.getLoginPwd())){
            return R.FAIL("新密码不得与原密码相同！");
        }
        tUser.setLoginPwd(passwordEncoder.encode(userQuery.getNewLoginPwd()));
        int i = tUserMapper.updateByPrimaryKeySelective(tUser);
        return i > 0 ?  R.OK() : R.FAIL("未知错误，请联系管理员");
    }
}
