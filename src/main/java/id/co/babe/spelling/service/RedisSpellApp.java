package id.co.babe.spelling.service;

import id.co.babe.analysis.util.TextfileIO;

import java.util.List;

import redis.clients.jedis.Jedis;


public class RedisSpellApp extends SpellApp {
	
	
	public static final String hash_redirect = "hash:redirect";
	public static final String set_stop = "set:stop";
	public static final String set_normal = "set:normal";
	public static final String set_entity = "set:entity";
	
	

	
	

	
	
	public static void setStop(String word) {
		Jedis jedis = RedisPool.getJedis();
		
		jedis.sadd(set_stop, word);
		
		jedis.close();
	}
	public static void setNormal(String word) {
		Jedis jedis = RedisPool.getJedis();
		
		jedis.sadd(set_normal, word);
		
		jedis.close();
	}
	public static void setEntity(String word) {
		Jedis jedis = RedisPool.getJedis();
		
		jedis.sadd(set_entity, word);
		
		jedis.close();
	}
	public static void setEntity(String... word) {
		Jedis jedis = RedisPool.getJedis();
		
		jedis.sadd(set_entity, word);
		
		jedis.close();
	}
	public static void setRedirect(String word, String redirect) {
		Jedis jedis = RedisPool.getJedis();
		
		jedis.hset(hash_redirect, word, redirect);
		
		jedis.close();
	}
	
	
	public static void main(String[] args) {
		init();
	}
	
	
	public static void init() {
		RedisSpellApp.initNormal("nlp_data/indo_dict/id_full.txt");
//		RedisSpellApp.initStop("nlp_data/indo_dict/stop_word.txt");
//		RedisSpellApp.initEntity(
//				"nlp_data/indo_dict/wiki_tag.txt", 
//				"/home/mainspring/tutorial/resources/data/DbPedia/en/filter/wiki_tag_en.2017.txt");
//		RedisSpellApp.initRedirect("nlp_data/indo_dict/redirect_entity_map.txt");
	}
	
	
	
	public static void initStop(String path) {
		List<String> lines = TextfileIO.readFile(path);
		for(String line : lines) {
			try {
				setStop(line.toLowerCase());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void initNormal(String path) {
		List<String> lines = TextfileIO.readFile(path);
		for(String line : lines) {
			try {
				String[] tokens = line.split(" ");
				setNormal(tokens[0].toLowerCase());
			} catch (Exception e) {
				System.out.println(" --- " + line);
				e.printStackTrace();
			}
		}
		
	}
	public static void initEntity(String path) {
		List<String> dictionary = TextfileIO.readFile(path);
		for (String word : dictionary) {
			setEntity(variedWord(word.toLowerCase()).toArray(new String[0]));
		}
	}
	public static void initEntity(String... tagDict) {
		for(String t : tagDict) {
			List<String> dictionary = TextfileIO.readFile(t);
			for (String word : dictionary) {
				setEntity(variedWord(word.toLowerCase()).toArray(new String[0]));
			}
		}
	}
	public static void initRedirect(String path) {
		List<String[]> redirect = TextfileIO.readCsv(path);
		for(String[] r : redirect) {
			if(r.length == 2)
				setRedirect(r[0].toLowerCase(), r[1].toLowerCase());
		}
	}

	
	/**
	 * Check word is Stop
	 * @param word
	 * @return
	 */
	public static boolean checkStop(String word) {
		Jedis jedis = RedisPool.getJedis();
		
		boolean result = jedis.sismember(set_stop, word.toLowerCase());
		
		jedis.close();
		
		return result;
	}
	
	/**
	 * Check word is Entity 
	 * @param word
	 * @return
	 */
	public static int checkEntity(String word) {
		Jedis jedis = RedisPool.getJedis();
		
		boolean result = jedis.sismember(set_entity, word.toLowerCase());
		
		jedis.close();
		
		if(result)
			return 1;
		else 
			return 0;
	}
	
	/**
	 * Get root of Word
	 * @param word
	 * @return
	 */
	public static String checkRedirect(String word) {
		String result = word;
		Jedis jedis = RedisPool.getJedis();
		
		result = jedis.hget(hash_redirect, word.toLowerCase());
		
		jedis.close();
		
		if(result == null)
			return word;
		return result;
	}
	
	/**
	 * Check word is Normal
	 * @param word
	 * @return
	 */
	public static boolean checkNormal(String word) {
		Jedis jedis = RedisPool.getJedis();
		
		boolean result = jedis.sismember(set_normal, word.toLowerCase());
		
		jedis.close();
		
		return result;
	}

}
