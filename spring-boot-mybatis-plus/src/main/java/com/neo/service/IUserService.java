package com.neo.service;

import com.neo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rcb
 * @since 2019-02-27
 */
public interface IUserService extends IService<User> {
    List<User> getAllTestXml(User user);
}
