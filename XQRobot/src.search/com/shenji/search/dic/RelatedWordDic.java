package com.shenji.search.dic;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.shenji.common.log.Log;
import com.shenji.search.Configuration;
import com.shenji.search.engine.RelatedWordEngine;
import com.shenji.search.exception.EngineException;

public class RelatedWordDic implements RelatedWordEngine {
	private IndexSearcher indexSearcher;
	private Directory directory;
	private static String WORD = "word";
	private static String SOURCE = "source";

	public RelatedWordDic() throws EngineException {
		try {
			File index = new File(Configuration.indexPath + File.separator
					+ Configuration.relatedWordFolder);
			if (index == null || !index.exists()) {
				throw new EngineException("relatedWordIndex File isNull");
			}
			directory = FSDirectory.open(index);
			indexSearcher = new IndexSearcher(directory);
		} catch (IOException e) {
			//e.printStackTrace();
			throw new EngineException("relatedWordIndex open Exception",e);
		}
	}

	@Override
	public Map<String, Double> getRelatedWord(String word) {
		// TODO Auto-generated method stub
		Map<String, Double> map=new LinkedHashMap<String, Double>();
		Term term=new Term(WORD, word);
		TermQuery query=new TermQuery(term);
		TopDocs topDocs = null;
		try {
			topDocs = indexSearcher.search(query, 20);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		if(topDocs==null)
			return map;
		ScoreDoc[] docs = topDocs.scoreDocs;
		/*for(ScoreDoc doc :docs){
			Document d = indexSearcher.doc(doc.doc);
			String[] values=d.getValues(SYNONM);
			for(int i=1;i<values.length;i++){
				map.add(values[i]);
			}
		}*/
		//close();
		return map;	
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			if (indexSearcher != null)
				indexSearcher.close();
			if (directory != null)
				directory.close();
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

}
