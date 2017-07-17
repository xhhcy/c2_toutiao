package com.nowcoder.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisAdapter  implements InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
	public static void  print(int index,Object value) {
		{
			System.out.println(String.format("%d,%s", index,value.toString()));
		}
		
	}
	public static void main(String[] args) {
		Jedis jedis = new Jedis();
		jedis.flushAll();
		jedis.set("xhh", "cy");
		print(1,jedis.get("xhh"));
		
		jedis.set("pv", "100");
		jedis.incr("pv");
		jedis.incrBy("pv", 5);
		
		print(2, jedis.get("pv"));
		
		String list = "listA";
		for (int i = 0; i < 10; i++) {
			jedis.lpush("listA", "a"+i);
			
		}
		jedis.lpush("listA", "a0");
		print(3, jedis.lrange("listA", 0, 10));
		print(4, jedis.lindex("listA", 3));
		print(4, jedis.linsert("listA", BinaryClient.LIST_POSITION.AFTER, "a0", "xx"));
		print(5,jedis.lrange("listA", 0, 100));
		
		
		String userKey = "user1";
		jedis.hset(userKey, "name", "xhh");
		jedis.hset(userKey, "gender", "male");
		jedis.hset(userKey, "age", "20");
		jedis.hset(userKey, "phone", "110");
		print(6, jedis.hget(userKey, "name"));
		print(7, jedis.hgetAll(userKey));
		jedis.hdel(userKey, "phone");
		print(7, jedis.hgetAll(userKey));
		print(8, jedis.hkeys(userKey));
		print(9,jedis.hvals(userKey));
		print(10, jedis.hexists(userKey, "email"));
		
		
		
		
	}
	
	private Jedis jedis = null;
	private JedisPool jedisPool = null;
	@Override
	public void afterPropertiesSet() throws Exception {
		jedisPool = new JedisPool("localhost",6379);
	}	
	private Jedis getJedis()
	{
		return jedisPool.getResource();
	}
	//String 操作
	public String get(String key)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
				return jedis.get(key);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常1"+e.getMessage());
			return null;
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	public void set(String key,String value)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			jedis.set(key, value);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常2"+e.getMessage());
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	
	public void lpush(String key,String value)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			jedis.lpush(key, value);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常2"+e.getMessage());
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	public List<String> brpop(int timeout, String key)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			return jedis.brpop(timeout,key);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常2"+e.getMessage());
			return null;
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	//无序集合操作
	public void sadd(String key,String value)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			jedis.sadd(key, value);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常3"+e.getMessage());
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	
	public long srem(String key,String value)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			return jedis.srem(key, value);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常3"+e.getMessage());
			return 0;
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	public boolean sismember(String key,String value)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			return jedis.sismember(key, value);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常3"+e.getMessage());
			return false;
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	public long scard(String key)
	{
		Jedis jedis = null;
		try {
			jedis = getJedis();
			
			return jedis.scard(key);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发生异常3"+e.getMessage());
			return 0;
		}finally{
			if(jedis!= null)
				jedis.close();
		}
	}
	public void setex(String key, String value) {
        // 验证码, 防机器注册，记录上次注册时间，有效期3天
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.setex(key, 10, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
	public void setObject(String key,Object object)
	{
		set(key, JSON.toJSONString(object));
	}
	public <T>T getObjet(String key,Class<T> calzz)
	{
		return JSON.parseObject(get(key),calzz);
	}
}
