package id.co.babe.analysis.nlp;

import id.co.babe.spelling.service.HttpSpellApp;
import id.co.babe.spelling.service.RedisPool;
import id.co.babe.spelling.service.RedisSpellApp;
import id.co.babe.spelling.service.SpellApp;

import java.util.List;
import java.util.Map;

/**
 * Created by mainspring on 07/06/17.
 */
public class DictUtils {
    public static final int storage_redis = 0;
    public static final int storage_java = 1;
    public static final int storage_service = 2;

    public static int storage = storage_redis;


    public static void initRedis(String redis_host, int redis_port, int redis_index) {
        storage = storage_redis;
        RedisPool.initRedis(redis_host, redis_port, redis_index);
    }

    public static void init() {

//		DictUtils.initNormal("data/nlp_data/indo_dict/id_full.txt");
//		DictUtils.initStop("data/nlp_data/indo_dict/stop_word.txt");
//		DictUtils.initEntity(
//				"data/nlp_data/indo_dict/wiki_tag.txt",
//				"/home/mainspring/tutorial/resources/data/DbPedia/en/filter/wiki_tag_en.2017.txt");
//		DictUtils.initRedirect("data/nlp_data/indo_dict/redirect_entity_map.txt");

        TextParser.init();
    }


    public static void initLocal() {

//		DictUtils.initNormal("data/nlp_data/indo_dict/id_full.txt");
//		DictUtils.initStop("data/nlp_data/indo_dict/stop_word.txt");
//		DictUtils.initEntity(
//				"data/nlp_data/indo_dict/wiki_tag.txt",
//				"/home/mainspring/tutorial/resources/data/DbPedia/en/filter/wiki_tag_en.2017.txt");
//		DictUtils.initRedirect("data/nlp_data/indo_dict/redirect_entity_map.txt");
        initRedis("localhost", 6379, 0);
        TextParser.init();
    }

    public static void setDictType(int dictType) {
        switch(dictType) {
            case storage_redis:
                storage = storage_redis;
                return;
            case storage_java:
                storage = storage_java;
                return;
            case storage_service:
                storage = storage_service;
                return;
            default:
                storage = storage_redis;
                return;
        }
    }

    public static void initStop(String path) {
        switch(storage) {
            case storage_redis:
                //RedisSpellApp.getInstance().initStop(path);
                return;
            case storage_java:
                SpellApp.getInstance().initStop(path);
                return;
            case storage_service:
                HttpSpellApp.getInstance().initStop(path);
                return;
            default:
                RedisSpellApp.getInstance().initStop(path);
                return;
        }
    }
    public static void initNormal(String path) {
        switch(storage) {
            case storage_redis:
                //RedisSpellApp.getInstance().initNormal(path);
                return;
            case storage_java:
                SpellApp.getInstance().initNormal(path);
                return;
            case storage_service:
                HttpSpellApp.getInstance().initNormal(path);
                return;
            default:
                RedisSpellApp.getInstance().initNormal(path);
                return;
        }

    }
    public static void initEntity(String path) {
        switch(storage) {
            case storage_redis:
                //RedisSpellApp.getInstance().initEntity(path);
                return;
            case storage_java:
                SpellApp.getInstance().initEntity(path);
                return;
            case storage_service:
                HttpSpellApp.getInstance().initEntity(path);
                return;
            default:
                RedisSpellApp.getInstance().initEntity(path);
                return;
        }

    }
    public static void initEntity(String... path) {
        switch(storage) {
            case storage_redis:
                //RedisSpellApp.getInstance().initEntity(path);
                return;
            case storage_java:
                SpellApp.getInstance().initEntity(path);
                return;
            case storage_service:
                HttpSpellApp.getInstance().initEntity(path);
                return;
            default:
                RedisSpellApp.getInstance().initEntity(path);
                return;
        }

    }
    public static void initRedirect(String path) {
        switch(storage) {
            case storage_redis:
                //RedisSpellApp.getInstance().initRedirect(path);
                return;
            case storage_java:
                SpellApp.getInstance().initRedirect(path);
                return;
            case storage_service:
                HttpSpellApp.getInstance().initRedirect(path);
                return;
            default:
                RedisSpellApp.getInstance().initRedirect(path);
                return;
        }

    }


    public static boolean checkNormal(String word) {
        switch(storage) {
            case storage_redis:
                return RedisSpellApp.getInstance().checkNormal(word.toLowerCase());
            case storage_java:
                return SpellApp.getInstance().checkNormal(word.toLowerCase());
            case storage_service:
                return HttpSpellApp.getInstance().checkNormal(word.toLowerCase());
            default:
                return RedisSpellApp.getInstance().checkNormal(word.toLowerCase());
        }
    }

    public static boolean checkStop(String word) {
        switch(storage) {
            case storage_redis:
                return RedisSpellApp.getInstance().checkStop(word.toLowerCase());
            case storage_java:
                return SpellApp.getInstance().checkStop(word.toLowerCase());
            case storage_service:
                return HttpSpellApp.getInstance().checkStop(word.toLowerCase());
            default:
                return RedisSpellApp.getInstance().checkStop(word.toLowerCase());
        }
    }

