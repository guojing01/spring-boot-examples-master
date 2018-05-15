package com.neo.web;

import com.neo.entity.UserInfo;
import com.neo.entity.UserTest;
import com.neo.utils.RedisUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test/userInfo")
public class UserInfoController extends BaseController{
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户查询.
     * @return
     */
    @RequestMapping("/userList")
//    @RequiresPermissions("userInfo:view")//权限管理;
    @ResponseBody
    public Object userInfo(){
        redisUtil.set("testjack","12312122");
        String str = redisUtil.get("testjack","jacktest");
        System.out.println("===测试redis集群===" + str);
        UserInfo user = this.getCurrentUser();
        /**
         * 测试存入bean对象的转换操作
         */
        UserTest userTest = new UserTest();
        userTest.setName("jack");
        userTest.setPassword("123456");
        userTest.setUid(21212);
        userTest.setUsername("qwewqe");
        redisUtil.set("testrose",userTest);
        UserTest userinfo = (UserTest)redisUtil.get("testrose",UserTest.class);
        System.out.println("===测试redis集群userinfo===" + userinfo.toString());
        System.out.println("======" + user.getName());
        Integer userId = this.getCurrentUserId();
        System.out.println("======" + userId);
        return "用户查询成功userInfo" + user.getName();
    }

    /**
     * 用户添加;
     * @return
     */
    @RequestMapping("/userAdd")
    @RequiresPermissions("userInfo:add")//权限管理;
    @ResponseBody
    public Object userInfoAdd(){
        return "userInfoAdd";
    }

    /**
     * 用户删除;
     * @return
     */
    @RequestMapping("/userDel")
    @RequiresPermissions("userInfo:del")//权限管理;
    @ResponseBody
    public Object userDel(){
        return "userInfoDel";
    }
}