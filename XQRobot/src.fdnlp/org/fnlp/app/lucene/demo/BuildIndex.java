/*package org.fnlp.app.lucene.demo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.fnlp.app.lucene.FNLPAnalyzer;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.util.exception.LoadModelException;

public class BuildIndex {

	*//**
	 * @param args
	 * @throws IOException 
	 * @throws LoadModelException 
	 *//*
	public static void main(String[] args) throws IOException, LoadModelException {
		String indexPath = "./tmp/faqidx";
		System.out.println("Indexing to directory '" + indexPath  + "'...");
		Date start = new Date();
		Directory dir = FSDirectory.open(new File(indexPath));
		//需要先初始化 CNFactory
		CNFactory factory = CNFactory.getInstance("./models",Models.SEG_TAG);
		Analyzer analyzer = new FNLPAnalyzer(Version.LUCENE_40);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = new IndexWriter(dir, iwc);

		String[] strs = new String[]{
				"终端的保修期为一年。",
				"凡在保修期内非人为损坏，均可免费保修。",
				"人为损坏的终端将视情况收取维修费用。"
		};
		for(int i=0;i<strs.length;i++){

			Document doc = new Document();

			Field field = new TextField("content", strs[i] , Field.Store.YES);
			doc.add(field);
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("content",strs[i]), doc);
			}
		}
		writer.close();

		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	}

}
*/