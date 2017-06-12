package id.co.babe.spelling.service;

import id.co.babe.analysis.util.HttpUtils;
import id.co.babe.analysis.util.TextfileIO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import id.co.babe.analysis.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class HttpSpellApp implements ISpellApp {
    private static HttpSpellApp instance = new HttpSpellApp();
    private HttpSpellApp(){}
    public static HttpSpellApp getInstance(){
        return instance;
    }



    public static final int type_normal = 1;
    public static final int type_stop = 2;
    public static final int type_entity = 3;


    public static String remote_url = "http://10.2.15.176:9004";
    public static boolean isLocal = false;


    public static void main(String[] args) {
        //initRemoteNormal("nlp_data/indo_dict/id_full.txt");
        //initRemoteStop("nlp_data/indo_dict/stop_word.txt");
        //initRemoteEntity("nlp_data/indo_dict/wiki_tag.txt");
        //testInsert();
        //updateType();
        //testUpdate();
        //testRedirect();
        //testCheckWord();
    }

    public void testCheckWord() {
        String result = checkWord("Abraham Paz Cruz".toLowerCase());
        System.out.println(result);
    }

    public void testRedirect() {
        postRedirect("hihi", "hehe");
        //getRedirect("hihi");
    }

    public void testInsert() {
        System.out.println(postToDict("adi", type_normal));
        postToDict("ada", type_stop);
    }

    public void testUpdate() {
        String data = "{\"keyword\": \"akhir\",\"classification\": [],\"taggedUrl\": \"\",\"state\": 0,\"entityType\": [1,2]}";
        System.out.println(HttpUtils.postRequest(remote_url + "/entity/v1/update", data));
    }

    public String checkWord(String word) {
        String jsonData = "{"
                + "\"tokens\": [\"" + word + "\"]"
                + "}";
        String result = HttpUtils.postRequest(remote_url + "/entity/v1/check", jsonData);
        return result;
    }

    public Set<Integer> checkWordType(String word) {
        Set<Integer> result = new HashSet<Integer>();


        try {
            String r = checkWord(word);
            JSONObject d = new JSONObject(r);
            JSONArray a = d.getJSONArray("entities");
            JSONObject e = a.getJSONObject(0);
            JSONArray t = e.getJSONArray("entityType");
            for (Object o : t) {
                int i = Integer.parseInt(o.toString());
                result.add(i);
            }
        } catch (Exception e) {

        }

        return result;
    }

    public boolean checkWordType(String word, int type) {
        boolean result = false;
        try {
            String r = checkWord(word);
            JSONObject d = new JSONObject(r);
            JSONArray a = d.getJSONArray("entities");
            JSONObject e = a.getJSONObject(0);
            JSONArray t = e.getJSONArray("entityType");
            for (Object o : t) {
                int i = Integer.parseInt(o.toString());
                if (type == i) {
                    return true;
                }
            }
        } catch (Exception e) {

        }

        return result;

    }

    public String postToDict(String word, int type) {
        String jsonData = "{"
                + "\"keyword\": \"" + word + "\","
                + "\"classification\": [],"
                + "\"taggedUrl\": \"\","
                + "\"state\": 0,"
                + "\"entityType\": [" + type + "]"
                + "}";
        String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert", jsonData);
        //System.out.println(result);

        return result;
    }

    public String postToDict(String word, String types) {
        String jsonData = "{"
                + "\"keyword\": \"" + word + "\","
                + "\"classification\": [],"
                + "\"taggedUrl\": \"\","
                + "\"state\": 0,"
                + "\"entityType\": " + types + ""
                + "}";
        System.out.println(jsonData);
        String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert", jsonData);
        System.out.println(result);
        //System.out.println(result);

        return result;
    }


    public String getRedirect(String word) {
        String result = HttpUtils.getRequest(remote_url + "/entity/v1/check/synonym/" + word);
        System.out.println(result);
        return result;
    }

    public String postRedirect(String word, String root) {
        String jsonData = "{"
                + "\"keyword\": \"" + word + "\","
                + "\"synonym\": [\"" + root + "\"]"
                + "}";
        System.out.println(jsonData);
        String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert/synonyms", jsonData);
        System.out.println(result);
        //System.out.println(result);

        return result;
    }

    public String updateToDict(String word, String types) {
        String jsonData = "{"
                + "\"keyword\": \"" + word + "\","
                + "\"classification\": [],"
                + "\"taggedUrl\": \"\","
                + "\"state\": 0,"
                + "\"entityType\": " + types + ""
                + "}";
        //System.out.println(jsonData);
        String result = HttpUtils.postRequest(remote_url + "/entity/v1/update", jsonData);
        //System.out.println(result);
        //System.out.println();

        return result;
    }

    public int postUpdateToDict(String word, int type) {
        int r = 0;
        String jsonData = "{"
                + "\"keyword\": \"" + word + "\","
                + "\"classification\": [],"
                + "\"taggedUrl\": \"\","
                + "\"state\": 0,"
                + "\"entityType\": [" + type + "]"
                + "}";
        //System.out.println(jsonData);
        String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert", jsonData);
        //System.out.println(result);
        JSONObject o = new JSONObject(result);
        int id = -1;
        try {
            id = o.getInt("insertedId");
        } catch (Exception e) {
            id = -1;
        }
        //System.out.println(id);
        // Duplicate --> re-insert
        if (id == -1) {
            try {
                JSONObject entity = o;//.getJSONArray("entities").getJSONObject(0);
                JSONArray entityType = entity.getJSONArray("entityType");
                //System.out.println(type + " -- " + entityType);
                boolean check = true;
                for (int i = 0; i < entityType.length(); i++) {

                    //System.out.println("element " + entityType.get(i).toString());
                    int t = Integer.parseInt(entityType.get(i).toString());
                    if (t == type) {
                        check = false;
                    }
                }
                //System.out.println(check);
                if (check) {
                    entityType = entityType.put(type);
                    //System.out.println(entityType);
                    updateToDict(word, entityType.toString());
                    r = 1;
                }
                //System.out.println(entityType.toString());

            } catch (Exception e) {

            }
        }

        return r;
    }

    public void updateType() {
        String jsonData = "{"
                + "\"keyword\": \"" + "hihi" + "\","
                + "\"classification\": [],"
                + "\"taggedUrl\": \"\","
                + "\"state\": 0,"
                + "\"entityType\": [" + 1 + "]"
                + "}";

        try {
            JSONObject entity = new JSONObject(jsonData);
            JSONArray entityType = entity.getJSONArray("entityType");
            JSONArray newType = entityType.put(2);
            entity.put("entityType", newType);

            System.out.println(entity);
        } catch (Exception e) {

        }
    }


    public void initRemoteNormal(String path) {

        List<String> lines = TextfileIO.readFile(path);
        int count = 0;
        long start = System.currentTimeMillis();
        for (String line : lines) {
            try {
                count++;
                String word = line.split(" ")[0];
                //indoDict.put(tokens[0], Integer.parseInt(tokens[1]));
                postToDict(word, type_normal);
                if (count % 100 == 0) {
                    long time = System.currentTimeMillis() - start;
                    System.out.println(time + " -- " + count);
                    start = System.currentTimeMillis();
                }

            } catch (Exception e) {
                System.out.println(" --- " + line);
                e.printStackTrace();
            }
        }

    }


    public void initRemoteStop(String path) {


        List<String> lines = TextfileIO.readFile(path);
        int dups = 0;
        int count = 0;
        long start = System.currentTimeMillis();
        for (String line : lines) {
            try {
                count++;
                String word = line.toLowerCase();
                int r = postUpdateToDict(word, type_stop);
                dups += r;
                if (count % 100 == 0) {
                    long time = System.currentTimeMillis() - start;
                    System.out.println(time + " -- " + count + " -- " + dups);
                    start = System.currentTimeMillis();
                    //break;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initRemoteEntity(String path) {

        List<String> dictionary = TextfileIO.readFile(path);
        int dups = 0;
        int count = 0;
        long start = System.currentTimeMillis();
        for (String word : dictionary) {
            Set<String> varied = Utils.variedWord(word.toLowerCase());

            for (String w : varied) {
                count++;
                if (count > 121400) {
                    int r = postUpdateToDict(w, type_entity);
                    dups += r;
                    if (count % 100 == 0) {
                        long time = System.currentTimeMillis() - start;
                        System.out.println(time + " -- " + count + " -- " + dups);
                        start = System.currentTimeMillis();
                        //break;
                    }
                }
            }

        }
    }

    public void initRemoteEntity(String... path) {
        if (isLocal) {
            SpellApp.getInstance().initEntity(path);
        }
    }

    public void initRemoteRedirect(String path) {
        if (isLocal) {
            SpellApp.getInstance().initRedirect(path);
        }
    }


    public void initNormal(String path) {
        if (isLocal) {
            SpellApp.getInstance().initNormal(path);
        }

    }

    @Override
    public void insertNormal(String normal) {

    }

    @Override
    public void removeNormal(String normal) {

    }

    @Override
    public void insertNormal(List<String> normal) {

    }

    public void initStop(String path) {
        if (isLocal) {
            SpellApp.getInstance().initStop(path);
        }
    }

    @Override
    public void insertStop(String word) {

    }

    @Override
    public void removeStop(String word) {

    }

    @Override
    public void insertStop(List<String> word) {

    }

    public void initEntity(String path) {
        if (isLocal) {
            SpellApp.getInstance().initEntity(path);
        }
    }

    public void initEntity(String... path) {
        if (isLocal) {
            SpellApp.getInstance().initEntity(path);
        }
    }

    @Override
    public void insertEntity(String entity) {

    }

    @Override
    public void removeEntity(String entity) {

    }

    @Override
    public void insertEntity(List<String> entity) {

    }


    public void initRedirect(String path) {
        if (isLocal) {
            SpellApp.getInstance().initRedirect(path);
        }
    }

    @Override
    public void insertRedirect(String word, String redirect) {

    }

    @Override
    public void removeRedirect(String word, String redirect) {

    }

    @Override
    public void insertRedirect(Map<String, String> redirect) {

    }

    /**
     * Check word is Stop
     *
     * @param word
     * @return
     */
    public boolean checkStop(String word) {
        if (isLocal) {
            return SpellApp.getInstance().checkStop(word);
        }

        boolean r = checkWordType(word.toLowerCase(), type_stop);

        return r;
    }

    /**
     * Check word is Entity
     *
     * @param word
     * @return
     */
    public boolean checkEntity(String word) {
        if (isLocal) {
            return SpellApp.getInstance().checkEntity(word);
        }

        boolean r = checkWordType(word.toLowerCase(), type_entity);
        return r;

    }

    public boolean[] checkEntity(String... word) {
        boolean[] result = new boolean[word.length];
        for(int i = 0 ; i < word.length ; i ++) {
            result[i] = checkEntity(word[i]);
        }
        return result;
    }

    /**
     * Get root of Word
     *
     * @param word
     * @return
     */
    public String checkRedirect(String word) {
        if (isLocal) {
            return SpellApp.getInstance().checkRedirect(word);
        }
        return word;
    }

    /**
     * Check word is Normal
     *
     * @param word
     * @return
     */
    public boolean checkNormal(String word) {
        if (isLocal) {
            return SpellApp.getInstance().checkNormal(word);
        }
        boolean r = checkWordType(word.toLowerCase(), type_normal);

        return r;
    }

}
