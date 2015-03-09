package com.shenji.search.dic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.shenji.common.log.Log;
import com.shenji.search.Configuration;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;

public class CustomSynonymDic implements SynonymEngine {
	private IndexSearcher indexSearcher;
	private Directory directory;

	public CustomSynonymDic() throws EngineException {
		File file = new File(Configuration.indexPath + "/"
				+ Configuration.mySynFolder);
		if (!file.exists())
			throw new EngineException("CustomSynonymDic file is not Exist!");
		try {
			directory = FSDirectory.open(file);
			indexSearcher = new IndexSearcher(directory);
		} catch (IOException e) {
			throw new EngineException("CustomSynonymDic open Exception!", e);
		}
	}

	public void close() {
		try {
			if (indexSearcher != null)
				indexSearcher.close();
			if (directory != null)
				directory.close();
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public String[] getSynonyms(String word) throws EngineException {
		// TODO Auto-generated method stub
		List<String> synList = new ArrayList<String>();
		Term term = new Term(WORD, word);
		TermQuery query = new TermQuery(term);
		TopDocs topDocs = null;
		try {
			topDocs = indexSearcher.search(query, 20);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new EngineException("CustomSysnoymDic Search Exception", e);
		}
		if (topDocs == null)
			return null;
		ScoreDoc[] docs = topDocs.scoreDocs;
		for (ScoreDoc doc : docs) {
			Document d;
			try {
				d = indexSearcher.doc(doc.doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new EngineException(
						"CustomSysnoymDic indexSearcher Transform Exception", e);
			}
			String[] values = d.getValues(SYNONM);
			for (int i = 1; i < values.length; i++) {
				synList.add(values[i]);
			}
		}
		try {
			return synList.toArray(new String[0]);
		} finally {
			synList.clear();
		}
	}

}
