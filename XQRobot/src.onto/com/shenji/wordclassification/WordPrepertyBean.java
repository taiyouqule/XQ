package com.shenji.wordclassification;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ctc.wstx.evt.WProcInstr;
import com.shenji.common.log.Log;
import com.shenji.common.util.ConfigurantionUtil;


public class WordPrepertyBean{
	public static final String DEFAULT="DEFAULT";
	public static final String LTP="LTP";
	public static final String USER_DEFINED="USER_DEFINED";
	private String word;
	//词汇来源 0:自定义 1:LTP 默认为自定义
	private String source=DEFAULT;
	//词性
	private String speech_en=null;	
	private String speech_ch=null;
	private int count=-1;
	//是否被占用,对词表应用
	private boolean used=false;
	
	
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSpeech_en() {
		return speech_en;
	}
	public void setSpeech_en(String speech_en) {
		this.speech_en = speech_en;
	}
	public String getSpeech_ch() {
		return speech_ch;
	}
	public void setSpeech_ch(String speech_ch) {
		this.speech_ch = speech_ch;
	}
	
	
	
}
