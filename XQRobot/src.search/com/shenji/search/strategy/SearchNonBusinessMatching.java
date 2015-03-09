package com.shenji.search.strategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.StringMatching;
import com.shenji.search.Configuration;
import com.shenji.search.FenciControl;
import com.shenji.search.database.DBPhraseManager;
import com.shenji.search.dic.CommonSynonymDic;
import com.shenji.search.dic.CustomWordDic;
import com.shenji.search.engine.CustomWordEngine;
import com.shenji.search.engine.SynonymEngine;
import com.shenji.search.exception.EngineException;

public class SearchNonBusinessMatching {
	
	private static boolean isBusinessQuestion(String arg) throws EngineException{
		boolean isBusiness=false;
		//同义词引擎
		SynonymEngine synonymEngine=null;
		synonymEngine=new CommonSynonymDic();
		CustomWordEngine customWordEngine=null;
		customWordEngine=CustomWordDic.getInstance();
		//进行分词
		String ikStr=new FenciControl().iKAnalysis(arg);
		String[] ikStrs=ikStr.split("/");
		for(String word:ikStrs){
			//词在自建词典中
			if(word.length()>1&&customWordEngine.isCustomWord(word))
				return true;
			//分的词的同义词在自建词典中
			String[] synStrs=synonymEngine.getSynonyms(word.toLowerCase());
			if(synStrs==null||synStrs.length==0)
				continue;
			for(String syn:synStrs){
				if(syn.length()>1&&customWordEngine.isCustomWord(syn))
					return true;
			}
		}	
		if(synonymEngine!=null)
			synonymEngine.close();
		if(customWordEngine!=null)
			customWordEngine.close();
		return isBusiness;
	}
	public static String matching(String arg) throws EngineException{
		String answer=null;
		HashMap<String, String> qaMap=null;
		List<String> commonAnwserList=new ArrayList<String>();
		if(isBusinessQuestion(arg))
			return null;
		//Iterator<Map.Entry<String, String>> iterator=SearchNonBusinessMatching.getCommonFaqMap().entrySet().iterator();
		try {
			DBPhraseManager dbManager = new DBPhraseManager();
			qaMap=(HashMap<String, String>) dbManager.listAllQA();
			Iterator<Map.Entry<String, String>> iterator=qaMap.entrySet().iterator();
			double minEditDistance=0;
			while(iterator.hasNext()){
				Map.Entry<String, String> map=iterator.next();
				String q=map.getKey();
				if(q.startsWith("TY"))
					commonAnwserList.add(map.getValue());
				double editDistance=StringMatching.getSimilarity(arg, q);
				if(editDistance>minEditDistance){
					minEditDistance=editDistance;
					answer=map.getValue();
				}
			}
			if(minEditDistance==0){
				//要求写死在程序。。
				answer="用户您好，我是机器人。请输入关键字我可以帮助您查找问题答案。";
			}
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(SearchNonBusinessMatching.class).error(e.getMessage(),e);
		}
		finally{
			if(qaMap!=null)
				qaMap.clear();
			if(commonAnwserList!=null)
				commonAnwserList.clear();
		}
		return answer;
	} 
	
	
	

}
