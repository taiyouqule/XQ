package com.shenji.onto.reasoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.shenji.common.log.Log;
import com.shenji.onto.Configuration;
import com.shenji.onto.reasoner.data.ReasonerRoute;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.data.UserReasonerTree;
import com.shenji.onto.reasoner.estimate.ReasonerEstimate;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.regress.ReasonerRegress;
import com.shenji.onto.reasoner.server.ReasonerServer;


public class ReasonerInteract extends ReasonerComplex {
	private ReasonerMachine reasonerMachine = null;

	// private ReasonerReturnBean returnBean = null;

	public ReasonerInteract() {
		this.reasonerMachine = new ReasonerMachine();
		// this.returnBean = new ReasonerReturnBean();
	}

	@Override
	public DoOneFaqResult doOneFaqReasoning(Object... obj) {
		// TODO Auto-generated method stub
		ReasonerTree[] userTrees = (ReasonerTree[]) obj[0];
		ReasonerTree[] faqTrees = (ReasonerTree[]) obj[1];
		// List<String> reFaqList = (List<String>) obj[2];

		String faqMessage = (String) obj[3];
		Object[] reObj = userTrees[0].relevanceAndMaxRoute(faqTrees[0]);
		double relevance = (Double) reObj[0];
		ReasonerRoute maxUserRoute = (ReasonerRoute) reObj[1];
		ReasonerRoute maxFaqRoute = (ReasonerRoute) reObj[2];

		ReasonerInteractBean interactBean = new ReasonerInteractBean(
				faqMessage, userTrees[0], faqTrees[0], maxUserRoute,
				maxFaqRoute);
		interactBean.setRelevance(relevance);

		// this.returnBean.setReasonerTree(userTrees[0]);
		this.reasonerMachine.estimate(interactBean);
		/* //一条FAQ推理结束，重置评价模式，进入下一条FAQ推理 */
		this.reasonerMachine.reSetEstimateMode();
		// this.reasonerMachine.setFinish(true);
		return DoOneFaqResult.Continue;
	}

	@Override
	public boolean doAfterReasoning(Object... obj) {
		// TODO Auto-generated method stub
		List<String> reFaqList = (List<String>) obj[0];
		// this.reasonerMachine.getResult(reFaqList);
		// 进入回归模式
		this.reasonerMachine.setMachineMode(ReasonerRegress.class);
		this.reasonerMachine.regree(reFaqList);
		this.reasonerMachine.clear();
		return true;

	}

}
