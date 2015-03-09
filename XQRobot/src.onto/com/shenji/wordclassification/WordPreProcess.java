package com.shenji.wordclassification;

import java.io.File;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.shenji.common.log.Log;
import com.shenji.common.util.ConfigurantionUtil;
import com.shenji.onto.Configuration;

import edu.hit.ir.ltpService.LTML;
import edu.hit.ir.ltpService.LTPOption;
import edu.hit.ir.ltpService.LTPService;
import edu.hit.ir.ltpService.Word;

public class WordPreProcess {
	private static Properties properties;
	private static List<String> selfCreatedDict;
	private static int maxLength=60;
	static{
		WordPrepertyBean bean=new WordPrepertyBean();
		String systemPath = null;
		try {
			systemPath = bean.getClass().getClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file=new File(systemPath+"/"+Configuration.SeechTaggingSetFileName);
		ConfigurantionUtil configurantionUtil=new ConfigurantionUtil(file);
		properties=configurantionUtil.getProperties();
		selfCreatedDict=new ArrayList<String>();
		initializeSelfCreatedDict(selfCreatedDict);
	}
	private void longWordsCope(int wordsLength,String[] words,HashMap<Object, Object> map,boolean isWeb){
		int count=wordsLength/maxLength;
		String[][] strs=new String[count+1][maxLength];
		for(int i=0;i<=count;i++){
			for(int j=0;j<maxLength;j++){
				if((i*maxLength+j)>=words.length)
					break;
				strs[i][j]=words[i*maxLength+j];
			}
		}
		for(int k=0;k<=count;k++){
			wordSpeechTagging(map,strs[k],isWeb);	
		}
	}
	
	public ArrayList<WordPrepertyBean> wordPreProcess(String str,boolean isWeb){
		ArrayList<WordPrepertyBean> wordPropertyBeansList=new ArrayList<WordPrepertyBean>();
		String[] words=wordParticiple_(str);
		int wordsLength=words.length;
		HashMap<Object, Object> map=new HashMap<Object, Object>();
		longWordsCope(wordsLength,words,map,isWeb);
		for(int i=0;i<words.length;i++){
			WordPrepertyBean bean=new WordPrepertyBean();
			String word=words[i];
			bean.setWord(word);
			if(map==null){
				continue;
			}
			//LTP识别出的词
			if(map.containsKey(word)){
				if(selfCreatedDict.contains(word)){
					bean.setSource(WordPrepertyBean.LTP+"|"+WordPrepertyBean.USER_DEFINED);
				}
				else
					bean.setSource(WordPrepertyBean.LTP);
				String speech=(String) map.get(word);
				bean.setSpeech_en(speech);
				if(speech!=null)
					bean.setSpeech_ch(properties.getProperty(speech));
				else
					bean.setSpeech_ch("未知");
			}
			//未识别的词
			else{
				bean.setSpeech_en("?");
				bean.setSpeech_ch("未知");		
				boolean flag=singleWordHandle(bean,isWeb);
				if(selfCreatedDict.contains(word)){
					if(flag==true){
						bean.setSource(WordPrepertyBean.LTP+"|"+WordPrepertyBean.USER_DEFINED);
					}
					else
						bean.setSource(WordPrepertyBean.USER_DEFINED);
				}							
			}
			
			wordPropertyBeansList.add(bean);
		}
		if(map!=null){
    		map.clear();
    	}
		return wordPropertyBeansList;
	}
	/**
	 * 分词处理
	 * @param str
	 * @return
	 */
	private String[] wordParticiple_(String str){		
		return wordParticiple(str).split("/");		
	}
	private boolean singleWordHandle(WordPrepertyBean bean,boolean isWeb){
		HashMap<Object, Object> map=new HashMap<Object, Object>();
		String word=bean.getWord();
		wordSpeechTagging(map,word,isWeb);
		try{
			if(map.containsKey(word)){
				bean.setSource(WordPrepertyBean.LTP);
				bean.setSpeech_en(map.get(word).toString());
				bean.setSpeech_ch(properties.getProperty(map.get(word).toString()));
				return true;
			}
		}
		finally{
			if(map!=null)
				map.clear();
		}
		return false;
		
	}
	
	public String wordParticiple(String str){
		/*FAQlStub faQlStub = null;
		String[]  wordParticipleList = null;
		String returnStr=null;
		try {
			faQlStub=new FAQlStub();
			IKAnalysis ikAnalysis=new IKAnalysis();
			ikAnalysis.setStr(str);
			IKAnalysisResponse ikAnalysisResponse=faQlStub.iKAnalysis(ikAnalysis);
			returnStr=ikAnalysisResponse.get_return();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
		}
		finally{
		}
		return returnStr;*/
		return null;
	}
	
	/**
	 * 词性标注
	 * @param sentence 1、用^隔开 或者 2、完成句子（则采用LTP分词）
	 * @return
	 */
	private void wordSpeechTagging(HashMap<Object, Object> map,String[] words,boolean isWeb){
		if(words.length==0)
			return;
		String sentence=words[0];
		for(int i=1;i<words.length;i++){
			if(words[i]==null)
				continue;
			sentence=sentence+words[i];
		}
		wordSpeechTagging(map,sentence,isWeb);
	} 
	//带自动解析LIML格式的词性分析
	private synchronized HashMap<Object, Object> wordSpeechTagging(HashMap<Object, Object> map,String sentence,boolean isWeb){
		 LTPService ls = new LTPService(Configuration.LTPToken); 	 
		 if(sentence==null)
			 return null;
	     try {
	         ls.setEncoding(LTPOption.UTF8);
	         LTML ltml=null;         
	         if(isWeb)
	        	 ltml= ls.analyzeWeb(LTPOption.POS,sentence);
	         else
	        	 ltml=ls.analyze(LTPOption.POS, sentence);
		     int sentNum = ltml.countSentence();
		     for(int i = 0; i< sentNum; ++i){
		         ArrayList<Word> wordList = ltml.getWords(i);	           
		         for(int j = 0; j < wordList.size(); ++j){		        
		        	 String word=wordList.get(j).getWS();
		        	 String speech=wordList.get(j).getPOS();
		        	 if(word.equals("^"))
		        		 continue;	        	         
		        	 else{ 
		        		 map.put(word, speech);
		             }
		         }
		     }	          
	         //long end=System.currentTimeMillis();
	         //System.err.println(end-start);	         
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	        	Log.getLogger().error(e.getMessage(),e);
	            e.printStackTrace();
	            return null;
	        } finally {    	
	        	//没有明白，HHTP关闭取消
	           // ls.close();
	     }	
	     return map;	     
	}
	
	
	private static void initializeSelfCreatedDict(List<String> selfCreatedDict2){
		/*FAQlStub faQlStub = null;
		try {
			faQlStub=new FAQlStub();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GetMyAllWords allWords=new GetMyAllWords();
		GetMyAllWordsResponse response = null;
		try {
			response = faQlStub.getMyAllWords(allWords);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.err.println(response.get_return().toString());
		String[] strs=response.get_return().toString().split("/");
		for(String s:strs){
			selfCreatedDict2.add(s);
		}*/
	}
	
	
	//测试输出
	public void sopHashMap(HashMap<Object, Object> map){
		Iterator<Map.Entry<Object, Object>> iterable=  map.entrySet().iterator();
		while(iterable.hasNext()){
			Map.Entry<Object, Object> entry=iterable.next();
			Object key=entry.getKey();
			Object value=entry.getValue();
			System.out.println(key+" : "+value);
		}
	}
	
	public static void sopArrayList(ArrayList<WordPrepertyBean> arrayList){
		for(int i=0;i<arrayList.size();i++){
			if(arrayList.get(i).getSpeech_en()==null)
				System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(arrayList.get(i).getWord()+"  来源："+
					arrayList.get(i).getSource()+"  词性："+
					arrayList.get(i).getSpeech_en()+"  词性："+
					arrayList.get(i).getSpeech_ch());
		}
	}
	
	
}
