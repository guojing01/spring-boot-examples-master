package com.neo.entity.vo;

import lombok.Data;

/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2019/2/27
 */
@Data
public class UserDto {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}