package com.shenji.onto.mapping;

import java.util.Iterator;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.shenji.onto.mapping.inter.IFaqMapDelete;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class FaqMapDelete implements IFaqMapDelete{
	private OntModel model;
	public FaqMapDelete(OntModel model){
			this.model=model;
	}	
	public int deleteOWLFaqDataProperty(String importUrl,String individualName,String importPropertyName,String importPropertyValue){
		Individual individual=this.model.getIndividual(FaqMapServices.getInstance().getBaseXmlns()+individualName);
		if(individual==null)
			return -3;
		Iterator iterator=individual.listProperties();
		Property property=null;
		while(iterator.hasNext()){
			StatementImpl statementImpl=(StatementImpl)iterator.next();
			Property temp=statementImpl.getPredicate();
			if(temp.getNameSpace().equals(importUrl+"#")&&temp.getLocalName().equals(importPropertyName))
				property=temp;
		}
		if(property==null)
			return -2;		
		Iterator iterator2=individual.listPropertyValues(property);
		RDFNode node=null;
		while(iterator2.hasNext()){
			RDFNode tempNode=(RDFNode)iterator2.next();
			if(tempNode.toString().equals(importPropertyValue))
				node=tempNode;
		}
		if(node==null)
			return -4;
		individual.removeProperty(property,node);
		FaqMapServices.getInstance().save();
		return 1;
	}
	
	/*public int deleteOWLFaqDataProperty(JenaOWLModel importModels,String individualName,String importPropertyName,String importPropertyValue){
		RDFProperty importProperty=null;
		if((importProperty=importModels.getRDFProperty(importPropertyName))==null){
			System.err.println("不存在该属性");
			return -2;
		}
		String importXmlns=importModels.getNamespaceManager().getDefaultNamespace();
		Property property=importModels.getOntModel().getDatatypeProperty(importXmlns+importPropertyName);
		Individual individual=this.model.getIndividual(FaqMapServices.getInstance().getBaseXmlns()+individualName);
		if(individual==null)
			return -3;
		Iterator iterator=individual.listPropertyValues(property);
		RDFNode node=null;
		while(iterator.hasNext()){
			RDFNode tempNode=(RDFNode)iterator.next();
			if(tempNode.toString().equals(importPropertyValue))
				node=tempNode;
		}
		if(node==null)
			return -4;
		individual.removeProperty(property,node);
		FaqMapServices.getInstance().save();
		return 1;
	}*/
	
	/*public int deleteOWLFAQType(JenaOWLModel importModels,String individualName,String importClassName){
		RDFSNamedClass namedClass=null;
		if((namedClass=importModels.getRDFSNamedClass(importClassName))==null){
			return -2;
		}	
		//导入文档空间
		String importXmlns=importModels.getNamespaceManager().getDefaultNamespace();
		Individual individual=this.model.getIndividual(FaqMapServices.getInstance().getBaseXmlns()+individualName);
		if(individual==null)
			return -3;
		//Resource resource=individual.getRDFType();
		individual.removeRDFType(importModels.getOntModel().getResource(importXmlns+importClassName));	
		FaqMapServices.getInstance().save();	
		return 1;
	}*/
	
	public int deleteOWLFAQType(String importUrl,String individualName,String importClassName){
		Individual individual=this.model.getIndividual(FaqMapServices.getInstance().getBaseXmlns()+individualName);
		if(individual==null)
			return -3;
		Resource resource=null;
		Iterator iterator=individual.listRDFTypes(false);
		while(iterator.hasNext()){
			Resource temp=(Resource)iterator.next();
			if(temp.getNameSpace().equals(importUrl+"#")&&temp.getLocalName().equals(importClassName))
				resource=temp;
		}
		if(resource==null)
			return -2;
		individual.removeRDFType(resource);	
		FaqMapServices.getInstance().save();	
		return 1;
	}
}
