package com.shenji.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.search.bean.SearchBean;
import com.shenji.search.database.DBPhraseManager;
import com.shenji.search.dic.SynonmIndexServer;
import com.shenji.search.search.AllSearch;
import com.shenji.search.train.FAQControl;
import com.shenji.search.train.FAQIndexServer;
import com.shenji.search.train.QAControl;

public class ResourcesControl {
	// 全局锁
	// private static final Object LOCK = ResourcesControl.class;//这里不需要

	/**
	 * 列出所有的FAQ
	 * 
	 * @return HTML文本
	 */
	public String listAllFaq() {
		StringBuilder html = new StringBuilder(
				"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><head><title>Search</title>");
		html.append("<style>em{font-style:normal;color:#cc0000}</style></head><body>");
		try {
			List<SearchBean> result = new AllSearch()
					.getResult(Configuration.searchDir[0]);
			for (SearchBean bean : result) {
				html.append("<div>" + bean.getContent() + "</div><br>");
			}
			return html.toString();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			html.append("<div>FAQ为空!</div>");
		}
		html.append("</body></html>");
		return html.toString();
	}

	/**
	 * 添加新的FAQ
	 * 
	 * @param question
	 *            问题租
	 * @param answer
	 *            答案组
	 * @return 1成功 小于1失败
	 */
	public int addNewFAQ(String question[], String answer[]) {
		if (question.length == 0)
			return 0;
		FAQControl c = null;
		// int num = 0;
		try {
			c = new FAQControl();
			if (!c.createIndexPath())
				return 0;
			// System.err.println(question.length + ":::" + answer.length + "");
			for (int i = 0; i < question.length; i++) {

				if (i < answer.length && answer[i] == null)
					continue;
				c.addIndex(question[i].toLowerCase(), answer[i].toLowerCase());
				// num++;
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return 1;
	}

	/**
	 * 【暂时无用，待修改】添加新的FAQ（带权重）
	 * 
	 * @param question
	 *            问题租
	 * @param answer
	 *            答案组
	 * @param boost
	 *            权重
	 * @return 1成功 小于1失败
	 */
	public int addNewFAQWithBoost(String question[], String answer[],
			float boost) {
		if (question.length == 0)
			return 0;
		FAQControl c = new FAQControl();
		if (!c.createIndexPath())
			return 0;
		int num = 0;
		// System.err.println(question.length + ":::" + answer.length + "");
		for (int i = 0; i < question.length; i++) {

			if (i < answer.length && answer[i] == null)
				continue;
			c.addIndex(question[i].toLowerCase(), answer[i].toLowerCase(),
					boost);
			num++;
		}
		c.close();
		return 1;
	}

	/**
	 * 删除FAQ
	 * 
	 * @param url
	 *            url地址组
	 * @return 1成功 小于1失败
	 */
	public int deleteFAQ(String url[]) {
		if (url == null)
			return 0;
		int num = 0;
		FAQControl c = null;
		try {
			c = new FAQControl();
			if (!c.createIndexPath())
				return 0;
			for (int i = 0; i < url.length; i++) {
				if (url[i] == null)
					continue;
				c.deleteIndex(url[i]);
				num++;
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return num;
	}

	/**
	 * 修改FAQ
	 * 
	 * @param url
	 *            url地址组
	 * @param q
	 *            问题组
	 * @param a
	 *            答案组
	 * @return 1成功 小于1失败
	 */
	public int changeFAQ(String url[], String q[], String a[]) {
		if (url == null)
			return 0;
		FAQControl c = null;
		int num = 0;
		try {
			c = new FAQControl();
			if (!c.createIndexPath())
				return 0;
			for (int i = 0; i < url.length; i++) {
				if (url[i] == null || q[i] == null || a[i] == null)
					continue;
				c.modifyIndex(url[i], q[i].toLowerCase(), a[i].toLowerCase());
				num++;
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return num;
	}

	/**
	 * 【暂时无用，待修改】修改FAQ
	 * 
	 * @param url
	 *            url地址组
	 * @param q
	 *            问题组
	 * @param a
	 *            答案组
	 * @param boost
	 *            权重
	 * @return 1成功 小于1失败
	 */
	public int changeFAQWithBoost(String url[], String q[], String a[],
			float boost) {
		if (url == null)
			return 0;
		FAQControl c = new FAQControl();
		if (!c.createIndexPath())
			return 0;
		int num = 0;
		for (int i = 0; i < url.length; i++) {
			if (url[i] == null || q[i] == null || a[i] == null)
				continue;
			c.modifyIndex(url[i], q[i].toLowerCase(), a[i].toLowerCase(), boost);
			num++;
		}
		c.close();
		return num;
	}

	/**
	 * 重建索引
	 * 
	 * @param index
	 *            传入0即可
	 * @return 1成功 小于1失败
	 */
	public boolean rebuildIndex(String index) {
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "重建索引";
		dbManager.modifyLog(operation, operation);
		boolean b = false;
		b = new SynonmIndexServer().createIndex();
		FAQIndexServer indexServer = new FAQIndexServer(index);
		b = indexServer.createIndex() && b;
		indexServer.close();
		return b;
	}

	/**
	 * 【未启用】添加对话
	 * 
	 * @param str
	 * @param sessionid
	 * @return
	 */
	public int addNewDialogue(String str, String sessionid) {
		QAControl control = new QAControl();
		int b = control.addNewDialogue(sessionid, str);
		control.close();
		return b;
	}

	// 获得常用语
	public String getPhrase(String Question) {
		Question = Question.trim();
		DBPhraseManager dbManager = new DBPhraseManager();
		try {
			return dbManager.getAnswer(Question);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
	}

	// 增加常用语
	public int addPhrase(String Question, String Answer) {
		Question = Question.trim();
		Answer = Answer.trim();
		DBPhraseManager dbManager = new DBPhraseManager();
		try {
			return dbManager.addQA(Question, Answer);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		}
	}

	// 删除常用语
	public int delPhrase(String Question) {
		Question = Question.trim();
		DBPhraseManager dbManager = new DBPhraseManager();
		try {
			return dbManager.delQA(Question);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		}
	}

	// 修改常用语
	public int modifyPhrase(String Question, String Answer) {
		Question = Question.trim();
		Answer = Answer.trim();
		DBPhraseManager dbManager = new DBPhraseManager();
		try {
			return dbManager.modifyQA(Question, Answer);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		}
	}

	public String[][] listAllPhrase() {
		HashMap<String, String> qaMap = null;
		DBPhraseManager dbManager = new DBPhraseManager();
		try {
			qaMap = (HashMap<String, String>) dbManager.listAllQA();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		Iterator<Map.Entry<String, String>> iterator = qaMap.entrySet()
				.iterator();
		String[][] reStrs = new String[qaMap.size()][2];
		int count = 0;
		while (iterator.hasNext()) {
			Map.Entry<String, String> map = iterator.next();
			reStrs[count][0] = map.getKey();
			reStrs[count][1] = map.getValue();
			count++;
		}
		return reStrs;
	}

}
