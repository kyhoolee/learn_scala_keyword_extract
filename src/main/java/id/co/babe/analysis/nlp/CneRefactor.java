package id.co.babe.analysis.nlp;

import id.co.babe.analysis.model.Entity;
import id.co.babe.analysis.util.Utils;

import java.util.*;

/**
 * Created by mainspring on 07/06/17.
 */
public class CneRefactor extends CneDetector {




    public static Set<String> processCapitalized(String text) {
        Set<String> result = new HashSet<String>();
        String[] sents = TextParser.sentenize(text);

        for (String sent : sents) {
            sent = FilterUtils.setenceFilter(sent);
            String[] word = TextParser.tokenize(sent);
            int j = 0;
            while (j < word.length) {
                String start = word[j];
                if (checkCapital(start)) {
                    int next = j + 1;
                    String candidate = start;
                    int count = 1;
                    while (next < word.length && checkCapitalorNumeric(word[next])) {
                        candidate += " " + word[next];
                        next++;
                        count++;
                    }
                    boolean check = candidateFilter(candidate);
                    if (j == 0)
                        check = check && ((count > 1) || (count == 1 && !DictUtils.checkNormal(start)));
                    if ( check) {
                        result.add(postProcess(candidate));
                    }
                    j = next;
                } else {
                    j++;
                }
            }
        }
        return result;
    }


    public static Map<String, Integer> processCombination(Set<String> ws, String text) {
        String filter_text = textReplace(text);
        Map<String, Integer> r = new HashMap<String, Integer>();

        Set<String> combination = new HashSet<String>();
        for (String w : ws) {
            Set<String> comb = genComb(w);
            combination.addAll(comb);
        }

        for(String com : combination) {
            r.put(com, countComb(com, filter_text));
        }

        return r;
    }

    public static Set<String> genComb(String word) {
        Set<String> result = new HashSet<String>();
        result.add(word);

        String[] tokens = word.split("\\s+");
        for (int i = 1; i < tokens.length; i++) {
            for (int start = 0; start < tokens.length - i; start++) {
                if(!set_connected.contains(tokens[start])) {
                    String c = new String(tokens[start]);
                    for (int j = 1; j <= i; j++) {
                        if(set_connect_punc.contains(tokens[start + j])) {
                            c += tokens[start + j];
                        } else {
                            c += " " + tokens[start + j];
                        }
                    }
                    c = FilterUtils.removeLastPunctuation(c);
                    if (candidateFilter(c)) {
                        System.out.println(c);
                        result.add(c);
                    }
                }
            }
        }

        return result;
    }


    public static Set<String> filterShort(Map<String, Integer> candidate) {
        Set<String> r = new HashSet<String>();

        Set<String> filtered = new HashSet<String>();

        // check in one bulk
        String[] cans = candidate.keySet().toArray(new String[0]);
        boolean[] checkEntities = DictUtils.checkEntity(cans);

        for (int i = 0 ; i < cans.length ; i ++) {

            String c = cans[i];
            if (checkEntities[i] && (c.length() > 2 || candidate.get(c) > 1) ) {
                filtered.add(c);
            }
        }

        for (String c : filtered) {
            boolean check = true;
            for (String p : filtered) {
                if (p.toLowerCase().contains(c.toLowerCase()) && p.length() > c.length()) {
                    check = false;
                    break;
                }
            }

            if (check) {
                r.add(c);
            }
        }
        return r;
    }


    public static Map<String, Double> countCan(Set<String> longCan, String text) {
        Map<String, Double> result = new HashMap<String, Double>();
        for (String can : longCan) {
            result.put(can, countDecay(can, text));
        }
        return result;
    }

    public static Map<String, Double> groupCan(Set<String> canSet,
                                               Map<String, Double> countSet) {
        Map<String, Double> result = new HashMap<String, Double>();
        for (String can : canSet) {
            double c = 0;
            for (String w : countSet.keySet()) {
                if (can.contains(w)) {
                    if (can.length() == w.length())
                        c += countSet.get(w) * 0.8 * (can.split("\\s+").length);
                    else
                        c += countSet.get(w) * 0.2 / (can.split("\\s+").length);
                }
            }
            result.put(can, c);
        }
        return result;
    }

