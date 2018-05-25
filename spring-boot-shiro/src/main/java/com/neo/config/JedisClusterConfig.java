package com.neo.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: renchunbao
 * @Description:
 * @Date: 2018/3/30
 */

/**
 * Created by xiaxuan on 16/11/8.
 * 获取JedisCluster的配置
 */
@Configuration
@ConditionalOnClass({JedisCluster.class})
public class JedisClusterConfig {
    @Value("${spring.redis.cache.clusterNodes}")
    private String clusterNodes;
    @Value("${spring.redis.cache.commandTimeout}")
    private String commandTimeout;
    @Value("${spring.redis.cache.password}")
    private String pass;

    @Value("${spring.redis.cache.so.timeout}")
    private String soTimeout;
    @Value("${spring.redis.cache.max.attempts}")
    private  String maxAttempts;

    @Value("${spring.redis.cache.max.idle}")
    private String maxIdle;
    @Value("${spring.redis.cache.min.idle}")
    private String minIdle;
    @Value("${spring.redis.cache.max.total}")
    private String maxTotal;

    @Bean
    public JedisCluster getJedisCluster() {
        String[] serverArray = clusterNodes.split(",");
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        for (String ipPort: serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(),Integer.valueOf(ipPortPair[1].trim())));
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(Integer.parseInt(maxIdle));
        config.setMaxTotal(Integer.parseInt(maxTotal));
        config.setMinIdle(Integer.parseInt(minIdle));
        JedisCluster jedisCluster = new JedisCluster(nodes, Integer.parseInt(commandTimeout),Integer.parseInt(soTimeout)
                ,Integer.parseInt(maxAttempts),pass,config);
        return jedisCluster;
    }
}