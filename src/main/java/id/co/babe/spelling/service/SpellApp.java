package id.co.babe.spelling.service;


import id.co.babe.analysis.util.TextfileIO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class SpellApp {
	public static Set<String> exactTag;
	public static Map<String, String> redirectDict;
	public static Map<String, Integer> indoDict;
	public static Set<String> indoStopDict;
	
	
	public static String checkRedirect(String word) {
		if(redirectDict.containsKey(word.toLowerCase())) {
			return redirectDict.get(word.toLowerCase());
		}
		return word;
	}
	
	public static void initRedirect(String path) {
		redirectDict = new HashMap<String, String>();
		
		List<String[]> redirect = TextfileIO.readCsv(path);
		System.out.println("redirect size " + redirect.size());
		for(String[] r : redirect) {
			if(r.length == 2)
				redirectDict.put(r[0].toLowerCase(), r[1]);
		}
	}
	
	public static void printRedirect() {
		for(String key: redirectDict.keySet()) {
			System.out.println(key + " -- " + redirectDict.get(key));
		}
	}
	
	public static void initTag(String tagDict) {
		//trieDict = Tries.forInsensitiveStrings(Boolean.FALSE);
		exactTag = new HashSet<String>();
		
//		try {
//			LuceneSpell.init(tagDict);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		List<String> dictionary = TextfileIO.readFile(tagDict);
		for (String word : dictionary) {
			exactTag.addAll(variedWord(word.toLowerCase()));
//			if(word!=null && word.trim().length() > 0)
//				trieDict.put(word.trim().toLowerCase(), Boolean.TRUE);
		}

	}
	
	
	public static void initEntity(String... tagDict) {
		//trieDict = Tries.forInsensitiveStrings(Boolean.FALSE);
		exactTag = new HashSet<String>();
		
//		try {
//			LuceneSpell.init(tagDict);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		for(String t : tagDict) {
			TextfileIO.initSetFile(t, exactTag);
			System.out.println(t);
//			List<String> dictionary = TextfileIO.readFile(t);
//			for (String word : dictionary) {
//				exactTag.addAll(variedWord(word.toLowerCase()));
//			}
		
		}
		
		System.out.println(exactTag.size());

	}
	
//	public static Set<String> prefixEntity(String word) {
////		Set<String> keys = trieDict.keySet(word, TrieMatch.PARTIAL);
////		return keys;
//		
//		try {
//			return LuceneSpell.getPrefix(word).keySet();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new HashSet<String>();
//	}
//	public static int countPrefix(String word) {
////		Set<String> keys = trieDict.keySet(word, TrieMatch.PARTIAL);
////		if(keys == null)
////			return 0;
////		return keys.size();
//		return LuceneSpell.countPrefix(word);
//	}
	
	public static Set<String> variedWord(String w) {
		Set<String> r = new HashSet<String>();
		
		r.add(w);
		r.add(w.replace("-", " "));
		r.add(w.replace("-", ""));
		return r;
	}
	
	public static int checkEntity(String word) {
//		Set<String> keys = trieDict.keySet(word, TrieMatch.EXACT);
//		if(keys == null)
//			return 0;
//		return keys.size();
		
		int r = 0;
		for(String w: variedWord(word.toLowerCase())) {
			if(exactTag.contains(w))
				r ++;
		}
		return r;
		
//		if(exactTag.contains(word.toLowerCase()))
//			return 1;
//		else 
//			return 0;
	}
	
	
	
	public static boolean checkStop(String word) {
		return indoStopDict.contains(word.toLowerCase());
	}
	
	public static void initStop(String indoStopPath) {
		indoStopDict = new HashSet<String>();
		List<String> lines = TextfileIO.readFile(indoStopPath);
		for(String line : lines) {
			try {
				indoStopDict.add(line.toLowerCase());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean checkNormal(String word) {
		return indoDict.containsKey(word.toLowerCase());
	}
	
	public static void initNormal(String indoDictPath) {
		indoDict = new HashMap<String, Integer>();
		
		List<String> lines = TextfileIO.readFile(indoDictPath);
		for(String line : lines) {
			try {
				
				String[] tokens = line.split(" ");
				indoDict.put(tokens[0], Integer.parseInt(tokens[1]));
			} catch (Exception e) {
				System.out.println(" --- " + line);
				e.printStackTrace();
			}
		}
	}
	



	



}