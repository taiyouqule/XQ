/**
 * 
 */
package org.wltea.analyzer.dic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;

import com.shenji.common.log.Log;

/**
 * IK Analyzer v3.2
 * 词典管理类,单子模式
 * @author 林良益
 *
 */
public class MDictionary {
	/*
	 * 分词器默认字典路径 
	 */
	/*public static final String PATH_DIC_MAIN = "/org/wltea/analyzer/dic/main.dic";
	public static final String PATH_DIC_SURNAME = "/org/wltea/analyzer/dic/surname.dic";
	public static final String PATH_DIC_QUANTIFIER = "/org/wltea/analyzer/dic/quantifier.dic";
	public static final String PATH_DIC_SUFFIX = "/org/wltea/analyzer/dic/suffix.dic";
	public static final String PATH_DIC_PREP = "/org/wltea/analyzer/dic/preposition.dic";
	public static final String PATH_DIC_STOP = "/org/wltea/analyzer/dic/stopword.dic";*/
	public static final String PATH_DIC_MAIN = "/resources/dic/main.dic";
	public static final String PATH_DIC_SURNAME = "/resources/dic/surname.dic";
	public static final String PATH_DIC_QUANTIFIER = "/resources/dic/quantifier.dic";
	public static final String PATH_DIC_SUFFIX = "/resources/dic/suffix.dic";
	public static final String PATH_DIC_PREP = "/resources/dic/preposition.dic";
	public static final String PATH_DIC_STOP = "/resources/dic/stopword.dic";
	
	
	/*
	 * 词典单子实例
	 */
	private static  MDictionary singleton;
	
	/*
	 * 词典初始化
	 */
	static{
		singleton = new MDictionary();
		//System.err.println("DICT RUN!");
	}
	
	/*
	 * 主词典对象
	 */
	private DictSegment _MainDict;
	/*
	 * 姓氏词典
	 */
	private DictSegment _SurnameDict;
	/*
	 * 量词词典
	 */
	private DictSegment _QuantifierDict;
	/*
	 * 后缀词典
	 */
	private DictSegment _SuffixDict;
	/*
	 * 副词，介词词典
	 */
	private DictSegment _PrepDict;
	/*
	 * 停止词集合
	 */
	private DictSegment _StopWords;
	
	private MDictionary(){
		//初始化系统词典
		loadMainDict();
		loadSurnameDict();
		loadQuantifierDict();
		loadSuffixDict();
		loadPrepDict();
		loadStopWordDict();
	}

	public static void reset(){
		//System.err.println("DICT RUN!");
		clear();
		singleton=new MDictionary();
	}
	private static void clear(){
		singleton._MainDict.clear();
		singleton._SurnameDict.clear();
		singleton._QuantifierDict.clear();		
		singleton._SuffixDict.clear();		
		singleton._PrepDict.clear();
		singleton._StopWords.clear();
	}
	/**
	 * 加载主词典及扩展词典
	 */
	private void loadMainDict(){
		//建立一个主词典实例
		_MainDict = new DictSegment((char)0);
		//读取主词典文件
        InputStream is = MDictionary.class.getResourceAsStream(MDictionary.PATH_DIC_MAIN);
        if(is == null){
        	throw new RuntimeException("Main MDictionary not found!!!");
        }
       
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					_MainDict.fillSegment(theWord.trim().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		
		//加载扩展词典配置
		List<String> extDictFiles  = Configuration.getExtDictionarys();
		if(extDictFiles != null){
			for(String extDictName : extDictFiles){
				//读取扩展词典文件
				is = MDictionary.class.getResourceAsStream(extDictName);
				//如果找不到扩展的字典，则忽略
				if(is == null){
					//System.err.println("找不到扩展的字典!");
					continue;
				}
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
					String theWord = null;
					do {
						theWord = br.readLine();
						if (theWord != null && !"".equals(theWord.trim())) {
							//加载扩展词典数据到主内存词典中
							//System.out.println(theWord);
							_MainDict.fillSegment(theWord.trim().toCharArray());
						}
					} while (theWord != null);
					
				} catch (IOException e) {
					Log.getLogger(this.getClass()).error(e.getMessage(),e);
					
				}finally{
					try {
						if(is != null){
		                    is.close();
		                    is = null;
						}
					} catch (IOException e) {
						Log.getLogger(this.getClass()).error(e.getMessage(),e);
					}
				}
			}
		}
	}	
	
