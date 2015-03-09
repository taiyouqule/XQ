package com.shenji.onto.reasoner.server;

import java.util.List;
import java.util.Set;

import com.shenji.onto.reasoner.data.ReasonerRoute;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.search.bean.SearchBean;


public abstract class ReasonerTreeAbsServer {
	public ReasonerTreeAbsServer(){
	}
	
	public abstract ReasonerTree[] getFaqReasonerTree(String individualName);

	public abstract ReasonerTree[] getUserReasonerTree(String[] words);

	public abstract void deleteFaqTree(Object... obj);

	public abstract void addFaqTree(Object... obj);

	public abstract void updateFaqTree(Object... obj);

	public Set<String> filter(ReasonerTree tree) {
		return null;
	}

}
