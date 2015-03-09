package com.shenji.onto.reasoner.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.shenji.common.log.Log;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.reasoner.ReasonerLabel;
import com.shenji.onto.reasoner.data.ReasonerTree;


public class ReasonerTreeMemoryNoAutoServer extends ReasonerTreeAbsServer {
	private Map<String, ReasonerTree[]> map = null;

	public ReasonerTreeMemoryNoAutoServer() throws Exception {
		map = new HashMap<String, ReasonerTree[]>();
		this.initFaqsReasonerTrees();
	}


	public void clear() {
		if (this.map != null)
			this.map.clear();
	}

	private void initFaqsReasonerTrees() throws Exception {
		//Log.debugSystemOut(ReasonerServer.getInstance().getKnowledgeOntMode().listClasses());
		Iterator<Individual> iterator = FaqMapServices.getInstance()
				.getOntModel().listIndividuals();
		int count = 0;
		while (iterator.hasNext()) {
			Individual individual = (Individual) iterator.next();
			String faqId = individual.getLocalName();
			this.map.put(faqId, ReasonerTreeOperate.getFaqReasonerTree(faqId,true,true));
			System.out.print(".");
			count++;
			if (count != 0 && (count % 100 == 0))
				System.out.println(count + "");
		}
	}

	public void deleteFaqTree(Object... obj) {
		if(!(obj[0] instanceof String))
			return;
		String individualName=obj[0].toString();
		if (this.map.containsKey(individualName)){
			this.map.remove(individualName);
			Log.getLogger(this.getClass()).debug("删除了推理树" + individualName + ":" +this.map.get(individualName)[0].toString()
					+ "到内存中");
		}
	}

	public void addFaqTree(Object... obj) {
		if(!(obj[0] instanceof String))
			return;
		String individualName=obj[0].toString();
		if (!this.map.containsKey(individualName)) {
			ReasonerTree[] trees = ReasonerTreeOperate
					.getFaqReasonerTree(individualName,true,true);
			this.map.put(individualName, trees);
			Log.getLogger(this.getClass()).debug("添加了推理树" + individualName + ":" + trees[0].toString()
					+ "到内存中");
		}
	}

	public void updateFaqTree(Object... obj) {
		if(!(obj[0] instanceof String))
			return;
		String individualName=obj[0].toString();
		if (this.map.containsKey(individualName)) {
			ReasonerTree[] trees = ReasonerTreeOperate
					.getFaqReasonerTree(individualName,true,true);
			this.map.put(individualName, trees);
			Log.getLogger(this.getClass()).debug("修改了推理树" + individualName + ":"
					+ this.map.get(individualName)[0].toString() + "-->"
					+ trees[0].toString() + "到内存中");
		}
	}

	@Override
	public ReasonerTree[] getFaqReasonerTree(String individualName) {
		// TODO Auto-generated method stub
		if (this.map.containsKey(individualName)){
			ReasonerTree[] trees=this.map.get(individualName);
			//System.out.println("[输出内存树模型]"+trees[0].toString());
			return trees;
		}
		else
			return null;
	}

	@Override
	public ReasonerTree[] getUserReasonerTree(String[] words) {
		// TODO Auto-generated method stub
		String[] expandWords=ReasonerLabel.getInstance().wordsLabelExpand(words);
		words=null;
		boolean isWithPossiblity=false;
		return ReasonerTreeOperate.getUserReasonerTree(expandWords,true,isWithPossiblity);
	}

}
