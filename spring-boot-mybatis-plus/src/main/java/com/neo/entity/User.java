package com.neo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author rcb
 * @since 2019-02-27
 */
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
