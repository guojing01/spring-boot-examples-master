package com.neo.config;

import com.neo.config.redis.RedisCacheManager;
import com.neo.config.redis.RedisManager;
import com.neo.config.redis.RedisSessionDAO;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ShiroConfig {


    @Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.timeout}")
	private int timeout;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.pool.max-wait}")
	private long maxWaitMillis;

//    @Bean
//    public UserRealm getUserRealm() {
//        return new UserRealm();
//    }
	//单位是秒,3个小时
    private static final int redisTimeout = 3 * 60 * 60;
    //shiro中session的全局失效时间单位毫秒，
//	private static final long shiroSessionTimeout =  1 * 60 * 1000L;

	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		System.out.println("ShiroConfiguration.shirFilter()");
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		//拦截器.
		Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
		// 配置不会被拦截的链接 顺序判断
		filterChainDefinitionMap.put("/static/**", "anon");
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/logout", "anon");
//        filterChainDefinitionMap.put("/druid/*", "anon");
		//配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
//		filterChainDefinitionMap.put("/logout", "logout");
		//<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		//<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/test/**", "authc");
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/unlogin");//默认进入未登录界面
		// 登录成功后要跳转的链接
//		shiroFilterFactoryBean.setSuccessUrl("/index");

		//未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * 这个地方必须用静态方法，否则redis获取资源会报错
	 * @return
	 */
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

	@Bean
	public SecurityManager securityManager(){
		DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
		//设置realm.
		securityManager.setRealm(myShiroRealm());
		// 自定义缓存实现 使用redis默认是ehcatch
		securityManager.setCacheManager(cacheManager());
		// 自定义session管理 使用redis
		securityManager.setSessionManager(sessionManager());
		//自定义记住我功能
//		securityManager.setRememberMeManager(rememberMeManager());
		return securityManager;
	}

	/**
	 * 设置rememberMeCookie
	 * @return
	 */
//	@Bean(name = "rememberMeCookie")
//	public SimpleCookie rememberMeCookie() {
//		// 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
//		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//		// <!-- 记住我cookie生效时间30天 ,单位秒;-->
//		simpleCookie.setMaxAge(259200);
//		return simpleCookie;
//	}

	/**
	 * cookie管理对象;
	 *
	 * @return
	 */
//	@Bean(name = "rememberMeManager")
//	public CookieRememberMeManager rememberMeManager() {
//		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//		cookieRememberMeManager.setCookie(rememberMeCookie());
//		return cookieRememberMeManager;
//	}


	/**
	 * 配置shiro redisManager
	 * 使用的是shiro-redis开源插件
	 * @return
	 */
	public RedisManager redisManager() {
		//设置redis的配置
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		//设置redis的连接
		RedisManager redisManager = new RedisManager();
		redisManager.setJedisPoolConfig(jedisPoolConfig);
		redisManager.setHost(host);
		redisManager.setPort(port);
		redisManager.setExpire(redisTimeout);// 配置缓存过期时间
		redisManager.setTimeout(timeout);
		redisManager.setPassword(password);

		return redisManager;
	}

	/**
	 * cacheManager 缓存 redis实现
	 * 使用的是shiro-redis开源插件
	 * @return
	 */
	public RedisCacheManager cacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager());
		return redisCacheManager;
	}


	/**
	 * RedisSessionDAO shiro sessionDao层的实现 通过redis
	 * 使用的是shiro-redis开源插件
	 */
	@Bean
	public RedisSessionDAO redisSessionDAO() {
		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		redisSessionDAO.setRedisManager(redisManager());
		return redisSessionDAO;
	}

	/**
	 * shiro session的管理
	 */
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();

		sessionManager.setSessionDAO(redisSessionDAO());// 设置SessionDao
		/**
		 * 加入缓存管理器，这个地方实现setCacheManager的功能和setSessionDAO相同，
		 * 只不过2个方法生效的优先级不一样，自定义缓存可以直接忽略这个设置
		 */
//		sessionManager.setCacheManager(cacheManager());
		/**
		 * 全局session失效时间单位毫秒
		 * 使用了redis管理session，redisSessionDAO
		 * 中已经将redis的失效时间设置成了session的失效时间
		 */
//		sessionManager.setGlobalSessionTimeout(shiroSessionTimeout);
		sessionManager.setDeleteInvalidSessions(true);// 删除过期的session
		sessionManager.setSessionValidationSchedulerEnabled(true);// 是否定时检查session默认是一个小时
		return sessionManager;
	}


    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager());
        return new AuthorizationAttributeSourceAdvisor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }


    /**
	 * 密码凭证匹配器，如果不希望使用shiro进行密码验证而是自己实现业务逻辑的话，这个不用设置
	 * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
	 * ）
	 * @return
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher(){
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
		return hashedCredentialsMatcher;
	}

	@Bean
	public MyShiroRealm myShiroRealm(){
		MyShiroRealm myShiroRealm = new MyShiroRealm();
		/**
		 * 密码凭证匹配器，如果不希望使用shiro进行密码验证而是自己实现业务逻辑的话，这个不用设置
		 */
//		myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
		return myShiroRealm;
	}



	/**
	 *  开启shiro aop注解支持.
	 *  使用代理方式;所以需要开启代码支持;
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	/**
	 * shiro会自动找这个simpleMappingExceptionResolver  bean
	 * @return
	 */
	@Bean(name="simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver
	createSimpleMappingExceptionResolver() {
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
		Properties mappings = new Properties();
		mappings.setProperty("DatabaseException", "databaseError");//数据库异常处理
		/**
		 * 没有权限执行的url配置，此处要和shiroFilterFactoryBean中配置的没有权限的url要相同
		 */
		mappings.setProperty("UnauthorizedException","/unauth");
		r.setExceptionMappings(mappings);  // None by default
		r.setDefaultErrorView("error");    // No default
		r.setExceptionAttribute("ex");     // Default is "exception"
		//r.setWarnLogCategory("example.MvcLogger");     // No default
		return r;
	}





}