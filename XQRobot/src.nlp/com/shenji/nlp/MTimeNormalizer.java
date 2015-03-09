package com.shenji.nlp;

import com.shenji.common.log.Log;
import com.shenji.nlp.inter.IChineseWordSegmentation;

import edu.fudan.nlp.cn.ner.TimeNormalizer;
import edu.fudan.nlp.cn.ner.TimeUnit;
import edu.fudan.nlp.cn.tag.POSTagger;

/**
 * 时间表达式识别
 * @author zhq
 *
 */
public class MTimeNormalizer extends TimeNormalizer{
	public MTimeNormalizer(String src) throws Exception{
		super(src);
	}	
	
	public String[][] timeNormalizer(String sentence){
		this.parse(sentence);
		TimeUnit[] unit = this.getTimeUnit();
		String[][] reFlag=new String[unit.length][2];
		for(int i = 0; i < unit.length; i++){		
			reFlag[i][0]=unit[i].Time_Expression;
			reFlag[i][1]=unit[i].Time_Norm;
		}
		return reFlag;		
	}
}
