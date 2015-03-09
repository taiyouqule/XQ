/*package com.shenji.search.old;

import com.shenji.common.util.WriteLog;
import com.shenji.search.SearchControl;
import com.shenji.search.common.Common;

import cn.sjxx.knowledge.CreateFaqIndividual;
import cn.sjxx.knowledge.DeleteFaqIndividual;
import cn.sjxx.knowledge.OntologyStub;
import cn.sjxx.knowledge.ReNameFaqIndividual;

public class FAQFilesManager {
	private boolean OntologyState = "true".equalsIgnoreCase(Common.OntologyState);
	public void createFaqIndividual(String faqUrl){
		if(!OntologyState){
			System.out.println("FALSE");
			return;
		}
		try {
			OntologyStub stub = new OntologyStub(Common.OntologyIp);
			CreateFaqIndividual createFaqIndividual = new CreateFaqIndividual();
			createFaqIndividual.setUrl(faqUrl);
			stub.createFaqIndividual(createFaqIndividual);
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			try {
				writeLog.Write(e.getMessage(),FAQFilesManager.class.getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	public void deleteFaqIndividual(String faqId){
		if(!OntologyState){
			System.out.println("FALSE");
			return;
		}
		try {
//			String str = Common.OntologyIp+"/axis2/services/Ontology.OntologyHttpSoap12Endpoint/";
			OntologyStub stub = new OntologyStub(Common.OntologyIp);
			DeleteFaqIndividual deleteFaqIndividual = new DeleteFaqIndividual();
			deleteFaqIndividual.setFaqId(faqId);
			stub.deleteFaqIndividual(deleteFaqIndividual);
		} catch (Exception e) {
			WriteLog writeLog = new WriteLog();
			try {
				writeLog.Write(e.getMessage(),FAQFilesManager.class.getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void main(String[]args){
		FAQFilesManager faqFilesManager = new FAQFilesManager();
		faqFilesManager.reNameFaqIndividual("4839f5e515c2dc25a511fad61f818879", "4839f5e515c2dc25a511fad61f818879");
	}
	
	public void reNameFaqIndividual(String oldFaqId,String newFaqId){OntologyState = true;
		if(!OntologyState){
			System.out.println("FALSE");
			return;
		}
		try {
//			String str = Common.OntologyIp+"/axis2/services/Ontology.OntologyHttpSoap12Endpoint/";
			OntologyStub stub = new OntologyStub(Common.OntologyIp);
			ReNameFaqIndividual reNameFaqIndividual = new ReNameFaqIndividual();
			reNameFaqIndividual.setOldFaqId(oldFaqId);
			reNameFaqIndividual.setNewFaqId(newFaqId);
			stub.reNameFaqIndividual(reNameFaqIndividual);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		catch (Exception e) {
//			WriteLog writeLog = new WriteLog();
//			try {
//				writeLog.Write(e.getMessage(),FAQFilesManager.class.getName());
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
	}
	
	
//	public static void main(String[]args){
//		FAQFilesManager faqFilesManager= new FAQFilesManager();
//	try {
//		faqFilesManager.createFaqIndividual("xxxx");
//		System.out.println("xxxxx");
//	} catch (Exception e) {
//		WriteLog writeLog = new WriteLog();
//		try {
//			writeLog.Write(e.getMessage(),FAQFilesManager.class.getName());
//		} catch (Exception e1) {
//			System.out.println("xxxxxxxxxx");
//		}
//	}
//
//}
	
	
}
*/