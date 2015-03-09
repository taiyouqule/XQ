package com.shenji.onto.editer.impl;

import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoAdd;
import com.shenji.onto.editer.owl.OWLOntoAdd;
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

/**
 * 本体添加类
 * 
 * @author zhq 封装OWL本体添加类，并对其方法进行同步
 */
public class OntoAdd extends OWLOntoAdd implements IOntoAdd {
	private String token = null;
	private OntologyManage manage = null;

	/**
	 * 构造函数
	 * 
	 * @param manage
	 *            本体操作对象
	 */
	public OntoAdd(OntologyManage manage) {
		super(manage);
		// TODO Auto-generated constructor stub
		this.manage = manage;
		this.token = manage.getToken();
	}

	public int addOntologyObject(String ontologyName, String superOntologyName,
			int ontologyType) {
		synchronized (token) {
			int reFlag = super.addOWLOntologyObject(ontologyName, ontologyType);
			if (reFlag == 1 && !superOntologyName.equals("Thing"))
				reFlag = this.manage.getAbstractAddInterface()
						.addDescriptionOntologyObject(ontologyName,
								superOntologyName, ontologyType,
								OntologyCommon.Description.super_);
			/*
			 * if(reFlag==1&&ontologyType==OntologyCommon.DataType.namedClass)
			 * FAQWebServices.addNewWords(ontologyName);
			 */
			return reFlag;
		}
	}

	public int addDescriptionOntologyObject(String str, String str2, int type,
			int descriptionType) {
		// TODO Auto-generated method stub
		synchronized (token) {
			return super.addOWLDescriptionOntologyObject(str, str2, type,
					descriptionType);
		}
	}

	public int addObjectByEquivalent(String ontologyName,
			String superOntologyName, String equivalnetOntologyName,
			int ontologyType) {
		synchronized (token) {
			int reFlag = addOntologyObject(equivalnetOntologyName,
					superOntologyName, ontologyType);
			if (reFlag != 1)
				// 该节点已经存在
				return -11;
			reFlag = addDescriptionOntologyObject(ontologyName,
					equivalnetOntologyName, ontologyType,
					OntologyCommon.Description.equivalent_);
			if (reFlag == 1) {
				RDFResource resource = null;
				switch (ontologyType) {
				case OntologyCommon.DataType.namedClass:
					resource = this.manage.getAbstractGetInterface()
							.getOWLNamedClass(equivalnetOntologyName);
					break;
				case OntologyCommon.DataType.objectProperty:
				case OntologyCommon.DataType.dataTypeProperty:
					resource = this.manage.getAbstractGetInterface()
							.getOWLProperty(equivalnetOntologyName,
									ontologyType);
					break;
				case OntologyCommon.DataType.individual:
					resource = this.manage.getAbstractGetInterface()
							.getOWLIndividual(equivalnetOntologyName);
					break;
				}
				if (resource != null)
					resource.addVersionInfo(OntologyCommon.IsOthersSynonyms);
				if (manage.writeTemp(false) == -1)
					return -9;
			}
			return reFlag;
		}
	}

	public int addDescriptionOntologyObject(String objectName,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum, int descriptionType) {
		// TODO Auto-generated method stub
		synchronized (token) {
			return super.addOWLDescriptionOntologyObject(objectName,
					property_description, values_description,
					property_descriptionType, values_descriptionType,
					cardinalityType, cardinalityNum, descriptionType);
		}
	}

	public int addIndividualType(String className, String individualName) {
		synchronized (token) {
			return super.addOWLIndividualType(className, individualName);
		}
	}

	public int addAnnotations(String ontologyName, int ontologyType,
			String annotations, String annotationsType) {
		synchronized (token) {
			int reFlag = super.addOWLAnnotations(ontologyName, ontologyType,
					annotations, annotationsType);
			return reFlag;
		}
	}

	public int addUserAnnotation(String userAnnotationsName) {
		// TODO Auto-generated method stub
		synchronized (token) {
			return super.addOWLUserAnnotation(userAnnotationsName);
		}
	}

}
