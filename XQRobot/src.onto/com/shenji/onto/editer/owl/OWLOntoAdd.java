package com.shenji.onto.editer.owl;

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.shenji.common.log.Log;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoAdd;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyUtil;

import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractRDFSClass;

public class OWLOntoAdd extends AbsOntoDescription{
	private String token=null;
	private OntologyManage manage=null;
	public OWLOntoAdd(OntologyManage manage) {
		super(manage);
		// TODO Auto-generated constructor stub
		this.manage=manage;
		this.token=manage.getToken();
	}

	/**添加X
	 * @param dateTypeProperty
	 * @return
	 */
	private int addOWLObject(String str,int type) {
		try{
		    //Jena.dumpRDF(manage.getOwlModel().getOntModel());
			switch (type) {
			case OntologyCommon.DataType.namedClass:{
				manage.getOwlModel().createOWLNamedClass(str);		
				if(manage.writeTemp(false)==-1)
					return -9;
				return 1;
			}
			case OntologyCommon.DataType.dataTypeProperty:{
				manage.getOwlModel().createOWLDatatypeProperty(str);			
				if(manage.writeTemp(false)==-1)
					return -9;
				return 1;
			}
			case OntologyCommon.DataType.objectProperty:{
				manage.getOwlModel().createOWLObjectProperty(str);			
				if(manage.writeTemp(false)==-1)
					return -9;
				return 1;
			}
			case OntologyCommon.DataType.individual:{
				manage.getOwlModel().getOWLThingClass().createOWLIndividual(str);	
				if(manage.writeTemp(false)==-1)
					return -9;
				return 1;
			}
			default:
				return -3;
			}			
		}
		catch(IllegalArgumentException e){	
			//添加重复项
			return -12;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -8;
		}
	}
	
	
	
	

	public int dealOWLDescription_Class(String className_1,RDFSClass rdfClass_2,int descriptionType){
		OWLNamedClass class_1=manage.getAbstractGetInterface().getOWLNamedClass(className_1);
		if(class_1==null)
			return -2;
		if(rdfClass_2==null)
			return -3;
		//该描述已经存在
		if(getDescriptionClass(class_1, rdfClass_2, descriptionType)!=null)
			return -1;
		Collection<RDFSClass> collection=null;
		switch (descriptionType) {
			case OntologyCommon.Description.super_:{				
				//不允许把一个子类的父类 添加给子类当子类  (父-子 不允许 父-子+父)			
				/*try{
					collection=class_1.getSubclasses(true);
					for(RDFSClass rdfsClass:collection){						
						if(rdfsClass.getBrowserText().equals(rdfClass_2.getBrowserText())){					
							return -15;
						}
					}
					Iterator iterator=collection.iterator();
					while(iterator.hasNext()){
						RDFSClass rdfsClass=(RDFSClass)iterator.next();
						if(rdfsClass.getBrowserText().equals(rdfClass_2.getBrowserText())){					
							return -15;
						}
					}					
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Log.getLogger().error(e.getMessage(),e);
					
				}*/
				class_1.addSuperclass(rdfClass_2);							
				//移除父类thing类
				class_1.removeSuperclass(manage.getOwlModel().getOWLThingClass());
				break;
			}
			case OntologyCommon.Description.equivalent_:{				
				class_1.addEquivalentClass(rdfClass_2);
				break;
			}	
			case OntologyCommon.Description.disjoint_:{	
				class_1.addDisjointClass(rdfClass_2);
				break;
			}	
			default:				
				return -6;
		}	
		/*if(collection!=null)
			collection.clear();*/
		if(manage.writeTemp(false)==-1)
			return -9;	
		return 1;
	}
	
	
	public int dealOWLDescription_Property(String propertyName_1,String  propertyName_2,int descriptionType,int propertyType){
		OWLProperty owlProperty_1=manage.getAbstractGetInterface().getOWLProperty( propertyName_1, propertyType);
		OWLProperty owlProperty_2=manage.getAbstractGetInterface().getOWLProperty( propertyName_2, propertyType);
		if(owlProperty_1==null)
			return -2;
		if(owlProperty_2==null)
			return -3;
		switch (descriptionType) {
			case OntologyCommon.Description.super_:{
				owlProperty_1.addSuperproperty(owlProperty_2);
				break;
			}
			case OntologyCommon.Description.equivalent_:{
				owlProperty_1.addEquivalentProperty(owlProperty_2);
				break;
			}	
			case OntologyCommon.Description.disjoint_:{
				owlProperty_1.addDifferentFrom(owlProperty_2);
				break;
			}	
			case OntologyCommon.Description.inverse_:{
				owlProperty_1.setInverseProperty(owlProperty_2);
				break;
			}	
			default:
				//描述错误类型（descriptionType）错误
				return -4;
		}	
		if(manage.writeTemp(false)==-1)
			return -9;
		return 1;
	}
	
