package id.co.babe.spelling.service;

import java.io.FileInputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	
	private static JedisPool _instance = null;
	public static String CONFIG_PATH = "data/properties/default_redis.properties";
	public static String redis_host = "10.2.15.46";//"localhost";
	public static int redis_port = 6377;//6379;
	public static String redis_pass = null;
	public static int redis_index = 1;
	
	
	public static String configPath() {
		return CONFIG_PATH;
	}
	
	public static void load(String path) {
		Properties properties = new Properties();
		try {
			//properties.load(RedisPool.class.getClassLoader().getResourceAsStream(path));
			properties.load(new FileInputStream(CONFIG_PATH));
			
			redis_host = properties.getProperty("redis_host", redis_host);
			redis_port = Integer.parseInt(properties.getProperty("redis_port", String.valueOf(redis_port)));
			redis_pass = properties.getProperty("redis_pass", redis_pass);
			redis_index = Integer.parseInt(properties.getProperty("redis_index", String.valueOf(redis_index)));
			System.out.println(redis_host + " -- " + redis_port);
		} catch (Exception e) {
			System.out.println(path);
			e.printStackTrace();
		}
		
	}

	public static void initRedis(String host, int port, int index) {
		redis_host = host;
		redis_port = port;
		redis_index = index;
		getJedis();
	}


	public synchronized static JedisPool getInstance() {

		if (_instance == null) {
			System.out.println(redis_host + " " + redis_port + " " + redis_index);

			//load(path);
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxTotal(100);
			jedisPoolConfig.setMaxIdle(100);
			jedisPoolConfig.setMaxWaitMillis(10000);
			jedisPoolConfig.setMinIdle(10);
			jedisPoolConfig.setTimeBetweenEvictionRunsMillis(5000);
			jedisPoolConfig.setMinEvictableIdleTimeMillis(1000);
			jedisPoolConfig.setTestWhileIdle(true);
			System.out.println(redis_pass);
			System.out.println(redis_host);
			System.out.println(redis_port);
			_instance = new JedisPool(
					jedisPoolConfig,
					redis_host, redis_port,
					10000, redis_pass);

		}

		return _instance;
	}


	public synchronized static JedisPool getInstance(String path) {

		if (_instance == null) {
			load(path);
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxTotal(100);
			jedisPoolConfig.setMaxIdle(100);
			jedisPoolConfig.setMaxWaitMillis(10000);
			jedisPoolConfig.setMinIdle(10);
			jedisPoolConfig.setTimeBetweenEvictionRunsMillis(5000);
			jedisPoolConfig.setMinEvictableIdleTimeMillis(1000);
			jedisPoolConfig.setTestWhileIdle(true);
			System.out.println(redis_pass);
			System.out.println(redis_host);
			System.out.println(redis_port);
			_instance = new JedisPool(
					jedisPoolConfig, 
					redis_host, redis_port, 
					10000, redis_pass);

		}
		
		return _instance;
	}

	public synchronized static Jedis getJedis(String path) {
		try {
			Jedis jedis = RedisPool.getInstance(path).getResource();
			jedis.select(redis_index);
			return jedis;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public synchronized static Jedis getJedis() {
		try {
			Jedis jedis = RedisPool
					.getInstance()
					//.getInstance(CONFIG_PATH)
					.getResource();
			jedis.select(redis_index);
			return jedis;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


//	public static void main(String[] args) {
//		Jedis jedis = RedisPool.getJedis();
//	}


}