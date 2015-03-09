package com.shenji.webservices.port;

import java.io.IOException;

import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyServer;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;

public class OntoMapping {

	private OntologyServer ontologyServer;

	public OntoMapping() {
		this.ontologyServer = new OntologyServer();

	}

	public String[] getAllFaqIndividual() {
		// TODO Auto-generated method stub
		return FaqMapServices.getInstance().getQueryInter()
				.getAllFaqIndividual();
	}

	public String[][] getFaqDataProperty(String individualName) {
		// TODO Auto-generated method stub
		return FaqMapServices.getInstance().getQueryInter()
				.getDataProperty(individualName);
	}

	public String getBaseXml() {
		// TODO Auto-generated method stub
		return FaqMapServices.getInstance().getBaseXmlns();
	}

	public int reNameFaqIndividual(String oldFaqId, String newFaqId) {
		// TODO Auto-generated method stub
		int reFlag = FaqMapServices.getInstance().reNameFaqIndividual(oldFaqId,
				newFaqId);
		if (reFlag == 1) {
			ReasonerTreeServer.getInstance().deleteFaqTree(oldFaqId);
			ReasonerTreeServer.getInstance().addFaqTree(newFaqId);
		}
		return reFlag;
	}

	public String[][] getFAQContext(String individualName) {
		// TODO Auto-generated method stub
		try {
			return FaqMapServices.getInstance().getQueryInter()
					.getFAQContext(individualName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String[] getRDFType(String individualName) {
		// TODO Auto-generated method stub
		return FaqMapServices.getInstance().getQueryInter()
				.getRDFType(individualName);
	}

	public int addOWLFaqDataProperty(String token, String individualName,
			String import_propertyName, String import_propertyValue) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		JenaOWLModel owlModel = manage.getOwlModel();
		return FaqMapServices
				.getInstance()
				.getAddInter()
				.addOWLFaqDataProperty(owlModel, individualName,
						import_propertyName, import_propertyValue);
	}

	public int addOWLFAQType(String token, String individualName,
			String importClassName) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		JenaOWLModel owlModel = manage.getOwlModel();
		int reFlag = FaqMapServices.getInstance().getAddInter()
				.addOWLFAQType(owlModel, individualName, importClassName);
		if (reFlag == 1) {
			ReasonerTreeServer.getInstance().updateFaqTree(individualName);
		}
		return reFlag;
	}

	public int deleteOWLFaqDataProperty(String importUrl,
			String individualName, String importPropertyName,
			String importPropertyValue) {
		// TODO Auto-generated method stub
		return FaqMapServices
				.getInstance()
				.getDeleteInter()
				.deleteOWLFaqDataProperty(importUrl, individualName,
						importPropertyName, importPropertyValue);
	}

	public int deleteOWLFAQType(String importUrl, String individualName,
			String importClassName) {
		// TODO Auto-generated method stub
		int reFlag = FaqMapServices.getInstance().getDeleteInter()
				.deleteOWLFAQType(importUrl, individualName, importClassName);
		if (reFlag == 1) {
			ReasonerTreeServer.getInstance().updateFaqTree(individualName);
		}
		return reFlag;
	}

	public boolean reBuildFAQ() {
		// TODO Auto-generated method stub
		synchronized (OntoEdit.class) {
			return FaqMapServices.getInstance().reBuildFaqDB();
		}
	}

	public boolean initBuildFaqDB(String passWord) {
		// TODO Auto-generated method stub
		synchronized (OntoEdit.class) {
			boolean reFlag = false;
			if (passWord.equals(Configuration.testPassWord)) {
				reFlag = FaqMapServices.getInstance().initBuildFaqDB();
				if (reFlag == true)
					FaqMapServices.getInstance().resetOntModel();
			}
			return reFlag;
		}
	}

	public String getOntoBaseURL() {
		// TODO Auto-generated method stub
		return Configuration.getOntoBaseURL();
	}

	public String[] getAllFaqIndividualAndQ(int treeHight) {
		// TODO Auto-generated method stub
		return FaqMapServices.getInstance().getQueryInter()
				.getAllFaqIndividualAndQ(treeHight);
	}

	public String[] getFaqIndividualByClass(String token, String ontoClassName,
			int treeHight) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		JenaOWLModel owlModel = manage.getOwlModel();
		String path = owlModel.getNamespaceManager().getDefaultNamespace();
		return FaqMapServices.getInstance().getQueryInter()
				.getFaqIndividualByClass(path, ontoClassName, treeHight);
	}

	public int createFaqIndividual(String url) {
		// TODO Auto-generated method stub
		int reFlag = FaqMapServices.getInstance().createFaqIndividual(url);
		if (reFlag == 1) {
			String faqId = FaqMapServices.analysisHrefTogetName(url);
			ReasonerTreeServer.getInstance().addFaqTree(faqId);
		}
		return reFlag;

	}

	public void deleteFaqIndividual(String faqId) {
		// TODO Auto-generated method stub
		boolean reFlag = FaqMapServices.getInstance()
				.deleteFaqIndividual(faqId);
		if (reFlag) {
			ReasonerTreeServer.getInstance().deleteFaqTree(faqId);
		}
	}
}
