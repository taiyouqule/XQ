package com.shenji.nlp.structure;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import com.shenji.common.log.Log;
import com.shenji.common.util.ConfigurantionUtil;
import com.shenji.common.util.PathUtil;
import com.shenji.nlp.common.Common;

/**
 * 中文词性操作类
 * 
 * @author zhq
 * @since FudanNLP 1.5 Tag改造（package edu.fudan.nlp.cn;）
 */
public class MChineseTags {
	static {
		defaultTags();
		initTags();
	}

	private static void initTags() {
		ConfigurantionUtil p = null;
		p = getProperties();

		String values = p.getValue("entitiesPattern");
		entitiesPattern = Pattern.compile(values);
		values = p.getValue("nounsPattern");
		nounsPattern = Pattern.compile(values);
		values = p.getValue("stopwordPattern");
		stopwordPattern = Pattern.compile(values);
		p.clear();
	}

	private static void defaultTags() {
		nounsPattern = Pattern.compile("名词|人名|地名|机构名|专有名");
		entitiesPattern = Pattern.compile("人名|地名|机构名|专有名");
		stopwordPattern = Pattern
				.compile(".*代词|标点|介词|从属连词|语气词|叹词|结构助词|拟声词|方位词");
	}

	public static enum PartOfSpeech {
		Noun, Verb, Adjective, Adverb, Pronoun, Preposition, Conjunction, Article, Unknown
	}

	public static Pattern entitiesPattern;

	/**
	 * 判断词性是否为一个实体，包括：人名|地名|机构名|专有名。
	 * 
	 * @param pos
	 *            词性
	 * @return true,false
	 */
	public static boolean isEntiry(String pos) {
		return (entitiesPattern.matcher(pos).find());
	}

	protected static Pattern nounsPattern;

	public static boolean isNoun(String pos) {
		return (nounsPattern.matcher(pos).find());
	}

	protected static Pattern stopwordPattern;

	/**
	 * 判断词性是否为无意义词。
	 * 
	 * @param pos
	 *            词性
	 * @return true,false
	 */
	public static boolean isStopword(String pos) {
		return (stopwordPattern.matcher(pos).find());
	}

	private static ConfigurantionUtil getProperties() {
		String path = PathUtil.getWebInFAbsolutePath();
		ConfigurantionUtil properties = new ConfigurantionUtil(new File(path
				+ File.separator + Common.ChineseTagsProperties));
		return properties;
	}

	public static boolean add(String type, String str) {
		ConfigurantionUtil p = null;
		p = getProperties();
		String values = p.getValue(type);
		if (values.contains(str))
			return false;
		p.setValue(type, values + "|" + str);
		p.save();
		initTags();
		return true;
	}

	public static boolean delete(String type, String str) {
		ConfigurantionUtil p = null;
		p = getProperties();
		String values = p.getValue(type);
		String[] strs = values.split("\\|");
		values = "";
		for (String s : strs) {
			if (s.equals(str))
				continue;
			values = values + s + "|";
		}
		p.setValue(type, values.substring(0, values.length() - 1));
		p.save();
		initTags();
		return true;
	}

}
