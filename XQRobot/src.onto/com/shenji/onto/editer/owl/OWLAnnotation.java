package com.shenji.onto.editer.owl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.Doc;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


import com.hp.hpl.jena.sparql.resultset.XMLOutput;
import com.hp.hpl.jena.vocabulary.RDF;
import com.shenji.common.log.Log;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.server.OntologyManage;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.AbstractRDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;

public class OWLAnnotation {
	public static int addOWLAnnotations(Object resource,int ontologyType,String annotations,String annotationsType,OntologyManage manage){
		if(resource==null)
			return -2;
		if(annotationsType.equals(OntologyCommon.Annotations.AnnotationType.comment.toString())){
			if(resource instanceof RDFResource)
				((RDFResource)resource).addComment(annotations);	
			else if(resource instanceof OWLOntology)
				((OWLOntology)resource).addComment(annotations);	
		}
		else if(annotationsType.equals(OntologyCommon.Annotations.AnnotationType.label.toString())){
			if(resource instanceof RDFResource)
				((RDFResource)resource).addLabel(annotations, null);	
			else if(resource instanceof OWLOntology)
				((OWLOntology)resource).addLabel(annotations,null);
		}
		/*else if(annotationsType.equals(OntologyType.Annotations.AnnotationType.versionInfo.toString())){
			return -3;
		}*/
		//自定义标签
		else{
			RDFProperty property=null;
			try {
				property=manage.getOwlModel().createAnnotationProperty(annotationsType);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				//已存在该匿名属性
				property=manage.getOwlModel().getRDFProperty(annotationsType);
				if(!property.isAnnotationProperty())
					property=null;
			}
			if(property!=null)
				//这里只支持string
				if(resource instanceof RDFResource)
					((RDFResource)resource).addPropertyValue(property, annotations);	
				else if(resource instanceof OWLOntology)
					((OWLOntology)resource).addPropertyValue(property, annotations);
				
		}	
		return 1;
	}
	
	public static int deleteOWLAnnotations(RDFResource resource,String annotations,String annotationsType,OntologyManage manage){
		if(resource==null)
			return -2;
		if(annotationsType.equals(OntologyCommon.Annotations.AnnotationType.comment.toString())){
			resource.removeComment(annotations);	
		}
		else if(annotationsType.equals(OntologyCommon.Annotations.AnnotationType.label.toString())){
			resource.removeLabel(annotations, null);
		}
		/*else if(annotationsType.equals(OntologyType.Annotations.AnnotationType.versionInfo.toString())){
			//resource.removeVersionInfo(annotations);
		}*/
		//自定义标签
		else{
			RDFProperty property=null;			
			//已存在该匿名属性
			property=manage.getOwlModel().getRDFProperty(annotationsType);
			if(!property.isAnnotationProperty())
				return -3;
			else{
				resource.removePropertyValue(property, annotations);
			}
		}	
		return 1;
		
	}
	
	
	private static Collection getAnnotationsByType(RDFResource resource,String annotationsType,RDFProperty property){
		Collection collection=null;
		if(annotationsType.equals(OntologyCommon.Annotations.AnnotationType.comment.toString()))
			collection=resource.getComments();
		else if(annotationsType.equals(OntologyCommon.Annotations.AnnotationType.label.toString()))
			collection=resource.getLabels();
		/*else if(annotationsType.equals(OntologyType.Annotations.AnnotationType.versionInfo.toString()))
			collection=resource.getVersionInfo();*/
		//处理自定义标签
		else{					
			collection=resource.getPropertyValues(property);							
		}
		return collection;
		
	}
	
	/**
	 * 获得可以添加的RDF声明类型
	 * @return
	 */
	public static String[] getRDFAnnotations(){	
		return OntologyCommon.Annotations.getAnnotations();
	}
	
	
	
	/**
	 * 得到声明XML串
	 * @param resource
	 * @param ontologyName
	 * @return
	 */
	public static String getAnnotationsXML(RDFResource resource,String ontologyName,String ontologyTypeStr){
		Document document=new Document();
		String[] rdfAnnotaitons=getRDFAnnotations();
		Element rootElement=new Element(ontologyTypeStr);
		rootElement.setAttribute("name", ontologyName);
		document.addContent(rootElement);
		boolean isExitElement=false;
		for(String str:rdfAnnotaitons){
			Element e=getAnnotationsElement(resource, str, false,null);
			if(e==null)
				continue;
			isExitElement=true;
			rootElement.addContent(e);
		}
		//自定义声明处理，还没有写
		Collection<RDFProperty> tempcollection=resource.getRDFProperties();
		for(RDFProperty property:tempcollection){
			//匿名属性并且是自定义声明
			
			if((resource instanceof OWLOntology&&property.isAnnotationProperty()&&!(property instanceof DefaultOWLDatatypeProperty))
					||(!(resource instanceof OWLOntology)&&property.isAnnotationProperty()&&property.getNamespace().equals(resource.getNamespace()))){
				Element e=getAnnotationsElement(resource, property.getBrowserText(),true,property);
				if(e==null)
					continue;
				isExitElement=true;
				rootElement.addContent(e);
			}
			/*if(property.isAnnotationProperty()&&property.getNamespace().equals(resource.getNamespace())){
				Element e=getAnnotationsElement(resource, property.getBrowserText(),true,property);
				if(e==null)
					continue;
				isExitElement=true;
				rootElement.addContent(e.getMessage(),e);
			}*/
		}	
		String xmlStr=StringUtil.outputToString(document, "GBK");
		System.err.println(xmlStr);
		if(isExitElement==false)
			return null;
		xmlStr=StringUtil.changeCharset(xmlStr,"GBK");
		return xmlStr;
	}
	
	//通过资源生成声明XML节点
	private static Element getAnnotationsElement(RDFResource resource,String annotationsType,boolean isUserDefined,RDFProperty property){
		String reAnnotations=null;		
		Collection collection=getAnnotationsByType(resource, annotationsType,property);
		if(collection==null)
			return null;
		HashMap<Object,String> map= getCollection(collection);
		Element element=new Element(annotationsType);
		if(isUserDefined)
			element.setAttribute("isUserDefined", "true");
		else
			element.setAttribute("isUserDefined", "false");
		Iterator<Entry<Object, String>> iterator = map.entrySet().iterator();
		boolean exit=false;
		while(iterator.hasNext()) {
			exit=true;
			Map.Entry entry = (Map.Entry) iterator.next();
			Element e=new Element("content");
			e.setAttribute("type",(String)entry.getValue());
			e.addContent((String)entry.getKey());		
			element.addContent(e);
		}
		if(exit==false)
			return null;
		return element;		
	}
	//遍历集合得到节点集
	private static HashMap<Object,String> getCollection(Collection collection){
		Iterator it=collection.iterator();
		HashMap<Object,String> map=new HashMap<Object, String>();
		while(it.hasNext()){
			Object object=it.next();
			if(object instanceof String){
				map.put(object, "string");
				//System.out.println(((String)object).toString());
			}
			if(object instanceof RDFSLiteral){
				//System.out.println("sdfsdf");
			}
		}
		return map;
	}
}
