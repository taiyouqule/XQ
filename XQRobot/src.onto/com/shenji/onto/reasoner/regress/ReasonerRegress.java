package com.shenji.onto.reasoner.regress;

import java.util.List;

import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.ReasonerState;


public abstract class ReasonerRegress extends ReasonerState {

	public ReasonerRegress(ReasonerMachine machine) {
		super(machine);
		// TODO Auto-generated constructor stub
	}

	public abstract boolean regress(ReasonerReturnBean returnBean, Object reObj);

	public void doRegree(ReasonerReturnBean returnBean, Object reObj) {
		if (regress(returnBean, reObj)) {
			this.setNextState(this.machine.getNextState(this.getClass()));
		} else
			this.finish();
	}

	public boolean addMessage(List<String> reList,int key,String message,Enum<InteractCommon.ResultClassify> messageType){
		String title = null ;
		if(messageType.equals(InteractCommon.ResultClassify.Exact)){
			title="<Exact#IdentifyCode" + key + ">";		
		}
		else if(messageType.equals(InteractCommon.ResultClassify.Abstract)){
			title="<Abstract#IdentifyCode" + key + ">";		
		}
		if(messageType.equals(InteractCommon.ResultClassify.NunAbstract)){
			title="<NAbstract#IdentifyCode" + key + ">";		
		}
		if(title!=null){
			title = title + message;
			reList.add(0, title);
			return true;
		}
		return false;
	}
	
	

}
