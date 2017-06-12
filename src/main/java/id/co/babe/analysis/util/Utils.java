package id.co.babe.analysis.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static void main(String[] args) {
		System.out.println(checkRomanNumeral("VII"));
	}
	
	public static void printArray(String[] c) {
		for(String s : c) {
			System.out.println(s);
		}
	}
	
	public static boolean checkRomanNumeral(String input) {
		//M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})
		Pattern pattern = Pattern.compile("M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			String str = matcher.group();
			System.out.println(str);
			if(str.equals(input)) {
				return true;
			}
		}
		return false;
	}
	
	public static class MapUtil
	{
	    public static <K, V extends Comparable<? super V>> Map<K, V> 
	        sortByValue( Map<K, V> map )
	    {
	        List<Map.Entry<K, V>> list =
	            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	        {
	            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	            {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
	        } );

	        Map<K, V> result = new LinkedHashMap<K, V>();
	        for (Map.Entry<K, V> entry : list)
	        {
	            result.put( entry.getKey(), entry.getValue() );
	        }
	        return result;
	    }
	}
	
	public static void printArray(double[] values) {
		System.out.println();
		for(int i = 0 ; i < values.length ; i ++) {
			System.out.print(" " + values[i]);
		}
		System.out.println();
	}

	public static Set<String> variedWord(String w) {
		Set<String> r = new HashSet<String>();
		r.add(w);
		r.add(w.replace("-", " "));
		r.add(w.replace("-", ""));
		return r;
	}


	public static void printMapSet(Map<String, Set<String>> data) {
		for(String key : data.keySet()) {
			System.out.println();
			Set<String> value = data.get(key);
			System.out.println(key);
			for(String r : value) {
				System.out.print(" -- " + r);
			}

		}
	}
	
	
	public static void printArray(Collection<String> values) {
		System.out.println();
		for(String v : values) {
			System.out.println(v);
		}
		System.out.println();
	}
	
	public static void printCollection(Collection<?> values) {
		for(Object v : values) {
			System.out.println(v.toString());
		}
	}

	public static void printSet(Collection<?> values) {
		System.out.println();
		for(Object v : values) {
			System.out.print( " -- " + v.toString());
		}
	}

}
