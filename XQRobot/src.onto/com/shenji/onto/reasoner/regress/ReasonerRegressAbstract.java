package com.shenji.onto.reasoner.regress;

import java.util.List;

import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;


public class ReasonerRegressAbstract extends ReasonerRegress {

	public ReasonerRegressAbstract(ReasonerMachine machine) {
		super(machine);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean regress(ReasonerReturnBean returnBean, Object reObj) {
		// TODO Auto-generated method stub
		boolean reBool = true;
		InteractCommon.ReasultState key;
		List<String> reList = (List<String>) (reObj);
		if (returnBean.isInResultMap(InteractCommon.ReasultState.Abstract_L1)) {
			key = InteractCommon.ReasultState.Abstract_L1;
			reList.addAll(returnBean.getResultMapList(key));
			return addMessage(reList, key.getNum(), "太抽象了!",
					InteractCommon.ResultClassify.Abstract);

		}
		if (returnBean.isInResultMap(InteractCommon.ReasultState.Abstract_L2)) {
			key = InteractCommon.ReasultState.Abstract_L2;
			reList.addAll(returnBean.getResultMapList(key));
			return addMessage(reList, key.getNum(), "太抽象了!",
					InteractCommon.ResultClassify.Abstract);
		}
		if (returnBean.isInResultMap(InteractCommon.ReasultState.Abstract_L3)) {
			key = InteractCommon.ReasultState.Abstract_L3;
			reList.addAll(returnBean.getResultMapList(key));
			return addMessage(reList, key.getNum(), "太抽象了!",
					InteractCommon.ResultClassify.Abstract);
		}
		return reBool;
	}

	private String abs_L1() {

		return null;
	}

}
