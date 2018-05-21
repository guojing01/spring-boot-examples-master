package com.neo.entity;


import lombok.Data;

import java.io.Serializable;
@Data
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id; // 编号
    private String role; // 角色标识程序中判断使用,如"admin",这个是唯一的:
    private String description; // 角色描述,UI界面显示使用
    private Boolean available = Boolean.FALSE; // 是否可用,如果不可用将不会添加给用户



}