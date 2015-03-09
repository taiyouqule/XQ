package com.shenji.nlp;

import com.shenji.common.log.Log;
import com.shenji.nlp.inter.IChineseWordSegmentation;

import edu.fudan.ml.types.Dictionary;
import edu.fudan.ml.types.Instance;
import edu.fudan.nlp.cn.Chars;
import edu.fudan.nlp.cn.tag.AbstractTagger;
import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.util.exception.LoadModelException;

/**
 * 中文词性分析
 * @author zhq
 *
 */
public class MPOSTagger extends POSTagger{
	private IChineseWordSegmentation segment;
	public MPOSTagger(String src,IChineseWordSegmentation segment) throws LoadModelException{
		super(src);
		this.segment=segment;
	}
	
	public MPOSTagger(String src,Dictionary dict,IChineseWordSegmentation segment) throws LoadModelException{
		super(src,dict);
		this.segment=segment;
	}
	
	//词性标注
	@Override
	public String tag(String sentence) {
		String[] words=null;
		try {
			words = segment.segment(sentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
			return null;
		}
		String[] speechs = super.tagSeged(words);
		String res = format(words, speechs);
		return res;	
	}
	
	public String[] tagSeged(String sentence) {
		String[] words=null;
		try {
			words = segment.segment(sentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
			return null;
		}
		String[] speechs = super.tagSeged(words);
		return speechs;	
	}
	
	

	

}
