package com.shenji.onto.editer.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.shenji.onto.OntologyCommon;


import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.ProtegeCls;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

public class OntologyUtil {
	/**
	 * 2013年10月22日
	 * 创建新的本体文件，并写入相关信息
	 * @param filePath 文件路径
	 * @param fileName 文件名（不带后缀）
	 * @return 1成功 -1失败 -2URL为空
	 */
	public static int createNewOntology(String filePath,String fileName,String url){
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter=null;
		JenaOWLModel owlModel=null;
		OWLModelWriter owlModelWriter=null;
		File file=null;
		try {
			if(url==null)
				return -2;
			if(filePath.endsWith("/")||
					fileName.endsWith("\\"))
				file=new File(filePath+fileName+".owl");
			else
				file=new File(filePath+File.separator+fileName+".owl");
			fileOutputStream=new FileOutputStream(file);
			outputStreamWriter=new OutputStreamWriter(fileOutputStream);
			owlModel = ProtegeOWL.createJenaOWLModel();
			String baseUrl=url+"/"+fileName+".owl";
			//设置xml:base
			OWLUtil.renameOntology(owlModel, 
					owlModel.getDefaultOWLOntology(), baseUrl);
			//System.err.println(owlModel.getTripleStoreModel().getTopTripleStore().getOriginalXMLBase());
			//owlModel.getTripleStoreModel().getTopTripleStore().setOriginalXMLBase("http://www.w3#");
			//设置默认控件xml
			owlModel.getNamespaceManager().setDefaultNamespace(baseUrl+"#");
			//System.err.println(owlModel.se());
			owlModelWriter=new OWLModelWriter(owlModel, owlModel.getTripleStoreModel().getActiveTripleStore(), outputStreamWriter);
			owlModelWriter.write();
			outputStreamWriter.flush();
			Jena.dumpRDF(owlModel.getOntModel());
			return 1;
			
		} catch (OntologyLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		finally{			
				try {
					if(outputStreamWriter!=null){
						outputStreamWriter.close();
					}
					if(fileOutputStream!=null){
						fileOutputStream.close();
					}					
					if(owlModel!=null){
						owlModel.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}		
		}
	}
	
	public static boolean deleteOntology(String fileName){
		File file=new File(fileName);
		boolean reflag=false;
		if(file.exists()&&file.isFile()&&file.canExecute()){
			reflag=file.delete();
		}
		return reflag;
	}
	
	public static String getOntologyName(Object owl,int type){
		switch (type) {
		case OntologyCommon.DataType.namedClass:{
			RDFSClass namedClass=(RDFSClass)owl;
			return namedClass.getBrowserText();
			//return namedClass.getName().replace(namedClass.getNamespace(), "");
		}
		case OntologyCommon.DataType.objectProperty:
		case OntologyCommon.DataType.dataTypeProperty:{
			OWLProperty property=(OWLProperty)owl;
			return property.getName().replace(property.getNamespace(), "");
		}
		default:
			return null;
		}
	}
	
	public static void main(String[] str){
		//createNewOntology("c:/", "fsdfsdf");
	}
	
	
	

	
	
}
