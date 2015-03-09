package com.shenji.onto.editer.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoDelete;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyUtil;

import edu.stanford.smi.protegex.owl.database.OWLDatabasePlugin;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.AbstractRDFSClass;

public class OWLOntoDelete{
	private String token=null;
	private OntologyManage manage=null;
	private OntologyDescription descriptionClass=null; 
	private OntologyLimit limitClass=null;
	
	public OWLOntoDelete(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		this.manage=manage;
		this.token=manage.getToken();
		this.descriptionClass=new OntologyDescription(manage);
		this.limitClass=new OntologyLimit(manage);
		
	}

/**********************************内部类(删除与添加)*****************************************/
	private class OntologyDescription extends AbsOntoDescription{
		private String token=null;
		private OntologyManage manage=null;
		public OntologyDescription(OntologyManage manage){
			super(manage);
			this.manage=manage;
			this.token=manage.getToken();
		}
		@Override
		public int dealOWLDescription_Class(String className_1,
				RDFSClass rdfClass_2, int descriptionType) {
			// TODO Auto-generated method stub
			OWLNamedClass class_1=manage.getAbstractGetInterface().getOWLNamedClass(className_1);
			if(class_1==null)
				return -2;
			if(rdfClass_2==null)
				return -3;
			//不存在该关系，返回-1
			RDFResource rdfResource_2=null;
			if((rdfResource_2=getDescriptionClass(class_1, rdfClass_2, descriptionType))==null)
				return -1;
			switch (descriptionType) {
				case OntologyCommon.Description.super_:{				
					class_1.removeSuperclass((RDFSClass)rdfResource_2);								
					break;
				}
				case OntologyCommon.Description.equivalent_:{				
					class_1.removeEquivalentClass((RDFSClass)rdfResource_2);
					break;
				}	
				case OntologyCommon.Description.disjoint_:{	
					class_1.removeDisjointClass((RDFSClass)rdfResource_2);
					break;
				}	
				
				default:				
					break;
			}	
			if(manage.writeTemp(false)==-1)
				return -9;
			//只有一个THING类时，界面刷新会出问题，所以要重置模型
			if(descriptionType==OntologyCommon.Description.super_&&class_1.getSuperclassCount()==0)
				try {
					this.manage.resetOWLModel();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.getLogger(this.getClass()).error(e.getMessage(),e);
				}
			return 1;
		}
		@Override
		public int dealOWLDescription_Property(String propertyName_1,
				String propertyName_2, int descriptionType, int propertyType) {
			// TODO Auto-generated method stub
			OWLProperty owlProperty_1=manage.getAbstractGetInterface().getOWLProperty( propertyName_1, propertyType);
			OWLProperty owlProperty_2=manage.getAbstractGetInterface().getOWLProperty( propertyName_2, propertyType);
			if(owlProperty_1==null)
				return -2;
			if(owlProperty_2==null)
				return -3;
			switch (descriptionType) {
				case OntologyCommon.Description.super_:{
					owlProperty_1.removeSuperproperty(owlProperty_2);
					break;
				}
				case OntologyCommon.Description.equivalent_:{
					owlProperty_1.removeEquivalentProperty(owlProperty_2);
					break;
				}	
				case OntologyCommon.Description.disjoint_:{
					owlProperty_1.removeDifferentFrom(owlProperty_2);
					break;
				}	
				case OntologyCommon.Description.inverse_:{
					//owlProperty_1.remov(owlProperty_2);
					break;
				}	
				default:				
					break;
			}	
			if(manage.writeTemp(false)==-1)
				return -9;
			return 1;
		}
		
