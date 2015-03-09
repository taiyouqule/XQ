package com.shenji.onto.editer.owl;

import java.util.Collection;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyUtil;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public abstract class AbsOntoDescription {
	private String token = null;
	private OntologyManage manage = null;

	public AbsOntoDescription(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		this.manage = manage;
		this.token = manage.getToken();
	}

	public int dealDescription(String objectName_1, String objectName_2,
			int type, int descriptionType) {
		try {
			// 处理本体类
			if (type == OntologyCommon.DataType.namedClass) {
				return dealOWLDescription_Class_Simple(objectName_1,
						objectName_2, descriptionType);
			}
			if (type == OntologyCommon.DataType.dataTypeProperty
					|| type == OntologyCommon.DataType.objectProperty) {
				return dealOWLDescription_Property(objectName_1, objectName_2,
						descriptionType, type);
			}
			if (type == OntologyCommon.DataType.individual)
				return dealOWLDescription_Individual(objectName_1,
						objectName_2, descriptionType);
		} catch (IllegalArgumentException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -8;
		}
		return -7;
	}

	public int dealDescription(String objectName, int Ontologytype,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum, int descriptionType) {
		try {
			// 处理本体类
			if (Ontologytype == OntologyCommon.DataType.namedClass) {
				return dealOWLDescription_Class_Complex(objectName,
						property_description, values_description,
						property_descriptionType, values_descriptionType,
						cardinalityType, cardinalityNum, descriptionType);
			} else
				return -2;
		} catch (IllegalArgumentException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -8;
		}
	}

	/**
	 * 添加类的OWL描述
	 * 
	 * @param objectName_1
	 * @param objectName_2
	 * @param descriptionType
	 * @return
	 */
	private int dealOWLDescription_Class_Simple(String className_1,
			String className_2, int descriptionType) {
		OWLNamedClass class_2 = null;
		// 如果传入的是Thing
		if (className_2.equals(OntologyCommon.OntoRootName))
			class_2 = manage.getOwlModel().getOWLThingClass();
		else
			class_2 = manage.getAbstractGetInterface().getOWLNamedClass(
					className_2);
		return dealOWLDescription_Class(className_1, class_2, descriptionType);
	}

	public abstract int dealOWLDescription_Class(String className_1,
			RDFSClass rdfClass_2, int descriptionType);

	public abstract int dealOWLDescription_Property(String propertyName_1,
			String propertyName_2, int descriptionType, int propertyType);

	public abstract int dealOWLDescription_Individual(String individualName_1,
			String individualName_2, int descriptionType);

	// public abstract int dealOWLObject(String str,int type);

	/**
	 * 添加类的复合描述
	 * 
	 * @param objectName
	 *            类名
	 * @param property_description
	 *            需要描述的属性
	 * @param values_description
	 *            该属性的约束值
	 * @param propertyType
	 *            属性类型 (对象属性、数据属性)
	 * @param descriptionType
	 *            描述类型 （父类，字类，同位类，不相交类）
	 * @param values_descriptionType
	 *            约束值类型 (类，实例...)
	 * @param cardinalityType
	 *            复合描述类型
	 * @param cardinalityNum
	 *            复合描述值
	 * @return
	 */
	private int dealOWLDescription_Class_Complex(String className,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum, int descriptionType) {
		RDFSClass rdfsclass_description = this.manage.getAbstractGetInterface()
				.getCardinality(property_description, values_description,
						property_descriptionType, values_descriptionType,
						cardinalityType, cardinalityNum);
		if (rdfsclass_description == null)
			return -2;
		return dealOWLDescription_Class(className, rdfsclass_description,
				descriptionType);
	}

	public RDFResource getDescriptionClass(OWLNamedClass namedClass,
			RDFResource rdfResource, int type) {
		Collection<RDFResource> collection = null;
		try {
			switch (type) {
			case OntologyCommon.Description.super_: {
				collection = namedClass.getSuperclasses();
				// System.err.println("super数量："+namedClass.getSuperclassCount());
				break;
			}
			case OntologyCommon.Description.equivalent_: {
				collection = namedClass.getEquivalentClasses();
				break;
			}
			case OntologyCommon.Description.disjoint_: {
				collection = namedClass.getDisjointClasses();
				break;
			}
			}
			for (RDFResource resource : collection) {
				// System.err.println(resource.getName());
				if (resource.getBrowserText().equals(
						rdfResource.getBrowserText())
						&& !resource.getBrowserText().equals(
								this.manage.getOwlModel().getOWLThingClass()
										.getBrowserText()))
					return resource;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			if (collection != null)
				collection = null;
		}
		return null;
	}

}
