package com.shenji.onto.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.shenji.common.log.Log;
import com.shenji.onto.Configuration;
import com.shenji.onto.mapping.inter.IFaqMapQuery;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;
import com.shenji.wordclassification.FAQDataAnalysis;

public class FaqMapQuery implements IFaqMapQuery {
	private OntModel model;
	public static String IDQ_Segmentation = "#Q#";

	public FaqMapQuery(OntModel model) {
		this.model = model;
	}

	@Override
	public String[] getAllFaqIndividual() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Iterator iterator = model.listIndividuals();
		while (iterator.hasNext()) {
			Individual individual = (Individual) iterator.next();
			list.add(individual.getLocalName());
		}
		return list.toArray(new String[list.size()]);
	}

	public String[] getFaqIndividualByClass(String path, String ontoClassName,
			int treeHight) {
		// TODO Auto-generated method stub
		// CLASS概念为空层不可能
		if (treeHight == 0)
			return null;
		FaqMapDBServices dbServices = null;
		FAQDataAnalysis analysis = new FAQDataAnalysis();
		String baseXmln = FaqMapServices.getInstance().getBaseXmlns();
		Property property = this.model.getProperty(baseXmln
				+ Configuration.FaqMappingCommon.Data_URL);
		List<String> list = null;
		List<String> reList = new ArrayList<String>();
		String[] reStrs = null;
		try {
			dbServices = new FaqMapDBServices();
			list = dbServices.getFaqByType(path, ontoClassName);
			for (String faq : list) {
				String faqId = FaqMapDBServices.getFaqID(faq);
				Individual individual = this.model.getIndividual(baseXmln
						+ Configuration.FaqMappingCommon.INDIVIDUAL_HEAD
						+ faqId);
				String url = individual.getPropertyValue(property).toString();
				String[] html = analysis.jsoupOneFAQHtml(url,
						Configuration.FaqMappingCommon.Data_A);
				if (html == null || html.length == 0)
					continue;
				treeHightJudge(treeHight, reList, individual.getLocalName(),
						html[1]);
			}
			reStrs = reList.toArray(new String[reList.size()]);
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			if (dbServices != null)
				dbServices.close();
			if (list != null)
				list.clear();
			if (reStrs != null)
				reList.clear();
		}
		return reStrs;
	}

	private void treeHightJudge(int treeHight, List<String> list,
			String individualName, String question) {
		if (treeHight == -1) {
			list.add(individualName + IDQ_Segmentation + question);
		} else if (treeHight == 0) {
			ReasonerTree[] trees = ReasonerTreeServer.getInstance()
					.getFaqReasonerTree(individualName);
			if (trees == null || trees.length == 0) {
				list.add(individualName + IDQ_Segmentation + question);
			}
		} else if (treeHight > 0) {
			ReasonerTree[] trees = ReasonerTreeServer.getInstance()
					.getFaqReasonerTree(individualName);
			if (trees == null || trees.length == 0) {
				return;
			}
			int faqTreeHight = trees[0].getTreeHight();
			if (treeHight == faqTreeHight) {
				list.add(individualName + IDQ_Segmentation + question);
			}
		}
	}

	public String[] getAllFaqIndividualAndQ(int treeHight) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		List<String> list = new ArrayList<String>();
		FAQDataAnalysis analysis = new FAQDataAnalysis();
		String baseXmln = FaqMapServices.getInstance().getBaseXmlns();
		Property property = this.model.getProperty(baseXmln
				+ Configuration.FaqMappingCommon.Data_URL);
		Iterator<Individual> iterator = model.listIndividuals();
		while (iterator.hasNext()) {
			Individual individual = (Individual) iterator.next();
			String url = individual.getPropertyValue(property).toString();
			try {
				String[] html = analysis.jsoupOneFAQHtml(url,
						Configuration.FaqMappingCommon.Data_A);
				if (html == null || html.length == 0)
					continue;
				treeHightJudge(treeHight, list, individual.getLocalName(),
						html[1]);
				// list.add(individual.getLocalName() + IDQ_Segmentation +
				// html[1]);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		Log.getLogger(this.getClass()).debug(
				"得到FAQ问答从服务器花费的时间：" + (end - start));
		return list.toArray(new String[list.size()]);
	}

	public String[][] getFAQContext(String individualName) throws IOException {
		String baseXmln = FaqMapServices.getInstance().getBaseXmlns();
		FAQDataAnalysis analysis = new FAQDataAnalysis();
		Individual individual = this.model.getIndividual(baseXmln
				+ individualName);
		Property property = this.model.getProperty(baseXmln
				+ Configuration.FaqMappingCommon.Data_URL);
		String url = individual.getPropertyValue(property).toString();
		String[] html = analysis.jsoupOneFAQHtml(url);
		String[][] strs = new String[html.length][2];
		strs[0][0] = Configuration.FaqMappingCommon.Data_URL;
		strs[0][1] = html[0];
		strs[1][0] = Configuration.FaqMappingCommon.Data_Q;
		strs[1][1] = html[1];
		strs[2][0] = Configuration.FaqMappingCommon.Data_A;
		strs[2][1] = html[2];
		return strs;

	}

	/*
	 * public String[][] getDataProperty(String individualName){ String
	 * baseXmln=FaqMapServices.getInstance().getBaseXmlns(); Individual
	 * individual=this.model.getIndividual(baseXmln+individualName); Iterator
	 * iterator=this.model.listDatatypeProperties(); List<String[]> list=new
	 * ArrayList<String[]>(); while(iterator.hasNext()){ Property
	 * property=(Property)iterator.next(); String
	 * value=individual.getPropertyValue(property).toString(); String[] strs=new
	 * String[2]; strs[0]=property.toString(); strs[1]=value; list.add(strs); }
	 * String[][] reStrs=new String[list.size()][]; for(int
	 * i=0;i<list.size();i++){ reStrs[i]=list.get(i); } list.clear(); return
	 * reStrs; }
	 */
	public String[][] getDataProperty(String individualName) {
		String baseXmln = FaqMapServices.getInstance().getBaseXmlns();
		Individual individual = this.model.getIndividual(baseXmln
				+ individualName);
		Iterator iterator = individual.listProperties();
		List<String[]> list = new ArrayList<String[]>();
		while (iterator.hasNext()) {
			StatementImpl statementImpl = (StatementImpl) iterator.next();
			Property property = statementImpl.getPredicate();
			// 没有办法的办法，找不到合适API
			if (!property.toString().equals(
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
					&& !property.getLocalName().equals(
							Configuration.FaqMappingCommon.Data_URL)) {
				String[] strs = new String[2];
				strs[0] = property.toString();
				strs[1] = statementImpl.getObject().toString();
				list.add(strs);
				// System.out.println(strs[0]+":"+strs[1]);
			}
		}
		String[][] reStrs = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			reStrs[i] = list.get(i);
		}
		list.clear();
		return reStrs;
	}

	public String[] getRDFType(String individualName) {
		String baseXmln = FaqMapServices.getInstance().getBaseXmlns();
		List<String> list = new ArrayList<String>();
		Individual individual = this.model.getIndividual(baseXmln
				+ individualName);
		if (individual == null)
			return null;
		Iterator iterator = individual.listRDFTypes(false);
		while (iterator.hasNext()) {
			Resource resource = (Resource) iterator.next();
			if (resource.toString().equals(
					"http://www.w3.org/2002/07/owl#Thing"))
				continue;
			list.add(resource.toString());
			// System.out.println(resource.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
