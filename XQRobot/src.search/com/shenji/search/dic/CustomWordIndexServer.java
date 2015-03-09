package com.shenji.search.dic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.dic.Dictionary;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.common.util.PathUtil;
import com.shenji.search.Configuration;
import com.shenji.search.engine.CustomWordEngine;
import com.shenji.search.exception.EngineException;

public class CustomWordIndexServer {
	private CustomWordEngine engine;

	public CustomWordIndexServer() throws EngineException {
		this.engine = CustomWordDic.getInstance();
	}

	public void close() {
		if (this.engine != null)
			this.engine.close();
	}

	/**
	 * @param args
	 */
	public int addNewWords(String content) {
		if (content == null)
			return -1;
		BufferedWriter out = null;

		try {
			int num = 0;
			String path = PathUtil.getWebInFAbsolutePath();
			System.err.println(path);

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path + Configuration.myDict, true),
					"UTF-8"));
			String[] words = content.split("/");

			for (int i = 0; i < words.length; i++) {
				if (engine.isCustomWord(words[i])) {
					continue;
				}
				if (words[i] != null && !words[i].equals("")) {
					out.write(words[i]);
					out.newLine();
					out.flush();
					num++;
					// this.newWordsList.add(words[i]);
				}
			}
			// System.gc();
			out.close();
			Dictionary.reset();
			engine.reset();
			return num;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -2;
		}

	}

	public int modifyWords(String oldWord, String newWord)
			throws EngineException {
		for (int i = 0; i < oldWord.toCharArray().length; i++) {
			String s = String.valueOf(oldWord.toCharArray()[i]);
			if (!FileUse.isChineseWord(s))
				return -1;
		}
		for (int j = 0; j < newWord.toCharArray().length; j++) {
			String s = String.valueOf(newWord.toCharArray()[j]);
			if (!FileUse.isChineseWord(s))
				return -2;
		}
		String myDictPath = null;
		String mainDictPath = null;
		String surnameDictPath;
		String quantifierDictPath;
		String suffixDictPath;
		String prepositionDictPath;
		String stopwordDictPath;
		myDictPath = PathUtil.getWebInFAbsolutePath();
		mainDictPath = PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_MAIN;
		surnameDictPath = PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_MAIN;
		quantifierDictPath = PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_QUANTIFIER;
		suffixDictPath = PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_SUFFIX;
		prepositionDictPath = PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_PREP;
		stopwordDictPath = PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_STOP;

		ArrayList<String> arrayList_myDict = FileUse.modify(myDictPath,
				Configuration.myDict, oldWord, newWord, FileUse.EQUALS_TYPE);
		FileUse.write(myDictPath, Configuration.myDict, arrayList_myDict);
		arrayList_myDict.clear();

		ArrayList<String> arrayList_mainDict = FileUse.modify(mainDictPath,
				null, oldWord, newWord, FileUse.EQUALS_TYPE);
		FileUse.write(mainDictPath, null, arrayList_mainDict);
		arrayList_mainDict.clear();

		ArrayList<String> arrayList_surnameDictPath = FileUse.modify(
				surnameDictPath, null, oldWord, newWord, FileUse.EQUALS_TYPE);
		FileUse.write(surnameDictPath, null, arrayList_surnameDictPath);
		arrayList_surnameDictPath.clear();

		ArrayList<String> arrayList_quantifierDictPath = FileUse
				.modify(quantifierDictPath, null, oldWord, newWord,
						FileUse.EQUALS_TYPE);
		FileUse.write(quantifierDictPath, null, arrayList_quantifierDictPath);
		arrayList_quantifierDictPath.clear();

		ArrayList<String> arrayList_suffixDictPath = FileUse.modify(
				suffixDictPath, null, oldWord, newWord, FileUse.EQUALS_TYPE);
		FileUse.write(suffixDictPath, null, arrayList_suffixDictPath);
		arrayList_suffixDictPath.clear();

		ArrayList<String> arrayList_prepositionDictPath = FileUse.modify(
				prepositionDictPath, null, oldWord, newWord,
				FileUse.EQUALS_TYPE);
		FileUse.write(prepositionDictPath, null, arrayList_prepositionDictPath);
		arrayList_prepositionDictPath.clear();

		ArrayList<String> arrayList_stopwordDictPath = FileUse.modify(
				stopwordDictPath, null, oldWord, newWord, FileUse.EQUALS_TYPE);
		FileUse.write(stopwordDictPath, null, arrayList_stopwordDictPath);
		arrayList_stopwordDictPath.clear();
		System.err.println(myDictPath);
		System.err.println(mainDictPath);
		//
		Dictionary.reset();
		engine.reset();
		return 1;
	}

	public int deleteWords(String[] words) throws EngineException {
		String myDictPath = null;
		myDictPath = PathUtil.getWebInFAbsolutePath();
		ArrayList<String> arrayList_myDict = FileUse.delete(myDictPath,
				Configuration.myDict, words, FileUse.EQUALS_TYPE);
		FileUse.write(myDictPath, Configuration.myDict, arrayList_myDict);
		arrayList_myDict.clear();
		Dictionary.reset();
		engine.reset();
		return 1;
	}

	public String searchMyAllAboutWords() {
		List<String> list = new ArrayList<String>();
		File myDictFile = null;
		try {
			myDictFile = new File(this.getClass().getClassLoader()
					.getResource("").toURI().getPath()
					+ Configuration.myDict);
			ArrayList<String> arrayList_myDict = FileUse.read(myDictFile);
			for (String s : arrayList_myDict) {
				list.add(s);
			}
			arrayList_myDict.clear();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		String str = null;
		for (int i = 0; i < list.size(); i++) {
			if (str == null) {
				str = list.get(0);
				continue;
			}
			str = str + "/" + list.get(i);
		}
		return str;

	}

	public String searchAllAboutWords(String word) {
		List<String> list = new ArrayList<String>();
		File myDictFile = null;
		File mainDictFile = null;
		File surnameDictFile;
		File quantifierDictFile;
		File suffixDictFile;
		File prepositionDictFile;
		File stopwordDictFile;
		myDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Configuration.myDict);
		mainDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_MAIN);
		surnameDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_MAIN);
		quantifierDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_QUANTIFIER);
		suffixDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_SUFFIX);
		prepositionDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_PREP);
		stopwordDictFile = new File(PathUtil.getWebInFAbsolutePath()
				+ Dictionary.PATH_DIC_STOP);
		ArrayList<String> arrayList_myDict = FileUse.read(myDictFile, word);
		for (String s : arrayList_myDict) {
			list.add(s);
		}
		arrayList_myDict.clear();

		ArrayList<String> arrayList_mainDict = FileUse.read(mainDictFile, word);
		for (String s : arrayList_mainDict) {
			list.add(s);
		}
		arrayList_mainDict.clear();

		ArrayList<String> arrayList_surnameDictPath = FileUse.read(
				surnameDictFile, word);
		for (String s : arrayList_surnameDictPath) {
			list.add(s);
		}
		arrayList_surnameDictPath.clear();

		ArrayList<String> arrayList_quantifierDictPath = FileUse.read(
				quantifierDictFile, word);
		for (String s : arrayList_quantifierDictPath) {
			list.add(s);
		}
		arrayList_quantifierDictPath.clear();

		ArrayList<String> arrayList_suffixDictPath = FileUse.read(
				suffixDictFile, word);
		for (String s : arrayList_suffixDictPath) {
			list.add(s);
		}
		arrayList_suffixDictPath.clear();

		ArrayList<String> arrayList_prepositionDictPath = FileUse.read(
				prepositionDictFile, word);
		for (String s : arrayList_prepositionDictPath) {
			list.add(s);
		}
		arrayList_prepositionDictPath.clear();

		ArrayList<String> arrayList_stopwordDictPath = FileUse.read(
				stopwordDictFile, word);
		for (String s : arrayList_stopwordDictPath) {
			list.add(s);
		}
		arrayList_stopwordDictPath.clear();
		String result = null;
		for (String s : list) {
			if (result == null) {
				result = s;
				continue;
			}
			result = result + "/" + s;
		}
		return result;
	}
}
