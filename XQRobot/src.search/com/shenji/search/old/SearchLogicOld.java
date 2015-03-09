package com.shenji.search.old;
/*package com.shenji.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shenji.common.util.WriteLog;
import com.shenji.search.bean.SearchBean;
import com.shenji.search.common.Common;
import com.shenji.search.database.DBFeedBackManager;
import com.shenji.search.database.DBPhraseManager;
import com.shenji.search.database.DBUtil;
import com.shenji.search.dic.MySynonymDictTool;
import com.shenji.search.dic.SynonmIndexTool;
import com.shenji.search.search.AllSearch;
import com.shenji.search.search.SearchThread;
import com.shenji.search.strategy.DecisionSystem;
import com.shenji.search.strategy.SearchNonBusinessMatching;
import com.shenji.search.strategy.SearchPatternMatching;
import com.shenji.search.train.FAQControl;
import com.shenji.search.train.RefreshIndex;

public class SearchLogicOld {
	public String[] Search_numWithUrl(String args, int number) {
		String mattchingStr = null;
		if ((mattchingStr = SearchPatternMatching.questionMatching(args)) != null) {
			args = mattchingStr;
		}
		try {
			DBPhraseManager dbManager = new DBPhraseManager();
			String answer = dbManager.getAnswer(args);
			String[] s = new String[1];
			// 如果从数据库中取得数据则返回长度为1的字符串数组
			if (answer != null && !answer.equals("")) {
				s[0] = "答:" + answer;
				return s;
			} else {
				// 非业务问题直接提取答案
				if ((mattchingStr = SearchNonBusinessMatching.matching(args)) != null) {
					s[0] = "答:" + mattchingStr;
					return s;
				}
			}
		} catch (Exception e) {
			return Search_numFAQWithUrl(args, number);
		}
		return Search_numFAQWithUrl(args, number);
	}
	
	private String[] Search_numFAQWithUrl(String args, int number) {
		String[] reFlag = new String[number * 3];
		try {
			int count = 0;
			String xml = this.SearchFAQWithNum(args);
			org.jsoup.nodes.Document doc = Jsoup.parse(xml);
			Iterator iterator = doc.select("a").iterator();
			while (iterator.hasNext()) {
				Element em = (Element) iterator.next();
				if (count >= number * 3) {
					break;
				}
				String url = em.attr("href");
				String[] result = copeOneHtml(url);
				if (result == null || result.length == 0)
					continue;
				reFlag[count++] = url;
				reFlag[count++] = result[0];
				reFlag[count++] = result[1];
			}
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			writeLog.Write(e.getMessage(), SearchControl.class.getName());
			return null;
		}
		if (reFlag[1] == null)
			return null;
		else
			return reFlag;
	}
	
	private String[] search_numFAQ(String args, int number) {
		// 答案不足number条时会有问题，还需要修改
		String[] reFlag = new String[number * 2 + 1];
		int code = 101;// 101是确定回答，102是答案比较多的
		int maxAccurate = 4;// 当大于4个答案时认为不精准，即为5
		
		try {
			int count = 0;
			String xml = this.SearchFAQWithNum(args);
			org.jsoup.nodes.Document doc = Jsoup.parse(xml);
			Iterator iterator = doc.select("a").iterator();
			if (iterator.hasNext() == false) {
				reFlag = new String[1];
				reFlag[0] = "对不起，您的问题不完整，请重新具体描述！比如，“电子报税，点击填写增值税报表，提示：不存在当期的上期核定数据。怎么办？”";
				return reFlag;
			}
			while (iterator.hasNext()) {
				Element em = (Element) iterator.next();
				if (count >= number * 2) {
					code = 102;
					break;
				}
				String url = em.attr("href");
				String[] result = copeOneHtml(url);
				if (result == null || result.length == 0)
					continue;
				reFlag[count] = result[0];
				count++;
				reFlag[count++] = result[1];
			}
			int index = maxAccurate - number;
			while (index >= 0) {
				if (iterator.hasNext()) {
					index--;
					iterator.next();
				} else {
					code = 101;
					break;
				}
			}
			reFlag[number * 2] = String.valueOf(code);
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			writeLog.Write(e.getMessage(), SearchControl.class.getName());
			reFlag = new String[1];
			reFlag[0] = "对不起，您的问题不完整，请重新具体描述！比如，“电子报税，点击填写增值税报表，提示：不存在当期的上期核定数据。怎么办？”";
			return reFlag;
		}
		if (reFlag[1] == null)
			return null;
		else
			return reFlag;
	}

	

	
	private String SearchFAQWithNum(String args) {
		return SearchFAQWithType(args, "2");
	}

	public String[] search_num(String args, int number) {
		String mattchingStr = null;
		if ((mattchingStr = SearchPatternMatching.questionMatching(args)) != null) {
			args = mattchingStr;
		}
		try {
			DBPhraseManager dbManager = new DBPhraseManager();
			String answer = dbManager.getAnswer(args);
			String[] s = new String[1];
			// 如果从数据库中取得数据则返回长度为1的字符串数组
			if (answer != null && !answer.equals("")) {
				s[0] = "答:" + answer;
				return s;
			} else {
				// 非业务问题直接提取答案
				if ((mattchingStr = SearchNonBusinessMatching.matching(args)) != null) {
					s[0] = "答:" + mattchingStr;
					return s;
				}
			}
		} catch (Exception e) {
			return search_numFAQ(args, number);
		}
		return search_numFAQ(args, number);
	}

	private String[] copeOneHtml(String url) throws IOException {
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		String[] str = new String[2];
		try {
			String q = doc.getElementsByClass("q").get(0).html();
			String a = doc.getElementsByClass("a").get(0).html();
			str[0] = q;
			str[1] = a;
			return str;
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			writeLog.Write(e.getMessage(), SearchControl.class.getName());
			e.printStackTrace();
			return null;
		}
	}

	*//**
	 * 搜索（或搜索）
	 * 
	 * @param args
	 *            搜索的关键词或句子（查询请求）
	 * @return HTML文本
	 *//*
	public String SearchFAQ(String args) {
		return SearchFAQWithType(args, "3");
	}

	
	private String search(String args, IEnum.SearchConditionType cType) {
		args = args.toLowerCase();
		int taskSize = 3;
		// 新建线程池
		ExecutorService pool = Executors.newCachedThreadPool();
		// 存放带返回值的线程列表
		List<Future<List<SearchBean>>> list = new ArrayList<Future<List<SearchBean>>>();
		// 存放查询结果的结果集
		List<SearchBean> result = new ArrayList<SearchBean>();
		// 开启Common.searchDir.length个线程
		for (int i = 0; i < Common.searchDir.length; i++) {
			// 新建线程
			Callable<List<SearchBean>> c = new SearchThread(args,
					Common.searchDir[i], Configuration.maxTestShow,
					SearchThread.OR_SEARCH, cType.value());
			// 提交带返回值的线程给线程池
			Future<List<SearchBean>> f = pool.submit(c);
			list.add(f);
		}
		try {
			for (Future<List<SearchBean>> f : list) {
				// 阻塞方法，得到线程中的结果
				List<SearchBean> subList = f.get();
				// 讲该线程查询结果添加到结果集中
				for (SearchBean s : subList) {
					result.add(s);
				}
			}
			// 打分评测系统
			DecisionSystem system = new DecisionSystem(args);
			// 添加分割线
			String html = system.cutlineSortWithType(result, type);
			// 关闭线程池
			pool.shutdown();
			return html;
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			writeLog.Write(e.getMessage(), SearchControl.class.getName());
			return null;
		}
	}

	public String SearchFAQWithType(String args) {
		return SearchWithType(args, "3");
	}

	*//**
	 * 带搜索类型搜索
	 * 
	 * @param args
	 *            搜索的关键词或句子（查询请求）
	 * @param type
	 *            搜索类型：1为交互搜索；2为过滤搜索；3为普通搜索
	 *//*
	public String SearchWithType(String args, String type) {
		String mattchingStr = null;
		if ((mattchingStr = SearchPatternMatching.questionMatching(args)) != null) {
			args = mattchingStr;
		}
		try {
			// 搜索数据库
			DBPhraseManager dbManager = new DBPhraseManager();
			String answer = dbManager.getAnswer(args);
			if (answer != null && !answer.equals("")) {
				return "答:" + answer;
			} else {
				// 非业务问题直接提取答案
				if ((mattchingStr = SearchNonBusinessMatching.matching(args)) != null) {
					return "答:" + mattchingStr;
				}
				return SearchFAQWithType(args, type);
			}
		} catch (Exception e) {
			return SearchFAQWithType(args, type);
		}
	}

	*//**
	 * 搜索（和搜索）
	 * 
	 * @param args
	 *            搜索的关键词或句子
	 * @return HTML文本
	 *//*
	public String Search_and(String args) {
		args = args.toLowerCase();
		ExecutorService pool = Executors.newCachedThreadPool();
		List<Future<List<SearchBean>>> list = new ArrayList<Future<List<SearchBean>>>();
		List<SearchBean> result = new ArrayList<SearchBean>();

		for (int i = 0; i < Common.searchDir.length; i++) {
			Callable<List<SearchBean>> c = new SearchThread(args,
					Common.searchDir[i], Configuration.maxTestShow,
					SearchThread.AND_SEARCH);
			Future<List<SearchBean>> f = pool.submit(c);
			list.add(f);
		}
		try {
			for (Future<List<SearchBean>> f : list) {
				List<SearchBean> subList = f.get();
				for (SearchBean s : subList) {
					result.add(s);
				}
			}
			DecisionSystem system = new DecisionSystem(args);
			String html = system.cutlineSort(result);
			pool.shutdown();
			return html;
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			try {
				writeLog.Write(e.getMessage(), SearchControl.class.getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}

	*//**
	 * 列出所有的FAQ
	 * 
	 * @return HTML文本
	 *//*
	public String ListAllFaq() {
		StringBuilder html = new StringBuilder(
				"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><head><title>Search</title>");
		html.append("<style>em{font-style:normal;color:#cc0000}</style></head><body>");
		try {
			List<SearchBean> result = new AllSearch()
					.getResult(Common.searchDir[0]);
			for (SearchBean bean : result) {
				html.append("<div>" + bean.getContent() + "</div><br>");
			}
			return html.toString();
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			try {
				writeLog.Write(e.getMessage(), SearchControl.class.getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			html.append("<div>FAQ为空!</div>");
		}
		html.append("</body></html>");
		return html.toString();
	}

	*//**
	 * 【暂时无用】反馈
	 * 
	 * @param url
	 *            url地址
	 * @param count
	 *            评分
	 * @return
	 *//*
	public int FeedBack(String url, String count) {
		DBUtil dataBaseUtil = new DBUtil();
		DBFeedBackManager dataBaseOpeare = new DBFeedBackManager(
				dataBaseUtil.getConnection());
		int result = 0;
		if (url.indexOf(Common.faqFolder) > -1)
			result = dataBaseOpeare.modify(url, Common.faqFolder, count);
		else if (url.indexOf(Common.webFolder) > -1)
			result = dataBaseOpeare.modify(url, Common.webFolder, count);
		else
			result = dataBaseOpeare.modify(url, Common.learnFolder, count);
		dataBaseOpeare.close();
		dataBaseUtil.close();
		return result;
	}

	*//**
	 * 添加新的FAQ
	 * 
	 * @param question
	 *            问题租
	 * @param answer
	 *            答案组
	 * @return 1成功 小于1失败
	 *//*
	public int AddNewFAQ(String question[], String answer[]) {
		if (question.length == 0)
			return 0;
		FAQControl c = null;
		int num = 0;
		try {
			c = new FAQControl();
			if (!c.CreateIndexPath())
				return 0;
			// System.err.println(question.length + ":::" + answer.length + "");
			for (int i = 0; i < question.length; i++) {

				if (i < answer.length && answer[i] == null)
					continue;
				c.AddIndex(question[i].toLowerCase(), answer[i].toLowerCase());
				num++;
			}
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			try {
				writeLog.Write(e.getMessage(), SearchControl.class.getName()
						+ ":AddNewFAQ");
			} catch (Exception e1) {
			}
		} finally {
			if (c != null) {
				c.End();
			}
		}
		return 1;
	}

	*//**
	 * 【暂时无用，待修改】添加新的FAQ（带权重）
	 * 
	 * @param question
	 *            问题租
	 * @param answer
	 *            答案组
	 * @param boost
	 *            权重
	 * @return 1成功 小于1失败
	 *//*
	public int AddNewFAQWithBoost(String question[], String answer[],
			float boost) {
		if (question.length == 0)
			return 0;
		FAQControl c = new FAQControl();
		if (!c.CreateIndexPath())
			return 0;
		int num = 0;
		// System.err.println(question.length + ":::" + answer.length + "");
		for (int i = 0; i < question.length; i++) {

			if (i < answer.length && answer[i] == null)
				continue;
			c.AddIndex(question[i].toLowerCase(), answer[i].toLowerCase(),
					boost);
			num++;
		}
		c.End();
		return 1;
	}

	*//**
	 * 删除FAQ
	 * 
	 * @param url
	 *            url地址组
	 * @return 1成功 小于1失败
	 *//*
	public int deleteFAQ(String url[]) {
		if (url == null)
			return 0;
		int num = 0;
		FAQControl c = null;
		try {
			c = new FAQControl();
			if (!c.CreateIndexPath())
				return 0;
			for (int i = 0; i < url.length; i++) {
				if (url[i] == null)
					continue;
				c.deleteIndex(url[i]);
				num++;
			}
		} catch (Exception e) {
		} finally {
			if (c != null) {
				c.End();
			}
		}
		return num;
	}

	*//**
	 * 修改FAQ
	 * 
	 * @param url
	 *            url地址组
	 * @param q
	 *            问题组
	 * @param a
	 *            答案组
	 * @return 1成功 小于1失败
	 *//*
	public int ChangeFAQ(String url[], String q[], String a[]) {
		if (url == null)
			return 0;
		FAQControl c = null;
		int num = 0;
		try {
			c = new FAQControl();
			if (!c.CreateIndexPath())
				return 0;
			for (int i = 0; i < url.length; i++) {
				if (url[i] == null || q[i] == null || a[i] == null)
					continue;
				c.ModifyIndex(url[i], q[i].toLowerCase(), a[i].toLowerCase());
				num++;
			}
		} catch (Exception e) {
		} finally {
			if (c != null) {
				c.End();
			}
		}
		return num;
	}

	*//**
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
	 *//*
	public int ChangeFAQWithBoost(String url[], String q[], String a[],
			float boost) {
		if (url == null)
			return 0;
		FAQControl c = new FAQControl();
		if (!c.CreateIndexPath())
			return 0;
		int num = 0;
		for (int i = 0; i < url.length; i++) {
			if (url[i] == null || q[i] == null || a[i] == null)
				continue;
			c.ModifyIndex(url[i], q[i].toLowerCase(), a[i].toLowerCase(), boost);
			num++;
		}
		c.End();
		return num;
	}

	*//**
	 * 重建索引
	 * 
	 * @param index
	 *            传入0即可
	 * @return 1成功 小于1失败
	 *//*
	public int RebuildIndex(String index) {
		DBPhraseManager dbManager = new DBPhraseManager();
		String operation = "重建索引";
		dbManager.ModifyLog(operation, operation);
		synchronized (LOCK) {
			new MySynonymDictTool().createIndex();
			new SynonmIndexTool().createIndex();
			return new RefreshIndex(index).createIndex();
		}
	}

}
*/