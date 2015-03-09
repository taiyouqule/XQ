package com.shenji.search.strategy;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.shenji.common.log.Log;
import com.shenji.common.util.ConfigurantionUtil;
import com.shenji.common.util.PathUtil;


public class SearchPatternMatching {
	private static String patternMatchingFileName = "patternMatching.properties";
	private static HashMap<Object, Object> patternMap;
	static {
		try {
			String path = PathUtil.getWebInFAbsolutePath();
			File file = new File(path + patternMatchingFileName);
			if (!file.exists())
				file.createNewFile();
			patternMap = new HashMap<Object, Object>();
			ConfigurantionUtil configurantionUtil = new ConfigurantionUtil(file);
			patternMap = configurantionUtil.getValues();
		} catch (Exception e) {
			Log.getLogger(SearchPatternMatching.class).error(e.getMessage(),e);
		}
	}

	public static String questionMatching(String arg) {
		Iterator<Map.Entry<Object, Object>> iterator = patternMap.entrySet()
				.iterator();
		String resultStr = null;
		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator
					.next();
			String matcher = entry.getKey().toString();
			int strategyType = (Integer) entry.getValue();
			PatternMatchingStrategy strategy = new PatternMatchingStrategy(
					strategyType);
			resultStr = strategy.getResult(arg, matcher);
			if (resultStr != null && resultStr.length() > 0) {
				arg = resultStr;
			}
		}
		return arg;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
