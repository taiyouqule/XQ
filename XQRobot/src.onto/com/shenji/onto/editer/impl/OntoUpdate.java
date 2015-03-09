package com.shenji.onto.editer.impl;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoUpdate;
import com.shenji.onto.editer.owl.OWLOntoUpdate;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.mapping.FaqMapDBServices;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * 本体修改类
 * 
 * @author zhq
 */
public class OntoUpdate extends OWLOntoUpdate implements IOntoUpdate {
	private String token = null;
	private OntologyManage manage = null;

	/**
	 * 构造函数
	 * 
	 * @param manage
	 *            本体管理对象
	 */
	public OntoUpdate(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		super(manage);
		this.manage = manage;
		this.token = manage.getToken();
	}

	public int renameOntologyObject(String oldStr, String newStr, int type) {
		synchronized (token) {
			int reFlag = super.renameOWLOntologyObject(oldStr, newStr, type);
			if (reFlag == 1) {
				manage.resetWordSet();
				FaqMapDBServices dbServices;
				try {
					dbServices = new FaqMapDBServices();
					String url = manage.getAbstractQueryInterface()
							.getBaseNameSpeaceUrl();
					dbServices.reNameOntology(url, oldStr, newStr, type);
					dbServices.close();
					manage.resetWordSet();
				} catch (ConnectionPoolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reFlag == 1 && type == OntologyCommon.DataType.namedClass) {
				// 暂时不启用
				/*
				 * FAQWebServices.deleteWords(oldStr);
				 * FAQWebServices.deleteWords(oldStr);
				 */
			}
			return reFlag;
		}
	}

	public int updateDescriptionOntologyObject(String objectName_1,
			String oldObjectName_2, String newObjectName_2, int type,
			int descriptionType) {
		synchronized (token) {
			return super.updateOWLDescriptionOntologyObject(objectName_1,
					oldObjectName_2, newObjectName_2, type, descriptionType);
		}
	}

	public int updateDescriptionOntologyObject(String objectName,
			String oldProperty_description, String oldValues_description,
			String newProperty_description, String newValues_description,
			int oldProperty_descriptionType, int oldValues_descriptionType,
			int newProperty_descriptionType, int newValues_descriptionType,
			int oldCardinalityType, int oldCardinalityNum,
			int newCardinalityType, int newCardinalityNum, int descriptionType) {
		synchronized (token) {
			return super.updateOWLDescriptionOntologyObject(objectName,
					oldProperty_description, oldValues_description,
					newProperty_description, newValues_description,
					oldProperty_descriptionType, oldValues_descriptionType,
					newProperty_descriptionType, newValues_descriptionType,
					oldCardinalityType, oldCardinalityNum, newCardinalityType,
					newCardinalityNum, descriptionType);
		}
	}

	public int updateIndividualType(String individual, String oldClassName,
			String newClassName) {
		synchronized (token) {
			return super.updateOWLIndividualType(individual, oldClassName,
					newClassName);
		}
	}

	public int updateIndividualDataProperty(String individualName,
			String oldPropertyName, String oldPropertyValue,
			String oldPropertyValueType, String newPropertyName,
			String newPropertyValue, String newPropertyValueType) {
		synchronized (token) {
			return super.updateOWLIndividualDataProperty(individualName,
					oldPropertyName, oldPropertyValue, oldPropertyValueType,
					newPropertyName, newPropertyValue, newPropertyValueType);
		}
	}

	public int updateIndividualObjectProPerty(String individualName_1,
			String oldIndividualName_2, String oldRelation,
			String newIndividualName_2, String newRelation) {
		synchronized (token) {
			return super.updateOWLIndividualObjectProPerty(individualName_1,
					oldIndividualName_2, oldRelation, newIndividualName_2,
					newRelation);
		}
	}
}
