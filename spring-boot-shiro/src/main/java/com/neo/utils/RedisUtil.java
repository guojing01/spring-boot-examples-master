package com.neo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;
import java.util.List;
import java.util.Set;

@Component
public class RedisUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
	@Autowired(required=true)
	private JedisCluster jedisCluster;

	/**
	 * 设置一个key的过期时间（单位：秒）
	 *
	 * @param key
	 *            key值
	 * @param seconds
	 *            多少秒后过期
	 * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
	 */
	@ResponseBody
	public long expire(String key, int seconds) {
		if (key == null || key.equals("")) {
			return 0;
		}
		try {
			return jedisCluster.expire(key, seconds);
		} catch (Exception ex) {
			LOGGER.error("EXPIRE error[key=" + key + " seconds=" + seconds + "]" + ex.getMessage(), ex);
		}
		return 0;
	}

	/**
	 * 设置一个key在某个时间点过期
	 *
	 * @param key
	 *            key值
	 * @param unixTimestamp
	 *            unix时间戳，从1970-01-01 00:00:00开始到现在的秒数
	 * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
	 */
	public long expireAt(String key, int unixTimestamp) {
		if (key == null || key.equals("")) {
			return 0;
		}

		try {
			return jedisCluster.expireAt(key, unixTimestamp);
		} catch (Exception ex) {
			LOGGER.error("EXPIRE error[key=" + key + " unixTimestamp=" + unixTimestamp + "]" + ex.getMessage(), ex);
		}
		return 0;
	}

	/**
	 * 截断一个List
	 *
	 * @param key
	 *            列表key
	 * @param start
	 *            开始位置 从0开始
	 * @param end
	 *            结束位置
	 * @return 状态码
	 */
	public String trimList(String key, long start, long end) {
		if (key == null || key.equals("")) {
			return "-";
		}
		try {
			return jedisCluster.ltrim(key, start, end);
		} catch (Exception ex) {
			LOGGER.error("LTRIM 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage(), ex);
		}
		return "-";
	}

	/**
	 * 检查Set长度
	 *
	 * @param key
	 * @return
	 */
	public long countSet(String key) {
		if (key == null) {
			return 0;
		}
		try {
			return jedisCluster.scard(key);
		} catch (Exception ex) {
			LOGGER.error("countSet error.", ex);
		}
		return 0;
	}

	/**
	 * 添加到Set中（同时设置过期时间）
	 *
	 * @param key
	 *            key值
	 * @param seconds
	 *            过期时间 单位s
	 * @param value
	 * @return
	 */
	public boolean addSet(String key, int seconds, String... value) {
		boolean result = addSet(key, value);
		if (result) {
			long i = expire(key, seconds);
			return i == 1;
		}
		return false;
	}

	/**
	 * 添加到Set中
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addSet(String key, String... value) {
		if (key == null || value == null) {
			return false;
		}
		try {
			jedisCluster.sadd(key, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("setList error.", ex);
		}
		return false;
	}

	/**
	 * @param key
	 * @param value
	 * @return 判断值是否包含在set中
	 */
	public boolean containsInSet(String key, String value) {
		if (key == null || value == null) {
			return false;
		}
		try {
			return jedisCluster.sismember(key, value);
		} catch (Exception ex) {
			LOGGER.error("setList error.", ex);
		}
		return false;
	}

	/**
	 * 获取Set
	 *
	 * @param key
	 * @return
	 */
	public Set<String> getSet(String key) {
		try {
			return jedisCluster.smembers(key);
		} catch (Exception ex) {
			LOGGER.error("getList error.", ex);
		}
		return null;
	}

	/**
	 * 从set中删除value
	 *
	 * @param key
	 * @return
	 */
	public boolean removeSetValue(String key, String... value) {
		try {
			jedisCluster.srem(key, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("getList error.", ex);
		}
		return false;
	}

	/**
	 * 从list中删除value 默认count 1
	 *
	 * @param key
	 * @param values
	 *            值list
	 * @return
	 */
	public int removeListValue(String key, List<String> values) {
		return removeListValue(key, 1, values);
	}

	/**
	 * 从list中删除value
	 *
	 * @param key
	 * @param count
	 * @param values
	 *            值list
	 * @return
	 */
	public int removeListValue(String key, long count, List<String> values) {
		int result = 0;
		if (values != null && values.size() > 0) {
			for (String value : values) {
				if (removeListValue(key, count, value)) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * 从list中删除value
	 *
	 * @param key
	 * @param count
	 *            要删除个数
	 * @param value
	 * @return
	 */
	public boolean removeListValue(String key, long count, String value) {
		try {
			jedisCluster.lrem(key, count, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("getList error.", ex);
		}
		return false;
	}

	/**
	 * 截取List
	 *
	 * @param key
	 * @param start
	 *            起始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	public List<String> rangeList(String key, long start, long end) {
		if (key == null || key.equals("")) {
			return null;
		}
		try {
			return jedisCluster.lrange(key, start, end);
		} catch (Exception ex) {
			LOGGER.error("rangeList 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 检查List长度
	 *
	 * @param key
	 * @return
	 */
	public long countList(String key) {
		if (key == null) {
			return 0;
		}
		try {
			return jedisCluster.llen(key);
		} catch (Exception ex) {
			LOGGER.error("countList error.", ex);
		}
		return 0;
	}

	/**
	 * 添加到List中（同时设置过期时间）
	 *
	 * @param key
	 *            key值
	 * @param seconds
	 *            过期时间 单位s
	 * @param value
	 * @return
	 */
	public boolean addList(String key, int seconds, String... value) {
		boolean result = addList(key, value);
		if (result) {
			long i = expire(key, seconds);
			return i == 1;
		}
		return false;
	}

	/**
	 * 添加到List
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addList(String key, String... value) {
		if (key == null || value == null) {
			return false;
		}
		try {
			jedisCluster.lpush(key, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("setList error.", ex);
		}
		return false;
	}

	/**
	 * 添加到List(只新增)
	 *
	 * @param key
	 * @param
	 * @return
	 */
	public boolean addList(String key, List<String> list) {
		if (key == null || list == null || list.size() == 0) {
			return false;
		}
		for (String value : list) {
			addList(key, value);
		}
		return true;
	}

	/**
	 * 获取List
	 *
	 * @param key
	 * @return
	 */
	public List<String> getList(String key) {
		try {
			return jedisCluster.lrange(key, 0, -1);
		} catch (Exception ex) {
			LOGGER.error("getList error.", ex);
		}
		return null;
	}

	/**
	 * 设置HashSet对象
	 *
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @param value
	 *            Json String or String value
	 * @return
	 */
	public boolean setHSet(String domain, String key, String value) {
		if (value == null)
			return false;
		try {
			jedisCluster.hset(domain, key, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("setHSet error.", ex);
		}
		return false;
	}

	/**
	 * 获得HashSet对象
	 *
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return Json String or String value
	 */
	public String getHSet(String domain, String key) {
		try {
			return jedisCluster.hget(domain, key);
		} catch (Exception ex) {
			LOGGER.error("getHSet error.", ex);
		}
		return null;
	}

	/**
	 * 删除HashSet对象
	 *
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return 删除的记录数
	 */
	public long delHSet(String domain, String key) {
		long count = 0;
		try {
			count = jedisCluster.hdel(domain, key);
		} catch (Exception ex) {
			LOGGER.error("delHSet error.", ex);
		}
		return count;
	}

	/**
	 * 删除HashSet对象
	 *
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return 删除的记录数
	 */
	public long delHSet(String domain, String... key) {
		long count = 0;
		try {
			count = jedisCluster.hdel(domain, key);
		} catch (Exception ex) {
			LOGGER.error("delHSet error.", ex);
		}
		return count;
	}

	/**
	 * 判断key是否存在
	 *
	 * @param domain
	 *            域名
	 * @param key
	 *            键值
	 * @return
	 */
	public boolean existsHSet(String domain, String key) {
		boolean isExist = false;
		try {
			isExist = jedisCluster.hexists(domain, key);
		} catch (Exception ex) {
			LOGGER.error("existsHSet error.", ex);
		}
		return isExist;
	}



	/**
	 * 返回 domain 指定的哈希集中所有字段的value值
	 *
	 * @param domain
	 * @return
	 */

	public List<String> hvals(String domain) {
		List<String> retList = null;
		try {
			retList = jedisCluster.hvals(domain);
		} catch (Exception ex) {
			LOGGER.error("hvals error.", ex);
		}
		return retList;
	}

	/**
	 * 返回 domain 指定的哈希集中所有字段的key值
	 *
	 * @param domain
	 * @return
	 */

	public Set<String> hkeys(String domain) {
		Set<String> retList = null;
		try {
			retList = jedisCluster.hkeys(domain);
		} catch (Exception ex) {
			LOGGER.error("hkeys error.", ex);
		}
		return retList;
	}

	/**
	 * 返回 domain 指定的哈希key值总数
	 *
	 * @param domain
	 * @return
	 */
	public long lenHset(String domain) {
		long retList = 0;
		try {
			retList = jedisCluster.hlen(domain);
		} catch (Exception ex) {
			LOGGER.error("hkeys error.", ex);
		}
		return retList;
	}

	/**
	 * 设置排序集合
	 *
	 * @param key
	 * @param score
	 * @param value
	 * @return
	 */
	public boolean setSortedSet(String key, long score, String value) {
		try {
			jedisCluster.zadd(key, score, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("setSortedSet error.", ex);
		}
		return false;
	}

	/**
	 * 获得排序集合
	 *
	 * @param key
	 * @param startScore
	 * @param endScore
	 * @param orderByDesc
	 * @return
	 */
	public Set<String> getSoredSet(String key, long startScore, long endScore, boolean orderByDesc) {
		try {
			if (orderByDesc) {
				return jedisCluster.zrevrangeByScore(key, endScore, startScore);
			} else {
				return jedisCluster.zrangeByScore(key, startScore, endScore);
			}
		} catch (Exception ex) {
			LOGGER.error("getSoredSet error.", ex);
		}
		return null;
	}

	/**
	 * 计算排序长度
	 *
	 * @param key
	 * @param startScore
	 * @param endScore
	 * @return
	 */
	public long countSoredSet(String key, long startScore, long endScore) {
		try {
			Long count = jedisCluster.zcount(key, startScore, endScore);
			return count == null ? 0L : count;
		} catch (Exception ex) {
			LOGGER.error("countSoredSet error.", ex);
		}
		return 0L;
	}

	/**
	 * 删除排序集合
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean delSortedSet(String key, String value) {
		try {
			long count = jedisCluster.zrem(key, value);
			return count > 0;
		} catch (Exception ex) {
			LOGGER.error("delSortedSet error.", ex);
		}
		return false;
	}

	/**
	 * 获得排序集合
	 *
	 * @param key
	 * @param startRange
	 * @param endRange
	 * @param orderByDesc
	 * @return
	 */
	public Set<String> getSoredSetByRange(String key, int startRange, int endRange, boolean orderByDesc) {
		try {
			if (orderByDesc) {
				return jedisCluster.zrevrange(key, startRange, endRange);
			} else {
				return jedisCluster.zrange(key, startRange, endRange);
			}
		} catch (Exception ex) {
			LOGGER.error("getSoredSetByRange error.", ex);
		}
		return null;
	}

	/**
	 * 获得排序打分
	 *
	 * @param key
	 * @return
	 */
	public Double getScore(String key, String member) {
		try {
			return jedisCluster.zscore(key, member);
		} catch (Exception ex) {
			LOGGER.error("getSoredSet error.", ex);
		}
		return null;
	}

	public boolean set(String key, String value, int second) {
		try {
			jedisCluster.setex(key, second, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("set error.", ex);
		}
		return false;
	}

	public boolean set(String key, byte[] value, int second) {
		try {
			jedisCluster.setex(key, second, new String(value, "UTF-8"));
			return true;
		} catch (Exception ex) {
			LOGGER.error("set error.", ex);
		}
		return false;
	}

	public boolean set(String key, String value) {
		try {
			jedisCluster.set(key, value);
			return true;
		} catch (Exception ex) {
			LOGGER.error("set error.", ex);
		}
		return false;
	}

	public boolean set(String key, byte[] value) {
		try {
			jedisCluster.set(key, new String(value, "UTF-8"));
			return true;
		} catch (Exception ex) {
			LOGGER.error("set error.", ex);
		}
		return false;
	}

	public String get(String key, String defaultValue) {
		try {
			return jedisCluster.get(key) == null ? defaultValue : jedisCluster.get(key);
		} catch (Exception ex) {
			LOGGER.error("get error.", ex);
		}
		return defaultValue;
	}

	public boolean del(String key) {
		try {
			jedisCluster.del(key);
			return true;
		} catch (Exception ex) {
			LOGGER.error("del error.", ex);
		}
		return false;
	}

	public long incr(String key) {
		try {
			return jedisCluster.incr(key);
		} catch (Exception ex) {
			LOGGER.error("incr error.", ex);
		}
		return 0;
	}

	public long decr(String key) {
		try {
			return jedisCluster.decr(key);
		} catch (Exception ex) {
			LOGGER.error("incr error.", ex);
		}
		return 0;
	}


}
