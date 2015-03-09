package com.shenji.onto.reasoner.interact;


public class ReasonerState{
	protected ReasonerMachine machine;

	public ReasonerState(ReasonerMachine machine) {
		this.machine = machine;
	}

	protected void setNextState(ReasonerState nextState) {
		// Log.debugSystemOut(nextStep.getClass().getName());
		this.machine.SetState(nextState);
	}

	protected void finish() {
		this.machine.setFinish(true);
	}

	

}
