package com.shenji.onto.reasoner.estimate;

import java.util.List;

import com.shenji.onto.reasoner.inter.IReasonerLimit;
import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.InteractCommon.ReasultState;
import com.shenji.onto.reasoner.interact.InteractCommon.ResultClassify;


public class ReasonerEstimateSameTree extends ReasonerEstimate {
	public ReasonerEstimateSameTree(ReasonerMachine state) {
		super(state);
	}

	@Override
	public boolean esitimate(ReasonerInteractBean interactBean,
			ReasonerReturnBean returnBean) {
		boolean reBool = true;
		// 拥有完全一致的推理树
		if (Math.abs(interactBean.getRelevance()) == 1) {
			IReasonerLimit.Level maxLevel = IReasonerLimit.Level.Level_2;
			IReasonerLimit.Level minLevel = IReasonerLimit.Level.Level_1;
			Enum<InteractCommon.ResultClassify> reasult = comEstimate(
					interactBean, maxLevel, minLevel);
			if (reasult.equals(InteractCommon.ResultClassify.Exact)) {
				returnBean.getResultMap().put(
						InteractCommon.ReasultState.Exact_SameTree,
						interactBean.getFaqMessage());
			} else if (reasult
					.equals(InteractCommon.ResultClassify.NunAbstract)) {
				returnBean.getResultMap().put(
						InteractCommon.ReasultState.NAbstract_L1,
						interactBean.getFaqMessage());
			} else if (reasult.equals(InteractCommon.ResultClassify.Abstract)) {
				returnBean.getResultMap().put(
						InteractCommon.ReasultState.Abstract_L1,
						interactBean.getFaqMessage());
			}
			reBool = false;
		}
		return reBool;

	}

}
