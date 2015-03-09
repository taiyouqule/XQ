package com.shenji.onto.reasoner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.data.ReasonerTree;


public class ReasonerFilter extends ReasonerComplex {
	@Override
	public DoOneFaqResult doOneFaqReasoning(Object... obj) {
		// TODO Auto-generated method stub
		ReasonerTree[] userTrees = (ReasonerTree[]) obj[0];
		ReasonerTree[] faqTrees = (ReasonerTree[]) obj[1];
		List<String> reFaqList = (List<String>) obj[2];
		String faq = (String) obj[3];
		if (userTrees[0].isNullTree()) {
			// relevant = 0;
			return DoOneFaqResult.Return;
		}
		int relevant = userTrees[0].isSubTree(faqTrees[0]);
		if (relevant >= 0) {
			reFaqList.add(faq + "#" + relevant);
		}
		return DoOneFaqResult.Continue;
	}

	@Override
	public boolean doAfterReasoning(Object... obj) {
		// TODO Auto-generated method stub
		List<String> reFaqList = (List<String>) obj[0];
		if (reFaqList != null && reFaqList.size() > 0
				&& reFaqList.get(0) != null) {
			ReasonerComplex.ListCompara compara = new ListCompara();
			Collections.sort(reFaqList, compara);
			return true;
		} else
			return false;
	}

	

}
