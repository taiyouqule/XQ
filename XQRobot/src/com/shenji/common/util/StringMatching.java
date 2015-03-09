package com.shenji.common.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatching {
	public static String mathingPatternStr = "\n";
	/**
	 * 字符串编辑距离
	 * 
	 * 这是一种字符串之间相似度计算的方法。 给定字符串S、T，将S转换T所需要的插入、删除、替代操作的数量叫做S到T的编辑路径。
	 * 其中最短的路径叫做编辑距离。
	 * 
	 * 这里使用了Levenshtein Distance(LD)算法
	 * 
	 * @author
	 * 
	 */
/*	private String stringA = null;
	private String StringB = null;*/

	public StringMatching() {

	}

	public static int strEditDistance(String stringOne, String stringTwo) {
		int editDistance = 0;
		int editArray[][] = null;
		int stringOneLength = stringOne.length();
		int stringTwoLength = stringTwo.length();
		char chOne, chTwo;
		int temp;
		// 两串中若有一串为空则返回另一串长度
		if (stringOneLength == 0)
			return stringTwoLength;
		if (stringTwoLength == 0)
			return stringOneLength;
		editArray = new int[stringOneLength + 1][stringTwoLength + 1];
		// 初始化距离矩阵
		for (int i = 0; i < stringOneLength + 1; i++) {
			editArray[i][0] = i;
		}
		for (int j = 0; j < stringTwoLength + 1; j++) {
			editArray[0][j] = j;
		}

		for (int i = 1; i < stringOneLength + 1; i++) {
			chOne = stringOne.charAt(i - 1);
			for (int j = 1; j < stringTwoLength + 1; j++) {
				chTwo = stringTwo.charAt(j - 1);
				temp = isModify(chOne, chTwo);
				editArray[i][j] = minDistance(editArray[i - 1][j] + 1,
						editArray[i][j - 1] + 1, editArray[i - 1][j - 1] + temp);
			}

		}
		editDistance = editArray[stringOneLength][stringTwoLength];
		return editDistance;
	}

	/**
	 * 求最小值
	 * 
	 * @param disa
	 *            编辑距离a
	 * @param disb
	 *            编辑距离b
	 * @param disc
	 *            编辑距离c
	 */
	private static int minDistance(int disa, int disb, int disc) {
		int dismin = Integer.MAX_VALUE;
		if (dismin > disa)
			dismin = disa;
		if (dismin > disb)
			dismin = disb;
		if (dismin > disc)
			dismin = disc;
		return dismin;
	}

	/**
	 * 单字符间是否替换 isModify(a,b)表示a字符转换到b字符所需要的操作次数。 如果a==b，则不需要任何操作isModify(a,
	 * b)=0； 否则，需要替换操作，isModify(a, b)=1。
	 * 
	 * @param a
	 *            字符a
	 * @param b
	 *            字符b
	 * @return 需要替换，返回1；否则，返回0
	 */
	private static int isModify(char a, char b) {
		if (a == b)
			return 0;
		else
			return 1;
	}

	public static double getSimilarity(String str1, String str2) {
		int editDistance = strEditDistance(str1, str2);
		return 1 - (double) editDistance
				/ Math.max(str1.length(), str2.length());
	}

	public static int getSpaceNum(String str) {
		int spaceNum = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			// System.err.println(ch);
			if (ch == ' ')
				spaceNum++;
		}
		return spaceNum;
	}

	public static boolean isChineseWord(String str) {
		boolean mark = false;
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]");
		Matcher matc = pattern.matcher(str);
		while (matc.find()) {
			mark = true;
		}
		return mark;
	}

	public static double getInherentSimilarity(String[] words, String str) {
		double inherentSimilarity = 0;
		double count = 0;
		for (int i = 0; i < words.length; i++) {
			if (str.contains(words[i]))
				count++;
		}
		inherentSimilarity = count / words.length;
		return inherentSimilarity;
	}

	public static int getRandomNum(int max) {
		Random r = new Random();
		int i = r.nextInt(max);
		return i;

	}

	public static String toLowerCase(String word) {
		return word.toLowerCase();
	}

	public static String questionMatching(String input) {
		if (input.startsWith("\n")) {
			input = input.replaceFirst("\n", "");
		}
		Pattern pattern = Pattern.compile(mathingPatternStr);
		String[] dataArr = pattern.split(input);
		if (dataArr != null && dataArr.length > 0)
			return dataArr[0];
		else
			return null;
	}

}
