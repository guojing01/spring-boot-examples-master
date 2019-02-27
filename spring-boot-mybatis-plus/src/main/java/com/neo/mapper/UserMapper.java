package com.neo.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.neo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neo.entity.vo.UserDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rcb
 * @since 2019-02-27
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where age =${age}")
    List<User> getAllTest(User user);



    @Select("select * from user where age =${age}")
    List<UserDto> getAllTest2(User user);

    List<User> getAllTestXml(User user);

}
