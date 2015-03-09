package com.shenji.search.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKSimilarity;

import com.shenji.common.log.Log;
import com.shenji.search.Configuration;
import com.shenji.search.bean.SearchBean;
import com.shenji.search.database.DBFeedBackManager;

public class DefaultSearch {
	private DBFeedBackManager helper = null;
	private Searcher searcher = null;
	private int maxTextShow = 200;
	private List<String> matchList = null;

	public DefaultSearch(List<String> matchList, int maxTextShow) {
		this.matchList = matchList;
		this.maxTextShow = maxTextShow;
		// helper = new MySqlHelper();
		// helper.OpenDatabase();
	}

	private String markContent(String str) {
		for (String s : matchList) {
			str = str.replace(s, "<em>" + s + "</em>");
		}
		return str;
	}

	private int getScore(String url) {
		String which;
		if (url.indexOf(Configuration.faqFolder) > -1)
			which = Configuration.faqFolder;
		else if (url.indexOf(Configuration.webFolder) > -1)
			which = Configuration.webFolder;
		else {
			which = Configuration.learnFolder;
		}
		String sql = "select * from " + which + " where A= '" + url + "'; ";
		return 0;
		/*
		 * try { return helper.SearchInt(sql); }catch (Exception e){
		 * e.printStackTrace(); return 0; }
		 */
	}

	/**
	 * ��ȡ��ݿ����
	 * 
	 * @return ResultSet
	 * @throws Exception
	 */
	public List<SearchBean> getResult(String queryStr, String from)
			throws Exception {
		try {
			TopDocs topDocs = search(queryStr, from);
			if (topDocs == null)
				return new ArrayList<SearchBean>();
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			List<SearchBean> result = addHits2List(scoreDocs);
			if (searcher != null)
				searcher.close();
			/*
			 * if(helper!=null) helper.CloseMySql();
			 */
			return result;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (searcher != null)
				searcher.close();
		}
		return new ArrayList<SearchBean>();
	}

	/**
	 * ��������
	 * 
	 * @param queryStr
	 * @return
	 * @throws Exception
	 */
	private TopDocs search(String queryStr, String from) throws Exception {
		if (searcher == null) {
			File file = new File(from);
			if (file.isDirectory() && file.listFiles().length == 0)
				return null;
			searcher = new IndexSearcher(FSDirectory.open(file));
		}
		searcher.setSimilarity(new IKSimilarity());
		QueryParser parser = new QueryParser(Version.LUCENE_30, "content",
				new IKAnalyzer());
		Query query = parser.parse(queryStr);

		TopDocs topDocs = searcher.search(query, searcher.maxDoc());
		return topDocs;
	}

	/**
	 * ���ؽ����ӵ�List��
	 * 
	 * @param scoreDocs
	 * @return
	 * @throws Exception
	 */
	private List<SearchBean> addHits2List(ScoreDoc[] scoreDocs)
			throws Exception {
		List<SearchBean> listBean = new ArrayList<SearchBean>();
		SearchBean bean = null;
		for (int i = 0; i < scoreDocs.length; i++) {
			int docId = scoreDocs[i].doc;
			Document doc = searcher.doc(docId);
			bean = new SearchBean();
			String content = doc.get("content");
			content = content.substring(
					0,
					content.length() > maxTextShow ? maxTextShow : content
							.length());
			content = markContent(content);
			String path = doc.get("path");
			path = Configuration.webPath + "/" + path;
			bean.setContent("<a href=\"" + path + "\">" + content + "</a>");
			bean.setScore(getScore(path));
			listBean.add(bean);
		}
		return listBean;
	}
}