    public static Map<String, Double> redirectCandidate(Map<String, Double> input) {
        Map<String, Double> result = new HashMap<String, Double>();

        Map<String, Set<String>> redirect = new HashMap<String, Set<String>>();
        for(String key : input.keySet()) {
            String root = DictUtils.checkRedirect(key);

            if(redirect.containsKey(root)) {
                Set<String> val = redirect.get(root);
                val.add(key);
                redirect.put(root, val);
            } else {
                Set<String> val = new HashSet<String>();
                val.add(key);
                redirect.put(root, val);
            }

        }

        System.out.println("\n\n-----------map---------------");
        Utils.printMapSet(redirect);
        System.out.println("\n-----------map---------------\n\n");

        Map<String, Double> reScore = new HashMap<String, Double>();
        for(String key : redirect.keySet()) {
            double score = 0;
            for(String can : redirect.get(key)) {
                score += input.get(can);
            }

            reScore.put(key, score);
        }

        Map<String, String> rootCan = new HashMap<String, String>();
        for(String key: redirect.keySet()) {
            int len = 0;
            String can = "";
            for(String c : redirect.get(key)) {

                if(c.length() > len) {
                    len = c.length();
                    can = c;
                }
            }
            rootCan.put(key, can);
        }


        for(String key: redirect.keySet()) {
            result.put(rootCan.get(key), reScore.get(key));
        }


        return result;
    }

    public static Map<String, List<Entity>> genGroupCan(String text) {
        Map<String, List<Entity>> result = new HashMap<String, List<Entity>>();


        Map<String, Double> matched;

        Set<String> capCan = processCapitalized(text);
        System.out.println("\n\n");
        System.out.println("------------ capCan-----------------");
        Utils.printArray(capCan);
        System.out.println("------------ capCan-----------------");
        System.out.println("\n\n");


        Map<String, Integer> combCan = processCombination(capCan, text);
        System.out.println("\n\n");
        System.out.println("------------ comCan-----------------");
        Utils.printArray(combCan.keySet());
        System.out.println("------------ comCan-----------------");
        System.out.println("\n\n");


        Set<String> longCan = filterShort(combCan);
        System.out.println("\n\n");
        System.out.println("------------ longCan-----------------");
        Utils.printArray(longCan);
        System.out.println("------------ longCan-----------------");
        System.out.println("\n\n");

        Map<String, Double> countMap = countCan(combCan.keySet(), text);
        Map<String, Double> groupMap = groupCan(longCan, countMap);

        matched = redirectCandidate(groupMap);

        System.out.println("\n\n");
        System.out.println("------------ matched-----------------");
        Utils.printArray(matched.keySet());
        System.out.println("------------ matched-----------------");
        System.out.println("\n\n");
        matched = Utils.MapUtil.sortByValue(matched);
        combCan = Utils.MapUtil.sortByValue(combCan);

        List<Entity> matchedEntity = getMatchedEntity(matched, combCan);
        List<Entity> unmatchedEntity = getUnmatchedEntity(combCan, groupMap.keySet());

        result.put("matched", matchedEntity);

        unmatchedEntity = filter(unmatchedEntity, matchedEntity);
        result.put("unmatched", unmatchedEntity);

        return result;
    }


    public static boolean candidateFilter(String w) {
        String phrase = w.toLowerCase();
        phrase = removePunctuation(phrase);
        boolean result =
                phrase.length() > 1 && !DictUtils.checkStop(phrase)
                        && !checkCommonPhrase(phrase)
                        && !checkDatePhrase(phrase)
                        && !checkMoneyPhrase(phrase)
                        && !checkWebPhrase(phrase)
                        && !checkRomanNumeral(w);

        return result;
    }


    public static void main(String[] args) {
        System.out.println(checkRomanNumeral("xi"));
        System.out.println(candidateFilter("XI"));
    }
}
