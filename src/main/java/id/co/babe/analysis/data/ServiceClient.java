package id.co.babe.analysis.data;

import id.co.babe.analysis.model.Article;
import id.co.babe.analysis.model.Category;
import id.co.babe.analysis.model.Entity;
import id.co.babe.analysis.nlp.CneRefactor;
import id.co.babe.analysis.nlp.DictUtils;
import id.co.babe.analysis.util.HttpUtils;
import id.co.babe.analysis.util.TextfileIO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mainspring on 12/06/17.
 */
public class ServiceClient extends SolrClient {
    public static String host = "http://10.2.15.150:9006/";
    public static String extract_service = "/entity/extract/";


    public static Entity parseEntity(JSONObject object) {
        Entity result = new Entity();
        try {
            result.occFreq = object.getInt("occFreq");
        } catch(Exception e) {
            //System.out.println(object);

        }
        result.name = object.getString("name");
        try {
            result.score = object.getDouble("score");
        } catch (Exception e) {
            //System.out.println(object);
        }
        result.entityType = object.getInt("entityType");
        return result;
    }

    public static List<Entity> parseEntityList(JSONArray array) {
        List<Entity> result = new ArrayList<>();

        for(int i = 0 ; i < array.length() ; i ++) {
            JSONObject o = array.getJSONObject(i);

            Entity e = parseEntity(o);

            result.add(e);

        }

        return result;
    }

    public static Map<String, List<Entity>> parseEntityMap(String json) {
        Map<String, List<Entity>> result = new HashMap<>();

        JSONObject o = new JSONObject(json);
        JSONArray matches = null;
        JSONArray unmatches = null;
        try {
            matches = o.getJSONArray("matches");
            unmatches = o.getJSONArray("unmatches");
        } catch(Exception e) {

        }
        if(matches != null) {
            List<Entity> matchesList = parseEntityList(matches);
            result.put("matched", matchesList);
        }
        if(unmatches != null) {
            List<Entity> unmatchesList = parseEntityList(unmatches);
            result.put("unmatched", unmatchesList);
        }

        return result;
    }

    public static String entityHttp(long articleId, int version) {
        String result = HttpUtils.postRequest(host + "v" + version + extract_service + articleId);
        return result;
    }

    public static Map<String, List<Entity>> entityService(long articleId, int version) {
        Map<String, List<Entity>> result = parseEntityMap(entityHttp(articleId, version));

        return result;
    }

    public static List<List<String>> getEntityResult(long articleId, int version) {
        Map<String, List<Entity>> canScore = entityService(articleId, version);

        List<String> r0 = new ArrayList<String>();
        List<String> r1 = new ArrayList<String>();
        List<List<String>> r = new ArrayList<>();

        if(canScore.get("matched") != null) {
            for (Entity e : canScore.get("matched")) {
                r0.add("-t-t-" + version + " " + e.name + " -- " + e.occFreq);
            }
        }

        if(canScore.get("unmatched") != null) {
            for (Entity e : canScore.get("unmatched")) {
                r1.add("-f-f-" + version + " " + e.name + " -- " + e.occFreq);
            }
        }

        r.add(r0);
        r.add(r1);

        return r;
    }

    public static void compareServiceCandidate(int catId, int size) {
        List<String> result = new ArrayList<String>();

        List<Article> as = getBabeArticleByCat(catId, 0, size);


        String sign = "-*-";
        for(Article a : as) {
            String content = htmlText(a.content);

            result.add(sign + a.content);
            result.add("\n");
            result.add(sign + a.articleId + "");
            result.add(sign + a.url+ "\n\n--------------------\n\n");


            long start = System.currentTimeMillis();
            List<List<String>> candidate = getEntityResult(a.articleId, 1);
            long value = System.currentTimeMillis() - start;
            System.out.println("service-v1 id: " + a.articleId + " -- time: " + (value * 0.001) + "\n\n");
            result.addAll(candidate.get(0));
            result.add(sign + " -- time: " + (value * 0.001) + "\n\n--------------------\n\n");
            System.out.println("Time: " + (value * 0.001));
            result.addAll(candidate.get(1));
            result.add(sign + " -- time: " + (value * 0.001) + "\n\n--------------------\n\n");

            start = System.currentTimeMillis();
            candidate = getEntityResult(a.articleId, 3);
            value = System.currentTimeMillis() - start;
            System.out.println("service-v3 id: " + a.articleId + " -- time: " + (value * 0.001) + "\n\n");
            result.addAll(candidate.get(0));
            result.add(sign + " -- time: " + (value * 0.001) + "\n\n--------------------\n\n");
            System.out.println("Time: " + (value * 0.001));
            result.addAll(candidate.get(1));
            result.add(sign + " -- time: " + (value * 0.001) + "\n\n--------------------\n\n");

        }

        TextfileIO.writeFile("data/sample_result/check_result/entity_sample." + catId + ".13.6.txt", result);

    }


    public static void compareServiceCategoryCandidate() {
        List<Category> cats = SqlClient.getEnabledCategory();
        int count = 0;
        for(Category c : cats) {
            count ++;
            System.out.println(count + " :: " + c.catId + " " + c.catName);
            compareServiceCandidate(c.catId, 10);
            //break;
        }
    }


    public static void test(String[] args) {
        compareServiceCategoryCandidate();
    }
}
