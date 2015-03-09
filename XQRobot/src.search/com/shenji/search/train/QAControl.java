package com.shenji.search.train;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.SynonymAnalyzer;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.search.Configuration;
import com.shenji.search.bean.XmlDialogueBean;
import com.shenji.search.dic.CommonSynonymDic;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;
import com.shenji.search.services.FileService;
import com.shenji.search.services.HtmlService;
import com.shenji.search.services.OmniService;
import com.shenji.search.services.XmlUseTool;

public class QAControl {
	private SynonymEngine engine = null;
	private IndexWriter writer = null;
	private Analyzer analyzer;
	private static String servant = "客服人员";
	private static String custom = "客户";

	private void initWriter() {
		try {
			engine = new CommonSynonymDic();
			analyzer = new SynonymAnalyzer(engine);
			Directory directory;
			directory = FSDirectory.open(new File(Configuration.searchDir[1]));
			writer = new IndexWriter(directory, analyzer,
					IndexWriter.MaxFieldLength.UNLIMITED);
			writer.setMaxBufferedDocs(500);
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	private void addIndex(String context, String html, String sessionid) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		String path = Configuration.notesPath + "/" + Configuration.learnFolder
				+ "/" + year + "/" + month + "/" + day;
		try {
			File localPath = new File(path);
			if (!localPath.exists())
				localPath.mkdirs();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		FileService f = new FileService();
		// String fileName = jobNum+"+"+endTime;
		String fileName = f.getStringMD5String(sessionid);
		if (fileName == null)
			return;
		if (!f.write(html, path + "/" + fileName + ".htm", "UTF-8"))
			return;
		try {
			Document doc = new Document();
			Field c = new Field("content", context, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(c);
			Field p = new Field("path", (path.replace(Configuration.notesPath
					+ "/", "")).replace('\\', '/')
					+ "/" + fileName + ".htm", Field.Store.YES,
					Field.Index.NOT_ANALYZED, TermVector.NO);
			doc.add(p);
			String[] score = HtmlService.getAllScore(path + "/" + fileName
					+ ".htm");
			float boost = OmniService.getOmniBoost(score);
			doc.setBoost(boost);
			// System.err.println(doc.getBoost());
			writer.addDocument(doc);

		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (engine != null)
				engine.close();
		}
	}

	/**
	 * 修改索引
	 * 
	 * @param context
	 * @param jobNum
	 * @param endTime
	 * @return -1文件不存在 1修改成功 -2修改失败
	 */
	public int modifyIndex(String context, String filePath, String sessionid,
			String html, boolean isAddScore) {
		if (filePath == null)
			return -1;
		String body = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				String path = (filePath.replace(Configuration.notesPath, ""))
						.replace('\\', '/');
				path = path.substring(1);
				if (isAddScore == false) {
					body = HtmlService.getBody(filePath);
					html = html.replace(body,
							"<body>" + context.replace("\n", "<br>")
									+ "</body>");
					file.delete();
					this.writer.deleteDocuments(new Term("path", path));
					this.addIndex(context, html, sessionid);
				} else {
					file.delete();

					this.writer.deleteDocuments(new Term("path", path));
					this.addIndex(context, html, sessionid);
				}

			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -2;
		}
		return 1;
	}

	/**
	 * @param str
	 *            XML字符串
	 * @param sessionid
	 *            工号（唯一）
	 * @return 1添加成功
	 */
	public int addNewDialogue(String sessionid, String str) {
		int backFlag = 1;
		StringBuilder sb = new StringBuilder();
		String startTime = null;
		String endTime = null;
		String context = null;
		XmlDialogueBean dialogueBean = contextDeal(str);
		if (dialogueBean == null)
			context = str.replace("\n", "<br>");
		else {
			startTime = dialogueBean.getStartTime();
			endTime = dialogueBean.getEndTime();
			StringBuilder sbBuilder = new StringBuilder();
			for (XmlDialogueBean.RecordBean recordBean : dialogueBean
					.getRocords()) {
				String speaker = recordBean.getSpeaker();
				String content = recordBean.getContent();
				if (speaker.contains("user")) {
					speaker = servant;
				} else
					speaker = custom;
				sbBuilder.append("[" + speaker + "]" + " : " + content + "\n");
			}
			context = sbBuilder.toString().replace("\n", "<br>");
		}

		sb.append("<html>");
		String meta = createMeta(sessionid, startTime, endTime, -1, null,
				false, null);
		// 添加meta
		sb.append(meta);
		// OmniService.setOmniBoost(sb, weight);
		sb.append("<head><title>Search</title></head><body>");
		sb.append(context);
		sb.append("</body></html>");

		this.initWriter();
		this.addIndex(str, sb.toString(), sessionid);
		this.close();
		// System.out.println(sb.toString());
		return backFlag;
	}

	public XmlDialogueBean contextDeal(String str) {
		XmlDialogueBean bean = null;
		if (str.contains("<dialog>") && str.contains("</dialog>")
				&& (bean = XmlUseTool.Dom4jRead(str)) != null)
			return bean;
		else
			return null;

	}

	/**
	 * 追加评分
	 * 
	 * @param sessionid
	 * @param score
	 *            -1为删除该评分
	 * @param scorer
	 *            传入相同的scorer为覆盖原scorer的值
	 * @return 1添加成功 -1添加失败（文件不存在）-2添加失败（索引出现错误） -3添加失败（索引目录不存在）
	 */
	public int addScore(String sessionid, int score, String scorer) {
		FileService fileManager = new FileService();
		String rootPath = Configuration.notesPath + "/"
				+ Configuration.learnFolder;
		String fileName = fileManager.getStringMD5String(sessionid) + ".htm";
		String filePath = FileUse.find(rootPath, fileName);
		if (filePath == null)
			return -1;
		String oldHtml = FileUse.read(new File(filePath)).toString();
		String oldMeta = HtmlService.getMeta(filePath);
		String newHtml = null;
		String newMeta = null;
		this.initWriter();
		String context = HtmlService.getContextFromBody(filePath);
		String str = null;

		if ((str = HtmlService.getContentFromMeta(filePath, scorer, "score")) != null) {
			oldMeta = HtmlService.getMeta(filePath);
			oldMeta = deleteMetaItem(oldMeta, scorer, str);
			newMeta = createMeta(sessionid, null, null, score, scorer, true,
					oldMeta);
			newHtml = changeMeta(filePath, newMeta);
		} else {
			oldMeta = HtmlService.getMeta(filePath);
			newMeta = createMeta(sessionid, null, null, score, scorer, true,
					oldMeta);
			newHtml = changeMeta(filePath, newMeta);
		}
		if (score == -1) {
			oldMeta = HtmlService.getMeta(filePath);
			oldMeta = deleteMetaItem(oldMeta, scorer, str);
			newMeta = createMeta(sessionid, null, null, -1, null, true, oldMeta);
			newMeta = newMeta.replace("  ", " ");
			newMeta = newMeta.replace("   ", " ");
			newMeta = newMeta.replace("    ", " ");
			newMeta = newMeta.replace("     ", " ");
			newHtml = changeMeta(filePath, newMeta);
		}
		int b = modifyIndex(context, filePath, sessionid, newHtml, true);
		if (b != 1) {
			if (b == -1)
				return -3;
			else
				return b;
		}
		this.close();
		return 1;
	}

	private String deleteMetaItem(String meta, String scorer, String score) {
		String newMeta = null;
		newMeta = meta.replace("score=\"" + scorer + "\"", "");
		newMeta = newMeta.replace("content=\"" + score + "\"", "");
		newMeta = newMeta.replace("  ", " ");
		newMeta = newMeta.replace("   ", " ");
		return newMeta;
	}

	/*
	 * public int modifyDialogue(String context,int jobNum,int endTime){
	 * this.initWriter(); FileManager fileManager=new FileManager(); String
	 * rootPath = Common.notesPath+"\\"+Common.learnFolder; String fileName=
	 * fileManager.getStringMD5String(sessionid)+".htm"; String filePath =
	 * FileUse.find(rootPath, fileName); int result=modifyIndex(context, jobNum,
	 * endTime); this.end(); return result; }
	 */

	/**
	 * 创建meta标签
	 * 
	 * @param jobNum
	 *            工号
	 * @param endTime
	 *            会话结束时间
	 * @param cmScore
	 *            用户评分
	 * @param dtScore
	 *            管理人员评分
	 * @return meta标签
	 */
	private String createMeta(String sessionid, String startTime,
			String endTime, int score, String scorer, boolean isAppend,
			String oldMeta) {
		StringBuilder sb = new StringBuilder();
		if (isAppend == false) {
			sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" ");
			if (sessionid != null)
				sb.append("id=\"sessionid\" content=\"" + sessionid + "\" ");
			if (startTime != null)
				sb.append("time=\"startTime\" content=\"" + startTime + "\" ");
			if (endTime != null)
				sb.append("time=\"endTime\" content=\"" + endTime + "\" ");
			sb.append(">");
		} else {
			oldMeta = oldMeta.replace(">", " ");
			sb.append(oldMeta);
			if (scorer != null)
				sb.append("score=\"" + scorer + "\" content=\"" + score + "\" ");
			sb.append(">");
		}
		return sb.toString();
	}

	/**
	 * 修改某个文档中的meta标签
	 * 
	 * @param jobNum
	 * @param endTime
	 * @param newMeta
	 * @return 新的HTML串
	 */
	private String changeMeta(String path, String newMeta) {
		String newHtml = HtmlService.changeHtmlMeta(path, newMeta);
		return newHtml;
	}

	public void close() {
		try {
			if (writer != null) {
				writer.optimize();
				writer.commit();
				writer.close();
			}
			if (engine != null)
				engine.close();
			if (analyzer != null)
				analyzer.close();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

}
