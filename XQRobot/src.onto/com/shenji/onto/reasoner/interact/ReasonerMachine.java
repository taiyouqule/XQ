package com.shenji.onto.reasoner.interact;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Queue;

import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.estimate.ReasonerEstimate;
import com.shenji.onto.reasoner.inter.IReasonerLimit;
import com.shenji.onto.reasoner.regress.ReasonerRegress;


public class ReasonerMachine {
	private ReasonerState state;
	private boolean finish = false;
	private IReasonerLimit limit;
	private Queue<String> stateQueue;
	private ReasonerReturnBean returnBean = null;
	private Class machineModel;

	public ReasonerMachine() {
		this.returnBean = new ReasonerReturnBean();
		this.stateQueue = new LinkedList<String>();
		this.limit = new ReasonerLimitSimple();
		// 初始为[评估模式]
		this.setMachineMode(ReasonerEstimate.class);

	}

	public void reSetEstimateMode() {
		this.setMachineMode(ReasonerEstimate.class);
	}

	public void setMachineMode(Class machineModelclass) {
		setFinish(false);
		this.machineModel = machineModelclass;
		if (machineModelclass.getName()
				.equals(ReasonerEstimate.class.getName())) {
			initEstimateQueue();
			this.state = this.getNextState(machineModelclass);
		} else if (machineModelclass.getName().equals(
				ReasonerRegress.class.getName())) {
			initRegressQueue();
			this.state = this.getNextState(machineModelclass);
		}
	}

	public void clear() {
		if (this.stateQueue != null)
			this.stateQueue.clear();
		if (this.limit != null)
			this.limit = null;
		if (this.returnBean != null)
			this.returnBean.clear();
	}

	private void initEstimateQueue() {
		// 这里应该读配置或者用spring
		if (this.stateQueue != null)
			this.stateQueue.clear();
		this.stateQueue.add("ReasonerEstimateOnlyRoot");
		this.stateQueue.add("ReasonerEstimateSameTree");
		this.stateQueue.add("ReasonerEstimateFaqSubTree");
		this.stateQueue.add("ReasonerEstimateUserSubTree");
		this.stateQueue.add("ReasonerEstimateAbstract");
	}

	private void initRegressQueue() {
		// 这里应该读配置或者用spring
		if (this.stateQueue != null)
			this.stateQueue.clear();
		this.stateQueue.add("ReasonerRegressOnlyRoot");
		this.stateQueue.add("ReasonerRegressExact");
		this.stateQueue.add("ReasonerRegressNAbstract");
		this.stateQueue.add("ReasonerRegressAbstract");
	}

	@SuppressWarnings("unchecked")
	public ReasonerState getNextState(Class class_) {
		String stepName = this.stateQueue.poll();
		String packName = class_.getPackage().getName();
		try {
			if (stepName == null)
				return null;
			// Log.debugSystemOut(packName + "." + stepName);
			Class<ReasonerState> step = (Class<ReasonerState>) Class
					.forName(packName + "." + stepName);
			Constructor<ReasonerState> constructor = step
					.getDeclaredConstructor(new Class[] { ReasonerMachine.class });
			ReasonerState reasonerState = constructor.newInstance(this);
			return reasonerState;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e);
			return null;
		}
	}

	public IReasonerLimit getLimit() {
		return limit;
	}

	public void setLimit(IReasonerLimit limit) {
		this.limit = limit;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
		// 推理机结束，重置推理机
		/*if (this.finish == true) {
			this.setMachineMode(this.machineModel);
		}*/
	}

	public void SetState(ReasonerState state) {
		this.state = state;
	}

	public void estimate(ReasonerInteractBean interactBean) {
		 Log.getLogger(this.getClass()).debug("用户树：" + interactBean.getUserTree().toString());
		 Log.getLogger(this.getClass()).debug("FAQ树：" + interactBean.getFaqTree().toString());
		while (true) {
			if (isFinish())
				break;
			else {
				if (this.state == null)
					return;
				else
					((ReasonerEstimate) this.state).doEstimate(interactBean,
							this.returnBean);
			}

		}
	}

	public void regree(Object reObj) {
		while (true) {
			if (isFinish())
				break;
			else {
				if (this.state == null)
					return;
				else {
					((ReasonerRegress) this.state).doRegree(this.returnBean,
							reObj);
				}
			}
		}
	}
}
