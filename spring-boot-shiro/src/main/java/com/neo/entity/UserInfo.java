package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer uid;
    private String username;//帐号
    private String name;//名称（昵称或者真实姓名，不同系统不同定义）
    private String password; //密码;
    private String salt;//加密密码的盐
    private Integer state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.

    /**
     * 密码盐.
     * @return
     */
    //重新对盐重新进行了定义，用户名+salt，这样就更加不容易被破解
    @JsonIgnore
    public String getCredentialsSalt(){
        return this.username+this.salt;
    }

}