	/**
	 * 加载姓氏词典
	 */
	private void loadSurnameDict(){
		//建立一个姓氏词典实例
		_SurnameDict = new DictSegment((char)0);
		//读取姓氏词典文件
        InputStream is = MDictionary.class.getResourceAsStream(MDictionary.PATH_DIC_SURNAME);
        if(is == null){
        	throw new RuntimeException("Surname MDictionary not found!!!");
        }
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					_SurnameDict.fillSegment(theWord.trim().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 加载量词词典
	 */
	private void loadQuantifierDict(){
		//建立一个量词典实例
		_QuantifierDict = new DictSegment((char)0);
		//读取量词词典文件
        InputStream is = MDictionary.class.getResourceAsStream(MDictionary.PATH_DIC_QUANTIFIER);
        if(is == null){
        	throw new RuntimeException("Quantifier MDictionary not found!!!");
        }
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					_QuantifierDict.fillSegment(theWord.trim().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 加载后缀词典
	 */
	private void loadSuffixDict(){
		//建立一个后缀词典实例
		_SuffixDict = new DictSegment((char)0);
		//读取量词词典文件
        InputStream is = MDictionary.class.getResourceAsStream(MDictionary.PATH_DIC_SUFFIX);
        if(is == null){
        	throw new RuntimeException("Suffix MDictionary not found!!!");
        }
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					_SuffixDict.fillSegment(theWord.trim().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}			

	/**
	 * 加载介词\副词词典
	 */
	private void loadPrepDict(){
		//建立一个介词\副词词典实例
		_PrepDict = new DictSegment((char)0);
		//读取量词词典文件
        InputStream is = MDictionary.class.getResourceAsStream(MDictionary.PATH_DIC_PREP);
        if(is == null){
        	throw new RuntimeException("Preposition MDictionary not found!!!");
        }
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					//System.out.println(theWord);
					_PrepDict.fillSegment(theWord.trim().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 加载停止词词典
	 */
	private void loadStopWordDict(){
		//建立一个停止词典实例
		_StopWords = new DictSegment((char)0);
		//读取量词词典文件
        InputStream is = MDictionary.class.getResourceAsStream(MDictionary.PATH_DIC_STOP);
        if(is == null){
        	throw new RuntimeException("Stopword MDictionary not found!!!");
        }
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					_StopWords.fillSegment(theWord.trim().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		
		//加载扩展停止词典
		List<String> extStopWordDictFiles  = Configuration.getExtStopWordDictionarys();
		if(extStopWordDictFiles != null){
			for(String extStopWordDictName : extStopWordDictFiles){
				//读取扩展词典文件
				is = MDictionary.class.getResourceAsStream(extStopWordDictName);
				//如果找不到扩展的字典，则忽略
				if(is == null){
					continue;
				}
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
					String theWord = null;
					do {
						theWord = br.readLine();
						if (theWord != null && !"".equals(theWord.trim())) {
							//System.out.println(theWord);
							//加载扩展停止词典数据到内存中
							_StopWords.fillSegment(theWord.trim().toCharArray());
						}
					} while (theWord != null);
					
				} catch (IOException e) {
					Log.getLogger(this.getClass()).error(e.getMessage(),e);
					
				}finally{
					try {
						if(is != null){
		                    is.close();
		                    is = null;
						}
					} catch (IOException e) {
						Log.getLogger(this.getClass()).error(e.getMessage(),e);
					}
				}
			}
		}		
		
	}			
	
	/**
	 * 词典初始化
	 * 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
	 * 只有当Dictionary类被实际调用时，才会开始载入词典，
	 * 这将延长首次分词操作的时间
	 * 该方法提供了一个在应用加载阶段就初始化字典的手段
	 * 用来缩短首次分词时的时延
	 * @return MDictionary
	 */
	public static MDictionary getInstance(){
		return MDictionary.singleton;
	}
	
	/**
	 * 加载扩展的词条
	 * @param extWords List<String>词条列表
	 */
	public static void loadExtendWords(List<String> extWords){
		if(extWords != null){
			for(String extWord : extWords){
				if (extWord != null) {
					//加载扩展词条到主内存词典中
					singleton._MainDict.fillSegment(extWord.trim().toCharArray());
				}
			}
		}
	}
	
	/**
	 * 加载扩展的停止词条
	 * @param extStopWords List<String>词条列表
	 */
	public static void loadExtendStopWords(List<String> extStopWords){
		if(extStopWords != null){
			for(String extStopWord : extStopWords){
				if (extStopWord != null) {
					//加载扩展的停止词条
					singleton._StopWords.fillSegment(extStopWord.trim().toCharArray());
				}
			}
		}
	}
	
	/**
	 * 检索匹配主词典
	 * @param charArray
	 * @return Hit 匹配结果描述
	 */
	public static Hit matchInMainDict(char[] charArray){
		return singleton._MainDict.match(charArray);
	}
	
	/**
	 * 检索匹配主词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return Hit 匹配结果描述
	 */
	public static Hit matchInMainDict(char[] charArray , int begin, int length){
		return singleton._MainDict.match(charArray, begin, length);
	}
	
	/**
	 * 检索匹配主词典,
	 * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
	 * @param charArray
	 * @param currentIndex
	 * @param matchedHit
	 * @return
	 */
	public static Hit matchInMainDictWithHit(char[] charArray , int currentIndex , Hit matchedHit){
		DictSegment ds = matchedHit.getMatchedDictSegment();
		return ds.match(charArray, currentIndex, 1 , matchedHit);
	}

	/**
	 * 检索匹配姓氏词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return Hit 匹配结果描述
	 */
	public static Hit matchInSurnameDict(char[] charArray , int begin, int length){
		return singleton._SurnameDict.match(charArray, begin, length);
	}		
	
//	/**
//	 * 
//	 * 在姓氏词典中匹配指定位置的char数组
//	 * （对传入的字串进行后缀匹配）
//	 * @param charArray
//	 * @param begin
//	 * @param end
//	 * @return
//	 */
//	public static boolean endsWithSurnameDict(char[] charArray , int begin, int length){
//		Hit hit = null;
//		for(int i = 1 ; i <= length ; i++){
//			hit = singleton._SurnameDict.match(charArray, begin + (length - i) , i);
//			if(hit.isMatch()){
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * 检索匹配量词词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return Hit 匹配结果描述
	 */
	public static Hit matchInQuantifierDict(char[] charArray , int begin, int length){
		return singleton._QuantifierDict.match(charArray, begin, length);
	}
	
	/**
	 * 检索匹配在后缀词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return Hit 匹配结果描述
	 */
	public static Hit matchInSuffixDict(char[] charArray , int begin, int length){
		return singleton._SuffixDict.match(charArray, begin, length);
	}
	
//	/**
//	 * 在后缀词典中匹配指定位置的char数组
//	 * （对传入的字串进行前缀匹配）
//	 * @param charArray
//	 * @param begin
//	 * @param end
//	 * @return
//	 */
//	public static boolean startsWithSuffixDict(char[] charArray , int begin, int length){
//		Hit hit = null;
//		for(int i = 1 ; i <= length ; i++){
//			hit = singleton._SuffixDict.match(charArray, begin , i);
//			if(hit.isMatch()){
//				return true;
//			}else if(hit.isUnmatch()){
//				return false;
//			}
//		}
//		return false;
//	}
	
	/**
	 * 检索匹配介词、副词词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return  Hit 匹配结果描述
	 */
	public static Hit matchInPrepDict(char[] charArray , int begin, int length){
		return singleton._PrepDict.match(charArray, begin, length);
	}
	
	/**
	 * 判断是否是停止词
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return boolean
	 */
	public static boolean isStopWord(char[] charArray , int begin, int length){			
		return singleton._StopWords.match(charArray, begin, length).isMatch();
	}	
}

