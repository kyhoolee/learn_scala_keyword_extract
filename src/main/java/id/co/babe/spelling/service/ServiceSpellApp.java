package id.co.babe.spelling.service;

import com.twitter.util.Await;
import com.twitter.util.Future;
import id.co.babe.entity.client.EntityClient;
import id.co.babe.entity.client.domain.JEntityRedisBulkCheckRequest;
import id.co.babe.entity.client.domain.JEntityRedisBulkCheckResponse;
import id.co.babe.entity.client.domain.JEntityRedisCheckRequest;
import id.co.babe.entity.client.domain.JEntityRedisCheckResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mainspring on 16/06/17.
 */
public class ServiceSpellApp implements ISpellApp {
    private static ServiceSpellApp instance = new ServiceSpellApp();
    private ServiceSpellApp() {
    }
    public static ServiceSpellApp getInstance() {
        return instance;
    }


    EntityClient client = EntityClient.apply("10.2.15.176:9004");

    public void initClient(String address) {
        client = EntityClient.apply(address);
    }


    @Override
    public String checkRedirect(String word) {
        Future<String> f = client.jGetRedirect(word.toLowerCase().replace(" ", "%20"));

        try {
            String resp = Await.result(f);
            return resp;
        } catch (Exception e) {
            System.out.println("error insert article : " + e);
        }

        return word.toLowerCase();
    }

    @Override
    public void initRedirect(String path) {

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

    @Override
    public void initEntity(String tagDict) {

    }

    @Override
    public void initEntity(String... tagDict) {

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

    public boolean check(String word, String type) {
        JEntityRedisCheckRequest keyword1 = new JEntityRedisCheckRequest(word.toLowerCase(), type);
        Future<String> bulkCheckEntity = client.jCheckEntity(keyword1);
        try {
            String resp = Await.result(bulkCheckEntity).toString();
            System.out.println(resp);

            return (resp.equalsIgnoreCase("true"));
        } catch (Exception e) {
            System.out.println("error insert article : " + e);
        }

        return false;
    }

    @Override
    public boolean checkEntity(String word) {
        return check(word, "entity");
    }

    @Override
    public boolean[] checkEntity(String... word) {
        return new boolean[0];
    }

    @Override
    public boolean checkStop(String word) {
        return check(word, "stop");
    }

    @Override
    public void initStop(String indoStopPath) {

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

    @Override
    public boolean checkNormal(String word) {
        return check(word, "normal");
    }

    @Override
    public void initNormal(String indoDictPath) {

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

    public static void main_test(String[] args) {
        boolean r = ServiceSpellApp.getInstance().checkEntity("jokowi");
        System.out.println(r);
        String res = ServiceSpellApp.getInstance().checkRedirect("manchester united");
        System.out.println(res);
    }
}
