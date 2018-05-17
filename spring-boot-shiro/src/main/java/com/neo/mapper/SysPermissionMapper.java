package com.neo.mapper;

import com.neo.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2018/5/17
 */
public interface SysPermissionMapper {

    Set<String> selectRolePermission(@Param("roleId")Integer roleId);

}