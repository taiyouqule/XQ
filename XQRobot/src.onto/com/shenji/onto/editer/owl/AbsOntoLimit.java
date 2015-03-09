package com.shenji.onto.editer.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.server.OntologyManage;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

public abstract class AbsOntoLimit {
	private String token = null;
	private OntologyManage manage = null;

	public AbsOntoLimit(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		this.manage = manage;
		this.token = manage.getToken();
	}

	public int dealRange(OWLProperty property, int propertyType,
			String property_description, String values_description,
			int property_descriptionType, int values_descriptionType,
			int cardinalityType, int cardinalityNum) {
		RDFSClass rdfsLimit = this.manage.getAbstractGetInterface()
				.getCardinality(property_description, values_description,
						property_descriptionType, values_descriptionType,
						cardinalityType, cardinalityNum);
		if (rdfsLimit == null)
			return -1;
		return dealRange(property, propertyType, rdfsLimit);
	}

	public int dealRange(OWLProperty property, int propertyType, String limit) {
		// TODO Auto-generated method stub
		RDFResource rdfLimit = null;
		if (propertyType == OntologyCommon.DataType.dataTypeProperty) {
			rdfLimit = OntologyCommon.RDFSDataType.getRDFSDatatype(
					manage.getOwlModel(), limit);
		}
		if (propertyType == OntologyCommon.DataType.objectProperty) {
			// 一定是类
			rdfLimit = manage.getAbstractGetInterface().getOWLNamedClass(limit);
		}
		if (rdfLimit == null)
			return -2;
		return dealRange(property, propertyType, rdfLimit);
	}

	public abstract int dealRange(OWLProperty property, int propertyType,
			RDFResource limit);

	public abstract int dealDomain(OWLProperty property, RDFSClass limit);

	public int dealDomain(OWLProperty property, String limit) {
		// TODO Auto-generated method stub
		OWLNamedClass rdfLimit = manage.getAbstractGetInterface()
				.getOWLNamedClass(limit);
		if (rdfLimit == null)
			return -2;
		return this.dealDomain(property, rdfLimit);
	}

	public int dealDomain(OWLProperty property, String property_description,
			String values_description, int property_descriptionType,
			int values_descriptionType, int cardinalityType, int cardinalityNum) {
		RDFSClass rdfsLimit = this.manage.getAbstractGetInterface()
				.getCardinality(property_description, values_description,
						property_descriptionType, values_descriptionType,
						cardinalityType, cardinalityNum);
		if (rdfsLimit == null)
			return -2;
		return this.dealDomain(property, rdfsLimit);
	}

	public int dealLimit(String ontologyProperty, int ontologyPropertyType,
			String limit, int limitType) {
		OWLProperty owlProperty = manage.getAbstractGetInterface()
				.getOWLProperty(ontologyProperty, ontologyPropertyType);
		if (owlProperty == null)
			return -1;
		switch (limitType) {
		case OntologyCommon.ProPertyLimitType.range: {
			return this.dealRange(owlProperty, ontologyPropertyType, limit);
		}
		case OntologyCommon.ProPertyLimitType.domain: {
			return this.dealDomain(owlProperty, limit);
		}
		default:
			return -3;
		}
	}

	public int dealLimit(String ontologyProperty, int ontologyPropertyType,
			int limitType, String property_description,
			String values_description, int property_descriptionType,
			int values_descriptionType, int cardinalityType, int cardinalityNum) {
		OWLProperty owlProperty = manage.getAbstractGetInterface()
				.getOWLProperty(ontologyProperty, ontologyPropertyType);
		if (owlProperty == null)
			return -1;
		switch (limitType) {
		case OntologyCommon.ProPertyLimitType.range: {
			return this.dealRange(owlProperty, ontologyPropertyType,
					property_description, values_description,
					property_descriptionType, values_descriptionType,
					cardinalityType, cardinalityNum);
		}
		case OntologyCommon.ProPertyLimitType.domain: {
			return this.dealDomain(owlProperty, property_description,
					values_description, property_descriptionType,
					values_descriptionType, cardinalityType, cardinalityNum);
		}
		default:
			return -3;
		}
	}

