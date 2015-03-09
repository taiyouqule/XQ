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

public class CommonSynonymDic implements SynonymEngine {
	private IndexSearcher indexSearcher;
	private Directory directory;

	public CommonSynonymDic() throws EngineException {
		File file = new File(Configuration.indexPath + "/"
				+ Configuration.mySynFolder);
		if (!file.exists())
			throw new EngineException("CommonSynonymDic file is not Exist!");
		try {
			directory = FSDirectory.open(file);
			indexSearcher = new IndexSearcher(directory);
		} catch (IOException e) {
			throw new EngineException("CommonSynonymDic open Exception!", e);
		}
	}

	public void close() {
		try {
			if (indexSearcher != null)
				indexSearcher.close();
			if (directory != null)
				directory.close();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public String[] getSynonyms(String word) throws EngineException {
		// TODO Auto-generated method stub
		List<String> synList = new ArrayList<String>();
		Term term = new Term(WORD, word);
		TermQuery query = new TermQuery(term);
		TopDocs topDocs;
		try {
			topDocs = indexSearcher.search(query, 20);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new EngineException("CommonSynonymDic Search Exception", e);
		}
		ScoreDoc[] docs = topDocs.scoreDocs;
		for (ScoreDoc doc : docs) {
			Document d;
			try {
				d = indexSearcher.doc(doc.doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new EngineException(
						"CommonSynonymDic indexSearcher Transform Exception", e);
			}
			String[] values = d.getValues(SYNONM);
			for (int i = 1; i < values.length; i++) {
				synList.add(values[i]);
				// System.err.println(values[i]);
			}
		}
		return synList.toArray(new String[0]);

	}

}
