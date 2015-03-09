package com.shenji.search.bean;

import java.util.ArrayList;

public class SynonmBean {
	private String orcWord;
	private ArrayList<String> synonmWords=new ArrayList<String>();
	
	public String getOrcWord() {
		return orcWord;
	}
	public void setOrcWord(String orcWord) {
		this.orcWord = orcWord;
	}
	public ArrayList<String> getSynonmWords() {
		return synonmWords;
	}
	public String get(int i){
		return this.synonmWords.get(i);
	}
	public void add(String s){
		this.synonmWords.add(s);
	}
	
}
