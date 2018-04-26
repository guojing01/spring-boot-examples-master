package com.neo.web;

import com.neo.entity.UserInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/userInfo")
public class UserInfoController extends BaseController{

    /**
     * 用户查询.
     * @return
     */
    @RequestMapping("/userList")
//    @RequiresPermissions("userInfo:view")//权限管理;
    @ResponseBody
    public Object userInfo(){
        UserInfo user = this.getCurrentUser();
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