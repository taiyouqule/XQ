package com.shenji.nlp;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import com.shenji.common.log.Log;
import com.shenji.nlp.common.Common;
import com.shenji.nlp.inter.IChineseWordSegmentation;
import com.shenji.nlp.structure.MChineseTags;


import edu.fudan.nlp.cn.Tags;
import edu.fudan.nlp.cn.tag.NERTagger;
import edu.fudan.nlp.cn.tag.POSTagger;

/**
 * 命名实体识别
 * @author zhq
 *
 */
public class MNERTagger extends NERTagger{
	private POSTagger posmodel;
	private IChineseWordSegmentation segmentation; 
	public MNERTagger(POSTagger posmodel,IChineseWordSegmentation segmentation) {
		super(posmodel);
		this.posmodel=posmodel;
		this.segmentation=segmentation;
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<String, String> tag(String sentence) {
		HashMap<String, String> map=new HashMap<String, String>();	
		String[] words=null;
		try {
			words = segmentation.segment(sentence);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e1);
			e1.printStackTrace();
			return null;
		}
		String[] speechs=posmodel.tagSeged(words);
		String[][] res=new String[2][];
		res[0]=words;
		res[1]=speechs;
		try {
			for (int i = 0; i < words.length; i++) {
				if(res!=null){
					for(int j=0;j<res[0].length;j++){
						//采用自己的词性操作类
						if(MChineseTags.isEntiry(res[1][j])){
							map.put(res[0][j], res[1][j]);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public String getEntiry(){
		return MChineseTags.entitiesPattern.toString();
	}
	public boolean addEntiry(String NamedEntiry){
		return MChineseTags.add("entitiesPattern", NamedEntiry);	
	}
	public boolean deleteEntiry(String NamedEntiry){
		return MChineseTags.delete("entitiesPattern", NamedEntiry);	
	}
	

	
}
