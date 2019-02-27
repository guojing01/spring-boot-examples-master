package com.neo.service.impl;

import com.neo.entity.User;
import com.neo.mapper.UserMapper;
import com.neo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rcb
 * @since 2019-02-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllTestXml(User user) {
        return userMapper.getAllTestXml(user);
    }
}
