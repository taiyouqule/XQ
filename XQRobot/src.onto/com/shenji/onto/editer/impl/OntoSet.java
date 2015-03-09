package com.shenji.onto.editer.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.shenji.onto.editer.inter.IOntoSet;
import com.shenji.onto.editer.owl.OWLOntoSet;
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
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.ui.properties.OWLObjectPropertySubpropertyPane;
import edu.stanford.smi.protegex.owl.ui.properties.types.OWLObjectPropertyTypesWidget;

/**
 * 本体设置类
 * 
 * @author zhq 主要设置定义域值域，属性的特征，实例的属性
 */
public class OntoSet extends OWLOntoSet implements IOntoSet {
	private String token = null;
	private OntologyManage manage = null;

	/**
	 * 构造函数
	 * 
	 * @param manage
	 *            本体管理对象
	 */
	public OntoSet(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		super(manage);
		this.manage = manage;
		this.token = manage.getToken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.sjxx.inter.AbstractOntologySetInterface#setLimit(java.lang.String,
	 * int, java.lang.String, int)
	 */
	public int setLimit(String ontologyProperty, int ontologyPropertyType,
			String limit, int limitType) {
		synchronized (token) {
			return super.setOWLLimit(ontologyProperty, ontologyPropertyType,
					limit, limitType);
		}
	}

	public int setLimit(String ontologyProperty, int ontologyPropertyType,
			int limitType, String property_description,
			String values_description, int property_descriptionType,
			int values_descriptionType, int cardinalityType, int cardinalityNum) {
		synchronized (token) {
			return super.setOWLLimit(ontologyProperty, ontologyPropertyType,
					limitType, property_description, values_description,
					property_descriptionType, values_descriptionType,
					cardinalityType, cardinalityNum);
		}
	}

	public int setCharacteristics(String ontologyProperty,
			int ontologyPropertyType, int characteristicsType) {
		synchronized (token) {
			return super.setOWLCharacteristics(ontologyProperty,
					ontologyPropertyType, characteristicsType, true);
		}
	}

	@Override
	public int setIndividualDataProperty(String individualName,
			String propertyName, String propertyValue, String propertyValueType) {
		synchronized (token) {
			return super.setOWLIndividualDataProperty(individualName,
					propertyName, propertyValue, propertyValueType);
		}
	}

	@Override
	public int setIndividualObjectProPerty(String individualName_1,
			String individualName_2, String relation) {
		// TODO Auto-generated method stub
		synchronized (token) {
			return super.setOWLIndividualObjectProPerty(individualName_1,
					individualName_2, relation);
		}
	}

}
