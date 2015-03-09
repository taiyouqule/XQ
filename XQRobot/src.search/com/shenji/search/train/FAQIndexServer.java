package com.shenji.search.train;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.wltea.analyzer.lucene.SynonymAnalyzer;

import com.shenji.common.log.Log;
import com.shenji.search.Configuration;
import com.shenji.search.dic.CommonSynonymDic;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;
import com.shenji.search.services.FileService;

public class FAQIndexServer {

	private Document doc = null;
	private IndexWriter indexWriter = null;
	private int[] index = null;

	public FAQIndexServer(String index) {
		String[] in = index.split(";");
		this.index = new int[in.length];
		for (int i = 0; i < in.length; i++) {
			this.index[i] = Integer.parseInt(in[i]);
		}
	}

	public void close() {
		if (indexWriter != null)
			try {
				indexWriter.close();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
	}

	private String delHTMLTag(String htmlStr) {

		htmlStr = htmlStr.replaceAll(".*?<body.*?>(.*?)", "$1"); // match body
		htmlStr = htmlStr.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove
																			// javascript
		htmlStr = htmlStr.replaceAll("(?is)<style.*?>.*?</style>", ""); // remove
																		// css
		htmlStr = htmlStr.replaceAll("(?is)<.*?>", ""); // remove all

		return htmlStr.trim(); // �����ı��ַ�
	}

	private String getQuestion(String htmlStr) {
		org.jsoup.nodes.Document document = null;
		document = Jsoup.parse(htmlStr);
		Elements meta = document.select(".q");
		// System.out.println(meta.text());
		return meta.text();
	}

	private String getAnswer(String htmlStr) {
		org.jsoup.nodes.Document document = null;
		document = Jsoup.parse(htmlStr);
		Elements meta = document.select(".a");
		// System.out.println(meta.text());
		return meta.text();
	}

	private void dealDir(File dir) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory())
				dealDir(files[i]);
			else
				dealFile(files[i]);
		}
	}

	private String getName(File file) {
		try {
			String path = file.getCanonicalPath();
			path = path.replace("\\", "/");
			path = path.replace(Configuration.notesPath, "");
			path = path.substring(1, path.length());
			return path.replace('\\', '/');
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
	}

	private void dealFile(File file) {
		FileService f = new FileService();
		try {
			String html = f.read(file.getCanonicalPath(), "utf-8");

			String content = delHTMLTag(html);
			doc = new Document();
			Field c = new Field("content", content, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(c);

			String question = getQuestion(html);
			Field q = new Field("question", question, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(q);

			String answer = getAnswer(html);
			Field a = new Field("answer", answer, Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO);
			doc.add(a);

			Field p = new Field("path", getName(file), Field.Store.YES,
					Field.Index.NOT_ANALYZED);
			//System.err.println(getName(file));
			Log.getLogger(this.getClass()).debug(getName(file));
			doc.add(p);
			indexWriter.addDocument(doc);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	private void deleteDir(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
				return;
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteDir(files[i]);
				}
				file.delete();
			}
		} else {
			System.out.println("文件不存在" + '\n');
		}
	}

	public boolean createIndex() {
		Directory directory = null;
		String[] searchDir = {
				Configuration.indexPath + "/" + Configuration.faqFolder,
				Configuration.indexPath + "/" + Configuration.learnFolder };
		File[] htmlDir = {
				new File(Configuration.notesPath + "/"
						+ Configuration.faqFolder),
				new File(Configuration.notesPath + "/"
						+ Configuration.learnFolder) };
		File indexFile = null;
		Analyzer analyzer = null;
		int maxBufferedDocs = 500;
		SynonymEngine engine = null;
		try {
			engine = new CommonSynonymDic();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return false;
		}
		try {
			for (int i = 0; i < index.length; i++) {

				indexFile = new File(searchDir[index[i]]);
				if (indexFile.exists()) {
					deleteDir(indexFile);
				}
				indexFile.mkdir();
				directory = FSDirectory.open(indexFile);
				// analyzer = new IKAnalyzer();
				analyzer = new SynonymAnalyzer(engine);
				indexWriter = new IndexWriter(directory, analyzer, true,
						IndexWriter.MaxFieldLength.UNLIMITED);
				indexWriter.setMaxBufferedDocs(maxBufferedDocs);
				dealDir(htmlDir[i]);
				indexWriter.optimize();
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (engine != null)
				engine.close();
			if (analyzer != null)
				analyzer.close();
		}
		Log.getLogger(this.getClass()).info("FAQ库重建索引完成！");
		return true;
	}

}
