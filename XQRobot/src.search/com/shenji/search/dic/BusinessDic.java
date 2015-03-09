package com.shenji.search.dic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.common.util.PathUtil;
import com.shenji.search.Configuration;
import com.shenji.search.engine.BusinessEngine;
import com.shenji.search.exception.EngineException;

public class BusinessDic implements BusinessEngine {
	public static HashMap<String, Float> businessDictMap;
	private static BusinessDic instance;

	private BusinessDic() throws EngineException {
		businessDictMap = new HashMap<String, Float>();
		loadBusinessDict();
	}

	public static synchronized BusinessDic getInstance() throws EngineException {
		if (instance == null) {
			synchronized (BusinessDic.class) {
				if (instance == null)
					instance = new BusinessDic();
			}
		}
		return instance;
	}

	public float getWeight(String s) throws EngineException {
		// TODO Auto-generated method stub
		if (businessDictMap.get(s) != null)
			return businessDictMap.get(s);
		else
			return -1;
	}

	private void loadBusinessDict() throws EngineException {
		String path = PathUtil.getWebInFAbsolutePath();
		File file = new File(path + Configuration.businessDict);
		if (!file.exists())
			throw new EngineException("businessDict File is Null!");
		List<String> list = FileUse.read(file);
		for (String s : list) {
			String[] strings = s.split(" ");
			businessDictMap.put(strings[0], Float.valueOf(strings[1]));
		}
		list.clear();
	}

	public static void reset() throws EngineException {
		businessDictMap.clear();
		instance.loadBusinessDict();
	}

	public int addNewBusinessWord(String word, float weight)
			throws EngineException {
		String path;
		int result;
		try {
			path = PathUtil.getWebInFAbsolutePath();
			File file = new File(path + Configuration.businessDict);
			String str = word + " " + weight;
			result = FileUse.write(file, FileUse.add(file, str));
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		}
		try {
			reset();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw new EngineException("Reset BusinessDic Exception!", e);
		}
		return result;
	}

	public int modifyBusinessWord(String oldWord, String newWord,
			float newWeight) throws EngineException {
		String path;
		int result;
		path = PathUtil.getWebInFAbsolutePath();
		String str = newWord + " " + newWeight;
		result = FileUse.write(path, Configuration.businessDict, FileUse
				.modify(path, Configuration.businessDict, oldWord, str,
						FileUse.CONTAINS_TYPE));
		try {
			reset();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw new EngineException("Reset BusinessDic Exception!", e);
		}
		return result;
	}

	public int deleteBusinessWord(String word) throws EngineException {
		String[] words = { word };
		String path;
		int result;
		path = PathUtil.getWebInFAbsolutePath();
		result = FileUse.write(path, Configuration.businessDict, FileUse
				.delete(path, Configuration.businessDict, words,
						FileUse.CONTAINS_TYPE));
		try {
			reset();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			throw new EngineException("Reset BusinessDic Exception!", e);
		}
		return result;
	}

	/*
	 * public String[] listBusinessDict(){ List<String> list=new
	 * ArrayList<String>(); File file=null; try { file= new
	 * File(this.getClass().
	 * getClassLoader().getResource("").toURI().getPath()+Configuration
	 * .businessDict); ArrayList<String> arrayList_myDict=FileUse.read(file);
	 * for(String s:arrayList_myDict){
	 * 
	 * String[] str=s.split(" "); String string=str[0]+"/"+str[1];
	 * list.add(string); } arrayList_myDict.clear(); } catch (URISyntaxException
	 * e) { e.printStackTrace(); } return (String[])list.toArray(new
	 * String[list.size()]); }
	 */

	public String[] listBusinessDict() {
		if (businessDictMap == null || businessDictMap.size() == 0)
			return null;
		String[] strs = new String[businessDictMap.size()];
		int count = 0;
		Iterator<Map.Entry<String, Float>> iterator = businessDictMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Float> entry = iterator.next();
			strs[count++] = entry.getKey() + "/" + entry.getValue();

		}
		return strs;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	private boolean createFile() {
		String path;
		try {
			path = PathUtil.getWebInFAbsolutePath();
			File file = new File(path + Configuration.businessDict);
			if (!file.exists()) {
				file.createNewFile();
				return true;
			} else
				return false;
		} catch (IOException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		return true;
	}

}
