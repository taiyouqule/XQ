package com.shenji.onto.reasoner.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FaqReasonerNodeData extends ReasonerNodeData {
	//不要随便清理
	private Set<String> faqIdList;
	public FaqReasonerNodeData(String nodeName) {
		super(nodeName);
		faqIdList=new HashSet<String>();
		// TODO Auto-generated constructor stub
	}
	
	public void clear(){
		super.clear();
		if(this.faqIdList!=null)
			this.faqIdList.clear();
	}
	
	public void addFaqId(String faqId){
		if(this.faqIdList!=null&&!this.faqIdList.contains(faqId)){
			this.faqIdList.add(faqId);
		}
	}
	
	public Set<String> getFaqIdList(){
		return this.faqIdList;
	}
	

}
