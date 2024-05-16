package com.csk2024.bltx.mapper;

import com.csk2024.bltx.model.TUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TUser record);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TUser record);

    int updateByPrimaryKey(TUser record);

    /**
     * 通过账号查询用户
     */
    TUser selectByLoginAct(String username);

    /**
     * 分页查询所有用户
     */
    List<TUser> selectUsersByPage();

    TUser selectUserDetailById(@Param("id") Integer id);

    int batchDelete(@Param("ids") List<String> ids);
}