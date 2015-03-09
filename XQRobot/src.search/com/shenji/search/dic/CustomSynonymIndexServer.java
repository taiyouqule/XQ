package com.shenji.search.dic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.common.util.PathUtil;
import com.shenji.search.Configuration;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;

public class CustomSynonymIndexServer {
	private IndexWriter indexWriter = null;
	private Directory directory = null;
	// private SynonymEngine engine = null;
	private Analyzer analyzer = null;

	public CustomSynonymIndexServer() {
		File file = new File(Configuration.indexPath + "/"
				+ Configuration.mySynFolder);
		try {
			init(file);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		}
	}

	public void close() {
		try {
			if (indexWriter != null) {
				indexWriter.close();
			}
			destroy(directory);
			if (directory != null)
				directory.close();
			/*
			 * if (engine != null) engine.close();
			 */
			if (analyzer != null)
				analyzer.close();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		}
	}

	public void init(File file) throws EngineException {
		try {
			analyzer = new IKAnalyzer();
			this.directory = FSDirectory.open(file);
			indexWriter = new IndexWriter(directory, analyzer,
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new EngineException(
					"CustomSynony indexWriter open Exception!", e);
		}
		/*
		 * try { engine = new CustomSynonymDic(); } catch (EngineException e) {
		 * // TODO Auto-generated catch block throw e; }
		 */
	}

	public void destroy(Directory directory) throws Exception {
		// Directory directory = FSDirectory.open( new File(FAQIndexPath) );
		if (IndexWriter.isLocked(directory)) {
			IndexWriter.unlock(directory);
		}
	}

	public boolean createIndex() {
		String orcName = "word";
		String synonmName = "syn";
		// Analyzer analyzer = new IKAnalyzer();

		try {
			List<String[]> list_mainDict = readSynonmDict(Configuration.mySynonmDict);
			List<String[]> list = list_mainDict;
			for (int i = 0; i < list.size(); i++) {
				Document document = new Document();
				document.add(new Field(orcName, list.get(i)[0].toLowerCase(),
						Field.Store.YES, Field.Index.NOT_ANALYZED));
				for (int j = 0; j < list.get(i).length; j++) {
					document.add(new Field(synonmName, list.get(i)[j]
							.toLowerCase(), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
				}
				indexWriter.addDocument(document);
			}
			indexWriter.optimize();

		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close();
				}
				destroy(directory);
				if (directory != null)
					directory.close();
			} catch (Exception e) {
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
			}
		}
		return true;
	}

	public String getMySynonmWordsFromIndex(String word) throws EngineException {
		// List<String> list = new ArrayList<String>();
		String result = null;
		String[] strings = null;
		SynonymEngine engine = null;
		try {
			engine = new CustomSynonymDic();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		try {
			strings = engine.getSynonyms(word);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			if (engine != null)
				engine.close();
		}

		for (int i = 0; i < strings.length; i++) {
			if (i == 0) {
				result = strings[0];
				continue;
			}
			result = result + "/" + strings[i];
		}
		return result;
	}

	private List<String[]> readSynonmDict(String fileName) {
		List<String[]> list = new ArrayList<String[]>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		String path;
		try {
			path = PathUtil.getWebInFAbsolutePath();
			File file = new File(path + fileName);
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				String[] strings = str.split(" ");
				// System.err.println(strings[0]);
				list.add(strings);
			}
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (inputStreamReader != null)
					inputStreamReader.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
			}
		}
		return list;

	}

}
