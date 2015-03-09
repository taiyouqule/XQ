package com.shenji.onto.reasoner.regress;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.shenji.onto.reasoner.data.FaqReasonerTree;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.interact.InteractCommon;
import com.shenji.onto.reasoner.interact.ReasonerInteractBean;
import com.shenji.onto.reasoner.interact.ReasonerMachine;
import com.shenji.onto.reasoner.interact.ReasonerReturnBean;
import com.shenji.onto.reasoner.interact.InteractCommon.ResultMark;
import com.shenji.onto.reasoner.server.ReasonerServer;


public class ReasonerRegressOnlyRoot extends ReasonerRegress {

	public ReasonerRegressOnlyRoot(ReasonerMachine state) {
		super(state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean regress(ReasonerReturnBean returnBean, Object reObj) {
		// TODO Auto-generated method stub
		boolean reBool = true;
		if (returnBean.isInMarkSet(InteractCommon.ResultMark.ONLY_THING)) {
			List<String> reList = (List<String>) (reObj);
			String reStr=doKnowledge();
			addMessage(reList, 101, reStr,
					InteractCommon.ResultClassify.Abstract);
			reBool = false;
		}
		return reBool;
	}

	private String doKnowledge() {
		OntModel model = ReasonerServer.getInstance().getOnlyOntMode();
		// 得到模型根节点(root)
		OntClass rootOntClass = model
				.getOntClass("http://www.w3.org/2002/07/owl#Thing");
		StringBuilder builder=new StringBuilder();
		builder.append("您好，我的能力有限，不能很好理解您的问题。请问您咨询的是关于本公司以下哪项业务：");
		int count=1;
		Iterator<OntClass> iterator = model.listNamedClasses();
		while (iterator.hasNext()) {
			OntClass subOntClass = (OntClass) iterator.next();
			if ((subOntClass.hasSuperClass(rootOntClass, false)
					|| !subOntClass.hasSuperClass())&&
					subOntClass.getVersionInfo()==null) {//没有父类或者父类是THING,这是模型的第一层(是不是jenaBUG),并且不是其他词定义的同义词
				builder.append((count++)+"、"+subOntClass.getLocalName()+";");
			}
		}	
		builder.delete(builder.length()-1, builder.length());
		builder.append("。可以直接回复数字。谢谢！");
		return builder.toString();
	}

}
