package com.neo.web;
import com.neo.entity.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2018/4/26
 */
public class BaseController {

    public static final String CURRENT_USER_INFO = "currentUserInfo";
    public static final String CURRENT_USER_ID = "currentUserID";

    public UserInfo getCurrentUser() {
        Session session = SecurityUtils.getSubject().getSession();
        UserInfo user = (UserInfo)session.getAttribute(CURRENT_USER_INFO);
        return user;
    }

    public Integer getCurrentUserId() {
        Session session = SecurityUtils.getSubject().getSession();
        Integer userId = (Integer)session.getAttribute(CURRENT_USER_ID);
        return userId;
    }
}