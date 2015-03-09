package com.shenji.onto.reasoner.estimate;

import com.shenji.onto.reasoner.inter.IReasonerLimit;
import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.ReasonerState;



public abstract class ReasonerEstimate extends ReasonerState{
	public ReasonerEstimate(ReasonerMachine machine) {
		super(machine);
	}

	
	public abstract boolean esitimate(ReasonerInteractBean interactBean,
			ReasonerReturnBean returnBean);
	
	
	public void doEstimate(ReasonerInteractBean interactBean,
			ReasonerReturnBean returnBean) {
		if (esitimate(interactBean, returnBean)) {
			this.setNextState(this.machine.getNextState(this.getClass()));
		} else
			this.finish();
	}
	

	public Enum<InteractCommon.ResultClassify> comEstimate(
			ReasonerInteractBean interactBean,IReasonerLimit.Level maxLevel,IReasonerLimit.Level minLevel) {
		// 得分>L1 || 相似度>L1 --> 是一条精确值
		if (interactBean.getScore() >= this.machine.getLimit().getScore(
				maxLevel)
				|| interactBean.getSimiliraty() >= this.machine.getLimit()
						.getSimiliraty(maxLevel)) {
			return InteractCommon.ResultClassify.Exact;
		}
		// 得分<L1 && 相似度<L1 --> 是一条完全抽象值
		else if (interactBean.getScore() <= this.machine.getLimit().getScore(
				minLevel)
				&& interactBean.getSimiliraty() <= this.machine.getLimit()
						.getSimiliraty(minLevel)) {
			return InteractCommon.ResultClassify.Abstract;
		}
		// 得分>=L2 && 得分<L1 --> 是一条半抽象值
		else if (interactBean.getScore() < this.machine.getLimit().getScore(
				maxLevel)
				&& interactBean.getScore() >= this.machine.getLimit().getScore(
						minLevel)) {
			return InteractCommon.ResultClassify.NunAbstract;
		}
		// 相似度>=L2 && 相似度<L1 --> 是一条半抽象值
		else if (interactBean.getSimiliraty() < this.machine.getLimit()
				.getSimiliraty(maxLevel)
				&& interactBean.getSimiliraty() >= this.machine.getLimit()
						.getSimiliraty(minLevel)) {
			return InteractCommon.ResultClassify.NunAbstract;
		} else
			return null;
	}

}
