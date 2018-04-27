package com.shinho.restaurant.pos.business.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
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

    private int soTimeout;
    private  String maxAttempts="5";
    @Value("${spring.redis.cache.password}")
    private String pass;

    @Bean
    public JedisCluster getJedisCluster() {
        String[] serverArray = clusterNodes.split(",");
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        for (String ipPort: serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(),Integer.valueOf(ipPortPair[1].trim())));
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        JedisCluster jedisCluster = new JedisCluster(nodes, Integer.parseInt(commandTimeout),soTimeout
                ,Integer.parseInt(maxAttempts),pass,config);
        return jedisCluster;
    }
}