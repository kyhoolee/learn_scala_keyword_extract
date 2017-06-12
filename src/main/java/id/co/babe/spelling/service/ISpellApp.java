package id.co.babe.spelling.service;

import id.co.babe.analysis.util.TextfileIO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mainspring on 08/06/17.
 */
public interface ISpellApp {


    public String checkRedirect(String word);
    public void initRedirect(String path);
    public void insertRedirect(String word, String redirect);
    public void removeRedirect(String word, String redirect);
    public void insertRedirect(Map<String, String> redirect);


    public void initEntity(String tagDict);
    public void initEntity(String... tagDict);
    public void insertEntity(String entity);
    public void removeEntity(String entity);
    public void insertEntity(List<String> entity);

    public boolean checkEntity(String word);
    public boolean[] checkEntity(String... word);


    public boolean checkStop(String word);
    public void initStop(String indoStopPath);
    public void insertStop(String word);
    public void removeStop(String word);
    public void insertStop(List<String> word);

    public boolean checkNormal(String word);
    public void initNormal(String indoDictPath);
    public void insertNormal(String normal);
    public void removeNormal(String normal);
    public void insertNormal(List<String> normal);


}
