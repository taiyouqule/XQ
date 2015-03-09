package com.shenji.search.dic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.common.util.PathUtil;
import com.shenji.search.Configuration;
import com.shenji.search.bean.SynonmBean;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;

public class CommonSynonymIndexServer {
	// 这两个是哈工大同义词库进行处理的远文件
	private String hgdSynonmDict = "synonm.txt";
	private String codeSynonmDict = "synonm_new.txt";
	private IndexWriter indexWriter = null;
	private Directory directory;
	//private SynonymEngine engine;
	private Analyzer analyzer;

	public CommonSynonymIndexServer() {
		File file = new File(Configuration.indexPath + "/"
				+ Configuration.synFolder);
		try {
			init(file);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
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
		/*try {
			engine = new CustomSynonymDic();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw e;
		}*/
	}

	public void close() {
		try {
			if (indexWriter != null) {
				indexWriter.close();
			}
			destroy(directory);
			if (directory != null)
				directory.close();
		/*	if (engine != null)
				engine.close();*/
			if (analyzer != null)
				analyzer.close();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public void destroy(Directory directory) throws Exception {
		if (IndexWriter.isLocked(directory)) {
			IndexWriter.unlock(directory);
		}
	}

	

	/*
	 * synonmWords:新增的同义词 newSynonWords:新增之后全部的同义词(包括旧的同义词)
	 */
	public boolean addIndex(String word, String[] synonmWords,
			String[] newSynonWords) throws EngineException {
		if (synonmWords == null || synonmWords.length == 0)
			return true;
		String[][] words = new String[synonmWords.length][];
		SynonymEngine engine=null;
		try {
			engine = new CustomSynonymDic();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		for (int i = 0; i < synonmWords.length; i++) {
			String[] s = null;
			try {
				s = engine.getSynonyms(synonmWords[i]);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				continue;
			}
			if (s != null) {
				words[i] = new String[s.length];
				for (int j = 0; j < s.length; j++)
					words[i][j] = s[j];
			} else {
				words[i] = new String[0];
			}
		}
		if(engine!=null)
			engine.close();
		try {
			indexWriter.deleteDocuments(new Term(SynonymEngine.WORD, word));
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		try {
			for (int k = 0; k < synonmWords.length; k++) {
				indexWriter.deleteDocuments(new Term(SynonymEngine.WORD,
						synonmWords[k]));
				Document doc1 = new Document();
				doc1.add(new Field(SynonymEngine.WORD, synonmWords[k]
						.toLowerCase(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc1.add(new Field(SynonymEngine.SYNONM, synonmWords[k]
						.toLowerCase(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				for (String syn : words[k]) {
					if (!word.equalsIgnoreCase(syn))
						doc1.add(new Field(SynonymEngine.SYNONM, syn
								.toLowerCase(), Field.Store.YES,
								Field.Index.NOT_ANALYZED));
				}
				doc1.add(new Field(SynonymEngine.SYNONM, word.toLowerCase(),
						Field.Store.YES, Field.Index.NOT_ANALYZED));
				indexWriter.addDocument(doc1);
			}
			Document doc2 = new Document();
			doc2.add(new Field(SynonymEngine.WORD, word.toLowerCase(),
					Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc2.add(new Field(SynonymEngine.SYNONM, word.toLowerCase(),
					Field.Store.YES, Field.Index.NOT_ANALYZED));
			for (String syn : newSynonWords) {
				doc2.add(new Field(SynonymEngine.SYNONM, syn.toLowerCase(),
						Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
			indexWriter.addDocument(doc2);
			indexWriter.optimize();
			return true;
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		return false;
	}

	public boolean modifyIndex(String word, String[] oldSynonmWords,
			String[] newSynonWords) throws EngineException {
		List<String> synonmWordslList = new ArrayList<String>();
		List<String> oldSynonmWordsList = Arrays.asList(oldSynonmWords);
		List<String> newSynonmWordsList = Arrays.asList(newSynonWords);
		for (String syn : newSynonWords) {
			if (!oldSynonmWordsList.contains(syn)) {
				synonmWordslList.add(syn);
			}
		}
		SynonymEngine engine=null;
		try {
			engine = new CustomSynonymDic();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		try {
			for (String syn : oldSynonmWords) {
				if (!newSynonmWordsList.contains(syn)) {
					String[] synWords = null;
					try {
						synWords = engine.getSynonyms(syn);
					} catch (EngineException e) {
						// TODO Auto-generated catch block
						Log.getLogger(this.getClass()).error(e.getMessage(),e);
						continue;
					}
					if (synWords == null)
						continue;
					indexWriter.deleteDocuments(new Term(SynonymEngine.WORD,
							syn));
					Document doc = new Document();
					doc.add(new Field(SynonymEngine.WORD, syn.toLowerCase(),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field(SynonymEngine.SYNONM, syn.toLowerCase(),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					for (String synWord : synWords) {
						if (!synWord.equalsIgnoreCase(word)) {
							doc.add(new Field(SynonymEngine.SYNONM, synWord
									.toLowerCase(), Field.Store.YES,
									Field.Index.NOT_ANALYZED));
						}
					}
					indexWriter.addDocument(doc);
				}
			}
			indexWriter.optimize();
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return false;
		}
		if(engine!=null)
			engine.close();
		String[] synonmWords = new String[synonmWordslList.size()];
		synonmWordslList.toArray(synonmWords);
		return addIndex(word, synonmWords, newSynonWords);
	}

	public boolean createIndex() {
		// Analyzer analyzer = new IKAnalyzer();
		
		try {
			List<String[]> list = readSynonmDict(Configuration.synonmDict);
			List<String> nonSameList = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				if (!nonSameList.contains(list.get(i)[0])) {
					nonSameList.add(list.get(i)[0]);
					Document document = new Document();
					document.add(new Field(SynonymEngine.WORD, list.get(i)[0]
							.toLowerCase(), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					for (int j = 0; j < list.get(i).length; j++) {
						document.add(new Field(SynonymEngine.SYNONM, list
								.get(i)[j].toLowerCase(), Field.Store.YES,
								Field.Index.NOT_ANALYZED));
					}
					indexWriter.addDocument(document);
				}
			}
			indexWriter.optimize();
			return true;
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		return false;
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
		} catch (FileNotFoundException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (UnsupportedEncodingException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (inputStreamReader != null)
					inputStreamReader.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return list;

	}

	private boolean writeHGDSynonmDict() {
		List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		List<SynonmBean> listBeans = new ArrayList<SynonmBean>();
		List<SynonmBean> listBeans_resutlt = new ArrayList<SynonmBean>();
		String path = null;
		File file = null;
		File newFile = null;
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		String str = null;
		try {
			path = PathUtil.getWebInFAbsolutePath();
			file = new File(path + hgdSynonmDict);
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			while ((str = bufferedReader.readLine()) != null) {
				ArrayList<String> arrayList = new ArrayList<String>();
				String[] strings = str.split(" ");
				for (String s : strings) {
					arrayList.add(s);
					// System.err.print(s);
				}
				System.err.println();
				list.add(arrayList);
			}
			int size = list.size();
			for (int i = 0; i < size; i++) {
				String orcWord = list.get(i).get(0);
				for (int j = 2; j < list.get(i).size(); j++) {
					if (!list.get(i).get(j).contains("="))
						continue;
					String orcCode = list.get(i).get(j);
					SynonmBean bean = new SynonmBean();
					bean.setOrcWord(orcWord);
					System.out.println("orcWord:" + bean.getOrcWord());
					for (int k = 0; k < size; k++) {
						String synomWord = list.get(k).get(0);
						if (i == k)
							continue;
						for (int z = 2; z < list.get(k).size(); z++) {
							String synonmCode = list.get(k).get(z);
							if (orcCode.equals(synonmCode)) {
								bean.add(synomWord);
								System.out.println("synomWord:" + synomWord);
							}
						}

					}
					listBeans.add(bean);
				}

			}
			file = new File(path + codeSynonmDict);
			fileOutputStream = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream,
					"utf-8");
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			for (int i = 0; i < listBeans_resutlt.size(); i++) {
				SynonmBean bean = listBeans_resutlt.get(i);
				bufferedWriter.write(bean.getOrcWord() + " ");
				String[] synonmWords = (String[]) listBeans_resutlt
						.get(i)
						.getSynonmWords()
						.toArray(
								new String[listBeans_resutlt.get(i)
										.getSynonmWords().size()]);
				for (String s : synonmWords) {
					bufferedWriter.write(s + " ");
				}
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();

			/*
			 * StringBuilder builder=new StringBuilder(); SynonmBean bean=null;
			 * for(int i=0;i<listBeans.size();i++){ for(int
			 * j=0;j<listBeans.size();j++){ if(i==j) continue;
			 * if(listBeans.get(i).get(0).equals(listBeans.get(j).get(0))){
			 * for(int k=1;k<listBeans.get(j).getSynonmWords().size();k++){
			 * listBeans.get(i).getSynonmWords().add(listBeans.get(j).get(k)); }
			 * builder.append(j); } }
			 * if(builder.indexOf(String.valueOf(i))!=-1){
			 * bean=listBeans.get(i);
			 * bufferedWriter.write(bean.getOrcWord()+" "); String []
			 * synonmWords
			 * =(String[])listBeans.get(i).getSynonmWords().toArray(new
			 * String[listBeans.get(i).getSynonmWords().size()]); for(String
			 * s:synonmWords){ bufferedWriter.write(s+" "); }
			 * bufferedWriter.newLine(); } } bufferedWriter.flush();
			 */

		} catch (FileNotFoundException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (UnsupportedEncodingException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (inputStreamReader != null)
					inputStreamReader.close();
				if (bufferedReader != null)
					bufferedReader.close();
				if (fileOutputStream != null)
					fileOutputStream.close();
				if (outputStreamWriter != null)
					outputStreamWriter.close();
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return true;
	}

	private void dellSynonDict() {
		String path;
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			path =  PathUtil.getWebInFAbsolutePath();
			File file_in = new File(path + codeSynonmDict);
			File file_out = new File(path + Configuration.synonmDict);
			try {
				fileOutputStream = new FileOutputStream(file_out, true);
			} catch (Exception e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
			try {
				outputStreamWriter = new OutputStreamWriter(fileOutputStream,
						"utf-8");
			} catch (Exception e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			ArrayList<String> list = FileUse.read(file_in);

			StringBuilder builder = new StringBuilder();
			// ArrayList<String> listResult=new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				Set<String> set = new TreeSet<String>();
				String str = list.get(i).split(" ")[0] + " ";

				for (int m = 1; m < list.get(i).split(" ").length; m++) {
					set.add(list.get(i).split(" ")[m]);
				}
				for (int j = 0; j < list.size(); j++) {
					if (i == j)
						continue;
					String stri = (list.get(i).split(" "))[0];
					String strj = (list.get(j).split(" "))[0];
					if (stri.equals(strj)) {
						String[] strjs = list.get(j).split(" ");
						for (int k = 1; k < strjs.length; k++) {
							set.add(strjs[k]);
						}
						builder.append(j);
					}
				}
				if (builder.indexOf(String.valueOf(i)) == -1) {
					Iterator<String> iterator = set.iterator();
					while (iterator.hasNext()) {
						str = str + iterator.next() + " ";
					}
					System.out.println(str);
					bufferedWriter.write(str);
					bufferedWriter.newLine();
					bufferedWriter.flush();

				}
				set.clear();
			}

		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (fileOutputStream != null)
					fileOutputStream.close();
				if (outputStreamWriter != null)
					outputStreamWriter.close();
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}

	}

	/*
	 * public void End() { try { if (indexWriter != null) {
	 * indexWriter.optimize(); indexWriter.commit(); indexWriter.close(); }
	 * 
	 * } catch (Exception e) { WriteLog writeLog = new WriteLog(); try {
	 * writeLog.Write(e.getMessage(), CommonSynonmIndexServer.class.getName());
	 * } catch (Exception e1) { e1.printStackTrace(); } } }
	 */

}