	public int dealOWLDescription_Individual(String individualName_1,String individualName_2,int descriptionType){
		OWLIndividual individual_1=manage.getAbstractGetInterface().getOWLIndividual(individualName_1);
		OWLIndividual individual_2=manage.getAbstractGetInterface().getOWLIndividual(individualName_2);
		if(individual_1==null)
			return -2;
		if(individual_2==null)
			return -3;
		switch (descriptionType) {
			case OntologyCommon.Description.equivalent_:{
				individual_1.addSameAs(individual_2);
				break;
			}	
			case OntologyCommon.Description.disjoint_:{
				individual_1.addDifferentFrom(individual_2);
				break;
			}
			default:		
				return -4;
		}	
		if(manage.writeTemp(false)==-1)
			return -9;
		return 1;
	}
	
/***************************************公有方法********************************************/	
	
	
	public int addOWLOntologyObject(String str,int type){
		//-2类型错误
		int backFlag=-2;
		if(!StringUtil.wordVerification(str)){
			//存在非法字符
			return 501; 
		}
		switch (type) {
			case OntologyCommon.DataType.namedClass:		
			case OntologyCommon.DataType.dataTypeProperty:		
			case OntologyCommon.DataType.objectProperty:				
			case OntologyCommon.DataType.individual:
				backFlag=this.addOWLObject(str, type);
				break;
			default:
				break;
		}
		if(manage.writeTemp(false)==-1)
			return -9;
		return backFlag;
			
	}

	
	public int addOWLDescriptionOntologyObject(String str, String str2, int type, int descriptionType){
		if(str.equals(str2))
			return -13;
		return dealDescription(str, str2, type, descriptionType);
		
	}
	
	public int addOWLDescriptionOntologyObject(String objectName,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum,int descriptionType) {
		// TODO Auto-generated method stub
		int ontologyType=OntologyCommon.DataType.namedClass;
		return dealDescription(objectName,ontologyType,property_description, values_description, 
				property_descriptionType, values_descriptionType, cardinalityType, cardinalityNum,descriptionType);
	}
	
	
	public int addOWLIndividualType(String className, String individualName) {
	// TODO Auto-generated method stub
		OWLNamedClass namedClass=this.manage.getAbstractGetInterface().getOWLNamedClass(className);
		OWLIndividual individual=this.manage.getAbstractGetInterface().getOWLIndividual(individualName);
		//传入类不存在
		if(namedClass==null)
			return -2;
		Collection<RDFResource> collection=namedClass.getInstances(false);
		for(RDFResource resource:collection){
			if(individual!=null&&resource.getBrowserText().equals(individual.getBrowserText())){
				//实例已经存在
				return -1;
			}
		}		
		try{			
			if(individual==null){
				if(namedClass.createOWLIndividual(individualName)==null)
					//创建失败（未知）
					return -3;				
			}
			else{
				individual.addRDFType(namedClass);
				//individual.setRDFType(namedClass);
				
				/*//移除thing 类型
				individual.removeRDFType(this.manage.getOwlModel().getOWLThingClass());*/
			}
			if(manage.writeTemp(false)==-1)
				return -9;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
			
		}
		return 1;
	}
	
	public int addOWLAnnotations(String ontologyName,int ontologyType,String annotations,String annotationsType){
		Object resource=null;
		
		switch (ontologyType) {
			case OntologyCommon.DataType.namedClass:
				resource=manage.getAbstractGetInterface().getOWLNamedClass(ontologyName);
				break;
			case OntologyCommon.DataType.objectProperty:
			case OntologyCommon.DataType.dataTypeProperty:
				resource=manage.getAbstractGetInterface().getOWLProperty(ontologyName, ontologyType);
				break;
			case OntologyCommon.DataType.individual:
				resource=manage.getAbstractGetInterface().getOWLIndividual(ontologyName);
				break;
		}	
		if(ontologyName.equals("Thing")){
			return -4;
		}
			//resource=this.manage.getOwlModel().getOWLOntologyByURI(this.manage.getOwlModel().getDefaultOWLOntology().getName());
			
		OWLAnnotation.addOWLAnnotations(resource, ontologyType, annotations, annotationsType,this.manage);
		if(manage.writeTemp(false)==-1)
			return -9;
		return 1;
	}
	
	public int addOWLUserAnnotation(String userAnnotationsName){	
		try {
			manage.getOwlModel().createAnnotationProperty(userAnnotationsName);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			//已存在该匿名属性
			return -2;
		}
		if(manage.writeTemp(false)==-1)
			return -9;
		return 1;
	}
}
