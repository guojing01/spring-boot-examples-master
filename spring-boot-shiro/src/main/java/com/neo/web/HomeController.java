package com.neo.web;

import com.neo.entity.UserInfo;
import com.neo.utils.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController extends BaseController{
    private final static Logger log = LoggerFactory.getLogger(RedisUtil.class);
//    @Autowired
//    private RedisUtil redisUtil;

    @RequestMapping("/login")
    @ResponseBody
    public Object login(HttpServletRequest request, String username, String password) throws Exception{
        log.info("测试日志是否进来，登陆开始执行了。。。。。。。。。。。");
        Map<String, Object> map = new HashMap();
        Subject currentUser = SecurityUtils.getSubject();

        try {
            /**
             * 可以在此处实现自己的登陆验证逻辑
             */
            if (!currentUser.isAuthenticated()) {
                UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                //this is all you have to do to support 'remember me' (no config - built in!):
//                token.setRememberMe(true);
                /**
                 * 调用shiro的登陆方法，就是对应MyShiroRealm。doGetAuthenticationInfo方法
                 */
                currentUser.login(token);
                //判断用户是否有角色
//                if ( currentUser.hasRole( "schwartz" ) ) {
//                    log.info("May the Schwartz be with you!" );
//                } else {
//                    log.info( "Hello, mere mortal." );
//                }
                //判断用户是否有权限
//                if ( currentUser.isPermitted( "lightsaber:weild" ) ) {
//                    log.info("You may use a lightsaber ring.  Use it wisely.");
//                } else {
//                    log.info("Sorry, lightsaber rings are for schwartz masters only.");
//                }
                //退出登录
//                currentUser.logout();
                UserInfo userinfo = (UserInfo)currentUser.getPrincipal();
                Session session = currentUser.getSession();
                session.setAttribute("currentUserInfo", userinfo);
                session.setAttribute("currentUserID", userinfo.getUid());
            }
            map.put("token", currentUser.getSession().getId());
            map.put("msg", "登录成功");
        } catch (UnknownAccountException var9) {
            var9.printStackTrace();
            map.put("msg", "账户不存在！");
        } catch (IncorrectCredentialsException var10) {
            var10.printStackTrace();
            map.put("msg", "密码不正确！");
        } catch (LockedAccountException var11) {
            var11.printStackTrace();
            map.put("msg", "账户已锁定！");
        } catch (Exception var12) {
            var12.printStackTrace();
            map.put("msg", "用户名或者密码错误！");
        }

        return map.toString();
    }

    @RequestMapping({"/unauth"})
    @ResponseBody
    public Object unauthorizedRole() {
        Map<String, Object> map = new HashMap();
        map.put("code", "1000001");
        map.put("msg", "没有权限！");
        return map;
    }

    @RequestMapping({"/unlogin"})
    @ResponseBody
    public Object unauth() {
        Map<String, Object> map = new HashMap();
        map.put("code", "1000000");
        map.put("msg", "未登录");
        return map;
    }


    @RequestMapping("/logout")
    @ResponseBody
    public Object logout() throws Exception {
        Map<String, Object> map = new HashMap();
        //在这里执行退出系统前需要清空的数据
        try {

            UserInfo userinfo = getCurrentUser();
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {

                subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
            }
            map.put("code", "1000002");
            map.put("msg", userinfo.getUsername() + "退出登录成功！");
            return map;
        } catch (Exception e) {
            log.info("退出登录失败！");
        }
        map.put("msg", "退出登录失败！");
        return map;
    }
}