package id.co.babe.spelling.service;


import id.co.babe.analysis.util.TextfileIO;
import id.co.babe.analysis.util.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SpellApp implements ISpellApp{
    private static SpellApp instance = new SpellApp();
    private SpellApp(){}
    public static SpellApp getInstance(){
        return instance;
    }


    private Set<String> entityDict = new HashSet<>();
    private Set<String> normalDict = new HashSet<>();
    private Set<String> stopDict = new HashSet<>();

    private Map<String, String> redirectDict = new HashMap<>();


    public String checkRedirect(String word) {
        if (redirectDict.containsKey(word.toLowerCase())) {
            return redirectDict.get(word.toLowerCase());
        }
        return word;
    }

    public void initRedirect(String path) {
        List<String[]> redirect = TextfileIO.readCsv(path);
        System.out.println("redirect size " + redirect.size());
        for (String[] r : redirect) {
            if (r.length == 2)
                redirectDict.put(r[0].toLowerCase(), r[1]);
        }
    }

    public void insertRedirect(String word, String redirect) {
        redirectDict.put(word.toLowerCase(), redirect.toLowerCase());
    }

    @Override
    public void removeRedirect(String word, String redirect) {
        redirectDict.remove(word);

    }

    public void insertRedirect(Map<String, String> redirect) {
        redirectDict.putAll(redirect);
    }


    public void initEntity(String tagDict) {
        TextfileIO.initSetFile(tagDict, entityDict);
    }


    public void initEntity(String... tagDict) {
        for (String t : tagDict) {
            TextfileIO.initSetFile(t, entityDict);
            System.out.println(t);
        }

    }

    public void insertEntity(String entity) {
        entityDict.add(entity);
    }

    @Override
    public void removeEntity(String entity) {
        entityDict.remove(entity);

    }

    public void insertEntity(List<String> entity) {
        entityDict.addAll(entity);
    }




    public boolean checkEntity(String word) {
        int r = 0;
        for (String w : Utils.variedWord(word.toLowerCase())) {
            if (entityDict.contains(w))
                r++;
        }
        return r > 0;
    }

    public boolean[] checkEntity(String... word) {
        boolean[] result = new boolean[word.length];
        for(int i = 0 ; i < word.length ; i ++) {
            result[i] = checkEntity(word[i]);
        }
        return result;
    }


    public boolean checkStop(String word) {
        return stopDict.contains(word.toLowerCase());
    }

    public void initStop(String indoStopPath) {
        List<String> lines = TextfileIO.readFile(indoStopPath);
        for (String line : lines) {
            try {
                stopDict.add(line.toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insertStop(String word) {
        stopDict.add(word);
    }

    @Override
    public void removeStop(String word) {
        stopDict.remove(word);
    }

    public void insertStop(List<String> word) {
        stopDict.addAll(word);
    }

    public boolean checkNormal(String word) {
        return normalDict.contains(word.toLowerCase());
    }

    public void initNormal(String indoDictPath) {

        List<String> lines = TextfileIO.readFile(indoDictPath);
        for (String line : lines) {
            try {

                String[] tokens = line.split(" ");
                normalDict.add(tokens[0]);
            } catch (Exception e) {
                System.out.println(" --- " + line);
                e.printStackTrace();
            }
        }
    }

    public void insertNormal(String normal) {
        normalDict.add(normal);
    }

    @Override
    public void removeNormal(String normal) {
        normalDict.remove(normal);

    }

    public void insertNormal(List<String> normal) {
        normalDict.addAll(normal);
    }

}