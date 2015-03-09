package com.shenji.search;

import java.util.Set;
import java.util.TreeSet;

import com.shenji.common.log.Log;
import com.shenji.search.database.DBPhraseManager;
import com.shenji.search.dic.BusinessDic;
import com.shenji.search.dic.CommonSynonymDic;
import com.shenji.search.dic.CustomWordDic;
import com.shenji.search.dic.CustomWordIndexServer;
import com.shenji.search.dic.SimilarWordDic;
import com.shenji.search.dic.SynonmIndexServer;
import com.shenji.search.engine.CustomWordEngine;
import com.shenji.search.engine.SimilarWordEngine;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;

public class DicControl {
	// 全局锁（词典等文件互斥操作）
	private static final Object LOCK = DicControl.class;

	/**
	 * 修改分词库单词
	 * 
	 * @param oldWord
	 *            旧单词
	 * @param newWord
	 *            新单词
	 * @return 1成功 小于1失败
	 */
	public int modifyWords(String oldWord, String newWord) {
		oldWord = oldWord.trim().toLowerCase();
		newWord = newWord.trim().toLowerCase();
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "修改分词库单词";
		String parameter = "新分词：" + newWord + ";原来分词：" + oldWord;
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			CustomWordIndexServer server = null;
			try {
				server = new CustomWordIndexServer();
				return server.modifyWords(oldWord, newWord);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
				return -1;
			} finally {
				if (server != null)
					server.close();
			}
		}
	}

	/**
	 * 删除分词库单词
	 * 
	 * @param words
	 *            单词组
	 * @return 1成功 小于1失败
	 */
	public int deleteWords(String[] words) {
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "删除分词库单词";
		String parameter = "删除的分词：";
		for (String word : words)
			parameter = parameter + word + ";";
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			CustomWordIndexServer server = null;
			try {
				server = new CustomWordIndexServer();
				return server.deleteWords(words);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
				return -1;
			} finally {
				if (server != null)
					server.close();
			}
		}
	}

	/**
	 * 添加分词库单词
	 * 
	 * @param content
	 *            新单词
	 * @return 1成功 小于1失败
	 */
	public int addNewWords(String content) {
		content = content.trim().toLowerCase();
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "添加分词库单词";
		String parameter = "添加的分词：" + content;
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			CustomWordIndexServer server = null;
			try {
				server = new CustomWordIndexServer();
				return server.addNewWords(content);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
				return -1;
			} finally {
				if (server != null)
					server.close();
			}
		}
	}

	/**
	 * 得到词库中某个词的相关词（如报税，相关词：网上报税、报税服务等）
	 * 
	 * @param word
	 *            词
	 * @return 相关词字符串（以'/'分割）
	 */
	public String getAboutWords(String word) {
		CustomWordIndexServer server = null;
		try {
			server = new CustomWordIndexServer();
			return server.searchAllAboutWords(word.toLowerCase());
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
			return null;
		} finally {
			if (server != null)
				server.close();
		}
	}

	/**
	 * 得到词库中所有自建单词
	 * 
	 * @return 相关词字符串（以'/'分割）
	 */
	public String listMyAllWords() {
		CustomWordIndexServer server = null;
		try {
			server = new CustomWordIndexServer();
			return server.searchMyAllAboutWords();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
			return null;
		} finally {
			if (server != null)
				server.close();
		}
	}

	/**
	 * 添加带权重的自建词
	 * 
	 * @param word
	 *            词
	 * @param weight
	 *            权重
	 * @return 1成功 小于1失败
	 */
	public int addNewBusinessWord(String word, float weight) {
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "添加带权重的自建词";
		String parameter = "自建词：" + word + ";权重：" + weight;
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			try {
				return BusinessDic.getInstance().addNewBusinessWord(
						word.toLowerCase(), weight);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
				return -1;
			}
		}
	}

	/**
	 * 修改带权重的自建词
	 * 
	 * @param oldWord
	 *            旧词
	 * @param newWord
	 *            新词
	 * @param newWeight
	 *            权重
	 * @return 1成功 小于1失败
	 */
	public int modifyBusinessWord(String oldWord, String newWord,
			float newWeight) {
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "修改带权重的自建词";
		String parameter = "新自建词：" + newWord + ";旧自建词：" + oldWord + ";新权重："
				+ newWeight;
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			try {
				return BusinessDic.getInstance()
						.modifyBusinessWord(oldWord.toLowerCase(),
								newWord.toLowerCase(), newWeight);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
				return -1;
			}
		}
	}

	/**
	 * 删除带权重的自建词
	 * 
	 * @param word
	 *            词
	 * @return 1成功 小于1失败
	 */
	public int deleteBusinessWord(String word) {
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "删除带权重的自建词";
		String parameter = "删除的自建词：" + word;
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			try {
				return BusinessDic.getInstance().deleteBusinessWord(word);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
				return -1;
			}
		}
	}

	/**
	 * 查询带权重的自建词
	 * 
	 * @param word
	 *            词
	 * @return >0成功 -1无
	 */
	public float getBusinessWord(String word) {
		synchronized (LOCK) {
			try {
				return BusinessDic.getInstance().getWeight(word.toLowerCase());
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
			}
			return -1;
		}
	}

	/**
	 * 得到带权重的词的词表
	 * 
	 * @return 词表组
	 */
	public String[] listBusinessDict() {
		try {
			return BusinessDic.getInstance().listBusinessDict();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 添加同义词
	 * 
	 * @param word
	 *            词
	 * @param synonmWords
	 *            同义词组
	 * @return 1成功 小于1失败
	 */
	public int addNewSynonmWord(String word, String[] synonmWords) {
		String fc = new FenciControl().iKAnalysis(word);
		String[] words = fc.split("/");
		boolean isOneWord = false;
		for (String s : words) {
			if (s.length() == word.length())
				isOneWord = true;
		}
		if (!isOneWord) {
			addNewWords(word);
		}
		for (int i = 0; i < synonmWords.length; i++) {
			synonmWords[i] = synonmWords[i].toLowerCase().trim();
			String syfc = new FenciControl().iKAnalysis(synonmWords[i]);
			String[] syfcs = syfc.split("/");
			boolean b = false;
			for (String s : syfcs) {
				if (s.length() == synonmWords[i].length())
					b = true;
			}
			if (!b) {
				addNewWords(synonmWords[i]);
			}
		}
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "添加同义词";
		String parameter = word + "：";
		for (String s : synonmWords)
			parameter = parameter + s + "/";
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			SynonmIndexServer server = new SynonmIndexServer();
			return server.addNewSynonmWord(word.toLowerCase(), synonmWords);
		}
	}

	/**
	 * 修改同义词
	 * 
	 * @param word
	 *            词
	 * @param newSynonmWords
	 *            新的同义词组（如果旧的里有则不替换）
	 * @return 1成功 小于1失败
	 */
	public int modifySynonmWord(String word, String[] newSynonmWords) {
		String fc = new FenciControl().iKAnalysis(word);
		String[] words = fc.split("/");
		boolean isOneWord = false;
		for (String s : words) {
			if (s.length() == word.length())
				isOneWord = true;
		}
		if (!isOneWord) {
			addNewWords(word);
		}
		for (int i = 0; i < newSynonmWords.length; i++) {
			newSynonmWords[i] = newSynonmWords[i].toLowerCase().trim();
			String syfc = new FenciControl().iKAnalysis(newSynonmWords[i]);
			String[] syfcs = syfc.split("/");
			boolean b = false;
			for (String s : syfcs) {
				if (s.length() == newSynonmWords[i].length())
					b = true;
			}
			if (!b) {
				addNewWords(newSynonmWords[i]);
			}
		}
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "修改同义词";
		String parameter = word + "：";
		for (String s : newSynonmWords)
			parameter = parameter + s + "/";
		dbManager.modifyLog(operation, parameter);
		synchronized (LOCK) {
			SynonmIndexServer server = new SynonmIndexServer();
			return server.modifySynonmWord(word.toLowerCase(), newSynonmWords);
		}
	}

	// 去除接口
	/*
	 * public String[] getMySynonmDictList(){ MySynonmDict dict=new
	 * MySynonmDict(); return dict.getMySynonmDictList(); }
	 */

	/**
	 * 得到同义词
	 * 
	 * @param word
	 *            词
	 * @return 同义词字符串（以'/'分割）
	 */
	public String getSynonmWords(String word) {
		word = word.toLowerCase();
		SynonymEngine engine = null;
		StringBuilder sb = new StringBuilder();
		try {
			engine = new CommonSynonymDic();
			String[] strs = engine.getSynonyms(word);
			for (String s : strs) {
				sb.append(s + "/");
			}
		} catch (EngineException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			if (engine != null)
				engine.close();
		}
		return sb.toString();
	}

	/**
	 * 句子中查看哪些词在自建词典中
	 * 
	 * @param word
	 *            句子
	 * @return 句子中在自建词典中的词
	 */
	public String getNowMyWord(String word) {
		word = word.toLowerCase();
		StringBuffer sb = new StringBuffer();
		String result = new FenciControl().iKAnalysis(word);
		String[] strs = result.split("/");
		Set<String> mySet = new TreeSet<String>();
		CustomWordEngine engine = null;
		try {
			engine = CustomWordDic.getInstance();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
			return null;
		}
		for (String s : strs) {
			if (engine.isCustomWord(s)) {
				if (mySet.add(s))
					sb.append(s + "/");
			}
		}
		mySet.clear();
		if (engine != null)
			engine.close();
		if (sb.length() != 0)
			sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * 查看这个词是在自建词典中
	 * 
	 * @param word
	 *            词
	 * @return >=0在自建词典中 -1不在
	 */
	public float isCustomWord(String word) {
		word = word.toLowerCase();
		float reFlag;
		CustomWordEngine engine = null;
		try {
			engine = CustomWordDic.getInstance();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
			return -1;
		}
		if (engine.isCustomWord(word)) {
			reFlag = this.getBusinessWord(word);
			if (reFlag == -1)
				return 0;// 在自建词典中但是不在带权重业务词典中
			else
				return reFlag;// 返回权重
		}
		if (engine != null)
			engine.close();
		return -1;
	}

	public double getWordsSimilary(String wordA, String wordB) {
		SimilarWordEngine engine = null;
		double similary = -1;
		try {
			engine = new SimilarWordDic();
			similary = engine.getSimilar(wordA, wordB);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			if (engine != null)
				engine.close();
		}

		return similary;
	}

}
