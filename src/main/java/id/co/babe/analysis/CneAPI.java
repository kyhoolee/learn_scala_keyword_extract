package id.co.babe.analysis;

import id.co.babe.analysis.data.SolrClient;
import id.co.babe.analysis.model.Entity;
import id.co.babe.analysis.nlp.CneRefactor;
import id.co.babe.analysis.nlp.DictUtils;
import id.co.babe.analysis.nlp.TextParser;
import id.co.babe.analysis.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CneAPI {
	public static String id_word = "nlp_data/indo_dict/id_full.txt";
	public static String stop_word = "nlp_data/indo_dict/stop_word.txt";
	public static String tag_word = "nlp_data/indo_dict/wiki_tag.txt";
	public static String redirect_word = "nlp_data/indo_dict/redirect_entity_map.txt";
	
	public static String sent_parser = "nlp-model/en-sent.bin";
	public static String token_parser = "nlp-model/en-token.bin";
	
	public static void initDict() {

		DictUtils.initNormal(id_word);
		DictUtils.initStop(stop_word);
		DictUtils.initEntity(tag_word);
		DictUtils.initRedirect(redirect_word);
		//TextParser.init();
		TextParser.init(sent_parser, token_parser);
	}
	
	/**
	 * 
	 * @param id_word
	 * @param stop_word
	 * @param tag_word
	 * @param redirect_word
	 * @param sent_parser
	 * @param token_parser
	 */
	public static void initDict(String id_word, String stop_word, String tag_word, String redirect_word, String sent_parser, String token_parser) {

//		DictUtils.initNormal(id_word);
//		DictUtils.initStop(stop_word);
//		DictUtils.initTag(tag_word);
//		DictUtils.initRedirect(redirect_word);
		//TextParser.init();
		TextParser.init(sent_parser, token_parser);
	}
	
	
	
	public static void initDict(String id_word, String stop_word, String[] tag_word, String redirect_word, String sent_parser, String token_parser) {
		//CneRefactor.init();
		DictUtils.initNormal(id_word);
		System.out.println(id_word);
		DictUtils.initStop(stop_word);
		System.out.println(stop_word);
		DictUtils.initEntity(tag_word);
		Utils.printArray(tag_word);
		DictUtils.initRedirect(redirect_word);
		System.out.println(redirect_word);
		//TextParser.init();
		TextParser.init(sent_parser, token_parser);
		System.out.println(sent_parser);
		System.out.println(token_parser);
	}
	
	public static void initDict(String id_word, String stop_word, String redirect_word, String sent_parser, String token_parser, String tag_word, String tag_word2) {
		//CneRefactor.init();
		DictUtils.initNormal(id_word);
		System.out.println(id_word);
		DictUtils.initStop(stop_word);
		System.out.println(stop_word);
		DictUtils.initEntity(tag_word, tag_word2);
		System.out.println(tag_word);
		System.out.println(tag_word2);
		DictUtils.initRedirect(redirect_word);
		System.out.println(redirect_word);
		//TextParser.init();
		TextParser.init(sent_parser, token_parser);
		System.out.println(sent_parser);
		System.out.println(token_parser);
	}
	

	

	
	
	public static Map<String, List<Entity>> extractAllEntity(String text) {
		return CneRefactor.genGroupCan(text);
	}
	
	public static String htmlText(String html) {
		return SolrClient.htmlText(html);
	}
	
	
	public static List<Entity> getFullEntity(String text) {
		Map<String, List<Entity>> enMap = CneRefactor.genGroupCan(text);
		List<Entity> r = new ArrayList<Entity>();
		
		r.addAll(enMap.get("matched"));
		
		for(Entity e: enMap.get("unmatched")) {
			e.occFreq *= -1;
			r.add(e);
		}
		
		return r;
	}
	

	
	

}
