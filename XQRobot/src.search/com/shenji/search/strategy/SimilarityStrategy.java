package com.shenji.search.strategy;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.shenji.search.bean.SearchBean;
import com.shenji.search.exception.EngineException;

abstract class SimilarityStrategy {
	public abstract void setSimilarity(List<? extends SearchBean> result) throws EngineException;
	public void sort(Comparator<? super SearchBean> comparator,List<? extends SearchBean> beans){
		if(beans!=null){
			Collections.sort(beans,comparator);
		}
	}
}
