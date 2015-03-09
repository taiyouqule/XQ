package com.shenji.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shenji.common.data.ResultShowBean;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.exception.OntoReasonerException;
import com.shenji.common.inter.IComReasonerServer;
import com.shenji.common.log.Log;
import com.shenji.search.IEnumSearch.ResultCode;
import com.shenji.search.bean.SearchBean;
import com.shenji.search.database.DBPhraseManager;
import com.shenji.search.exception.EngineException;
import com.shenji.search.exception.SearchProcessException;
import com.shenji.search.search.SearchJsonThread;
import com.shenji.search.search.SearchThread;
import com.shenji.search.strategy.DividingLineServer;
import com.shenji.search.strategy.MaxAndMyDictSimilarity;
import com.shenji.search.strategy.ScoreComparator;
import com.shenji.search.strategy.ScoreComparatorJson;
import com.shenji.search.strategy.SearchNonBusinessMatching;
import com.shenji.search.strategy.SearchPatternMatching;
import com.shenji.search.strategy.SimilarityComparator;
import com.shenji.web.bean.OneResultBean;

public class SearchControl {
	private boolean pretreatmentResult = false;

	public SearchControl() {
	}

	private ExecutorService getExcutorService(int nThreads) {
		// 相当于定义一个newFixedThreadPool线程池
		ExecutorService pool = new ThreadPoolExecutor(nThreads, nThreads, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()) {
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);// 空方法,这里不做了,异常处理做过了
			}
		};
		return pool;
	}

	private List<SearchBean> search(String args,
			IEnumSearch.SearchRelationType rType) throws SearchProcessException {
		// 新建线程池
		// ExecutorService pool =
		// Executors.newFixedThreadPool(Common.searchDir.length);//重写线程池异常处理
		ExecutorService pool = this
				.getExcutorService(Configuration.searchDir.length);
		// 存放带返回值的线程列表
		List<Future<List<SearchBean>>> list = new ArrayList<Future<List<SearchBean>>>();
		// 存放查询结果的结果集
		List<SearchBean> result = new ArrayList<SearchBean>();
		// 开启Common.searchDir.length个线程
		try {
			for (int i = 0; i < Configuration.searchDir.length; i++) {
				// 新建线程
				Callable<List<SearchBean>> c = new SearchThread(args,
						Configuration.searchDir[i], rType);

				// 提交带返回值的线程给线程池
				Future<List<SearchBean>> f = pool.submit(c);
				list.add(f);
			}
			for (Future<List<SearchBean>> f : list) {
				// 阻塞方法，得到线程中的结果
				List<SearchBean> subList = f.get();
				// 普通打分排序
				if (subList != null && subList.size() > 0) {
					Collections
							.sort(subList, new ScoreComparator<SearchBean>());
					result.addAll(subList);
				}
				// 讲该线程查询结果添加到结果集中

			}
			// 关闭线程池
			pool.shutdown();
			return result;
		} catch (ExecutionException e) {
			// 判断ExecutionException包装的异常是否为自定义异常
			if (e.getCause() instanceof SearchProcessException) {// 自定义的异常
				throw ((SearchProcessException) e.getCause());
			} else {// 其他可能出现的异常
				throw new SearchProcessException("Unknow Error in Search!",
						e.getCause(),
						SearchProcessException.ErrorCode.UnKnowError);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new SearchProcessException(
					"Unknow Error in Search by interRuptedException!",
					e.getCause(), SearchProcessException.ErrorCode.UnKnowError);
		}
		// return result;
	}

	public String searchBasic(String args, IEnumSearch.SearchRelationType rType)
			throws SearchProcessException {
		List<SearchBean> beans = search(args, rType);
		// 转化为普通HTML文档
		String html = DividingLineServer.simpleSort(beans);
		return html;
	}

	private String aftertreatment(String args,
			List<? extends SearchBean> beans, Comparator comparator) {
		MaxAndMyDictSimilarity maxAndMyDictSimilarity = null;
		try {
			maxAndMyDictSimilarity = new MaxAndMyDictSimilarity(args);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return DividingLineServer.cutlineSort(beans);
		}
		// 设置相似度
		try {
			maxAndMyDictSimilarity.setSimilarity(beans);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		// 自定义排序
		maxAndMyDictSimilarity.sort(comparator, beans);
		// 添加分割线
		String html = DividingLineServer.cutlineSort(beans);
		return html;
	}

	private String pretreatment(String args) {
		String mattchingStr = null;
		// 大小写转换
		args = args.toLowerCase();
		// 模式匹配问句
		if ((mattchingStr = SearchPatternMatching.questionMatching(args)) != null) {
			args = mattchingStr;
		}
		// 搜索数据库
		DBPhraseManager dbManager = new DBPhraseManager();
		String answer = null;
		try {
			answer = dbManager.getAnswer(args);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		if (answer != null && answer.length() > 0) {
			pretreatmentResult = true;
			return "答:" + answer;
		} else {
			// 非业务问题直接提取答案
			try {
				if ((mattchingStr = SearchNonBusinessMatching.matching(args)) != null) {
					pretreatmentResult = true;
					return "答:" + mattchingStr;
				}
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				// 这里有问题，不应该抛到这层
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return args;

	}

	public String searchOrdinary(String args,
			IEnumSearch.SearchRelationType rType) throws SearchProcessException {
		// 预处理
		String str = pretreatment(args);
		if (pretreatmentResult)
			return str;
		else {
			List<SearchBean> beans = search(args, rType);
			return aftertreatment(args, beans,
					new SimilarityComparator<SearchBean>());
		}

	}

	public String searchFilterByOnto(String args,
			IEnumSearch.SearchRelationType rType,
			IComReasonerServer reasonerServer,
			Comparator<? extends SearchBean> comparator)
			throws SearchProcessException, OntoReasonerException {
		// 预处理
		String str = pretreatment(args);
		if (pretreatmentResult)
			return str;
		else {
			List<SearchBean> beans = search(args, rType);
			// 图谱过滤
			List<? extends SearchBean> exBeans = reasonerServer
					.reasoning(new Object[] { args, beans });
			return aftertreatment(args, exBeans, comparator);
		}
	}

	public ResultShowBean searchBasicNum(String args, int number,
			IEnumSearch.SearchRelationType rType) throws SearchProcessException {
		String html = this.searchBasic(args, rType);
		return this.convertHtmlToBean(html, number);
	}

	public ResultShowBean searchOrdinaryNum(String args, int number,
			IEnumSearch.SearchRelationType rType) throws SearchProcessException {
		String html = this.searchOrdinary(args, rType);
		return this.convertHtmlToBean(html, number);
	}

	public ResultShowBean searchFilterByOntoNum(String args, int number,
			IEnumSearch.SearchRelationType rType,
			IComReasonerServer reasonerServer,
			Comparator<? extends SearchBean> comparator)
			throws SearchProcessException, OntoReasonerException {
		String html = this.searchFilterByOnto(args, rType, reasonerServer,
				comparator);
		return this.convertHtmlToBean(html, number);
	}

	private ResultShowBean convertHtmlToBean(String html, int number)
			throws SearchProcessException {
		List<String> reList = new ArrayList<String>();
		IEnumSearch.ResultCode code = null;
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Iterator<Element> iterator = doc.select("a").iterator();
		if (!iterator.hasNext()) {
			// 没有查询结果
			throw new SearchProcessException("No search Result",
					SearchProcessException.ErrorCode.NoSearchResult);
		}
		try {
			int count = 0;
			while (iterator.hasNext()) {
				Element em = (Element) iterator.next();
				if (count >= number) {
					code = ResultCode.NunExact;
					break;
				}
				String url = em.attr("href");
				String[] result = null;
				try {
					result = copeOneHtml(url);
				} catch (IOException e) {
					// TODO: handle exception
					// [NeedToDo]这里不合理
					continue;
				}
				if (result == null || result.length == 0)
					continue;
				reList.add(result[0]);
				reList.add(result[1]);
				count++;
			}
			int index = Parameters.maxAccurat - number;
			while (index >= 0) {
				if (iterator.hasNext()) {
					index--;
					iterator.next();
				} else {
					code = ResultCode.Exact;
					break;
				}
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);

		}
		ResultShowBean resultShowBean = new ResultShowBean(code, reList);
		return resultShowBean;
	}

	private String[] copeOneHtml(String url) throws IOException {
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		String[] str = new String[2];
		String q = doc.getElementsByClass("q").get(0).html();
		String a = doc.getElementsByClass("a").get(0).html();
		str[0] = q;
		str[1] = a;
		return str;
	}

	public List<OneResultBean> searchBasicJson(String args,
			IEnumSearch.SearchRelationType rType) throws SearchProcessException {
		List<OneResultBean> beans = searchJson(args, rType);
		return beans;
	}
	
	
	private List<OneResultBean> searchJson(String args,
			IEnumSearch.SearchRelationType rType) throws SearchProcessException {
		// 新建线程池
		// ExecutorService pool =
		// Executors.newFixedThreadPool(Common.searchDir.length);//重写线程池异常处理
		ExecutorService pool = this
				.getExcutorService(Configuration.searchDir.length);
		// 存放带返回值的线程列表
		List<Future<List<OneResultBean>>> list = new ArrayList<Future<List<OneResultBean>>>();
		// 存放查询结果的结果集
		List<OneResultBean> result = new ArrayList<OneResultBean>();
		// 开启Common.searchDir.length个线程
		try {
			for (int i = 0; i < Configuration.searchDir.length; i++) {
				// 新建线程
				Callable<List<OneResultBean>> c = new SearchJsonThread(args,
						Configuration.searchDir[i], rType);

				// 提交带返回值的线程给线程池
				Future<List<OneResultBean>> f = pool.submit(c);
				list.add(f);
			}
			for (Future<List<OneResultBean>> f : list) {
				// 阻塞方法，得到线程中的结果
				List<OneResultBean> subList = f.get();
				// 普通打分排序
				if (subList != null && subList.size() > 0) {
					Collections.sort(subList,
							new ScoreComparatorJson<OneResultBean>());
					result.addAll(subList);
				}
				// 讲该线程查询结果添加到结果集中

			}
			// 关闭线程池
			pool.shutdown();
			return result;
		} catch (ExecutionException e) {
			// 判断ExecutionException包装的异常是否为自定义异常
			if (e.getCause() instanceof SearchProcessException) {// 自定义的异常
				throw ((SearchProcessException) e.getCause());
			} else {// 其他可能出现的异常
				throw new SearchProcessException("Unknow Error in Search!",
						e.getCause(),
						SearchProcessException.ErrorCode.UnKnowError);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new SearchProcessException(
					"Unknow Error in Search by interRuptedException!",
					e.getCause(), SearchProcessException.ErrorCode.UnKnowError);
		}
		// return result;
	}
}