    public static boolean checkEntity(String word) {
        switch(storage) {
            case storage_redis:
                return RedisSpellApp.getInstance().checkEntity(word.toLowerCase());
            case storage_java:
                return SpellApp.getInstance().checkEntity(word.toLowerCase());
            case storage_service:
                return HttpSpellApp.getInstance().checkEntity(word.toLowerCase());
            default:
                return RedisSpellApp.getInstance().checkEntity(word.toLowerCase());
        }
    }
    public static boolean[] checkEntity(String... word) {
        switch(storage) {
            case storage_redis:
                return RedisSpellApp.getInstance().checkEntity(word);
            case storage_java:
                return SpellApp.getInstance().checkEntity(word);
            case storage_service:
                return HttpSpellApp.getInstance().checkEntity(word);
            default:
                return RedisSpellApp.getInstance().checkEntity(word);
        }
    }

    public static String checkRedirect(String word) {
        switch(storage) {
            case storage_redis:
                return RedisSpellApp.getInstance().getInstance().checkRedirect(word.toLowerCase()).toLowerCase();
            case storage_java:
                return SpellApp.getInstance().getInstance().checkRedirect(word.toLowerCase()).toLowerCase();
            case storage_service:
                return HttpSpellApp.getInstance().checkRedirect(word.toLowerCase()).toLowerCase();
            default:
                return RedisSpellApp.getInstance().checkRedirect(word.toLowerCase()).toLowerCase();
        }
    }


    public static void insertRedirect(String word, String redirect) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertRedirect(word, redirect);
                return;
            case storage_java:
                SpellApp.getInstance().insertRedirect(word, redirect);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertRedirect(word, redirect);
                return;
            default:
                RedisSpellApp.getInstance().insertRedirect(word, redirect);
                return;
        }
    }
    public static void insertRedirect(Map<String, String> redirect) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertRedirect(redirect);
                return;
            case storage_java:
                SpellApp.getInstance().insertRedirect(redirect);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertRedirect(redirect);
                return;
            default:
                RedisSpellApp.getInstance().insertRedirect(redirect);
                return;
        }

    }


    public static void insertEntity(String entity) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertEntity(entity);
                return;
            case storage_java:
                SpellApp.getInstance().insertEntity(entity);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertEntity(entity);
                return;
            default:
                RedisSpellApp.getInstance().insertEntity(entity);
                return;
        }

    }
    public static void insertEntity(List<String> entity) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertEntity(entity);
                return;
            case storage_java:
                SpellApp.getInstance().insertEntity(entity);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertEntity(entity);
                return;
            default:
                RedisSpellApp.getInstance().insertEntity(entity);
                return;
        }

    }


    public static void insertStop(String word) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertStop(word);
                return;
            case storage_java:
                SpellApp.getInstance().insertStop(word);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertStop(word);
                return;
            default:
                RedisSpellApp.getInstance().insertStop(word);
                return;
        }

    }
    public static void insertStop(List<String> word) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertStop(word);
                return;
            case storage_java:
                SpellApp.getInstance().insertStop(word);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertStop(word);
                return;
            default:
                RedisSpellApp.getInstance().insertStop(word);
                return;
        }

    }

    public static void insertNormal(String normal) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertNormal(normal);
                return;
            case storage_java:
                SpellApp.getInstance().insertNormal(normal);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertNormal(normal);
                return;
            default:
                RedisSpellApp.getInstance().insertNormal(normal);
                return;
        }

    }
    public static void insertNormal(List<String> normal) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().insertNormal(normal);
                return;
            case storage_java:
                SpellApp.getInstance().insertNormal(normal);
                return;
            case storage_service:
                HttpSpellApp.getInstance().insertNormal(normal);
                return;
            default:
                RedisSpellApp.getInstance().insertNormal(normal);
                return;
        }

    }


    public static void removeStop(String stop) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().removeStop(stop);
                return;
            case storage_java:
                SpellApp.getInstance().removeStop(stop);
                return;
            case storage_service:
                HttpSpellApp.getInstance().removeStop(stop);
                return;
            default:
                RedisSpellApp.getInstance().removeStop(stop);
                return;
        }

    }

    public static void removeNormal(String normal) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().removeNormal(normal);
                return;
            case storage_java:
                SpellApp.getInstance().removeNormal(normal);
                return;
            case storage_service:
                HttpSpellApp.getInstance().removeNormal(normal);
                return;
            default:
                RedisSpellApp.getInstance().removeNormal(normal);
                return;
        }

    }

    public static void removeEntity(String entity) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().removeEntity(entity);
                return;
            case storage_java:
                SpellApp.getInstance().removeEntity(entity);
                return;
            case storage_service:
                HttpSpellApp.getInstance().removeEntity(entity);
                return;
            default:
                RedisSpellApp.getInstance().removeEntity(entity);
                return;
        }

    }

    public static void removeRedirect(String word, String redirect) {
        switch(storage) {
            case storage_redis:
                RedisSpellApp.getInstance().removeRedirect(word, redirect);
                return;
            case storage_java:
                SpellApp.getInstance().removeRedirect(word, redirect);
                return;
            case storage_service:
                HttpSpellApp.getInstance().removeRedirect(word, redirect);
                return;
            default:
                RedisSpellApp.getInstance().removeRedirect(word, redirect);
                return;
        }

    }
}
