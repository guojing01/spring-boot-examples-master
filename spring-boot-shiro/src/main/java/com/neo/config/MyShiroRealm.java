package com.neo.config;

import com.neo.entity.SysRole;
import com.neo.entity.UserInfo;
import com.neo.mapper.SysPermissionMapper;
import com.neo.mapper.SysRoleMapper;
import com.neo.mapper.UserInfoMapper;
import com.neo.utils.RedisUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Set;

public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserInfoMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPermissionMapper permissionMapper;
    //userinfo在redis中的缓存时间 单位是秒
    private static final int USERINFO_EXPIRE= 60 * 60;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo  = (UserInfo)principals.getPrimaryPrincipal();
        if(userInfo !=null){
            //用户的角色集合
            Set<String> roleNames=roleMapper.selectUserRole(userInfo.getUid());
            authorizationInfo.setRoles(roleNames);
            //用户的角色对应的所有权限，如果只使用角色定义访问权限，下面的四行可以不要
            List<SysRole> roleList=roleMapper.selectRoles(userInfo.getUid());
            for (SysRole role : roleList) {
                Set<String> permissions=permissionMapper.selectRolePermission(role.getId());
                authorizationInfo.addStringPermissions(permissions);
            }
        }

        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
        // 将AuthenticationToken转化为UsernamePasswordToken
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String username = token.getUsername().trim();
        String password = "";
        if (token.getPassword() != null) {
            password = new String(token.getPassword());
        }
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        //到数据库查是否有此对象
        UserInfo userInfo = (UserInfo) redisUtil.get("userinfo:"+username,UserInfo.class);
        if(userInfo==null){
            userInfo=userMapper.selectOne(username);
            redisUtil.setAndExpire("userinfo:"+username,userInfo,USERINFO_EXPIRE);
        }
        System.out.println("----->>userInfo="+userInfo);
        if(userInfo == null){
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户名
                password.toCharArray(),
                /**
                 * 这个地方是shiro密码验证的加密器进行加盐
                 */
//                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }

}