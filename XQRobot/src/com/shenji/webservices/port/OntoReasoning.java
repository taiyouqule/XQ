package com.shenji.webservices.port;

import java.util.ArrayList;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyServer;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.reasoner.data.CustomReaonerTree;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;

public class OntoReasoning {
	public final static Object OntoReasoning_LOCK = new Object();// 全局锁
	private OntologyServer ontologyServer;

	public OntoReasoning() {
		this.ontologyServer = new OntologyServer();
	}

	public class AutomaticClassificationThread implements Runnable {
		private OntologyManage manage;

		public AutomaticClassificationThread(OntologyManage manage) {
			this.manage = manage;
		}

		public void run() {
			// TODO Auto-generated method stub
			int reFlag = FaqMapServices.getInstance().AutomaticClassification(
					manage);
			boolean state = false;
			if (reFlag == 1) {
				state = true;
			}
			String msg = ontologyServer
					.createAutomaticClassificationNotifyMessage(state);
			ontologyServer.notifyMySelf(reFlag, manage, msg,
					OntologyManage.getUserName(manage.getToken()));
		}

	}

	public synchronized void automaticClassification(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return;
		new Thread(new AutomaticClassificationThread(manage)).start();
	}

	public String[] getFaqReasonerTreeXml(String individualName) {
		// TODO Auto-generated method stub
		// 加上前缀
		if (!individualName
				.startsWith(Configuration.FaqMappingCommon.INDIVIDUAL_HEAD)) {
			individualName = Configuration.FaqMappingCommon.INDIVIDUAL_HEAD
					+ individualName;
		}
		ReasonerTree[] trees = ReasonerTreeServer.getInstance()
				.getFaqReasonerTree(individualName);
		if (trees == null || trees.length == 0)
			return null;
		List<String> list = new ArrayList<String>();
		for (ReasonerTree tree : trees) {
			list.add(tree.toXmlString("GBK"));
		}
		return list.toArray(new String[list.size()]);
	}

	public String getUserReasonerTreeXml(String[] words) {
		// TODO Auto-generated method stub
		ReasonerTree[] trees = ReasonerTreeServer.getInstance()
				.getUserReasonerTree(words);
		if (trees == null || trees.length == 0)
			return null;
		else {
			String reXmls = trees[0].toXmlString("GBK");
			for (ReasonerTree tree : trees) {
				tree.clear();
			}
			trees = null;
			return reXmls;
		}
	}

	public String getUserReasonerTreeXmlBySentence(String sentence) {
		// TODO Auto-generated method stub
		ReasonerTree[] trees = ReasonerTreeServer.getInstance()
				.getUserReasonerTree(sentence);
		if (trees == null || trees.length == 0)
			return null;
		else {
			String reXmls = trees[0].toXmlString("GBK");
			for (ReasonerTree tree : trees) {
				tree.clear();
			}
			trees = null;
			return reXmls;
		}
	}

	public boolean reBuildFaqReasonerTree() {
		// TODO Auto-generated method stub
		synchronized(OntoReasoning_LOCK){
			return ReasonerTreeServer.getInstance().reBuildFaqReasonerTree();
		}		
	}

	public String getBigFaqReasonerXml() {
		// TODO Auto-generated method stub
		ReasonerTree tree = ReasonerTreeServer.getInstance().getBigFaqTree();
		if (tree != null)
			return ((CustomReaonerTree) tree).toXmlString("GBK");
		else
			return null;
	}

	public String getOntologyNamedClassXmlList(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getOntologyNamedClassXmlList(
				"GBK");
	}

}