	public int setOWLCharacteristics(String ontologyProperty,
			int ontologyPropertyType, int characteristicsType, boolean isUse) {
		OWLProperty property = manage.getAbstractGetInterface().getOWLProperty(
				ontologyProperty, ontologyPropertyType);
		OWLObjectProperty objectProperty = null;
		if (ontologyPropertyType == OntologyCommon.DataType.objectProperty) {
			objectProperty = (OWLObjectProperty) property;
		}
		if (characteristicsType == OntologyCommon.Characteristics.CharacteristicsType.functional
				.getNum()) {
			property.setFunctional(isUse);
		} else if (characteristicsType == OntologyCommon.Characteristics.CharacteristicsType.inverser_fuction
				.getNum()) {
			property.setInverseFunctional(isUse);
		} else if (characteristicsType == OntologyCommon.Characteristics.CharacteristicsType.transitvie
				.getNum()) {
			if (objectProperty != null)
				objectProperty.setTransitive(isUse);
		} else if (characteristicsType == OntologyCommon.Characteristics.CharacteristicsType.symmetric
				.getNum()) {
			if (objectProperty != null)
				objectProperty.setTransitive(isUse);
		} else
			return -2;
		if (manage.writeTemp(false) == -1)
			return -9;
		return 1;
	}

	public abstract int dealOWLIndividualObjectProPerty(
			OWLIndividual individual, OWLProperty property,
			OWLIndividual dealIndividual);

	public int dealOWLIndividualObjectProPerty(String individualName_1,
			String individualName_2, String relation) {
		// TODO Auto-generated method stub
		OWLIndividual individual_1 = this.manage.getAbstractGetInterface()
				.getOWLIndividual(individualName_1);
		if (individual_1 == null)
			return -1;
		OWLIndividual individual_2 = this.manage.getAbstractGetInterface()
				.getOWLIndividual(individualName_2);
		if (individual_2 == null)
			return -2;
		OWLProperty property = this.manage.getAbstractGetInterface()
				.getOWLProperty(relation,
						OntologyCommon.DataType.objectProperty);
		if (property == null)
			return -3;
		try {
			if (dealOWLIndividualObjectProPerty(individual_1, property,
					individual_2) < 1)
				return -8;
			if (manage.writeTemp(true) == -1)
				return -9;
		} catch (Exception e) {
			// TODO: handle exception
			return -4;
		}
		return 1;
	}

	public abstract int dealOWLIndividualDataProperty(OWLIndividual individual,
			OWLProperty property, RDFSLiteral rdfsLiteral);

	public int dealOWLIndividualDataProperty(String individualName,
			String propertyName, String propertyValue, String propertyValueType) {
		// TODO Auto-generated method stub
		OWLIndividual individual = this.manage.getAbstractGetInterface()
				.getOWLIndividual(individualName);
		if (individual == null)
			return -1;
		OWLProperty property = this.manage.getAbstractGetInterface()
				.getOWLProperty(propertyName,
						OntologyCommon.DataType.dataTypeProperty);
		if (property == null)
			return -2;
		try {
			RDFSLiteral literal = this.manage.getAbstractGetInterface()
					.getRDFSLiteral(propertyValue, propertyValueType);
			if (literal == null)
				return -7;
			if (dealOWLIndividualDataProperty(individual, property, literal) < 1)
				return -8;
			if (manage.writeTemp(true) == -1)
				return -9;
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -4;
		}
		return 1;
	}

	public Object getIndividualObjectProperty(OWLIndividual individual,
			OWLProperty property, Object object) {
		Collection<RDFResource> collection = individual
				.getPropertyValues(property);
		OWLIndividual owlIndividual = (OWLIndividual) object;
		for (RDFResource resource : collection) {
			if (resource.getBrowserText()
					.equals(owlIndividual.getBrowserText()))
				return resource;
		}
		return null;
	}

	public ArrayList<Object> getIndividualDataProperty(
			OWLIndividual individual, OWLProperty property, Object object) {
		ArrayList<Object> list = new ArrayList<Object>();
		RDFSLiteral rdfsLiteral = (RDFSLiteral) object;
		Collection<RDFSLiteral> collection = individual
				.getPropertyValueLiterals(property);
		for (RDFSLiteral resource : collection) {
			list.add(resource);
			if (resource.getRawValue().equals(rdfsLiteral.getRawValue()))
				list.remove(resource);
		}
		return list;
	}

	public ArrayList<RDFResource> getDealLimitResources(OWLProperty property,
			int limitType, RDFResource limit) {
		ArrayList<RDFResource> arrayList = null;
		try {
			Collection<RDFResource> collection;
			if (limitType == OntologyCommon.ProPertyLimitType.range)
				collection = property.getRanges(false);
			else if (limitType == OntologyCommon.ProPertyLimitType.domain)
				collection = property.getDomains(false);
			else
				return null;
			arrayList = new ArrayList<RDFResource>();
			for (RDFResource rdfsClass : collection) {
				arrayList.add(rdfsClass);
				if (rdfsClass.getBrowserText().equals(limit.getBrowserText())) {
					arrayList.remove(rdfsClass);
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		return arrayList;
	}

}