		@Override
		public int dealOWLDescription_Individual(String individualName_1,
				String individualName_2, int descriptionType) {
			// TODO Auto-generated method stub
			OWLIndividual individual_1=manage.getAbstractGetInterface().getOWLIndividual(individualName_1);
			OWLIndividual individual_2=manage.getAbstractGetInterface().getOWLIndividual(individualName_2);
			if(individual_1==null)
				return -2;
			if(individual_2==null)
				return -3;
			switch (descriptionType) {
				case OntologyCommon.Description.equivalent_:{
					individual_1.removeSameAs(individual_2);
					break;
				}	
				case OntologyCommon.Description.disjoint_:{
					individual_1.removeDifferentFrom(individual_2);
					break;
				}	
				default:		
					return -6;
			}	
			if(manage.writeTemp(false)==-1)
				return -9;
			return 1;
		}	
		public int deleteInnerOWLDescriptionOntologyObject(String str, String str2, int type,int descriptionType){
			return dealDescription(str, str2, type, descriptionType);
		}
		
		
		public int deleteInnerOWLDescriptionOntologyObject(String objectName,
				String property_description, String values_description,
				int property_descriptionType, int values_descriptionType,
				int cardinalityType, int cardinalityNum,int descriptionType) {
			// TODO Auto-generated method stub
			int ontologyType=OntologyCommon.DataType.namedClass;
			return dealDescription(objectName,ontologyType, property_description, values_description, 
					property_descriptionType, values_descriptionType, cardinalityType, cardinalityNum,descriptionType);
		}
			
	}
	
/**********************************内部类(删除与设置)*****************************************/	
	private class OntologyLimit extends AbsOntoLimit{
		private String token=null;
		private OntologyManage manage=null;
		public OntologyLimit(OntologyManage manage){	
			super(manage);
			this.manage=manage;
			this.token=manage.getToken();
		}
		@Override
		public int dealRange(OWLProperty property, int propertyType,
				RDFResource limit) {
			ArrayList<RDFResource> arrayList=null;
			Collection<RDFResource> collection=null;
			try{
				collection=property.getRanges(false);
				arrayList=this.getDealLimitResources(property, OntologyCommon.ProPertyLimitType.range, limit);
				if(arrayList!=null&&collection.size()==arrayList.size())
					return -1;
				property.setRanges(arrayList);
				if(manage.writeTemp(false)==-1)
					return -9;
			}catch (IllegalArgumentException e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				return -8;
			}
			finally{
				arrayList.clear();
			}
			return 1;
		}
		
		public int deleteLimit(String ontologyProperty,int ontologyPropertyType,String limit,int limitType){
			return dealLimit(ontologyProperty, ontologyPropertyType, limit, limitType);
		}
		
		public int deleteLimit(String ontologyProperty,int ontologyPropertyType,int limitType,String property_description, String values_description,
				int property_descriptionType,int values_descriptionType,int cardinalityType,int cardinalityNum){
			return dealLimit(ontologyProperty, ontologyPropertyType, limitType, property_description, values_description, property_descriptionType, values_descriptionType, cardinalityType, cardinalityNum);
		}
		

		
		@Override
		public int dealDomain(OWLProperty property, RDFSClass limit) {
			ArrayList<RDFResource> arrayList=null;
			Collection<RDFResource> collection=null;
			try{
				collection=property.getDomains(false);
				arrayList=this.getDealLimitResources(property, OntologyCommon.ProPertyLimitType.domain, limit);
				if(arrayList!=null&&collection.size()==arrayList.size())
					return -1;
				property.setDomains(arrayList);
				if(manage.writeTemp(false)==-1)
					return -9;
			}catch (IllegalArgumentException e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				return -8;
			}
			finally{
				collection=null;
				if(arrayList!=null)
					arrayList.clear();
			}
			//已存在
			return 1;
		}
		
		public int deleteCharacteristics(String ontologyProperty,
				int ontologyPropertyType,
				int characteristicsType) {
			return super.setOWLCharacteristics(ontologyProperty, ontologyPropertyType, characteristicsType, false);
		}
		@Override
		public int dealOWLIndividualObjectProPerty(OWLIndividual individual,
				OWLProperty property, OWLIndividual dealIndividual) {
			// TODO Auto-generated method stub
			if(super.getIndividualObjectProperty(individual, property, dealIndividual)!=null){
				individual.removePropertyValue(property, dealIndividual);
				return 1;
			}
			else 
				return -8;
		}
		@Override
		public int dealOWLIndividualDataProperty(OWLIndividual individual,
				OWLProperty property, RDFSLiteral rdfsLiteral) {
			// TODO Auto-generated method stub
			ArrayList<Object> literalList=null;
			try {
				int literaNum=individual.getPropertyValueLiterals(property).size();
				literalList=super.getIndividualDataProperty(individual, property,rdfsLiteral);
				if((literalList.size())!=literaNum){
					individual.setPropertyValues(property, literalList);
					return 1;
				}
				else 
					return -8;			
			} catch (Exception e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				return -4;
			}
			finally{
				if(literalList!=null)
					literalList.clear();
			}			
		}	
	
