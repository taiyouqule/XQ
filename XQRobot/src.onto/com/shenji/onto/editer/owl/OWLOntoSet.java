package com.shenji.onto.editer.owl;

import java.util.ArrayList;
import java.util.Collection;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoSet;
import com.shenji.onto.editer.server.OntologyManage;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.ui.properties.OWLObjectPropertySubpropertyPane;
import edu.stanford.smi.protegex.owl.ui.properties.types.OWLObjectPropertyTypesWidget;

public class OWLOntoSet extends AbsOntoLimit{
	private String token=null;
	private OntologyManage manage=null;
	public OWLOntoSet(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		super(manage);
		this.manage=manage;
		this.token=manage.getToken();
	}	

	@Override
	public int dealRange(OWLProperty property, int propertyType,
			RDFResource limit) {
		ArrayList<RDFResource> arrayList=null;
		Collection<RDFResource> collection;
		try{		
			collection=property.getRanges(false);
			arrayList=this.getDealLimitResources(property, OntologyCommon.ProPertyLimitType.range, limit);
			if(arrayList!=null&&collection.size()!=arrayList.size())
				return -1;	
			if(collection.size()==0)
				property.setRange(limit);
			else{
				arrayList.add(limit);
				property.setRanges(arrayList);
			}
			//设置联合属性暂时去除
			/*switch (propertyType) {
				case OntologyType.DataType.objectProperty:{
					if(collection.size()==0)
						property.setRange(limit);
					else{
						arrayList.add(limit);
						property.setRanges(arrayList);
					}
					break;
				}
				case OntologyType.DataType.dataTypeProperty:{
					OWLDatatypeProperty datatypeProperty=(OWLDatatypeProperty)property;
					property.setRange(limit);
					break;
				}			
				default:
					return -1;		
			}*/
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
		return 1;
	}	
	
	public int dealDomain(OWLProperty property,RDFSClass limit){
		ArrayList<RDFResource> arrayList=null;
		Collection<RDFResource> collection=null;
		try{
			collection=property.getDomains(false);
			arrayList=this.getDealLimitResources(property, OntologyCommon.ProPertyLimitType.domain, limit);
			if(arrayList!=null&&collection.size()!=arrayList.size())
				return -1;
			if(collection.size()==0)
				property.setDomain(limit);
			else{
				arrayList.add(limit);
				property.setDomains(arrayList);
			}
			//设置联合属性暂时去除
			/*if(property.getDomain(false)==null)
				property.setDomain(limit);
			else
				property.addUnionDomainClass(limit);*/
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
	
	@Override
	public int dealOWLIndividualObjectProPerty(OWLIndividual individual,
			OWLProperty property, OWLIndividual dealIndividual) {
		// TODO Auto-generated method stub
		if(super.getIndividualObjectProperty(individual, property, dealIndividual)==null){
			individual.setPropertyValue(property, dealIndividual);	
			return 1;
		}
		else 
			return -8;
	}
	
	@Override
	public int dealOWLIndividualDataProperty(OWLIndividual individual,
			OWLProperty property, RDFSLiteral rdfsLiteral) {
		ArrayList<Object> literalList=null;
		try {
			int literaNum=individual.getPropertyValueLiterals(property).size();
			literalList=super.getIndividualDataProperty(individual, property,rdfsLiteral);
			if((literalList.size())==literaNum){
				individual.setPropertyValue(property, rdfsLiteral);
				//individual.set
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
	
	

/***************************************************************************/	
	
	public int setOWLLimit(String ontologyProperty,int ontologyPropertyType,String limit,int limitType){
		return super.dealLimit(ontologyProperty, ontologyPropertyType, limit, limitType);
	}
	
	public int setOWLLimit(String ontologyProperty,int ontologyPropertyType,int limitType,String property_description, String values_description,
			int property_descriptionType,int values_descriptionType,int cardinalityType,int cardinalityNum){
		return super.dealLimit(ontologyProperty, ontologyPropertyType, limitType, property_description, values_description, property_descriptionType, values_descriptionType, cardinalityType, cardinalityNum);
	}
	public int setOWLIndividualObjectProPerty(String individualName_1,
			String individualName_2, String relation){
		return super.dealOWLIndividualObjectProPerty(individualName_1, individualName_2, relation);
	}
	
	public int setOWLCharacteristics(String ontologyProperty,
			int ontologyPropertyType,
			int characteristicsType) {
		return super.setOWLCharacteristics(ontologyProperty, ontologyPropertyType, characteristicsType, true);
	}

	public int setOWLIndividualDataProperty(String individualName,
			String propertyName, String propertyValue,String propertyValueType){
		return super.dealOWLIndividualDataProperty(individualName, propertyName, propertyValue, propertyValueType);
	}
	

	
}
