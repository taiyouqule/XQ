package com.shenji.onto.editer.impl;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoDelete;
import com.shenji.onto.editer.owl.OWLOntoDelete;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.mapping.FaqMapDBServices;

/**
 * 本体删除类
 * 
 * @author zhq 封装OWL本体删除类，并对其方法进行同步
 */
public class OntoDelete extends OWLOntoDelete implements IOntoDelete {
	private String token = null;
	private OntologyManage manage = null;

	/**
	 * 构造函数
	 * 
	 * @param manage
	 *            本体操作对象
	 */
	public OntoDelete(OntologyManage manage) {
		super(manage);
		this.manage = manage;
		this.token = manage.getToken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteDescriptionOntologyObject
	 * (java.lang.String, java.lang.String, int, int)
	 */
	public int deleteDescriptionOntologyObject(String str, String str2,
			int type, int descriptionType) {
		// TODO Auto-generated method stub
		synchronized (token) {
			return super.deleteOWLDescriptionOntologyObject(str, str2, type,
					descriptionType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteDescriptionOntologyObject
	 * (java.lang.String, java.lang.String, java.lang.String, int, int, int,
	 * int, int)
	 */
	public int deleteDescriptionOntologyObject(String objectName,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum, int descriptionType) {
		// TODO Auto-generated method stub
		synchronized (token) {
			return super.deleteOWLDescriptionOntologyObject(objectName,
					property_description, values_description,
					property_descriptionType, values_descriptionType,
					cardinalityType, cardinalityNum, descriptionType);
		}
	}

	public int deleteOntologyObject(String str, int type) {
		synchronized (token) {
			int reFlag = super.deleteOWLOntologyObject(str, type);
			// FAQ同步,重置词汇表
			if (reFlag == 1) {
				FaqMapDBServices dbServices = null;
				try {
					dbServices = new FaqMapDBServices();
					String url = manage.getAbstractQueryInterface()
							.getBaseNameSpeaceUrl();
					dbServices.delete(url, str, type);
					dbServices.close();
					manage.resetWordSet();
				} catch (ConnectionPoolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 删除本体词汇
			if (reFlag == 1 && type == OntologyCommon.DataType.namedClass) {
				// 不启用，不然删掉以前的词了,应该建个专门的本体词典
				// FAQWebServices.deleteWords(str);
			}
			return reFlag;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteIndividualType(java
	 * .lang.String, java.lang.String, boolean)
	 */
	public int deleteIndividualType(String className, String individualName,
			boolean deleteIndividual) {
		synchronized (token) {
			return super.deleteOWLIndividualType(className, individualName,
					deleteIndividual);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteIndividualType(java
	 * .lang.String, java.lang.String)
	 */
	public int deleteIndividualType(String className, String individualName) {
		synchronized (token) {
			return super.deleteOWLIndividualType(className, individualName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteLimit(java.lang.String
	 * , int, java.lang.String, int)
	 */
	public int deleteLimit(String ontologyProperty, int ontologyPropertyType,
			String limit, int limitType) {
		synchronized (token) {
			return super.deleteOWLLimit(ontologyProperty, ontologyPropertyType,
					limit, limitType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteLimit(java.lang.String
	 * , int, int, java.lang.String, java.lang.String, int, int, int, int)
	 */
	public int deleteLimit(String ontologyProperty, int ontologyPropertyType,
			int limitType, String property_description,
			String values_description, int property_descriptionType,
			int values_descriptionType, int cardinalityType, int cardinalityNum) {
		synchronized (token) {
			return super.deleteOWLLimit(ontologyProperty, ontologyPropertyType,
					limitType, property_description, values_description,
					property_descriptionType, values_descriptionType,
					cardinalityType, cardinalityNum);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.ontology.OWLOntologyDelete#deleteCharacteristics(java.lang.String
	 * , int, int)
	 */
	public int deleteCharacteristics(String ontologyProperty,
			int ontologyPropertyType, int characteristicsType) {
		synchronized (token) {
			return super.deleteCharacteristics(ontologyProperty,
					ontologyPropertyType, characteristicsType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteIndividualObjectProPerty
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public int deleteIndividualObjectProPerty(String individualName_1,
			String individualName_2, String relation) {
		synchronized (token) {
			return super.deleteOWLIndividualObjectProPerty(individualName_1,
					individualName_2, relation);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteIndividualDataProperty
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public int deleteIndividualDataProperty(String individualName,
			String propertyName, String propertyValue, String propertyValueType) {
		synchronized (token) {
			return super.deleteOWLIndividualDataProperty(individualName,
					propertyName, propertyValue, propertyValueType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologyDeleteInterface#deleteAnnotations(java.
	 * lang.String, int, java.lang.String, java.lang.String)
	 */
	public int deleteAnnotations(String ontologyName, int ontologyType,
			String annotations, String annotationsType) {
		synchronized (token) {
			return super.deleteOWLAnnotations(ontologyName, ontologyType,
					annotations, annotationsType);
		}
	}
}
