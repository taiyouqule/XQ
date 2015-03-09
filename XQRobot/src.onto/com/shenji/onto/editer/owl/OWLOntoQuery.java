package com.shenji.onto.editer.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.jdom.Document;
import org.jdom.Element;
import org.protege.editor.owl.model.hierarchy.roots.NamedClassExtractor;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleCache;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Resource;
import com.shenji.common.log.Log;
import com.shenji.common.util.FileUtil;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.LockedPointServer;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyUtil;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.importer.OWLImporter;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.AbstractRDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class OWLOntoQuery {
	private String token = null;
	private OntologyManage manage = null;
	private static String CLASS_COUNT = "classCount";
	private static String Division = ":";

	public OWLOntoQuery(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		this.manage = manage;
		this.token = manage.getToken();
	}

	protected Collection<RDFResource> duplicateRemoval(
			Collection<RDFResource> collection_orc,
			Collection<RDFResource> collection_romove) {
		if (collection_orc == null)
			return null;
		if (collection_romove == null)
			return null;
		Collection<RDFResource> tempCollection = new ArrayList<RDFResource>();
		for (RDFResource rdfResourc_orc : collection_orc) {
			tempCollection.add(rdfResourc_orc);
			for (RDFResource rdfResource_remove : collection_romove) {
				if (rdfResource_remove.getBrowserText().equals(
						rdfResourc_orc.getBrowserText())) {
					tempCollection.remove(rdfResource_remove);
				}
			}
		}
		return tempCollection;
	}

	protected Collection<RDFResource> duplicateRemoval(
			Collection<RDFResource> collection, RDFResource rdfResource) {
		if (collection == null)
			return null;
		Collection<RDFResource> tempCollection = new ArrayList<RDFResource>();
		for (RDFResource rdfResourc_orc : collection) {
			tempCollection.add(rdfResourc_orc);
			if (rdfResource.getBrowserText().equals(
					rdfResourc_orc.getBrowserText())) {
				tempCollection.remove(rdfResourc_orc);
			}
		}
		return tempCollection;
	}

	private Collection<RDFResource> getOntologyClassByDescription(
			String className, boolean transitive, int relation) {
		OWLNamedClass namedClass;
		if (className.equals(null)) {
			return null;
		}
		if (className.equals("Thing"))
			namedClass = this.manage.getOwlModel().getOWLThingClass();
		else
			namedClass = manage.getAbstractGetInterface().getOWLNamedClass(
					className);
		if (namedClass == null)
			return null;
		Collection<RDFResource> collection = null;
		switch (relation) {
		case OntologyCommon.Description.super_: {
			collection = duplicateRemoval(
					namedClass.getSuperclasses(transitive),
					namedClass.getEquivalentClasses());
			break;
		}
		case OntologyCommon.Description.sub_: {
			collection = duplicateRemoval(namedClass.getSubclasses(transitive),
					namedClass.getEquivalentClasses());
			break;
		}
		case OntologyCommon.Description.inferred_super: {
			collection = duplicateRemoval(namedClass.getSuperclasses(true),
					namedClass.getEquivalentClasses());
			collection = duplicateRemoval(collection,
					namedClass.getSuperclasses(false));
			collection = duplicateRemoval(collection, namedClass);
			if (collection == null)
				break;
			break;
		}
		case OntologyCommon.Description.inferred_sub: {
			collection = duplicateRemoval(namedClass.getSubclasses(true),
					namedClass.getEquivalentClasses());
			if (collection == null)
				break;
			for (RDFResource resource : collection) {
				if (resource.getBrowserText().equals(
						namedClass.getBrowserText()))
					collection.remove(resource);
			}
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
		default:
			return null;
		}

		return collection;
	}

	private Collection<RDFResource> getOntologyPropertyByDescription(
			String propertyName, int type, boolean transitive, int relation) {
		OWLProperty property;
		// 不是求所有类的子类
		if (propertyName.equals("Thing")
				&& relation != OntologyCommon.Description.sub_)
			return null;
		property = manage.getAbstractGetInterface().getOWLProperty(
				propertyName, type);
		if (property == null && (!propertyName.equals("Thing")))
			return null;
		Collection<RDFResource> collection = null;
		switch (relation) {
		case OntologyCommon.Description.super_: {
			collection = property.getSuperproperties(transitive);
			break;
		}
		case OntologyCommon.Description.sub_: {
			if (propertyName.equals("Thing")) {
				Iterator<RDFProperty> iterator = manage.getOwlModel()
						.listRDFProperties();
				Collection<RDFResource> tempList = new ArrayList<RDFResource>();
				while (iterator.hasNext()) {
					// RDFProperty property2=(RDFProperty) dd.next();
					RDFProperty rdfProperty = iterator.next();

					switch (type) {
					case OntologyCommon.DataType.objectProperty: {
						if (rdfProperty instanceof DefaultOWLObjectProperty
								&& rdfProperty.getSuperpropertyCount() == 0) {
							tempList.add(rdfProperty);
						}
						break;
					}
					case OntologyCommon.DataType.dataTypeProperty: {
						if (rdfProperty instanceof DefaultOWLDatatypeProperty
								&& rdfProperty.getSuperpropertyCount() == 0) {
							tempList.add(rdfProperty);
						}
						break;
					}
					default:
						break;
					}
				}
				collection = tempList;
				// collection=dd.getSubproperties(transitive);
			} else
				collection = property.getSubproperties(transitive);
			break;
		}
		case OntologyCommon.Description.equivalent_: {
			collection = property.getEquivalentProperties();
			break;
		}
		case OntologyCommon.Description.inverse_: {
			List<RDFResource> list = new ArrayList<RDFResource>();
			list.add(property.getInverseProperty());
			collection = list;
			break;
		}
		case OntologyCommon.Description.disjoint_: {
			collection = property.getDifferentFrom();
			break;
		}
		case OntologyCommon.ProPertyLimitType.range: {
			collection = property.getRanges(transitive);
			break;
		}
		case OntologyCommon.ProPertyLimitType.domain: {
			collection = property.getDomains(transitive);
			break;
		}
		default:
			return null;
		}
		return collection;
	}

	private Collection<RDFResource> getOntologyIndividualByDescription(
			String individualName, int relation) {
		OWLIndividual individual = manage.getAbstractGetInterface()
				.getOWLIndividual(individualName);
		if (individual == null)
			return null;
		Collection<RDFResource> collection = null;
		switch (relation) {
		case OntologyCommon.Description.equivalent_: {
			collection = individual.getSameAs();
			break;
		}
		case OntologyCommon.Description.disjoint_: {
			collection = individual.getDifferentFrom();
			break;
		}
		default:
			return null;
		}
		return collection;
	}

	public String[] getOntologyDescriptionObject(String ObjectName,
			int ObjectType, boolean transitive, int relation, boolean usePackage) {
		List<String> list = new ArrayList<String>();
		Collection<RDFResource> collection = null;
		switch (ObjectType) {
		case OntologyCommon.DataType.namedClass: {
			collection = getOntologyClassByDescription(ObjectName, transitive,
					relation);
			break;
		}
		case OntologyCommon.DataType.dataTypeProperty:
		case OntologyCommon.DataType.objectProperty: {
			collection = getOntologyPropertyByDescription(ObjectName,
					ObjectType, transitive, relation);
			break;
		}
		case OntologyCommon.DataType.individual: {
			collection = getOntologyIndividualByDescription(ObjectName,
					relation);
			break;
		}
		}
		// System.err.println(this.manage.getOwlModel().getNamespaceManager().getDefaultNamespace());
		// System.err.println(this.manage.getOwlModel().getDefaultOWLOntology().getName());
		if (collection == null)
			return null;
		for (RDFResource owl : collection) {
			// System.err.println(owl.getNamespace());
			if (owl instanceof DefaultOWLSomeValuesFrom
					|| owl instanceof DefaultOWLMinCardinality
					|| owl instanceof DefaultOWLAllValuesFrom
					|| owl instanceof DefaultOWLCardinality
					|| owl instanceof DefaultOWLMaxCardinality) {
				// 暂时修改
				// list.add(owl.getBrowserText());
				if (usePackage)
					if (relation == OntologyCommon.ProPertyLimitType.range
							|| relation == OntologyCommon.ProPertyLimitType.domain)
						list.add(packageLimitXml(owl, ObjectName, ObjectType,
								relation, true));
					else
						list.add(packageDescriptionXml(owl, ObjectName,
								ObjectType, relation, true));
				else
					list.add(owl.getBrowserText());
			} else if ((owl != null && owl.getNamespace() != null && owl
					.getNamespace().contains(
							this.manage.getOwlModel().getNamespaceManager()
									.getDefaultNamespace()))
					|| (ObjectType == OntologyCommon.DataType.dataTypeProperty && relation == OntologyCommon.ProPertyLimitType.range))
				// if(owl!=null&&owl.getNamespace()!=null&&owl.getNamespace().contains(this.manage.getOwlModel().getDefaultOWLOntology().getName()))
				// list.add(owl.getBrowserText());
				if (usePackage)
					if (relation == OntologyCommon.ProPertyLimitType.range
							|| relation == OntologyCommon.ProPertyLimitType.domain)
						list.add(packageLimitXml(owl, ObjectName, ObjectType,
								relation, false));
					else
						list.add(packageDescriptionXml(owl, ObjectName,
								ObjectType, relation, false));
				else
					list.add(owl.getBrowserText());
		}
		// Collections.sort(list,new QueryResultSort(this.manage));
		return (String[]) list.toArray(new String[list.size()]);
	}

	private String packageXml(RDFResource rdfResource, String ontologyName,
			int ontologyType, int dealType, boolean isComplex,
			boolean isDescription) {
		Document document = new Document();
		Element rootE = new Element(ontologyName);
		if (isDescription)
			rootE.setAttribute("decription",
					OntologyCommon.Description.getDescriptionStr(dealType));
		else
			rootE.setAttribute("Limit",
					OntologyCommon.ProPertyLimitType.getLimitStr(dealType));
		rootE.setAttribute("typeNum", ontologyType + "");
		Element subE = null;
		if (!isComplex) {
			// String locked=null;
			String name = rdfResource.getName().replace(
					manage.getOwlModel().getNamespaceManager()
							.getDefaultNamespace(), "");
			rootE.setAttribute("isComplex", "false");

			if (isDescription) {
				subE = new Element(
						OntologyCommon.DataType.getDataTypeStr(ontologyType));
				subE.setAttribute("typeNum", ontologyType + "");
			} else {
				subE = new Element(
						OntologyCommon.DataType
								.getDataTypeStr(OntologyCommon.DataType.namedClass));
				subE.setAttribute("typeNum", OntologyCommon.DataType.namedClass
						+ "");// 属性的非复合定义域值域一定是类
			}

			if (rdfResource.getBrowserText().contains(
					manage.getOwlModel().getNamespaceManager()
							.getDefaultNamespace()))
				subE.setAttribute("name", name);
			else
				subE.setAttribute("name", rdfResource.getBrowserText());
			if (rdfResource.getVersionInfo() == null
					|| rdfResource.getVersionInfo().size() == 0) {
			} else {
				subE.setAttribute("syn", OntologyCommon.IsOthersSynonyms);
			}
			// System.out.println(name);

		} else {
			rootE.setAttribute("isComplex", "true");
			RDFResource property = null;
			RDFResource value = null;
			int valueNum = -1;
			String cardinality = null;
			int cardinalityType = -1;
			if (rdfResource instanceof DefaultOWLSomeValuesFrom) {
				cardinalityType = OntologyCommon.Cardinality.some;
				cardinality = OntologyCommon.Cardinality
						.getCardinalityStr(cardinalityType);
				property = ((DefaultOWLSomeValuesFrom) rdfResource)
						.getOnProperty();
				value = ((DefaultOWLSomeValuesFrom) rdfResource)
						.getSomeValuesFrom();
			} else if (rdfResource instanceof DefaultOWLAllValuesFrom) {
				cardinalityType = OntologyCommon.Cardinality.only;
				cardinality = OntologyCommon.Cardinality
						.getCardinalityStr(cardinalityType);
				property = ((DefaultOWLAllValuesFrom) rdfResource)
						.getOnProperty();
				value = ((DefaultOWLAllValuesFrom) rdfResource)
						.getAllValuesFrom();
			} else if (rdfResource instanceof DefaultOWLMinCardinality) {
				cardinalityType = OntologyCommon.Cardinality.min;
				cardinality = OntologyCommon.Cardinality
						.getCardinalityStr(cardinalityType);
				property = ((DefaultOWLMinCardinality) rdfResource)
						.getOnProperty();
				value = ((DefaultOWLMinCardinality) rdfResource)
						.getValuesFrom();
				valueNum = ((DefaultOWLMinCardinality) rdfResource)
						.getCardinality();
			} else if (rdfResource instanceof DefaultOWLMaxCardinality) {
				cardinalityType = OntologyCommon.Cardinality.max;
				cardinality = OntologyCommon.Cardinality
						.getCardinalityStr(cardinalityType);
				property = ((DefaultOWLMaxCardinality) rdfResource)
						.getOnProperty();
				value = ((DefaultOWLMaxCardinality) rdfResource)
						.getValuesFrom();
				valueNum = ((DefaultOWLMaxCardinality) rdfResource)
						.getCardinality();
			} else if (rdfResource instanceof DefaultOWLCardinality) {
				cardinalityType = OntologyCommon.Cardinality.exactly;
				cardinality = OntologyCommon.Cardinality
						.getCardinalityStr(cardinalityType);
				property = ((DefaultOWLCardinality) rdfResource)
						.getOnProperty();
				value = ((DefaultOWLCardinality) rdfResource).getValuesFrom();
				valueNum = ((DefaultOWLCardinality) rdfResource)
						.getCardinality();
				;
			} else
				return null;
			subE = new Element(getComplexDataTypeStrByProperty(property));
			subE.setAttribute("typeNum",
					getComplexDataTypeNumByProperty(property) + "");
			subE.setAttribute("cardinality", cardinality);
			subE.setAttribute("cardinalityType", cardinalityType + "");
			Element propertyE = new Element(
					OntologyCommon.DataType.getDataTypeStr(property));
			String name_p = property.getName().replace(
					manage.getOwlModel().getNamespaceManager()
							.getDefaultNamespace(), "");
			propertyE.setAttribute("name", name_p);
			Element valueE = new Element("value");
			String name_v = null;
			if (value.getBrowserText().contains(
					manage.getOwlModel().getNamespaceManager()
							.getDefaultNamespace()))
				name_v = value.getName().replace(
						manage.getOwlModel().getNamespaceManager()
								.getDefaultNamespace(), "");
			else
				name_v = value.getBrowserText();
			valueE.setAttribute("name", name_v);
			subE.addContent(propertyE);
			subE.addContent(valueE);
			if (valueNum != -1) {
				Element valueNumE = new Element("ValueNum");
				valueNumE.setAttribute("number", valueNum + "");
				subE.addContent(valueNumE);
			}
		}
		rootE.addContent(subE);
		document.addContent(rootE);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		// Log.debugSystemOut(xmlStr);
		return xmlStr;
	}

	private String packageDescriptionXml(RDFResource rdfResource,
			String ontologyName, int ontologyType, int descriptionType,
			boolean isComplex) {
		return packageXml(rdfResource, ontologyName, ontologyType,
				descriptionType, isComplex, true);
	}

	private String getComplexDataTypeStrByProperty(RDFResource resource) {
		if (resource instanceof OWLObjectProperty)
			return OntologyCommon.ComplexDataType
					.getComplexDataTypeStr(OntologyCommon.ComplexDataType.objectProperty_namedClass);
		else if (resource instanceof OWLDatatypeProperty)
			return OntologyCommon.ComplexDataType
					.getComplexDataTypeStr(OntologyCommon.ComplexDataType.dataProperty_cardinality);
		return null;
	}

	private int getComplexDataTypeNumByProperty(RDFResource resource) {
		if (resource instanceof OWLObjectProperty)
			return OntologyCommon.ComplexDataType.objectProperty_namedClass;
		else if (resource instanceof OWLDatatypeProperty)
			return OntologyCommon.ComplexDataType.dataProperty_cardinality;
		return -1;
	}

	private String packageLimitXml(RDFResource rdfResource,
			String ontologyName, int ontologyType, int limitType,
			boolean isComplex) {
		return packageXml(rdfResource, ontologyName, ontologyType, limitType,
				isComplex, false);
	}

	public String getBasicInformation() {
		ArrayList<String> list = new ArrayList<String>();
		int classCount = getRDFSResourceCount(this.manage.getOwlModel()
				.getRDFSClasses(), OntologyCommon.DataType.namedClass);
		int individualCount = this.manage.getOwlModel().getRDFIndividuals()
				.size();
		int objectPropertyCount = getRDFSResourceCount(this.manage
				.getOwlModel().getRDFProperties(),
				OntologyCommon.DataType.objectProperty);
		int dataPropertyCount = getRDFSResourceCount(this.manage.getOwlModel()
				.getRDFProperties(), OntologyCommon.DataType.dataTypeProperty);
		Document document = new Document();
		Element rootElement = new Element("BasicInformation");
		Element classE = new Element("classCount");
		classE.setText(classCount + "");
		Element individualE = new Element("individualCount");
		individualE.setText(individualCount + "");
		Element objectProE = new Element("objectPropertyCount");
		objectProE.setText(objectPropertyCount + "");
		Element dataProE = new Element("dataPropertyCount");
		dataProE.setText(dataPropertyCount + "");
		rootElement.addContent(classE);
		rootElement.addContent(individualE);
		rootElement.addContent(objectProE);
		rootElement.addContent(dataProE);
		document.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		return xmlStr;
	}

	private int getRDFSResourceCount(Collection<RDFResource> collection,
			int type) {
		int count = 0;
		for (RDFResource resource : collection) {
			if (resource != null
					&& resource.getNamespace() != null
					&& resource.getNamespace().contains(
							this.manage.getOwlModel().getDefaultOWLOntology()
									.getName()))
				switch (type) {
				case OntologyCommon.DataType.namedClass:
					count++;
					break;
				case OntologyCommon.DataType.objectProperty:
					if (resource instanceof DefaultOWLObjectProperty)
						count++;
					break;
				case OntologyCommon.DataType.dataTypeProperty:
					if (resource instanceof DefaultOWLDatatypeProperty)
						count++;
					break;
				}

		}
		return count;
	}

	public String getOntologyOWLString(String tempPath) {
		// TODO Auto-generated method stub
		String str = "";
		ArrayList<String> context = FileUtil
				.read(this.manage.getOpenFileName());
		for (String s : context) {
			str = str + s + "\n";
		}
		return str;
	}

	public String[] getOntologyDataType() {
		return OntologyCommon.RDFSDataType.getRDFDataType();
	}

	public String[] getAnnotationsType() {
		return OWLAnnotation.getRDFAnnotations();
	}

	public String[] getUserAnnotationsType() {
		Collection<RDFProperty> annotationPropertiesList = this.manage
				.getOwlModel().getOWLAnnotationProperties();
		List<String> userAnnotationsList = new ArrayList<String>();
		// this.manage.getOwlModel().getOWLThingClass().getsub
		int i = 0;
		for (RDFProperty property : annotationPropertiesList) {
			if (property != null
					&& property.getNamespace().contains(
							this.manage.getOwlModel().getDefaultOWLOntology()
									.getName()))
				userAnnotationsList.add(property.getBrowserText());
		}
		return (String[]) userAnnotationsList
				.toArray(new String[userAnnotationsList.size()]);

	}

	public String getAnnotations(String ontologyName, int ontologyType) {
		RDFResource resource = null;
		String ontologyTypeStr = null;
		switch (ontologyType) {
		case OntologyCommon.DataType.namedClass:
			ontologyTypeStr = "namedClass";
			resource = (AbstractRDFSClass) manage.getAbstractGetInterface()
					.getOWLNamedClass(ontologyName);
			break;
		case OntologyCommon.DataType.objectProperty:
			ontologyTypeStr = "objectProperty";
			resource = manage.getAbstractGetInterface().getOWLProperty(
					ontologyName, ontologyType);
			break;
		case OntologyCommon.DataType.dataTypeProperty:
			ontologyTypeStr = "dataTypeProperty";
			resource = manage.getAbstractGetInterface().getOWLProperty(
					ontologyName, ontologyType);
			break;
		case OntologyCommon.DataType.individual:
			ontologyTypeStr = "individual";
			resource = manage.getAbstractGetInterface().getOWLIndividual(
					ontologyName);
			break;
		default:
			break;
		}
		if (ontologyName.equals("Thing")) {
			ontologyTypeStr = "Thing";
			// resource=this.manage.getOwlModel().getOWLOntologyByURI(this.manage.getOwlModel().getDefaultOWLOntology().getName());
			// resource=this.manage.getOwlModel().getOWLOntologyByURI(this.manage.getOwlModel().getNamespaceManager().getDefaultNamespace());
			resource = this.manage.getOwlModel().getOWLThingClass();
		}
		if (resource == null)
			return null;
		/*
		 * RDFProperty
		 * property=this.manage.getOwlModel().createAnnotationProperty("name");
		 * Collection c=this.manage.getOwlModel().getOWLAnnotationProperties();
		 * resource.addPropertyValue(property, "fsdf");
		 * if(manage.writeTemp()==-1) return null;
		 */
		return OWLAnnotation.getAnnotationsXML(resource, ontologyName,
				ontologyTypeStr);
	}

	public String getCharacteristics(String propertyName, int propertyType) {
		OWLProperty owlProperty = this.manage.getAbstractGetInterface()
				.getOWLProperty(propertyName, propertyType);
		if (owlProperty == null)
			return null;
		Document document = new Document();
		String[] characteristics = OntologyCommon.Characteristics
				.getCharacteristics();
		Element rootElement = new Element(propertyName);
		boolean isExitElement = false;
		if (propertyType == OntologyCommon.DataType.dataTypeProperty) {
			boolean isUse = false;
			Element e = new Element(
					OntologyCommon.Characteristics.CharacteristicsType.functional
							.toString());
			isUse = owlProperty.isFunctional();
			String isUseValues;
			if (isUse)
				isUseValues = "true";
			else
				isUseValues = "false";
			e.setAttribute("isUse", isUseValues);
			rootElement.addContent(e);
		} else {
			for (String str : characteristics) {
				boolean isUse = false;
				if (str.equals(OntologyCommon.Characteristics.CharacteristicsType.functional
						.toString()))
					isUse = owlProperty.isFunctional();
				if (str.equals(OntologyCommon.Characteristics.CharacteristicsType.inverser_fuction
						.toString()))
					isUse = owlProperty.isInverseFunctional();
				if (str.equals(OntologyCommon.Characteristics.CharacteristicsType.transitvie
						.toString()))
					isUse = ((OWLObjectProperty) owlProperty).isTransitive();
				if (str.equals(OntologyCommon.Characteristics.CharacteristicsType.symmetric
						.toString()))
					isUse = ((OWLObjectProperty) owlProperty).isSymmetric();
				Element e = new Element(str);
				String isUseValues;
				if (isUse)
					isUseValues = "true";
				else
					isUseValues = "false";
				e.setAttribute("isUse", isUseValues);
				rootElement.addContent(e);
			}
		}
		// 这里改了下,从上面移下来了
		document.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		// Log.debugSystemOut(xmlStr);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		return xmlStr;
	}

	public String getIndividuals() {
		Collection<RDFIndividual> individualsCollection = this.manage
				.getOwlModel().getRDFIndividuals();
		Document document = new Document();
		Element rootElement = new Element("Individuals");
		for (RDFResource rdfResource : individualsCollection) {
			Element superE = new Element("individual");
			superE.setAttribute("name", rdfResource.getBrowserText());
			// 去除每个实例输出类型
			// List<Object> list=getOWLIndividualType(rdfResource, true);
			// superE.addContent(list);
			rootElement.addContent(superE);
		}
		document.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		// Log.debugSystemOut(xmlStr);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		return xmlStr;
	}

	/*
	 * private List<Object> getOWLIndividualType(RDFResource individual,boolean
	 * reElement){ Collection<RDFResource>
	 * typeCollection=individual.getRDFTypes(); List<Object> list=new
	 * ArrayList<Object>(); for(RDFResource typeResource:typeCollection){ String
	 * type=typeResource.getBrowserText(); if(type.equals("owl:Thing"))
	 * type="Thing"; if(reElement){ Element e=new Element("type");
	 * e.setText(type); list.add(e.getMessage(),e); } else list.add(type); } return list; }
	 */

	public String getIndividualType(String individualName) {
		OWLIndividual individual = this.manage.getAbstractGetInterface()
				.getOWLIndividual(individualName);
		if (individual == null)
			return null;
		Document document = new Document();
		String[] characteristics = OntologyCommon.Characteristics
				.getCharacteristics();
		Element rootElement = new Element("individual");
		rootElement.setAttribute("name", individualName);
		Collection<RDFResource> typeCollection = individual.getRDFTypes();
		for (RDFResource typeResource : typeCollection) {
			Element e = null;
			if (typeResource instanceof DefaultOWLNamedClass) {
				if (typeResource.getBrowserText().contains("Thing"))
					continue;// “owl.Thing 类型不显示给用户”
				int typeNum = OntologyCommon.DataType.namedClass;
				String typeStr = OntologyCommon.DataType
						.getDataTypeStr(typeNum);
				e = new Element(typeStr);
				e.setAttribute("typeNum", typeNum + "");
				e.setAttribute("name", typeResource.getBrowserText());
				e.setAttribute("isComplex", "false");
			} else {
				// 待修改
				e = new Element("null");
			}
			rootElement.addContent(e);
		}
		document.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		// Log.debugSystemOut(xmlStr);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		return xmlStr;
	}

	public String[] getNamedClassIndividual(String namedClassName) {
		OWLNamedClass namedClass = this.manage.getAbstractGetInterface()
				.getOWLNamedClass(namedClassName);
		if (namedClass == null)
			return null;
		List<String> individualsList = new ArrayList<String>();
		Collection<RDFResource> individualsCollection = this.manage
				.getOwlModel().getOWLIndividuals();
		for (RDFResource rdfResource : individualsCollection) {
			Collection<RDFResource> rdfTypes = rdfResource.getRDFTypes();
			for (RDFResource rdfType : rdfTypes) {
				if (rdfType.getBrowserText()
						.equals(namedClass.getBrowserText())) {
					individualsList.add(rdfResource.getBrowserText());
				}
			}
		}
		return (String[]) individualsList.toArray(new String[individualsList
				.size()]);
	}

	public String getIndividualObjectProperty(String individualName) {
		OWLIndividual individual = this.manage.getAbstractGetInterface()
				.getOWLIndividual(individualName);
		if (individual == null)
			return null;
		Document document = new Document();
		// String[]
		// characteristics=OntologyType.Characteristics.getCharacteristics();
		Element rootElement = new Element(
				OntologyCommon.DataType
						.getDataTypeStr(OntologyCommon.DataType.individual));
		rootElement.setAttribute("name", individualName);
		Collection<RDFResource> typeCollection = individual.getRDFProperties();
		for (RDFResource objResource : typeCollection) {
			if (!(objResource instanceof DefaultOWLObjectProperty))
				continue;
			Iterator<RDFResource> iterator = individual
					.listPropertyValues((RDFProperty) objResource);
			while (iterator.hasNext()) {
				RDFResource individualResource = iterator.next();
				if (individualResource instanceof DefaultOWLIndividual) {
					Element element = new Element(
							OntologyCommon.ComplexDataType
									.getComplexDataTypeStr(OntologyCommon.ComplexDataType.objectProperty_individual));
					element.setAttribute(
							"typeNum",
							OntologyCommon.ComplexDataType.objectProperty_individual
									+ "");
					Element objE = new Element(
							OntologyCommon.DataType
									.getDataTypeStr(OntologyCommon.DataType.objectProperty));
					objE.setAttribute("name", objResource.getBrowserText());
					Element individualE = new Element("value");
					individualE.setAttribute("name",
							individualResource.getBrowserText());
					element.addContent(objE);
					element.addContent(individualE);
					rootElement.addContent(element);
				}
			}
		}
		document.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		// Log.debugSystemOut(xmlStr);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		return xmlStr;
	}

	public String getIndividualDataProperty(String individualName) {
		OWLIndividual individual = this.manage.getAbstractGetInterface()
				.getOWLIndividual(individualName);
		/*
		 * RDFIndividual
		 * aa=this.manage.getOwlModel().getRDFIndividual(individualName);
		 * aa.getRDFProperties();
		 */
		if (individual == null)
			return null;
		Document document = new Document();
		// String[]
		// characteristics=OntologyType.Characteristics.getCharacteristics();
		Element rootElement = new Element(
				OntologyCommon.DataType
						.getDataTypeStr(OntologyCommon.DataType.individual));
		rootElement.setAttribute("name", individualName);
		Collection<RDFResource> typeCollection = individual.getRDFProperties();
		for (RDFResource dataResource : typeCollection) {
			if (!(dataResource instanceof DefaultOWLDatatypeProperty))
				continue;
			Iterator<RDFResource> iterator = individual
					.listPropertyValues((RDFProperty) dataResource);
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				Element element = new Element(
						OntologyCommon.ComplexDataType
								.getComplexDataTypeStr(OntologyCommon.ComplexDataType.dataProperty_value));
				element.setAttribute("typeNum",
						OntologyCommon.ComplexDataType.dataProperty_value + "");
				Element dataE = new Element(
						OntologyCommon.DataType
								.getDataTypeStr(OntologyCommon.DataType.dataTypeProperty));
				dataE.setAttribute("name", dataResource.getBrowserText());
				Element ValueE = new Element("value");
				String type = OntologyCommon.RDFSDataType.getBasicDataType(obj);
				ValueE.setAttribute("name", obj.toString());
				ValueE.setAttribute("type", type);
				element.addContent(dataE);
				element.addContent(ValueE);
				rootElement.addContent(element);
			}
		}
		document.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document,
				Configuration.XML_CODE);
		// Log.debugSystemOut(xmlStr);
		xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
		return xmlStr;
	}

	/*
	 * public String getBaseNameSpeaceUrl(){
	 * //manage.getOwlModel().getOntModel()
	 * .getDocumentManager().addIgnoreImport(
	 * "http://172.30.3.56:8080/test/123.owl"); //TripleStore store=ne
	 * //edu.stanford.smi.protegex.owl.model.triplestore.Triple
	 * triple=edu.stanford.smi.protegex.owl.model.triplestore.Triple.
	 * //manage.getOwlModel().getOntModel().create //TripleStore
	 * //NarrowFrameStore JenaOWLModel model=null; try {
	 * model=ProtegeOWL.createJenaOWLModelFromURI
	 * ("http://172.30.3.56:8080/test/123.owl"); } catch (OntologyLoadException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * this.manage
	 * .getOwlModel().getNamespaceManager().addImport(model.getTripleStoreModel
	 * ().getActiveTripleStore());
	 * //this.manage.getOwlModel().getTerminalFrameStore(). //TripleStore
	 * store=model.getTripleStoreModel().get
	 * //manage.getOwlModel().getTripleStoreModel
	 * ().createActiveImportedTripleStore(arg0)
	 * //this.manage.getOwlModel().getNamespaceManager().addImport(" dsf"); try
	 * { //this.manage.resetOWLModel(); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return null; }
	 */

	public String getBaseNameSpeaceUrl() {
		String url = manage.getOwlModel().getNamespaceManager()
				.getDefaultNamespace();
		return url;
	}

	public boolean getOntologyIsOthersSyn(String ontologyName) {
		RDFResource resource = null;
		resource = manage.getOwlModel().getRDFResource(ontologyName);
		/*
		 * switch (ontologyType) { case OntologyType.DataType.namedClass:
		 * resource
		 * =manage.getAbstractGetInterface().getOWLNamedClass(ontologyName);
		 * break; case OntologyType.DataType.objectProperty: case
		 * OntologyType.DataType.dataTypeProperty:
		 * resource=manage.getAbstractGetInterface
		 * ().getOWLProperty(ontologyName, ontologyType); break; case
		 * OntologyType.DataType.individual:
		 * resource=manage.getAbstractGetInterface
		 * ().getOWLIndividual(ontologyName); break; }
		 */
		if (resource.getVersionInfo() == null
				|| resource.getVersionInfo().size() == 0) {
			return false;
		} else
			return true;
	}

	/*
	 * public static void main(String[] str){
	 * OWLOntologyQuery.getOntologyRDFDataType(); }
	 */

}
