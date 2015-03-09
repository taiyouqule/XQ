package com.shenji.onto.reasoner.estimate;

import com.shenji.onto.reasoner.inter.IReasonerLimit;
import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.InteractCommon.ReasultState;
import com.shenji.onto.reasoner.interact.InteractCommon.ResultClassify;


public class ReasonerEstimateFaqSubTree extends ReasonerEstimate {

	public ReasonerEstimateFaqSubTree(ReasonerMachine state) {
		super(state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean esitimate(ReasonerInteractBean interactBean,ReasonerReturnBean returnBean){	
		boolean reBool = true;
		// 用户推理树是FAQ推理是的子树
		if (interactBean.getRelevance() < -1
				&& interactBean.getRelevance() > -2) {
			IReasonerLimit.Level maxLevel=IReasonerLimit.Level.Level_3;
			IReasonerLimit.Level minLevel=IReasonerLimit.Level.Level_2;
			Enum<InteractCommon.ResultClassify> reasult=comEstimate(interactBean,maxLevel,minLevel);
			if (reasult.equals(InteractCommon.ResultClassify.Exact)) {
				returnBean.getResultMap().put(InteractCommon.ReasultState.Exact_FaqSub,
						interactBean.getFaqMessage());
			} else if (reasult.equals(InteractCommon.ResultClassify.NunAbstract)) {
				returnBean.getResultMap().put(InteractCommon.ReasultState.NAbstract_L2,
						interactBean.getFaqMessage());
			} else if (reasult.equals(InteractCommon.ResultClassify.Abstract)) {
				returnBean.getResultMap().put(InteractCommon.ReasultState.Abstract_L2,
						interactBean.getFaqMessage());
			}
			reBool = false;
		}
		return reBool;
	}

	
}
