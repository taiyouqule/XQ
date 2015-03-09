package com.shenji.nlp;

import com.shenji.common.log.Log;
import com.shenji.nlp.common.Common;
import com.shenji.nlp.inter.IChineseWordSegmentation;


import edu.fudan.nlp.cn.anaphora.Anaphora;
import edu.fudan.nlp.cn.tag.POSTagger;

/**
 * 指代消除
 * @author zhq
 */
public class MAnaphora extends Anaphora{
	private IChineseWordSegmentation segment;
	private POSTagger tagger;
	public MAnaphora(String src,IChineseWordSegmentation segment,POSTagger tagger) throws Exception{
		super(tagger,src);
		this.segment=segment;
		this.tagger=tagger;
	}
	
	public String anaphora(String sentence){	
		String words[];
		try {
			words = segment.segment(sentence);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e1);
			e1.printStackTrace();
			return null;
		}
		String speechs[] = tagger.tagSeged(words);
		String strs[][][] = new String[1][2][words.length];
		strs[0][0] = words;
		strs[0][1] = speechs;	
		String result=null;
		try {
			result = this.resultToString(strs, sentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
			Log.getLogger().error(e.getMessage(),e);
			return null;
		}
		System.out.printf(result);
		return result;		
	}

}
