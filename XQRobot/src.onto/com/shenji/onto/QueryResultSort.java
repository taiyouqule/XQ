package com.shenji.onto;

import java.util.Comparator;

import com.shenji.onto.editer.server.OntologyManage;


public class QueryResultSort implements Comparator<String>{
	private OntologyManage manage;
	public QueryResultSort(OntologyManage manage){
		this.manage=manage;
	}
	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		if(!o1.contains("IsOthersSynonyms"))
			return 1;
		else
			return -1;
	}
	
}