package id.co.babe.analysis.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import id.co.babe.analysis.nlp.CneRefactor;
import id.co.babe.analysis.nlp.DictUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import id.co.babe.spelling.service.SpellApp;

import id.co.babe.analysis.model.Article;
import id.co.babe.analysis.model.Category;
import id.co.babe.analysis.model.DocWordCat;
import id.co.babe.analysis.model.Entity;
import id.co.babe.analysis.nlp.CneRefactor;
import id.co.babe.analysis.nlp.TextParser;
import id.co.babe.analysis.util.HttpUtils;
import id.co.babe.analysis.util.TextfileIO;
import id.co.babe.analysis.util.Utils;

public class SolrClient {
	
	public static final long[] aIds = {
		10646013, 10646022, 10646021, 10646036, 10646039, 10646045, 10646047, 10646056, 10646054, 10646058,
		10646062, 10646069, 10646073, 10646074, 10646081, 10646083, 10646087, 10646095,
		10646094, 10646099, 10646114
		
	};
	
	public static final String url_entity = "http://10.2.15.89:9000/v1/entity/extract/";
	public static final String url_article = "http://10.2.15.5:8983/solr/article-repo/select?sort=created_ts_l+desc&wt=json&indent=true&q=type_i%3A0";
	
	public static List<Entity> getArticleEntity(String articleId) {
		String json = HttpUtils.postRequest(url_entity + articleId);
		JSONObject object = HttpUtils.jsonObject(json);
		
		JSONArray matches = object.getJSONArray("matches");
		
		List<Entity> result = new ArrayList<Entity>();
		for(int i = 0 ; i < matches.length() ; i ++) {
			JSONObject o = matches.getJSONObject(i);
			Entity e = new Entity(o.getString("name"), o.getInt("occFreq"), o.getInt("entityType"));
			result.add(e);
		}
		
		return result;
	}
	
	public static JSONArray getJSONArray(List<?> l) {
		JSONArray array = new JSONArray(l);
		return array;
	}
	
	public static List<String> getList(JSONArray array) {
		List<String> result = new ArrayList<String>();
		
		for(int i = 0 ; i < array.length() ; i ++) {
			result.add(array.getString(i));
		}
		
		return result;
	}
	
	public static List<Integer> getIntList(JSONArray array) {
		List<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0 ; i < array.length() ; i ++) {
			result.add(array.getInt(i));
		}
		
