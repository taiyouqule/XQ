package com.shenji.onto.reasoner.data;

import java.util.Comparator;

import com.shenji.search.bean.SearchBean;
import com.shenji.search.strategy.SimilarityComparator;

public class OntoDimensionComparator implements Comparator<SearchOntoBean> {

	@Override
	public int compare(SearchOntoBean bean1, SearchOntoBean bean2) {
		// TODO Auto-generated method stub
		if(bean1.getOntoDimension()==bean2.getOntoDimension()){
			return new SimilarityComparator<SearchBean>().compare(bean1, bean2);
		}
		else if (bean1.getOntoDimension() > bean2.getOntoDimension())
			return -1;
		else
			return 1;

	}

}
