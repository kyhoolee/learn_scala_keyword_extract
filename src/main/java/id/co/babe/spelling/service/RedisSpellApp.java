package id.co.babe.spelling.service;

import id.co.babe.analysis.util.TextfileIO;

import java.util.List;
import java.util.Map;

import id.co.babe.analysis.util.Utils;
import redis.clients.jedis.Jedis;


public class RedisSpellApp implements ISpellApp {

    private static RedisSpellApp instance = new RedisSpellApp();

    private RedisSpellApp() {
    }

    public static RedisSpellApp getInstance() {
        return instance;
    }


    public static final String entity_hash_redirect = "entity:hash:redirect";
    public static final String entity_set_stop = "entity:set:stop";
    public static final String entity_set_normal = "entity:set:normal";
    public static final String entity_set_entity = "entity:set:entity";
    public static final String hash_redirect = "hash:redirect";
    public static final String set_stop = "set:stop";
    public static final String set_normal = "set:normal";
    public static final String set_entity = "set:entity";


    public void setStop(String word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_stop, word);
        jedis.close();
    }

    public void setNormal(String word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_normal, word);
        jedis.close();
    }

    public void setEntity(String word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_entity, word);
        jedis.close();
    }

    public void setEntity(String... word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_entity, word);
        jedis.close();
    }

    public void setRedirect(String word, String redirect) {
        Jedis jedis = RedisPool.getJedis();
        jedis.hset(hash_redirect, word, redirect);
        jedis.close();
    }


    public void main(String[] args) {
        init();
    }


    public void init() {
        initNormal("data/nlp_data/indo_dict/id_full.txt");
//		RedisSpellApp.initStop("data/nlp_data/indo_dict/stop_word.txt");
//		RedisSpellApp.initEntity(
//				"data/nlp_data/indo_dict/wiki_tag.txt",
//				"/home/mainspring/tutorial/resources/data/DbPedia/en/filter/wiki_tag_en.2017.txt");
//		RedisSpellApp.initRedirect("data/nlp_data/indo_dict/redirect_entity_map.txt");
    }


    public void initStop(String path) {
        List<String> lines = TextfileIO.readFile(path);
        for (String line : lines) {
            try {
                setStop(line.toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insertStop(String word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_stop, word);
        jedis.close();
    }

    @Override
    public void removeStop(String word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.srem(set_stop, word);
        jedis.close();
    }

    @Override
    public void insertStop(List<String> word) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_stop, word.toArray(new String[0]));
        jedis.close();
    }

    public void initNormal(String path) {
        List<String> lines = TextfileIO.readFile(path);
        int count = 0;
        long start = System.currentTimeMillis();
        for (String line : lines) {
            try {

                String[] tokens = line.split(" ");
                setNormal(tokens[0].toLowerCase());
                count++;
                if (count % 100 == 0) {
                    long end = System.currentTimeMillis();
                    System.out.println(count + " -- " + (end - start));
                    start = end;
                }
            } catch (Exception e) {
                System.out.println(" --- " + line);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void insertNormal(String normal) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_normal, normal);
        jedis.close();
    }

    @Override
    public void removeNormal(String normal) {
        Jedis jedis = RedisPool.getJedis();
        jedis.srem(set_normal, normal);
        jedis.close();

    }

    @Override
    public void insertNormal(List<String> normal) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_normal, normal.toArray(new String[0]));
        jedis.close();
    }

    public void initEntity(String path) {
        List<String> dictionary = TextfileIO.readFile(path);
        for (String word : dictionary) {
            setEntity(Utils.variedWord(word.toLowerCase()).toArray(new String[0]));
        }
    }

    public void initEntity(String... tagDict) {
        for (String t : tagDict) {
            List<String> dictionary = TextfileIO.readFile(t);
            for (String word : dictionary) {
                setEntity(Utils.variedWord(word.toLowerCase()).toArray(new String[0]));
            }
        }
    }

    @Override
    public void insertEntity(String entity) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_entity, entity);
        jedis.close();
    }

    @Override
    public void removeEntity(String entity) {
        Jedis jedis = RedisPool.getJedis();
        jedis.srem(set_entity, entity);
        jedis.close();

    }

    @Override
    public void insertEntity(List<String> entity) {
        Jedis jedis = RedisPool.getJedis();
        jedis.sadd(set_entity, entity.toArray(new String[0]));
        jedis.close();
    }

    public void initRedirect(String path) {
        List<String[]> redirect = TextfileIO.readCsv(path);
        for (String[] r : redirect) {
            if (r.length == 2)
                setRedirect(r[0].toLowerCase(), r[1].toLowerCase());
        }
    }

    @Override
    public void insertRedirect(String word, String redirect) {
        Jedis jedis = RedisPool.getJedis();
        jedis.hset(hash_redirect, word, redirect);
        jedis.close();
    }

    @Override
    public void removeRedirect(String word, String redirect) {
        Jedis jedis = RedisPool.getJedis();
        jedis.hdel(hash_redirect, word);
        jedis.close();

    }

    @Override
    public void insertRedirect(Map<String, String> redirect) {
        Jedis jedis = RedisPool.getJedis();
        for(String word: redirect.keySet()) {
            jedis.hset(hash_redirect, word, redirect.get(word));
        }
        jedis.close();

    }


    /**
     * Check word is Stop
     *
     * @param word
     * @return
     */
    public boolean checkStop(String word) {
        Jedis jedis = RedisPool.getJedis();

        boolean result = jedis.sismember(set_stop, word.toLowerCase());


        jedis.close();

        return result;
    }

    /**
     * Check word is Entity
     *
     * @param word
     * @return
     */
    public boolean checkEntity(String word) {
        Jedis jedis = RedisPool.getJedis();

        boolean result = jedis.sismember(set_entity, word.toLowerCase());

        jedis.close();

        return result;
    }

    public boolean[] checkEntity(String... word) {
        Jedis jedis = RedisPool.getJedis();

        boolean[] result = new boolean[word.length];

        int i = 0;
        for (String w : word) {
            boolean r = jedis.sismember(set_entity, w.toLowerCase());

            result[i] = r;
            i++;
        }
        jedis.close();

        return result;
    }

    /**
     * Get root of Word
     *
     * @param word
     * @return
     */
    public String checkRedirect(String word) {
        String result = word;
        Jedis jedis = RedisPool.getJedis();

        result = jedis.hget(hash_redirect, word.toLowerCase());

        jedis.close();

        if (result == null)
            return word;
        return result;
    }

    /**
     * Check word is Normal
     *
     * @param word
     * @return
     */
    public boolean checkNormal(String word) {
        Jedis jedis = RedisPool.getJedis();

        boolean result = jedis.sismember(set_normal, word.toLowerCase());

        jedis.close();

        return result;
    }

}
