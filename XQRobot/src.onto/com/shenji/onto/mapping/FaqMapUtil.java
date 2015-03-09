package com.shenji.onto.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.shenji.common.log.Log;


import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

public class FaqMapUtil {
	public static String FAQ_ADDRESS="C:/Program Files/Apache Software Foundation/Tomcat 6.0/webapps/test/faq/";
	public static String OWL_ADDRESS="C:/Program Files/Apache Software Foundation/Tomcat 6.0/webapps/test/faq_owl/";
	public static String FAQ_URL="http://172.30.3.56:8080/test/faq/";
	public static String OWL_URL="http://172.30.3.56:8080/test/faq_owl/";
	public static String INDIVIDUAL_HEAD="FAQ"; 
	public static String RDF_LIVEL="RDF/XML-ABBREV";
	public static JenaOWLModel read() throws OntologyLoadException{
		JenaOWLModel jenaOWLModel=ProtegeOWL.createJenaOWLModel();
		return jenaOWLModel;		
	}
	/**
	JenaOWLdel消耗内存过大，除了新建，写入时和一些特殊操作（如addImport 外部实体等）尽量不要用。
	OntModel轻量级RDF内存模型，缺点为写入时无法写入DTD（可能自己没找对方法）。
	 **/	
	//必须关闭，太占内存了
		public static int createOWLFaq(String faqFileAllName,JenaOWLModel[] importModels){
			JenaOWLModel jenaOWLModel=null;
			try {
				jenaOWLModel = FaqMapUtil.read();
			} catch (OntologyLoadException e) {
				// TODO Auto-generated catch block
				Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
				return -1;
			}
			if(jenaOWLModel==null)
				return -8;
			//导入外部命名实体
			for(int i=0;importModels!=null&&i<importModels.length;i++){
				jenaOWLModel.getNamespaceManager().addImport(importModels[i].getTripleStoreModel().getActiveTripleStore());
			}
			String owlFileName=faqFileAllName.substring(0,faqFileAllName.lastIndexOf("."));
			File file=new File(FaqMapUtil.OWL_ADDRESS+owlFileName+".owl");	
			try {
				if(file.exists())
					file.delete();	
				//本来想：baseURL为FAQ_URL目录（非OWL目录）,名字为URL+HTML，但这样需要采用RDFa技术将RDF嵌入到HTML，这里暂时将OWL与HTML分离
				//现在：baseURL为OWL_URL目录，名字为OWL_URL+HTML
				String baseUrl=FaqMapUtil.OWL_URL+owlFileName+".owl";
				//设置xml:base与xmlns默认命名空间
				OWLUtil.renameOntology(jenaOWLModel, 
						jenaOWLModel.getDefaultOWLOntology(), baseUrl);
				jenaOWLModel.getNamespaceManager().setDefaultNamespace(baseUrl+"#");
				jenaOWLModel.getOWLThingClass().createOWLIndividual(FaqMapUtil.INDIVIDUAL_HEAD);	
				//jenaOWLModel.createOWLDatatypeProperty("p1:question");
				//jenaOWLModel.createOWLDatatypeProperty("p1:answer");
				/*String[] faq=analysisHTML(faqFileAllName);
				//写入问答对
				jenaOWLModel.getOWLIndividual(INDIVIDUAL_HEAD+owlFileName).addPropertyValue(jenaOWLModel.getOWLDatatypeProperty("p1:question"),faq[0]);
				jenaOWLModel.getOWLIndividual(INDIVIDUAL_HEAD+owlFileName).addPropertyValue(jenaOWLModel.getOWLDatatypeProperty("p1:answer"),faq[1]);*/
			} catch (Exception e) {
				Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
				//初始化失败
				return -3;
			}
			if(!FaqMapUtil.write(jenaOWLModel,file))
				return -2; //写入失败	
			return 1;
		}
	
	public static JenaOWLModel read(String uri) throws OntologyLoadException{	
		JenaOWLModel jenaOWLModel=ProtegeOWL.createJenaOWLModelFromURI(uri);
		return jenaOWLModel;		
	}
	public static JenaOWLModel read(File file) throws OntologyLoadException, FileNotFoundException{	
		FileInputStream inputStream = null;
		JenaOWLModel jenaOWLModel=null;
		inputStream = new FileInputStream(file);
		jenaOWLModel=ProtegeOWL.createJenaOWLModelFromInputStream(inputStream);			
		if(inputStream!=null)
			try {
				inputStream.close();
			} catch (IOException e) {
					// TODO Auto-generated catch block
				Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
		}	
		return jenaOWLModel;		
	}
	
	/**
	 * 将OWL写入文件中,并关闭内存对象
	 * @param jenaOWLModel
	 * @param file
	 * @throws IOException
	 */
	public static boolean write(JenaOWLModel jenaOWLModel,File file){
		FileWriter writer=null;
		try {
			writer=new FileWriter(file);
			OWLModelWriter modelWriter=new OWLModelWriter(jenaOWLModel, jenaOWLModel.getTripleStoreModel().getActiveTripleStore(), writer);		
			modelWriter.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
			return false;
		}
		finally{
			if(writer!=null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
				}
			//一定要释放掉,太占内存了
			if(jenaOWLModel!=null)
				jenaOWLModel.close();
			System.gc();
		}
		return true;
	}

	/**
	 * 添加本体文件外部导入，重现重复则不会添加（自动过滤）
	 * @param owlFileName
	 * @param importModels
	 * @return
	 */
	public static int addOWLFaqImport(String owlFileAllName,JenaOWLModel[] importModels){
		File file=new File(FaqMapUtil.OWL_ADDRESS+owlFileAllName);
		JenaOWLModel jenaOWLModel=null;
		try {
			jenaOWLModel=FaqMapUtil.read(file);
		} catch (OntologyLoadException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
		}
		if(jenaOWLModel==null)
			return -8;
		//导入外部命名实体
		for(int i=0;importModels!=null&&i<importModels.length;i++){
			jenaOWLModel.getNamespaceManager().addImport(importModels[i].getTripleStoreModel().getActiveTripleStore());
		}
		if(!FaqMapUtil.write(jenaOWLModel,file))
			return -2; //写入失败	
		return 1;
	}
	
	/**
	 * 解析HTML文档（不用了）
	 * @param fileAllName
	 * @return 问答对
	 */
	public  static String[] analysisHTML(String faqFileAllName){
		File file=new File(FaqMapUtil.FAQ_ADDRESS+faqFileAllName);
		String[] strs=new String[2];
		Document doc = null;
		try {
			doc = Jsoup.parse(file,"utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
		} 
		Iterator<Element> divqs=doc.getElementsByClass("q").iterator();	
		while(divqs.hasNext()){			
			Element e=divqs.next();
			if(e!=null){
				strs[0]=e.text();
			}
		}
		Iterator<Element> divas=doc.getElementsByClass("a").iterator();	
		while(divas.hasNext()){			
			Element e=divas.next();
			if(e!=null){
				strs[1]=e.text();
			}
		}
		return strs;
	}
}
