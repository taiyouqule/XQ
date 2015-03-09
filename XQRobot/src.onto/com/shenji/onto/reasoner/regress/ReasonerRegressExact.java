package com.shenji.onto.reasoner.regress;

import java.util.List;

import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;


public class ReasonerRegressExact extends ReasonerRegress {

	public ReasonerRegressExact(ReasonerMachine machine) {
		super(machine);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean regress(ReasonerReturnBean returnBean, Object reObj) {
		// TODO Auto-generated method stub
		boolean reBool = true;
		InteractCommon.ReasultState key = null;
		List<String> reList = (List<String>) (reObj);
		if (returnBean.isInResultMap(InteractCommon.ReasultState.Exact_SameTree)) {
			key = InteractCommon.ReasultState.Exact_SameTree;
			reList.addAll(returnBean.getResultMapList(key));
			reBool = false;
			// return
			// addMessage(reList,key,null,InteractResultCommon.ResultClassify.Exact);
		}
		if (returnBean.isInResultMap(InteractCommon.ReasultState.Exact_UserSub)) {
			key = InteractCommon.ReasultState.Exact_UserSub;
			reList.addAll(returnBean.getResultMapList(key));
			reBool = false;
			// return
			// addMessage(reList,key,null,InteractResultCommon.ResultClassify.Exact);
		}
		if (returnBean.isInResultMap(InteractCommon.ReasultState.Exact_FaqSub)) {
			key = InteractCommon.ReasultState.Exact_FaqSub;
			reList.addAll(returnBean.getResultMapList(key));
			reBool = false;
			// return
			// addMessage(reList,key,null,InteractResultCommon.ResultClassify.Exact);

		}
		if (reBool == false && key != null)
			addMessage(reList, key.getNum(), null,
					InteractCommon.ResultClassify.Exact);
		return reBool;
	}

}
