package com.shenji.onto.reasoner.estimate;

import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.InteractCommon.ResultMark;


public class ReasonerEstimateOnlyRoot extends ReasonerEstimate {

	public ReasonerEstimateOnlyRoot(ReasonerMachine state) {
		super(state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean esitimate(ReasonerInteractBean interactBean,ReasonerReturnBean returnBean){	
		boolean reBool = true;
		// 无本体概念 只有Thing节点	
		if (interactBean.getUserTree().isNullTree()) {
			returnBean.addMark(InteractCommon.ResultMark.ONLY_THING);
			reBool=false;
		}
		return reBool;
	}

	
}
