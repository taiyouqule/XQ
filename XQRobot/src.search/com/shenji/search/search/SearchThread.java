package com.shenji.search.search;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.shenji.search.FenciControl;
import com.shenji.search.IEnumSearch;
import com.shenji.search.bean.SearchBean;
import com.shenji.search.exception.SearchProcessException;

/**
 * 查询线程（带返回值）
 * 
 * @author sj
 * 
 */
public class SearchThread implements Callable<List<SearchBean>> {

	private String args;
	private String from;
	// 或查询 结果词集
	private Set<String> matchList = new LinkedHashSet<String>();
	// 和查询 结果词集（不能重复）
	private Set<String> maxMatchSet = new LinkedHashSet<String>();
	public IEnumSearch.SearchRelationType rType;
	
	private void initSet(){
		FenciControl control=new FenciControl();
		String matchStr=control.iKAnalysis(args);
		matchList.addAll(Arrays.asList(matchStr.split("/")));
		String maxStr=control.iKAnalysisMax(args);
		maxMatchSet.addAll(Arrays.asList(maxStr.split("/")));
	}
	/*private void IKAnalysis() {
		matchList.clear();
		maxMatchSet.clear();
		try {
			StringReader reader = new StringReader(args);
			// IK分词构造器
			IKSegmentation iks = new IKSegmentation(reader, false, args);
			Lexeme t;
			// 中英文混分词典
			String[] chEnDict = iks.getChEnDict();
			// IK分词
			while ((t = iks.next()) != null) {
				String word = t.getLexemeText();
				if (word.length() == 1) {
					Pattern pattern = Pattern.compile("[a-zA-Z]");
					Matcher m = pattern.matcher(word);
					if (!m.find()) {
						matchList.add(word);
					}
				} else {
					matchList.add(word);
				}
			}
			for (String s : chEnDict) {
				if (s.length() == 1) {
					Pattern pattern = Pattern.compile("[a-zA-Z]");
					Matcher m = pattern.matcher(s);
					if (!m.find()) {
						matchList.add(s);
					}
				} else {
					matchList.add(s);
				}
			}

			StringReader reader_max = new StringReader(args);
			// IK分词构造器
			IKSegmentation iks_max = new IKSegmentation(reader_max, true);
			Lexeme t_max;
			// IK分词
			while ((t_max = iks_max.next()) != null) {
				String word = t_max.getLexemeText();
				// if(word.length()>1){
				maxMatchSet.add(word);
				// System.err.println(word);
				// }
			}
			for (String s : chEnDict) {
				maxMatchSet.add(s);
			}

		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			writeLog.Write(e.getMessage(), SearchThread.class.getName());
		}
	}
*/
	/**
	 * 构造函数
	 * 
	 * @param args
	 *            查询请求
	 * @param from
	 *            来源
	 * @param maxTextShow
	 *            最大文本显示
	 * @param searchType
	 *            查询类型（并查询、或查询）
	 */
	public SearchThread(String args, String from,
			IEnumSearch.SearchRelationType rType) {
		this.args = args;
		this.from = from;
		this.rType = rType;
		// 中文分词
		//IKAnalysis();
		initSet();
	}

	public List<SearchBean> call() throws Exception {
		// 构建布尔查询对象并返回结果
		BooleanSearch booleanSearch = new BooleanSearch(args, matchList,
				maxMatchSet, rType);
		List<SearchBean> result = null;
		try {
			result = booleanSearch.getResult(args, from);
		} catch (SearchProcessException e) {
			// 这里发生严重错误，没有查询结果，需要子线程抛给主线程进行处理
			throw e;// 这里线程池会包装成ExecutionException异常
		}
		return result;

	}

}
