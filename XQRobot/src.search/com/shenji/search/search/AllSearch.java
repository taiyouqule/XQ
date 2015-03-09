package com.shenji.search.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.shenji.common.log.Log;
import com.shenji.search.Configuration;
import com.shenji.search.bean.SearchBean;

public class AllSearch {
	
	public List<SearchBean> getResult(String dir) {
		IndexReader indexReader = null;
		List<SearchBean> listBean = new ArrayList<SearchBean>();   
		try{                 
			indexReader = IndexReader.open(FSDirectory.open(new File(dir)));
			int max = indexReader.maxDoc();     
			SearchBean bean = null;
			for(int n=0;n < max;n++){
				Document doc = indexReader.document(n);
				if(doc.getBoost()>1.0)
					System.err.println(doc.getBoost());
				bean = new SearchBean();   
				String content = doc.get("content");
				content = content.substring(0,content.length()>200?200:content.length());
				String path = doc.get("path");
				path = Configuration.webPath+"/"+path;
				String question=doc.get("question");
				String answer=doc.get("answer");
				content="<div class=\"q\">"+question+"</div><div class=\"a\">"+answer+"<br></div>";
				bean.setContent("<a href=\""+path+"\">"+content+"</a>");   
				listBean.add(bean);   
			}
			indexReader.close();
		}catch(Exception e){
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		return listBean;
	}
	
}