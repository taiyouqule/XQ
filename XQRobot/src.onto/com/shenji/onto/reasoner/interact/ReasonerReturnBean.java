package com.shenji.onto.reasoner.interact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.data.ResultMap;
import com.shenji.onto.reasoner.data.UserReasonerTree;
import com.shenji.onto.reasoner.server.ReasonerServer;

import jxl.biff.ContinueRecord;


public class ReasonerReturnBean {
	private ResultMap<InteractCommon.ReasultState, String> resultMap;
	private ReasonerTree reasonerTree;
	private Set<InteractCommon.ResultMark> markSet;

	public void addMark(InteractCommon.ResultMark mark) {
		this.markSet.add(mark);
	}
	
	public boolean isInMarkSet(InteractCommon.ResultMark mark){
		return this.markSet.contains(mark);
	}
	public boolean isInResultMap(InteractCommon.ReasultState key){
		if(this.resultMap.contains(key)){
			return true;
		}
		else
			return false;
	}
	
	public List<String> getResultMapList(InteractCommon.ReasultState key){
		if(isInResultMap(key)){
			return this.resultMap.getKey(key);
		}
		else
			return null;
	}

	public ReasonerReturnBean() {
		resultMap = new ResultMap<InteractCommon.ReasultState, String>();
		markSet = new LinkedHashSet<InteractCommon.ResultMark>();
	}

	public void clear() {
		if (resultMap != null)
			resultMap.clear();
		if (reasonerTree != null) {
			reasonerTree.clear();
		}
		if (markSet != null) {
			markSet.clear();
		}
	}

	public ResultMap<InteractCommon.ReasultState, String> getResultMap() {
		return this.resultMap;
	}

	public ReasonerTree getReasonerTree() {
		return reasonerTree;
	}

	public void setReasonerTree(ReasonerTree reasonerTree) {
		this.reasonerTree = reasonerTree;
	}

	/*public void getResult(List<String> reList) {
		if (this.markSet != null && this.markSet.size() != 0)
		{
			for (int mark : this.markSet) {
				if (mark == InteractResultCommon.ResultMark.ONLY_THING) {
					reList.add("<Abstract#IdentifyCode" + 101 + ">"
							+ "什么本体概念都没！我怎么推理呢！");
					break;
				}
			}
		}
		if (this.resultMap == null || this.resultMap.size() == 0)
			return;
		Iterator<Map.Entry<Integer, List<String>>> iterator = this.resultMap
				.iterator();
		if (iterator == null)
			return;
		boolean isContinue = true;
		while (iterator.hasNext()) {
			Map.Entry<Integer, List<String>> entry = iterator.next();
			int key = entry.getKey();
			List<String> list = entry.getValue();
			if (!isContinue) {
				break;
			}
			if (!ReasonerResultCheck.checkResult_Exact(key, list, reList)) {
				break;// 精确只要有符合就跳出
			}
			if (!ReasonerResultCheck.checkResult_NunAbstract(key, list, reList,
					isContinue)) {
				isContinue = false;
				continue;
				// 半抽象所以都级别都要走一遍
			}
			if (!isContinue)
				ReasonerResultCheck.checkResult_Abstract(key, list, reList,
						this.getReasonerTree());
		}
	}*/

	
}
