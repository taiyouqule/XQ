package com.shenji.search.train;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
import com.shenji.search.Configuration;
import com.shenji.search.dic.CommonSynonymDic;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;
import com.shenji.search.services.FileService;
import com.shenji.search.services.OmniService;

public class FAQControl {

	private IndexWriter writer = null;
	private Analyzer analyzer = null;
	private SynonymEngine engine = null;

	public FAQControl() {
		Directory directory;
		try {
			directory = FSDirectory.open(new File(Configuration.indexPath + "/"
					+ Configuration.faqFolder));
			destroy(directory);
			System.out.println("unlock success!");
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public void destroy(Directory directory) throws Exception {
		// Directory directory = FSDirectory.open( new File(FAQIndexPath) );
		if (IndexWriter.isLocked(directory)) {
			IndexWriter.unlock(directory);
		}
	}

	public boolean createIndexPath() {
		try {
			engine = new CommonSynonymDic();
			Directory directory = FSDirectory.open(new File(
					Configuration.indexPath + "/" + Configuration.faqFolder));
			analyzer = new SynonymAnalyzer(engine);
			writer = new IndexWriter(directory, analyzer,
					IndexWriter.MaxFieldLength.UNLIMITED);
			writer.setMaxBufferedDocs(500);
			File localPath = new File(Configuration.notesPath + "/"
					+ Configuration.faqFolder);
			if (!localPath.exists())
				localPath.mkdirs();
			return true;
		} catch (EngineException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		return false;

	}

	public void addIndex(String q, String a) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		sb.append("<head><title>Search</title></head><body>");
		q = q.replace("\n", "<br>");
		sb.append("<div class=\"q\">" + q + "</div><br>");
		a = a.replace("\n", "<br>");
		sb.append("<div class=\"a\">" + a + "</div>");
		sb.append("</body></html>");
		FileService f = new FileService();
		String fileName = f.getStringMD5String(q + a);
		if (fileName == null)
			return;
		try {
			if (!f.write(sb.toString(), Configuration.notesPath + "/"
					+ Configuration.faqFolder + "/" + fileName + ".htm",
					"UTF-8"))
				return;
			Document doc = new Document();
			Field c = new Field("content", (q + a), Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(c);
			/* 9月23日添加，修改新建索引时的添加问题和答案 */
			Field q_field = new Field("question", q, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(q_field);
			Field a_field = new Field("answer", a, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(a_field);
			Field p = new Field("path", "faq/" + fileName + ".htm",
					Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.NO);
			doc.add(p);
			writer.addDocument(doc);
			// System.out.println(fileName);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return;
		}
		/*
		 * try{ //调用知识图谱增加FAQ String faqUrl = Common.webPath+Common.faqFolder +
		 * "/" + fileName +".htm"; FAQFilesManager faqFilesManager = new
		 * FAQFilesManager(); faqFilesManager.createFaqIndividual(faqUrl);
		 * }catch(Exception e){ WriteLog writeLog = new WriteLog(); try {
		 * writeLog.Write(e.getMessage(),"调用知识图谱增加FAQ异常"); } catch (Exception
		 * e1) { e1.printStackTrace(); } }
		 */
	}

	public void addIndex(String q, String a, float boost) {
		try {
			StringBuilder sb = new StringBuilder();
			// sb.append("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
			OmniService.setOmniBoost(sb, boost);
			sb.append("<head><title>Search</title></head><body>");
			// 3月28日修改，问题也可以换行
			q = q.replace("\n", "<br>");
			sb.append("<div class=\"q\">" + q + "</div><br>");
			a = a.replace("\n", "<br>");
			sb.append("<div class=\"a\">" + a + "</div>");
			sb.append("</body></html>");
			FileService f = new FileService();
			String fileName = f.getStringMD5String(q + a);
			if (fileName == null)
				return;
			if (!f.write(sb.toString(), Configuration.notesPath + "/"
					+ Configuration.faqFolder + "/" + fileName + ".htm",
					"UTF-8"))
				return;
			Document doc = new Document();
			Field c = new Field("content", (q + a), Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(c);
			Field q_field = new Field("question", q, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(q_field);
			Field a_field = new Field("answer", a, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(a_field);
			Field p = new Field("path", "faq/" + fileName + ".htm",
					Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.NO);
			doc.add(p);
			doc.setBoost(boost);
			writer.addDocument(doc);
			// System.out.println(fileName);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	
	/**
	 * 修改faq时，先删除原来的，再添加一个新的
	 * @param url
	 * @param q
	 * @param a
	 */
	public void modifyIndex(String url, String q, String a) {
		String fileName = url.replace(Configuration.faqwebPath + "/", "");
		String[] s = fileName.split("\\.");
		String filePath = Configuration.notesPath + "/"
				+ Configuration.faqFolder + "/" + fileName;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		// 删除
		try {
			File file = new File(filePath);
			if (file.exists()) {
				String oldFilePath = Configuration.notesPath + "/" + "oldFAQ/"
						+ fileName;
				File oldFile = new File(oldFilePath);
				if (oldFile.getParentFile() != null
						&& !oldFile.getParentFile().exists()) {
					oldFile.getParentFile().mkdirs();
				}
				// if(!oldFile.exists()){
				// oldFile.createNewFile();
				// }
				fis = new FileInputStream(file);
				fos = new FileOutputStream(oldFile);
				int n = fis.available();
				byte[] b = new byte[n];
				fis.read(b);
				fos.write(b);
				fis.close();
				fos.close();
				file.delete();
				writer.deleteDocuments(new Term("path", "faq/" + fileName));
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		// 增加
		StringBuilder sb = new StringBuilder();
		sb.append("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		sb.append("<head><title>Search</title></head><body>");
		q = q.replace("\n", "<br>");
		sb.append("<div class=\"q\">" + q + "</div><br>");
		a = a.replace("\n", "<br>");
		sb.append("<div class=\"a\">" + a + "</div>");
		sb.append("</body></html>");
		FileService f = new FileService();
		fileName = f.getStringMD5String(q + a);
		if (fileName == null)
			return;
		try {
			if (!f.write(sb.toString(), Configuration.notesPath + "/"
					+ Configuration.faqFolder + "/" + fileName + ".htm",
					"UTF-8"))
				return;
			Document doc = new Document();
			Field c = new Field("content", (q + a), Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(c);
			/* 9月23日添加，修改新建索引时的添加问题和答案 */
			Field q_field = new Field("question", q, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(q_field);
			Field a_field = new Field("answer", a, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(a_field);
			Field p = new Field("path", "faq/" + fileName + ".htm",
					Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.NO);
			doc.add(p);
			writer.addDocument(doc);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}

		/*
		 * if(s!=null&&s.length!=0){ try{ //调用知识图谱修改FAQ FAQFilesManager
		 * faqFilesManager = new FAQFilesManager();
		 * faqFilesManager.reNameFaqIndividual(s[0],fileName); }catch(Exception
		 * e){ WriteLog writeLog = new WriteLog(); try {
		 * writeLog.Write(e.getMessage(),"调用知识图谱修改FAQ异常"); } catch (Exception
		 * e1) { e1.printStackTrace(); } } }
		 */
	}

	public void modifyIndex(String url, String q, String a, float boost) {
		String fileName = url.replace(Configuration.faqwebPath + "/", "");
		String filePath = Configuration.notesPath + "/"
				+ Configuration.faqFolder + "/" + fileName;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				String oldFilePath = Configuration.notesPath + "/" + "oldFAQ/"
						+ fileName;
				File oldFile = new File(oldFilePath);
				if (oldFile.getParentFile() != null
						&& !oldFile.getParentFile().exists()) {
					oldFile.getParentFile().mkdirs();
				}
				fis = new FileInputStream(file);
				fos = new FileOutputStream(oldFile);
				int n = fis.available();
				byte[] b = new byte[n];
				fis.read(b);
				fos.write(b);
				fis.close();
				fos.close();
				file.delete();
				writer.deleteDocuments(new Term("path", "faq/" + fileName));
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		addIndex(q, a, boost);
	}

	/**
	 *先将源文件保存，再删除原来的faq，同时删除索引
	 * @param url
	 */
	public void deleteIndex(String url) {
		String fileName = url.replace(Configuration.faqwebPath + "/", "");
		String filePath = Configuration.notesPath + "/"
				+ Configuration.faqFolder + "/" + fileName;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				String oldFilePath = Configuration.notesPath + "/" + "oldFAQ/"
						+ fileName;
				File oldFile = new File(oldFilePath);
				if (oldFile.getParentFile() != null
						&& !oldFile.getParentFile().exists()) {
					oldFile.getParentFile().mkdirs();
				}
				fis = new FileInputStream(file);
				fos = new FileOutputStream(oldFile);
				int n = fis.available();
				byte[] b = new byte[n];
				fis.read(b);
				fos.write(b);
				fis.close();
				fos.close();
				file.delete();
				// Term term=new Term("content", q);
				writer.deleteDocuments(new Term("path", "faq/" + fileName));
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		/*
		 * try{ //调用知识图谱删除FAQ文件 String faqId = fileName.split(".")[0];
		 * FAQFilesManager faqFilesManager = new FAQFilesManager();
		 * faqFilesManager.deleteFaqIndividual(faqId); }catch(Exception e){
		 * WriteLog writeLog = new WriteLog(); try {
		 * writeLog.Write(e.getMessage(),"调用知识图谱删除FAQ文件异常"); } catch (Exception
		 * e1) { e1.printStackTrace(); } }
		 */
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