		return result;
	}
	
	public static void printArticle(Article a) {
		System.out.println();
		System.out.println(a.articleId);
		System.out.println(a.title);
		System.out.println(a.content);
		System.out.println(a.url);
		System.out.println();
		Utils.printCollection(a.category);
		System.out.println();
		Utils.printCollection(a.catId);
		System.out.println();
	}
	
	public static String queryList(Collection<?> l) {
		String result = "";
		for(Object c : l) {
			result += c.toString() + "+";
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		
		return result;
	}
	
	public static long countArticle(String word, int catId) {
		
		String json = HttpUtils.getRequest(url_article + "+AND+body_t%3A" + word.replace(" ", "+")  
				+ "+AND+category_is%3A" + catId
				+ "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static long countArticle(String word, List<Integer> category) {
		
		String json = HttpUtils.getRequest(url_article + "+AND+body_t%3A" + word.replace(" ", "+")  
				+ "+AND+category_is%3A" + queryList(category) 
				+ "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static long countArticle(String word) {
		String json = HttpUtils.getRequest(url_article + "+AND+body_t%3A" + word.replace(" ", "+") + "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static long totalArticle() {
		String json = HttpUtils.getRequest(url_article + "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static Map<String, Long> countArticle(Collection<String> ws) {
		Map<String, Long> result = new HashMap<String, Long>();
		
		for(String w : ws) {
			Long count = countArticle(w);
			result.put(w, count);
		}
		
		
		return result;
	}
	
	public static List<Article> getBabeArticleById(int articleId) {
		String json = HttpUtils.getRequest(url_article + "+AND+article_id_l%3A" + articleId);
		return getBabeArticle(json);
	}
	
	public static List<Article> getBabeArticle(int start, int rows) {
		String json = HttpUtils.getRequest(url_article + "&start=" + start + "&rows=" + rows);
		
		return getBabeArticle(json);
	}
	
	public static List<Article> getBabeArticleByCat(int categoryId) {
		String json = HttpUtils.getRequest(url_article + "+AND+category_is%3A" + categoryId);
		
		return getBabeArticle(json);
	}
	
	public static List<Article> getBabeArticleByCat(int categoryId, int start, int rows) {
		String json = HttpUtils.getRequest(url_article + "+AND+category_is%3A" + categoryId + "&start=" + start + "&rows=" + rows);
		
		return getBabeArticle(json);
	}
	
	public static List<Article> getBabeArticle(String json) {
		JSONObject object = HttpUtils.jsonObject(json);
		
		JSONArray docs = object.getJSONObject("response").getJSONArray("docs");
		
		List<Article> result = new ArrayList<Article>();
		for(int i = 0 ; i < docs.length() ; i ++) {
			JSONObject o = docs.getJSONObject(i);
			
			try {
				Article e = new Article(
						o.getInt("article_id_l"), 
						o.getString("title_t"),
						
						o.getString("body_t"), 
						o.getString("url_s"));
				
				e.allEntity = getList(o.getJSONArray("allentity_ss"));
				e.entity = getList(o.getJSONArray("entity_ss"));
				e.category = getList(o.getJSONArray("category_name_ss"));
				e.catId = getIntList(o.getJSONArray("category_is"));
				e.summary = o.getString("summary_t");
				
				result.add(e);
				
				printArticle(e);
				
			} catch (Exception er) {
				
			}
			
		}
		
		return result;
	}
	
	public static void parseBody(Article a) {
		String html = a.content;
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		
		String[] sents = TextParser.sentenize(text);
		
		for(int i = 0 ; i < sents.length ; i ++) {
			//System.out.println(sents[i]);
			System.out.println();
			String[] word = TextParser.tokenize(sents[i]);
			for(int j = 0 ; j < word.length ; j ++) {
				System.out.println(word[j]);
			}
			System.out.println();
		}
	}
	
	public static String htmlPText(String html) {
		List<String> ts = htmlTextList(html);
		String text = "";
		for(String s: ts) {
			text += s + " . ";
		}
		return text;
	}
	
	public static List<String> htmlTextList(String html) {
		Document doc = Jsoup.parse(html);
		Elements es = doc.getElementsByTag("p");
		
		List<String> text = new ArrayList<String>();
		
		for(Element e : es) {
			text.add(e.text());
		}
		
		return text;
	}
	
	public static String htmlText(String html) {
		html = html.replace("<p>", " . ").replace("</p>", " . ");
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		
		return text;
		
		
	}
	
	public static Set<String> parseCNE(Article a) {
		String html = a.content;
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		return CneRefactor.processCapitalized(text);
	}
	
	public static void parseBody() {
		String text = "Saint-Étienne is, arguably, the \"most successful\" club in French football history having won ten Ligue 1 titles, six Coupe de France titles, a Coupe de la Ligue title and five Trophée des Champions (the French Super Cup). The club's ten league titles are the most professional league titles won by a French club, while the six cup victories places the club third among most Coupe de France titles. Saint-Étienne has also won the second division championship on three occasions. The club achieved most of its honours in the 1960s and 1970s when the club was led by managers Jean Snella, Albert Batteux, and Robert Herbin. Saint-Étienne's primary rivals are Olympique Lyonnais, who are based in nearby Lyon. The two teams annually contest the Derby Rhône-Alpes. In 2009, the club added a female section to the football club.";
	
		CneRefactor.processCapitalized(text);
	}
	
	
	public static void sample() {
		//TextParser.init();
		DictUtils.init();
		List<Article> as = getBabeArticleById(11831647);//10672395); //10672395 //7911252
		String q = queryList(as.get(0).catId);
		System.out.println(q);
		//parseBody();
		Article a = as.get(0);
		Set<String> ws = parseCNE(a);
		
		System.out.println("category: " + a.catId);
		for(String w : ws) {
			long count = countArticle(w, a.catId);
			System.out.printf("'%-10d'", count);
			System.out.print(" :: " + w + "\n");
		}
		
		System.out.println("\n\n");
		
		DocWordCat dWC = new DocWordCat(a.articleId, a.catId.get(0), a.category.get(0)); 
		
		List<Category> cats = SqlClient.getCategory();
		for(String w: ws) {
			System.out.println("\n\n" + w);
			Map<Integer, Double> catCount = new HashMap<Integer, Double>();
			
			for(int k = 0 ; k < cats.size() ; k ++) {
				Category c = cats.get(k);
				long count = countArticle(w, c.catId);
				System.out.printf("'%-10d'", count);
				System.out.print(" :: " + c.catId + "\n");
				
				catCount.put(c.catId, (double)count);
			}
			
			dWC.addWordCat(w, catCount);
		}
		
		System.out.println("\n\n");
		Map<String, Integer> catRank = dWC.wordCatRank();
		for(String key: catRank.keySet()) {
			System.out.println(key + " -- " + catRank.get(key));
		}
	}
	
	public static void catSample() {
		List<Article> as = getBabeArticle(0, 1);
		Article a = as.get(0);
		System.out.println(getJSONArray(a.catId));
		System.out.println(getJSONArray(a.category));
	}
	
	public static void parseSample() {
		List<Article> as = getBabeArticle(0, 1);
		Article a = as.get(0);
		
		DictUtils.init();
		String content = htmlText(a.content);
		List<List<String>> res = CneRefactor.parse(content);
		
		System.out.println();
		for(List<String> s : res) {
			System.out.println();
			for(String i : s) {
				System.out.println(i);
			}
			
		}
	}
	
	public static void freqSample() {
		List<Article> as = getBabeArticle(0, 1);
		Article a = as.get(0);
		
		DictUtils.init();
		String content = htmlText(a.content);
		Map<String, Double> res = CneRefactor.processFreq(content);
		
		System.out.println();
		for(String key : res.keySet()) {
			System.out.println(key + " -- " + res.get(key));
		}
	}
	
	public static void averageDocLen() {
		List<Article> as = getBabeArticle(0, 1000);
		
		DictUtils.init();
		double tL = 0;
		double c = as.size();
		for(Article a : as) {
			String content = htmlText(a.content);
			long l = CneRefactor.docLen(content);
			System.out.println(a.articleId + " -- " + l);
			tL += l;
		}
		tL /= c;
		System.out.println("Average: ");
		System.out.println(tL);
	}
	
	public static void allCatCandidate(int... catId) {
		DictUtils.init();
		for(int c : catId) {
			allCandidate(c);
		}
	}
	public static void allCandidate(int catId) {
		allCandidate(catId, 200);
	}
	
	public static void allCandidate(int catId, int size) {
		List<String> result = new ArrayList<String>();
		
		List<Article> as = getBabeArticleByCat(catId, 0, size);
				//getBabeArticleById(
						//11588577);
						//11880499);
				//11588577);
				//11880499);
				//11880971);
				//11877525);
				//11873189);
				//11879701);
				//11831647); 
				//11720946);
				//11854246);
				//11795823);
				//getBabeArticle(0, 200); 
				
		
		String sign = "-*-";
		for(Article a : as) {
			String content = htmlText(a.content);
			
			result.add(sign + a.content);
			result.add("\n");
			result.add(sign + a.articleId + "");
			result.add(sign + a.url+ "\n\n--------------------\n\n");
			
			
			long start = System.currentTimeMillis();
			List<List<String>> candidate = CneRefactor.genCanScore(content);
			long value = System.currentTimeMillis() - start;
			System.out.println("id: " + a.articleId + " -- time: " + (value * 0.001) + "\n\n");
			result.addAll(candidate.get(0));
			result.add(sign + " -- time: " + (value * 0.001) + "\n\n--------------------\n\n");
			
			System.out.println("Time: " + (value * 0.001));
			result.addAll(candidate.get(1));
			result.add(sign + " -- time: " + (value * 0.001) + "\n\n--------------------\n\n");
			
		}
		
		TextfileIO.writeFile("sample_result/check_result/entity_sample." + catId + ".30.5.txt", result);
		
	}
	
	
	public static void allEntity() {
		List<Article> as = getBabeArticleById(10662651);
		
		long start = System.currentTimeMillis();
		DictUtils.init();
		long value = System.currentTimeMillis() - start;
		System.out.println("Processing time: " + value * 0.001);
		
		
		for(Article a : as) {
			String content = htmlText(a.content);
			
			
			start = System.currentTimeMillis();
			Map<String, List<Entity>> r = CneRefactor.genGroupCan(content);
			value = System.currentTimeMillis() - start;
			
			
			CneRefactor.printResult(r);
			System.out.println("Processing time: " + value * 0.001);
		}
		
		
	}
	
	public static void countSample() {
		String[] w = {
				"Perhelatan Miss Universe",
				"Perhelatan Miss",
				"Miss Universe",
//				"Kezia Warouw",
//				"Filipina",
//				"Courtesy",
//				"Pia Alonzo Wurtzbach",
//				"Pia Alonzo",
//				"Alonzo Wurtzbach",
//				"Ivan Gunawan",
//				"Januari",
//				"Puteri Indonesia",
//				"Ia",
//				"Instagram",
//				"Lara Dutta",
//				"Manado",
//				"Indonesia"	
		};
		
		List<Category> cats = SqlClient.getCategory();
		for(int i = 0 ; i < w.length ; i ++) {
			System.out.println("\n\n" + w[i]);
			for(int k = 0 ; k < cats.size() ; k ++) {
				Category c = cats.get(k);
				long count = countArticle(w[i], c.catId);
				System.out.printf("'%-10d'", count);
				System.out.print(" :: " + c.catId + "\n");
			}
		}
	}
	
	
	public static void printCategory() {
		List<Category> cats = SqlClient.getEnabledCategory();
		int count = 0;
		for(Category c : cats) {
			count ++;
			System.out.println(count + " :: " + c.catId + " " + c.catName);
		}
	}
	
	public static void test() {
		String c = "Januari 2017";
		DictUtils.init();
		boolean r = CneRefactor.candidateFilter(c);
		System.out.println(r);
	}
	
	public static void test1() {
		String c = "Lubang jalan di ruas Kudus-Pati, Desa Gondoharum, Kecamatan Jekulo. (suaramerdeka.com/Anton W. Hartono)";
		String r = CneRefactor.preProcess(c);
		System.out.println(r);
	}
	

	
	public static void allCategoryCandidate() {
		List<Category> cats = SqlClient.getEnabledCategory();
		int count = 0;
		DictUtils.init();
		for(Category c : cats) {
			count ++;
			System.out.println(count + " :: " + c.catId + " " + c.catName);
			allCandidate(c.catId, 20);
		}
	}
	
	
	
	public static EstimateResult estimateResult(String filePath) {
		List<String> lines = TextfileIO.readFile(filePath);
		
		double tp = 0;
		double fp = 0;
		double fn = 0;
		for(String l: lines) { 
			if(l.startsWith("-t-t-")) {
				tp ++;
			} else if(l.startsWith("-t-f")) {
				fn ++;
			} else if(l.startsWith("-f-t-")) {
				fp ++;
			}
		}
		
		double precision = (tp + 1) / (tp + fp + 1);
		double recall = (tp + 1) / (tp + fn + 1);
//		System.out.println("tp: " + tp);
//		System.out.println("fp: " + fp);
//		System.out.println("fn: " + fn);
//		System.out.println("precision: " + precision);
//		System.out.println("recall: " + recall);
		
		EstimateResult r = new EstimateResult(precision, recall, tp, fp, fn);
		r.result = String.format("%.3f\t%.3f\t%.0f\t%.0f\t%.0f", precision, recall, tp, fp, fn);
		return r;
	}
	
	public static class EstimateResult {
		public String result;
		public double precision;
		public double recall;
		public double tp;
		public double fp;
		public double fn;
		public EstimateResult(double precision, double recall, double tp, double fp,
				double fn) {
			super();
			this.precision = precision;
			this.recall = recall;
			this.tp = tp;
			this.fp = fp;
			this.fn = fn;
		}
	}
	
	public static void sampleEstimate() {
		List<String> result = new ArrayList<String>();
		
		
		List<Category> cats = SqlClient.getEnabledCategory();
		int count = 0;
		
		double tp = 0;
		double fp = 0;
		double fn = 0;
		
		for(Category c : cats) {
			count ++;
			
			//allCandidate(c.catId, 20);
			String filePath = "sample_result/verify_result/entity_sample." + c.catId + ".30.5.txt";
			EstimateResult r = estimateResult(filePath);
			String print_out = String.format("%1$-10d", c.catId) + String.format("%1$-30s", c.catName) + r.result; 
			result.add(print_out);
			
			tp += r.tp;
			fp += r.fp;
			fn += r.fn;
			System.out.println(print_out);
			System.out.println();
			
		}
		
		double precision = (tp + 1) / (tp + fp + 1);
		double recall = (tp + 1) / (tp + fn + 1);
		System.out.println("tp: " + tp);
		System.out.println("fp: " + fp);
		System.out.println("fn: " + fn);
		System.out.println("precision: " + precision);
		System.out.println("recall: " + recall);
		
		
		result.add("\n\nTotal");
		String l_r = precision + "\t" + recall + "\t" + tp + "\t" + fp + "\t" + fn;
		result.add(l_r);
		
		TextfileIO.writeFile("sample_result/accuracy.30.5.txt", result);
	}

	public static void main(String[] args) {
		//testRedirect();
		//test1();
		//test();
		//printCategory();
		//allEntity();
		//sampleEstimate();
		allCategoryCandidate();
		//allCandidate(45, 50, 26, 27, 28);
		//averageDocLen();
		//freqSample();
		//parseSample();
		//sample();
		//countSample();

	}
	
	

}
