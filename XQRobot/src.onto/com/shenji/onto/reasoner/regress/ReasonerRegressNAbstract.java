package com.shenji.onto.reasoner.regress;

import java.util.List;

import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;


public class ReasonerRegressNAbstract extends ReasonerRegress {

	public ReasonerRegressNAbstract(ReasonerMachine machine) {
		super(machine);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean regress(ReasonerReturnBean returnBean, Object reObj) {
		// TODO Auto-generated method stub
		boolean reBool = true;
		InteractCommon.ReasultState key = null;
		List<String> reList = (List<String>) (reObj);
		// addAll按顺序，这时候reList里面是按照精确等级排序
		if (returnBean.isInResultMap(InteractCommon.ReasultState.NAbstract_L1)) {
			key = InteractCommon.ReasultState.NAbstract_L1;
			reList.addAll(returnBean.getResultMapList(key));
			reBool = false;
		}
		if (returnBean.isInResultMap(InteractCommon.ReasultState.NAbstract_L2)) {
			key = InteractCommon.ReasultState.NAbstract_L2;
			reList.addAll(returnBean.getResultMapList(key));
			reBool = false;
		}
		if (returnBean.isInResultMap(InteractCommon.ReasultState.NAbstract_L3)) {
			key = InteractCommon.ReasultState.NAbstract_L3;
			reList.addAll(returnBean.getResultMapList(key));
			reBool = false;

		}
		if (reBool == false && key != null)
			addMessage(reList, key.getNum(), "根据您的问题，我们为您挑选了一些类似问题，请你看看是否满足需求？",
					InteractCommon.ResultClassify.NunAbstract);
		return reBool;
	}

}