		public int deleteOWLIndividualObjectProPerty(String individualName_1,
				String individualName_2, String relation){
			return super.dealOWLIndividualObjectProPerty(individualName_1, individualName_2, relation);
		}
		
		public int deleteOWLIndividualDataProperty(String individualName,
				String propertyName, String propertyValue,String propertyValueType){
			return super.dealOWLIndividualDataProperty(individualName, propertyName, propertyValue, propertyValueType);
		}
		
	}
/**********************************内部类END****************************************/	
	
	private int deleteOWLObject(String str, int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case OntologyCommon.DataType.namedClass:{
			OWLNamedClass namedClass=this.manage.getAbstractGetInterface().getOWLNamedClass(str);
			namedClass.delete();		
			break;
		}
		case OntologyCommon.DataType.objectProperty:
		case OntologyCommon.DataType.dataTypeProperty:{
			OWLProperty property=this.manage.getAbstractGetInterface().getOWLProperty(str, type);
			property.delete();
			break;
		}
		case OntologyCommon.DataType.individual:{
			OWLIndividual individual=this.manage.getAbstractGetInterface().getOWLIndividual(str);
			individual.delete();
			break;
		}
		default:
			return -3;
		}
		return 1;
	}
	

	
	
/***************************************************************************************/	
	/*public int deleteOWLIndividualProperty(String individualName,String propertyName,String value,int propertyType){
		OWLIndividual individual=this.manage.getAbstractGetInterface().getOWLIndividual(individualName);
		if(individual==null)
			return -2;
		OWLIndividual copeIndividual=this.manage.getAbstractGetInterface().getOWLIndividual(value);
		if(copeIndividual==null)
			return -3;
		OWLProperty objectProperty=this.manage.getAbstractGetInterface().getOWLProperty(value, propertyType);
		individual.removePropertyValue(objectProperty, copeIndividual);
		return 1;
	}*/
	
	public int deleteOWLDescriptionOntologyObject(String str, String str2, int type,int descriptionType) {
		// TODO Auto-generated method stub
		return descriptionClass.deleteInnerOWLDescriptionOntologyObject(str, str2, type, descriptionType);
	}
	
	public int deleteOWLDescriptionOntologyObject(String objectName,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum,int descriptionType) {
		// TODO Auto-generated method stub
		return descriptionClass.deleteInnerOWLDescriptionOntologyObject(objectName, property_description, values_description, property_descriptionType, values_descriptionType, cardinalityType, cardinalityNum, descriptionType);
	}
	
	public int deleteOWLOntologyObject(String str, int type) {
		// TODO Auto-generated method stub
		int backFlag=-3;
		switch (type) {
			case OntologyCommon.DataType.namedClass:				
			case OntologyCommon.DataType.dataTypeProperty:			
			case OntologyCommon.DataType.objectProperty:				
			case OntologyCommon.DataType.individual:
				backFlag=this.deleteOWLObject(str,type);
				break;	
			default:
				break;
		}
		if(backFlag!=1)
			return backFlag;
		if(manage.writeTemp(false)==-1)
			return -9;
		return backFlag;
	}
	
	/*public int deleteOWLIndividualType(String className, String individualName,boolean deleteIndividual){
		// TODO Auto-generated method stub
			OWLNamedClass namedClass=this.manage.getAbstractGetInterface().getOWLNamedClass(className);
			OWLIndividual individual=this.manage.getAbstractGetInterface().getOWLIndividual(individualName);
			if(namedClass==null)
				return -2;
			if(individual==null)
				return -3;
			Collection<RDFResource> collection=individual.getRDFTypes();
			boolean isExist=false;
			for(RDFResource resource:collection){
				if(resource.getBrowserText().equals(namedClass.getBrowserText())){
					//rdfResource=resource;
					isExist=true;
				}
			}
			if(isExist==false)
				return -7;
			try{		
				if(collection.size()<2){
					if(deleteIndividual==true)
						individual.delete();
					else
						individual.addRDFType(this.manage.getOwlModel().getOWLThingClass());
					return 2;
				}
				individual.removeRDFType(namedClass);			
				if(manage.writeTemp(true)==-1)
					return -9;
			}
			catch (Exception e) {
				// TODO: handle exception
				return -1;
			}
			finally{
				if(collection!=null)
					collection=null;
			}
			return 1;
	}*/
	public int deleteOWLIndividualType(String className, String individualName,boolean deleteIndividual){
		// TODO Auto-generated method stub
			OWLNamedClass namedClass=this.manage.getAbstractGetInterface().getOWLNamedClass(className);
			OWLIndividual individual=this.manage.getAbstractGetInterface().getOWLIndividual(individualName);
			if(namedClass==null)
				return -2;
			if(individual==null)
				return -3;
			Collection<RDFResource> collection=individual.getRDFTypes();
			boolean isExist=false;
			for(RDFResource resource:collection){
				if(resource.getBrowserText().equals(namedClass.getBrowserText())){
					//rdfResource=resource;
					isExist=true;
				}
			}
			if(isExist==false)
				return -7;
			try{	
				individual.removeRDFType(namedClass);		
				if(deleteIndividual==true)
					individual.delete();		
				if(manage.writeTemp(false)==-1)
					return -9;
			}
			catch (Exception e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				return -1;
			}
			finally{
				if(collection!=null)
					collection=null;
			}
			return 1;
	}

	public int deleteOWLIndividualType(String className, String individualName){
		return deleteOWLIndividualType(className, individualName, false);		
	}
	
	
	public int deleteOWLLimit(String ontologyProperty,int ontologyPropertyType,String limit,int limitType){
		return limitClass.deleteLimit(ontologyProperty, ontologyPropertyType, limit, limitType);
	}
	
	public int deleteOWLLimit(String ontologyProperty,int ontologyPropertyType,int limitType,String property_description, String values_description,
			int property_descriptionType,int values_descriptionType,int cardinalityType,int cardinalityNum){
		return limitClass.deleteLimit(ontologyProperty, ontologyPropertyType, limitType, property_description, values_description, property_descriptionType, values_descriptionType, cardinalityType, cardinalityNum);
	}
	public int deleteCharacteristics(String ontologyProperty,int ontologyPropertyType,int characteristicsType){
		return limitClass.deleteCharacteristics(ontologyProperty, ontologyPropertyType, characteristicsType);
	}
	
	public int deleteOWLIndividualObjectProPerty(String individualName_1,
			String individualName_2, String relation){
		return limitClass.dealOWLIndividualObjectProPerty(individualName_1, individualName_2, relation);
	}
	
	public int deleteOWLIndividualDataProperty(String individualName,
			String propertyName, String propertyValue,String propertyValueType){
		return limitClass.dealOWLIndividualDataProperty(individualName, propertyName, propertyValue, propertyValueType);
	}
	
	
	public int deleteOWLAnnotations(String ontologyName,int ontologyType,String annotations,String annotationsType){
		RDFResource resource=null;
		switch (ontologyType) {
			case OntologyCommon.DataType.namedClass:
				resource=(AbstractRDFSClass)manage.getAbstractGetInterface().getOWLNamedClass(ontologyName);
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
			resource=this.manage.getOwlModel().getOWLOntologyByURI(this.manage.getOwlModel().getDefaultOWLOntology().getName());
		}
		OWLAnnotation.deleteOWLAnnotations(resource, annotations, annotationsType,this.manage);
		if(manage.writeTemp(false)==-1)
			return -9;
		return 1;
	}
	
	public int deleteOWLUserAnnotation(String userAnnotationsName){	
		try {
			//manage.getOwlModel().
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			//已存在该匿名属性
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -2;
		}
		if(manage.writeTemp(false)==-1)
			return -9;
		return 1;
	}
	
}
