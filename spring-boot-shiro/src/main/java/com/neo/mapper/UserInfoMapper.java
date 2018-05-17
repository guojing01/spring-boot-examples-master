package com.neo.mapper;

import com.neo.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2018/5/17
 */
public interface UserInfoMapper {
    UserInfo selectOne(@Param("username")String username);

}