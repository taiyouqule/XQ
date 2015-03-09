package com.shenji.search.strategy;

import java.util.List;

import com.shenji.search.Parameters;
import com.shenji.search.bean.SearchBean;

public class DividingLineServer {
	// private List<String> matchList = new ArrayList<String>();
	// private Set<String> maxMatchSet = new TreeSet<String>();

	private static String more = "<a href=\"#\" onclick=\"display('hide')\"> <font size=\"5\"><center>更多/隐藏</center></font> </a>";
	private static String javascript = "<script type=\"text/javascript\" laguage=\"javascript\">"
			+ "function display(y){$(y).style.display=($(y).style.display==\"none\")?\"\":\"none\";}"
			+ " function $(s){return document.getElementById(s);} "
			+ " function scroll(){ alert(0); window.scrollTo(0,document.body.scrollHeight); alert(1);} "
			+
			// window.scrollTo(0,1000);
			// " document.getElementById('hide').onclick=function(){var cha=(document.body.scrollHeight-document.body.clientHeight)/2;scroll(0,cha.toFixed(0));}"+
			"</script> ";

	public DividingLineServer() {
	}

	/**
	 * 排序
	 * 
	 * @param list
	 * @return
	 */
	/*
	 * public List<SearchBean> customSort(List<SearchBean> list) {
	 * StrategeFactory factory = new StrategeFactory(StrategeFactory.COSTOM,
	 * list, matchList, maxMatchSet); return factory.getSortList(); }
	 */

	public static String simpleSort(List<? extends SearchBean> list) {
		int len = list.size() > Parameters.maxResult ? Parameters.maxResult
				: list.size();
		StringBuilder html = new StringBuilder(
				"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><head><title>Search</title>");
		html.append("<style>em{font-style:normal;color:#cc0000}</style></head><body>");
		if (len == 0)
			html.append("<div>无法找到答案！</div>");
		for (int j = 0; j < list.size(); j++) {
			SearchBean bean = list.get(j);
			html.append("<div>" + bean.getContent() + "</div><br>");
		}
		html.append("</body></html>");
		return html.toString();
	}

	public static String cutlineSort(List<? extends SearchBean> list) {
		int len = list.size() > Parameters.maxResult ? Parameters.maxResult
				: list.size();
		StringBuilder html = new StringBuilder(
				"<html>"
						+ javascript
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><head><title>Search</title>");
		html.append("<style>em{font-style:normal;color:#cc0000}</style></head><body>");
		int i = 0;
		int cutLineFlag = 0;
		// int cutLineCount = 0;
		double frontSimilarity = Parameters.cutLine[0];
		if (len == 0)
			html.append("<div>无法找到答案！</div>");
		// html.append(more);
		for (int j = 0; j < list.size(); j++) {
			SearchBean bean = list.get(j);
			if (j != 0)
				frontSimilarity = list.get(j - 1).getSimilarity();
			html.append("<div>" + bean.getContent() + "</div><br>");
			if ((cutLineFlag < Parameters.cutLine.length && bean
					.getSimilarity() >= Parameters.cutLine[cutLineFlag])
					|| bean.getSimilarity() > frontSimilarity) {
				// cutLineCount++;
			} else {
				{
					html.append("<div style=\"text-align:center; vertical-align:middle; line-height:24px\">以上相关度大于"
							+ Parameters.cutLine[cutLineFlag] + "</div>");
					html.append("<hr width=100% size=1 color=#00ffff style=\"border:1 dashed #00ffff\">");
					// cutLineCount=0;
				}
				cutLineFlag++;
			}
			if (j == (len - 1)
					&& cutLineFlag <= (Parameters.cutLine.length - 1)) {
				html.append("<div style=\"text-align:center; vertical-align:middle; line-height:24px\">以上相关度大于"
						+ Parameters.cutLine[cutLineFlag] + "</div>");
				html.append("<hr width=100% size=1 color=#00ffff style=\"border:1 dashed #00ffff\">");
			}
			if (cutLineFlag >= Parameters.cutLine.length)
				break;
			/*------------*/
			// System.out.println(bean.getScore());
			i++;
			if (i == len)
				break;
			if (j == Parameters.showResult) {
				html.append(more);
				html.append("<div id=\"hide\" style=\"display:none\">");
			}
		}
		if (list.size() >= Parameters.showResult) {
			html.append("</div>");
		}

		html.append("</body></html>");

		return html.toString();
	}

	/*
	 * private void initIkMatchResult(String args) { StringReader reader = new
	 * StringReader(args); IKSegmentation iks = new IKSegmentation(reader,
	 * false); Lexeme t; try { while ((t = iks.next()) != null) { String word =
	 * t.getLexemeText(); if (word.length() > 1) this.matchList.add(word); } }
	 * catch (IOException e) { WriteLog writeLog = new WriteLog(); try {
	 * writeLog.Write(e.getMessage(), DecisionSystem.class.getName()); } catch
	 * (Exception e1) { e1.printStackTrace(); } } StringReader reader_max = new
	 * StringReader(args); IKSegmentation iks_max = new
	 * IKSegmentation(reader_max, true); Lexeme t_max; try { while ((t_max =
	 * iks_max.next()) != null) { String word = t_max.getLexemeText(); if
	 * (word.length() > 1) { this.maxMatchSet.add(word); //
	 * System.err.println(word); } } } catch (IOException e) { WriteLog writeLog
	 * = new WriteLog(); try { writeLog.Write(e.getMessage(),
	 * DecisionSystem.class.getName()); } catch (Exception e1) {
	 * e1.printStackTrace(); } } }
	 */

}
