package com.shenji.onto.editer.owl;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoUpdate;
import com.shenji.onto.editer.server.OntologyManage;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class OWLOntoUpdate {
	private String token=null;
	private OntologyManage manage=null;
	public OWLOntoUpdate(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		this.manage=manage;
		this.token=manage.getToken();
	}
	
	
	private int updateDescriptionOntologyObject(String objectName_1,String oldObjectName_2,String newObjectName_2,int type,int descriptionType){
		switch (descriptionType) {
		case OntologyCommon.Description.super_:{
			int reFlag=manage.getAbstractDeleteInterface().deleteDescriptionOntologyObject(objectName_1, oldObjectName_2, type,descriptionType);
			if(reFlag!=1)
				return reFlag;		
			return manage.getAbstractAddInterface().addDescriptionOntologyObject(objectName_1, newObjectName_2, type,descriptionType);
		}
		case OntologyCommon.Description.equivalent_:{
			int reFlag=manage.getAbstractDeleteInterface().deleteDescriptionOntologyObject(objectName_1, oldObjectName_2, type,descriptionType);
			if(reFlag!=1)
				return reFlag;		
			return manage.getAbstractAddInterface().addDescriptionOntologyObject(objectName_1, newObjectName_2, type,descriptionType);
		}
		case OntologyCommon.Description.disjoint_:{
			int reFlag=manage.getAbstractDeleteInterface().deleteDescriptionOntologyObject(objectName_1, oldObjectName_2, type,descriptionType);
			if(reFlag!=1)
				return reFlag;		
			return manage.getAbstractAddInterface().addDescriptionOntologyObject(objectName_1, newObjectName_2, type,descriptionType);
		}
		default:
			return -7;
		}	
	}
	
	private int updateDescriptionOntologyObject(String objectName,
			String oldProperty_description, String oldValues_description,String newProperty_description, String newValues_description,
			int oldProperty_descriptionType,int oldValues_descriptionType,int newProperty_descriptionType, int newValues_descriptionType,
			int oldCardinalityType, int oldCardinalityNum,int newCardinalityType, int newCardinalityNum,int descriptionType){
		switch (descriptionType) {
		case OntologyCommon.Description.super_:{
			int reFlag=manage.getAbstractDeleteInterface().deleteDescriptionOntologyObject(objectName, oldProperty_description, oldValues_description, oldProperty_descriptionType,oldValues_descriptionType, oldCardinalityType, oldCardinalityNum,descriptionType);
			if(reFlag!=1)
				return reFlag;		
			return manage.getAbstractAddInterface().addDescriptionOntologyObject(objectName, newProperty_description, newValues_description, newProperty_descriptionType, newValues_descriptionType, newCardinalityType, newCardinalityNum,descriptionType);
		}
		case OntologyCommon.Description.equivalent_:{
			int reFlag=manage.getAbstractDeleteInterface().deleteDescriptionOntologyObject(objectName, oldProperty_description, oldValues_description, oldProperty_descriptionType,oldValues_descriptionType, oldCardinalityType, oldCardinalityNum,descriptionType);
			if(reFlag!=1)
				return reFlag;		
			return manage.getAbstractAddInterface().addDescriptionOntologyObject(objectName, newProperty_description, newValues_description, newProperty_descriptionType, newValues_descriptionType, newCardinalityType, newCardinalityNum,descriptionType);
		}
		case OntologyCommon.Description.disjoint_:{
			int reFlag=manage.getAbstractDeleteInterface().deleteDescriptionOntologyObject(objectName, oldProperty_description, oldValues_description, oldProperty_descriptionType,oldValues_descriptionType, oldCardinalityType, oldCardinalityNum,descriptionType);
			if(reFlag!=1)
				return reFlag;		
			return manage.getAbstractAddInterface().addDescriptionOntologyObject(objectName, newProperty_description, newValues_description, newProperty_descriptionType, newValues_descriptionType, newCardinalityType, newCardinalityNum,descriptionType);
		}
		default:
			return -7;
		}	
	}
	
/***********************************************************************/
	public int renameOWLOntologyObject(String oldStr, String newStr,int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case OntologyCommon.DataType.namedClass:
			OWLNamedClass namedClass=manage.getAbstractGetInterface().getOWLNamedClass(oldStr);
			if(namedClass==null)
				return -6;
			namedClass.rename(newStr);
			break;
		case OntologyCommon.DataType.dataTypeProperty:
		case OntologyCommon.DataType.objectProperty:
			OWLProperty owlProperty=manage.getAbstractGetInterface().getOWLProperty(oldStr, type);
			if(owlProperty==null)
				return -6;
			owlProperty.rename(newStr);		
			break;
		case OntologyCommon.DataType.individual:
			OWLIndividual individual=manage.getAbstractGetInterface().getOWLIndividual(oldStr);
			if(individual==null)
				return -6;
			individual.rename(newStr);
			break;
		}		
		if(manage.writeTemp(true)==-1)
			return -9;
		try {
			this.manage.resetOWLModel();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -7;
		}
		return 1;
	}
	
	public int updateOWLDescriptionOntologyObject(String objectName_1,String oldObjectName_2,String newObjectName_2,int type,int descriptionType){
		return updateDescriptionOntologyObject(objectName_1, oldObjectName_2, newObjectName_2, type, descriptionType);
	}
	
	public int updateOWLDescriptionOntologyObject(String objectName,
			String oldProperty_description, String oldValues_description,String newProperty_description, String newValues_description,
			int oldProperty_descriptionType,int oldValues_descriptionType,int newProperty_descriptionType, int newValues_descriptionType,
			int oldCardinalityType, int oldCardinalityNum,int newCardinalityType, int newCardinalityNum,int descriptionType){
		return updateDescriptionOntologyObject(objectName, oldProperty_description, oldValues_description, newProperty_description, newValues_description, oldProperty_descriptionType, oldValues_descriptionType, newProperty_descriptionType, newValues_descriptionType, oldCardinalityType, oldCardinalityNum, newCardinalityType, newCardinalityNum, descriptionType);
	}
	
	public int updateOWLIndividualType(String individual,String oldClassName,String newClassName){
		int reFlag=this.manage.getAbstractDeleteInterface().deleteIndividualType(oldClassName, individual,false);
		if(reFlag<1)
			return reFlag;
		return this.manage.getAbstractAddInterface().addIndividualType(newClassName, individual);
	}
	
	public int updateLimit(String ontologyProperty,int ontologyPropertyType,String oldLimit,int oldLimitType,String newLimit,int newLimitType){
		int reFlag=this.manage.getAbstractDeleteInterface().deleteLimit(ontologyProperty, ontologyPropertyType, oldLimit, oldLimitType);
		if(reFlag<1)
			return reFlag;
		return this.manage.getAbstractSetInterface().setLimit(ontologyProperty, ontologyPropertyType, newLimit, newLimitType);
	}
	
	public int updateLimit(String ontologyProperty,int ontologyPropertyType,int oldLimitType,String oldProperty_description, String oldValues_description,
			int oldProperty_descriptionType,int oldValues_descriptionType,int oldCardinalityType,int oldCardinalityNum,
			int newLimitType,String newProperty_description, String newValues_description,
			int newProperty_descriptionType,int newValues_descriptionType,int newCardinalityType,int newCardinalityNum){
		int reFlag=this.manage.getAbstractDeleteInterface().deleteLimit(ontologyProperty, ontologyPropertyType, oldLimitType, oldProperty_description, oldValues_description, oldProperty_descriptionType, oldValues_descriptionType, oldCardinalityType, oldCardinalityNum);
		if(reFlag<1)
			return reFlag;
		return this.manage.getAbstractSetInterface().setLimit(ontologyProperty, ontologyPropertyType, newLimitType, newProperty_description, newValues_description, newProperty_descriptionType, newValues_descriptionType, newCardinalityType, newCardinalityNum);
	}
	
	public int updateOWLIndividualDataProperty(String individualName,
			String oldPropertyName, String oldPropertyValue,String oldPropertyValueType,
			String newPropertyName, String newPropertyValue,String newPropertyValueType) {
		int reFlag=this.manage.getAbstractDeleteInterface().deleteIndividualDataProperty(individualName, oldPropertyName, oldPropertyValue, oldPropertyValueType);
		if(reFlag<1)
			return reFlag;
		return this.manage.getAbstractSetInterface().setIndividualDataProperty(individualName, newPropertyName, newPropertyValue, newPropertyValueType);		
	}

	public int updateOWLIndividualObjectProPerty(String individualName_1,
			String oldIndividualName_2, String oldRelation,
			String newIndividualName_2, String newRelation){
		int reFlag=this.manage.getAbstractDeleteInterface().deleteIndividualObjectProPerty(individualName_1, oldIndividualName_2, oldRelation);
		if(reFlag<1)
			return reFlag;
		return this.manage.getAbstractSetInterface().setIndividualObjectProPerty(individualName_1, newIndividualName_2, newRelation);		
	}
	
	
	
}
