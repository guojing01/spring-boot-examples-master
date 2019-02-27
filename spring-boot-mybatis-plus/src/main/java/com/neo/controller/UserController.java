package com.neo.controller;


import com.neo.entity.User;
import com.neo.mapper.UserMapper;
import com.neo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author rcb
 * @since 2019-02-27
 */
@RestController
@RequestMapping("/user")
public class UserController   {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(path = "/userinfo")
    public List<User> getAllTestXml( @RequestBody User user){
        return iUserService.getAllTestXml(user);
    }
}
