package com.shenji.onto.reasoner.estimate;

import com.shenji.onto.reasoner.inter.IReasonerLimit;
import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.InteractCommon.ReasultState;
import com.shenji.onto.reasoner.interact.InteractCommon.ResultClassify;




public class ReasonerEstimateAbstract extends ReasonerEstimate {

	public ReasonerEstimateAbstract(ReasonerMachine state) {
		super(state);
		// TODO Auto-generated constructor stub
	}


	@Override
	public boolean esitimate(ReasonerInteractBean interactBean,ReasonerReturnBean returnBean){	
		boolean reBool=true;
		//拥有的推理树
		if (interactBean.getRelevance()>0){
			IReasonerLimit.Level maxLevel=IReasonerLimit.Level.Level_4;
			IReasonerLimit.Level minLevel=IReasonerLimit.Level.Level_3;
			Enum<InteractCommon.ResultClassify> reasult=comEstimate(interactBean,maxLevel,minLevel);
			if(reasult.equals(InteractCommon.ResultClassify.Exact)){
				returnBean.getResultMap().put(InteractCommon.ReasultState.NAbstract_L1,interactBean.getFaqMessage());
			}
			else if(reasult.equals(InteractCommon.ResultClassify.NunAbstract)){
				returnBean.getResultMap().put(InteractCommon.ReasultState.NAbstract_L3,interactBean.getFaqMessage());
			}
			else if(reasult.equals(InteractCommon.ResultClassify.Abstract)){
				returnBean.getResultMap().put(InteractCommon.ReasultState.Abstract_L3,interactBean.getFaqMessage());
			}
			reBool=false;
		}
		return reBool;			
	}

	

}
