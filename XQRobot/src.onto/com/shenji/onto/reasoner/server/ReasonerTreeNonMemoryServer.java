package com.shenji.onto.reasoner.server;

import java.util.HashMap;
import java.util.Map;

import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.ReasonerLabel;
import com.shenji.onto.reasoner.data.ReasonerTree;


public class ReasonerTreeNonMemoryServer extends ReasonerTreeAbsServer{
	public ReasonerTreeNonMemoryServer(){
		super();
	}
	
	@Override
	public ReasonerTree[] getFaqReasonerTree(String individualName) {
		// TODO Auto-generated method stub
		return ReasonerTreeOperate.getFaqReasonerTree(individualName,true,true);
	}

	@Override
	public ReasonerTree[] getUserReasonerTree(String[] words) {
		// TODO Auto-generated method stub
		String[] expandWords=ReasonerLabel.getInstance().wordsLabelExpand(words);
		words=null;
		boolean isWithPossiblity=false;
		return ReasonerTreeOperate.getUserReasonerTree(expandWords,true,isWithPossiblity);
	}

	@Override
	public void deleteFaqTree(Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFaqTree(Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFaqTree(Object... obj) {
		// TODO Auto-generated method stub
		
	}

}
