package com.shenji.onto.reasoner.context;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.shenji.onto.reasoner.data.UserReasonerTree;


public class InteractContext {
	private UserReasonerTree[] userReasonerTrees;
	private String uId;
	private LinkedList<Date> dateList;

	public InteractContext(String uid,Date startDate,UserReasonerTree[] trees) {
		this.uId = uid;
		this.userReasonerTrees = trees;
		this.dateList = new LinkedList<Date>();
		this.dateList.add(startDate);
	}

	public void clear() {
		if (this.userReasonerTrees != null)
			for (UserReasonerTree tree : userReasonerTrees) {
				tree.clear();
			}
		if (this.dateList != null)
			dateList.clear();
	}
}
