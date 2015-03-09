package com.shenji.onto.editer.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoGet;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyUtil;

import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;

public class OWLOntoGet {
	private String token=null;
	private OntologyManage manage=null;
	public OWLOntoGet(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		this.manage=manage;
		this.token=manage.getToken();
	}
	

	public OWLNamedClass getOWLNamedClass(String className) {
		// TODO Auto-gen erated method stub
		OWLNamedClass object=manage.getOwlModel().getOWLNamedClass(className);	
		return object;
	}

	private OWLProperty getOWLDatatypeProperty(String datatypeProperty) {
		// TODO Auto-generated method stub
		OWLProperty property=null;
		try{
			property=manage.getOwlModel().getOWLDatatypeProperty(datatypeProperty);
		}
		catch(ClassCastException e){
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		return property;
	}

	private OWLProperty getOWLObjectProperty(String objectProperty) {
		// TODO Auto-generated method stub		
		OWLProperty property=null;
		try{
			property=manage.getOwlModel().getOWLObjectProperty(objectProperty);
		}
		catch(ClassCastException e){
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		return property;
	}
	


	
	public OWLProperty getOWLProperty(String property,int propertyType) {
		OWLProperty owlProperty = null;
		if(propertyType==OntologyCommon.DataType.dataTypeProperty)
			owlProperty=this.getOWLDatatypeProperty(property);
		if(propertyType==OntologyCommon.DataType.objectProperty)
			owlProperty=this.getOWLObjectProperty(property);
		return owlProperty;
	}


	

	public OWLIndividual getOWLIndividual(String individualName) {
		// TODO Auto-generated method stub
		OWLIndividual individual=null;
		try{
			individual=manage.getOwlModel().getOWLIndividual(individualName);
		}
		catch(ClassCastException e){
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		return individual;
	}
	
	
	private RDFSClass getCardinality(RDFResource resource,OWLProperty owlProperty,int cardinalityType,int cardinalityNum){
		RDFSClass rdfsClass=null;
		try{
			switch (cardinalityType) {
			//设置only约束
			case OntologyCommon.Cardinality.only:
				OWLAllValuesFrom allValuesFrom=this.manage.getOwlModel().createOWLAllValuesFrom(owlProperty, resource);		
				rdfsClass=(RDFSClass)allValuesFrom;
				break;
			//设置some约束
			case OntologyCommon.Cardinality.some:
				OWLSomeValuesFrom someValuesFrom=this.manage.getOwlModel().createOWLSomeValuesFrom(owlProperty, resource);
				rdfsClass=(RDFSClass)someValuesFrom;
				break;
			//设置精确exactly约束 
			case OntologyCommon.Cardinality.exactly:	{
				OWLCardinality cardinality=this.manage.getOwlModel().createOWLCardinality(owlProperty, cardinalityNum, (RDFSClass)resource);
				//OWLHasValue hasValue=this.manage.getOwlModel().createOWLHasValue(owlProperty, cardinalityNum);
				//System.err.println(cardinality.getBrowserText());
				rdfsClass=(RDFSClass)cardinality;			
				break;
			}	
			//设置最小约束
			case OntologyCommon.Cardinality.min:
				OWLMinCardinality minCardinality=this.manage.getOwlModel().createOWLMinCardinality(owlProperty, cardinalityNum,(RDFSClass)resource);
				rdfsClass=(RDFSClass)minCardinality;
				break;
			//设置最大约束
			case OntologyCommon.Cardinality.max:
				OWLMaxCardinality maxCardinality=this.manage.getOwlModel().createOWLMaxCardinality(owlProperty, cardinalityNum, (RDFSClass)resource);	
				rdfsClass=(RDFSClass)maxCardinality;
				break;
			default:
				break;
			}
			return rdfsClass;
		}catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
	}

	
	public RDFSClass getCardinality(String property_description,
			String values_description, int property_descriptionType,
			int values_descriptionType, int cardinalityType,
			int cardinalityNum) {
		// TODO Auto-generated method stub
		OWLProperty property=manage.getAbstractGetInterface().getOWLProperty(property_description, property_descriptionType);	
		if(property==null)
			return null;	
		RDFSClass rdfsclass_description=null;
		//处理对象属性
		if(property_descriptionType==OntologyCommon.DataType.objectProperty){
			RDFResource object_description=null;
			switch (values_descriptionType) {
				case OntologyCommon.DataType.namedClass:
					object_description=this.manage.getAbstractGetInterface().getOWLNamedClass(values_description);
					break;
				case OntologyCommon.DataType.individual:
					object_description=this.manage.getAbstractGetInterface().getOWLIndividual(values_description);
					break;
				default:
					return null;
			}	
			rdfsclass_description=this.getCardinality(object_description, property, cardinalityType, cardinalityNum);
		}
		if(property_descriptionType==OntologyCommon.DataType.dataTypeProperty){
			RDFSDatatype datatype=OntologyCommon.RDFSDataType.getRDFSDatatype(manage.getOwlModel(), values_description);
			rdfsclass_description=this.getCardinality(datatype, property, cardinalityType, cardinalityNum);
		}
		return rdfsclass_description;
	}
	
	public RDFSLiteral getRDFSLiteral(String value,String valueType){
		if(valueType==null||value==null)
			return null;
		RDFSDatatype datatype=OntologyCommon.RDFSDataType.getRDFSDatatype(this.manage.getOwlModel(), valueType);
		if(datatype==null)
			return null;
		RDFSLiteral rdfsLiteral=this.manage.getOwlModel().createRDFSLiteral(value, datatype);	
		return rdfsLiteral;
	} 

	public static int getRDFResourceType(RDFResource rdfResource){
		if(rdfResource instanceof DefaultOWLNamedClass)
			return OntologyCommon.DataType.namedClass;
		else if(rdfResource instanceof DefaultOWLObjectProperty)
			return OntologyCommon.DataType.objectProperty;
		else if(rdfResource instanceof DefaultOWLDatatypeProperty)
			return OntologyCommon.DataType.dataTypeProperty;
		else if(rdfResource instanceof DefaultOWLIndividual)
			return OntologyCommon.DataType.individual;
		return -1;
	}
	
	
	


	public List<String> getALLOntologyWords() {
		// TODO Auto-generated method stub
		//long startTime=System.currentTimeMillis();
		List<String> list=new ArrayList<String>();
		String defaultNameSpeace=this.manage.getOwlModel().getNamespaceManager().getDefaultNamespace();
		Collection<RDFResource> collection=this.manage.getOwlModel().getRDFSClasses();
		for(RDFResource resource:collection){
			if(collection!=null&&resource.getNamespace().equals(defaultNameSpeace))
				list.add(resource.getBrowserText());
		}
		
		Collection<RDFResource> collection2=this.manage.getOwlModel().getRDFProperties();
		for(RDFResource resource:collection2){
			if(collection2!=null&&resource.getNamespace().equals(defaultNameSpeace))
				list.add(resource.getBrowserText());
		}

		Collection<RDFIndividual> collection3=this.manage.getOwlModel().getRDFIndividuals();
		for(RDFIndividual resource:collection3){
			if(collection!=null&&resource.getNamespace().equals(defaultNameSpeace))
				list.add(resource.getBrowserText());
		}
		//long endTime=System.currentTimeMillis();
		//System.err.println("TIME:"+(endTime-startTime)+"");
		return list;
	}
	
	public List<String> getOntoClassWords() {
		// TODO Auto-generated method stub
		//long startTime=System.currentTimeMillis();
		List<String> list=new ArrayList<String>();
		String defaultNameSpeace=this.manage.getOwlModel().getNamespaceManager().getDefaultNamespace();
		Collection<RDFResource> collection=this.manage.getOwlModel().getRDFSClasses();
		for(RDFResource resource:collection){
			if(collection!=null&&resource.getNamespace().equals(defaultNameSpeace))
				list.add(resource.getBrowserText());
		}
		return list;
	}
	
	
	
	
}
