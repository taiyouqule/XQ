package com.shenji.onto.reasoner.data;

import com.shenji.search.bean.SearchBean;

public class SearchOntoBean extends SearchBean{
	public SearchOntoBean(){		
	}
	
	public SearchOntoBean(SearchBean bean){
		super();
		this.setAnswer(bean.getAnswer());
		this.setContent(bean.getContent());
		this.setFaqId(bean.getFaqId());
		this.setQuestion(bean.getQuestion());
		this.setSimilarity(bean.getSimilarity());
		this.setScore(bean.getScore());
		this.setPureContent(getPureContent());
	}
	/**
	 * 本体维度（相似度）
	 */
	private double ontoDimension=1;

	public double getOntoDimension() {
		return ontoDimension;
	}

	public void setOntoDimension(double ontoDimension) {
		this.ontoDimension = ontoDimension;
	} 
}
