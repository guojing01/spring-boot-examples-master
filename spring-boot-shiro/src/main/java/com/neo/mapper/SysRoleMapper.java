package com.neo.mapper;

import com.neo.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2018/5/17
 */
public interface SysRoleMapper {

    Set<String> selectUserRole(@Param("uid")Integer uid);

    List<SysRole> selectRoles(@Param("uid")Integer uid);
}