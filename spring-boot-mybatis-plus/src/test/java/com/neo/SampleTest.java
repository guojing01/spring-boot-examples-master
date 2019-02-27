package com.neo;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neo.entity.User;
import com.neo.entity.vo.UserDto;
import com.neo.mapper.UserMapper;
import com.neo.util.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2019/1/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.neo.mapper")
public class SampleTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testSelectByCondition() {
        System.out.println(("----- selectAll by condition 1 ------"));
        User user = new User();
        user.setAge(18);
        user.setName("Jone");
        List<User> userList1 = userMapper.selectList(new QueryWrapper<User>().setEntity(user));
        userList1.forEach(System.out::println);

        System.out.println(("----- selectAll by condition 2------"));
        List<User> userList2 = userMapper.selectList(new QueryWrapper<User>().like("name","J"));
        userList2.forEach(System.out::println);

        System.out.println(("----- selectAll by condition 3------"));
        List<User> userList3 = userMapper.selectList(new QueryWrapper<User>().gt("age",19).like("name","J"));
        userList3.forEach(System.out::println);



        System.out.println(("----- selectPage by condition 4------"));
        IPage<User> userList4=userMapper.selectPage(
                new Page<User>(1, 10),
                new QueryWrapper<User>().like("name","J")
        );
        System.out.println(JsonUtils.objectToJson(userList4));




        System.out.println(("----- selectAll by condition 5------"));
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        List<User> userList5 = userMapper.selectBatchIds(idList);
        userList5.forEach(System.out::println);


        System.out.println(("----- selectAll by 注解sql查询 6------"));
        User user0 = new User();
        user0.setAge(18);
        List<User> userList6 = userMapper.getAllTest(user0);
        userList6.forEach(System.out::println);



        System.out.println(("----- selectAll by xml 原生sql查询 7------"));
        User user01 = new User();
        user01.setAge(18);
        List<User> userList7 = userMapper.getAllTestXml(user01);
        userList7.forEach(System.out::println);

        System.out.println(("----- selectAll by 注解sql查询 8------"));
        User user02 = new User();
        user02.setAge(18);
        List<UserDto> userList8 = userMapper.getAllTest2(user02);
        userList8.forEach(System.out::println);



    }


    @Test
    public void testSelectByCondition2() {
        System.out.println(("----- selectAll by condition ------"));
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("name","Jack"));
        userList.forEach(System.out::println);
    }

}