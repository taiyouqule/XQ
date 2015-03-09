package com.shenji.search.strategy;

import java.util.Comparator;

import com.shenji.search.bean.SearchBean;

public class SimilarityComparator<T extends SearchBean> implements Comparator<T> {
	
	public int compare(T o1, T o2) {
		// TODO Auto-generated method stub
		SearchBean bean1=(SearchBean)o1;
		SearchBean bean2=(SearchBean)o2;
		//从大到小排列
		if(bean1.getSimilarity()==bean2.getSimilarity()){
			return new ScoreComparator<SearchBean>().compare(bean1, bean2);
		}
		else if(bean1.getSimilarity()>bean2.getSimilarity())
			return -1;
		else
			return 1;
	}

}
