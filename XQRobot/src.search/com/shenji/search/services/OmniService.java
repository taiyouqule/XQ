package com.shenji.search.services;

public class OmniService {
	public static void setOmniBoost(StringBuilder sb,float weight){
		  sb.append("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" name=\"ommiboost\" content=\""+weight+"\">");
		  //System.out.println(sb.toString());
	}
	public static float getOmniBoost(String[] score){
		float sum=0;
		if(score==null)
			return 0.5f;
		for(String s:score){
			sum=sum+(Float.valueOf(s)/6f);
		}
		sum=sum/score.length;
		return sum;
	}
	

	public static void main(String[] str){
		StringBuilder sb=new StringBuilder();
		OmniService.setOmniBoost(sb, 3f);
	}
}
