package com.shenji.onto.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.apache.axis2.AxisFault;
import org.apache.bcel.generic.SIPUSH;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.ResourceUtils;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.inter.IComFenciServer;
import com.shenji.common.log.Log;
import com.shenji.common.util.FileUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyDBServer;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.mapping.inter.IFaqMapAdd;
import com.shenji.onto.mapping.inter.IFaqMapDelete;
import com.shenji.onto.mapping.inter.IFaqMapQuery;
import com.shenji.search.FenciControl;
import com.shenji.search.ResourcesControl;
import com.shenji.wordclassification.FAQDataAnalysis;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class FaqMapServices {
	private static FaqMapServices instance;
	//private static final String A_HREF = "a[href]";
	private static String OWL_THING = "http://www.w3.org/2002/07/owl#Thing";
	private OntModel model;
	private IFaqMapQuery queryInterface;
	private IFaqMapAdd addInterface; 
	private IFaqMapDelete deleteInterFace;
	private OntologyDBServer dbServices;
	private String baseXmlns;
	//【！】这里不合理,要想办法传进来
	private ResourcesControl resourcesControl;
	private FenciControl fenciControl;

	protected FaqMapServices() {
		this.resourcesControl=new ResourcesControl();
		this.fenciControl=new FenciControl();
		init();
		
	}
	public void init(){
		try {
			//Log.debugSystemOut("FaqMapServices创建了");
			dbServices = new OntologyDBServer();
			initOntModel();
			baseXmlns = model.getNsPrefixURI("");
			initInterfaces();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public String getBaseXmlns() {
		return this.baseXmlns;
	}

	public void close() {
		if (model != null)
			model.close();
		if (dbServices != null)
			dbServices.close();
		//instance = null;
	}

	public void initInterfaces() {
		queryInterface = new FaqMapQuery(this.model);
		addInterface = new FaqMapAdd(this.model);
		deleteInterFace = new FaqMapDelete(this.model);
	}

	public IFaqMapQuery getQueryInter() {
		return queryInterface;
	}

	public IFaqMapAdd getAddInter() {
		return addInterface;
	}

	public IFaqMapDelete getDeleteInter() {
		return deleteInterFace;
	}

	public void start(){
		Log.getLogger(this.getClass()).info("FAQ映射服务启动！");
	}
	public static FaqMapServices getInstance() {		
		if (instance == null) {
			synchronized (FaqMapServices.class) {
				if (null == instance) {
					instance = new FaqMapServices();
				}
			}
		}
		return instance;
	}

	private void initOntModel() throws FileNotFoundException {
		//OntologyDBServices dbServices=new OntologyDBServices();
		if (this.model != null)
			this.model.close();
		this.model = this.dbServices
				.getModelFromMySQL(Configuration.FaqMappingCommon.ontoName,false);
		/*if(dbServices!=null)
			dbServices.close();*/	
	}

	public void resetOntModel() {
		try {
			initOntModel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public OntModel getOntModel() {
		return this.model;
	}

	public boolean reBuildFaqDB() {
		HashMap<String, Boolean> individualSet=null;
		try {
			Log.getLogger(this.getClass()).info("更新FAQ本体库开始");
			Iterator<Individual> iterator = this.model.listIndividuals();
			individualSet = new HashMap<String, Boolean>();
			// 获取所有实例保存在map中，并默认都不存在需要更新
			while (iterator.hasNext()) {
				String url = ((Individual) iterator.next()).getLocalName();
				individualSet.put(url, false);
			}		
			List<String> urls = JsonFaqHtml.jsoupAllFAQHtml(
					resourcesControl.listAllFaq(), JsonFaqHtml.A_HREF);
			for (String url : urls) {
				String faqId = analysisHrefTogetName(url);
				if (faqId == null)
					continue;
				faqId = Configuration.FaqMappingCommon.INDIVIDUAL_HEAD + faqId;
				if (individualSet.containsKey(faqId))
					individualSet.put(faqId, true);
				else {
					// 添加新出现的FAQ
					try {
						String[] strs = FAQDataAnalysis.jsoupOneFAQHtml(url);
						if (strs != null)
							Log.getLogger(this.getClass()).debug("添加FAQ库中:" + strs[0] + ":" + strs[1]);
						createModelIndividual(this.model, strs, baseXmlns);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						Log.getLogger(this.getClass()).error(e.getMessage(),e);
						continue;
					}				
				}
			}
			// 删除FAQ中不存在的实例（被FAQ删除的了）
			Iterator<Map.Entry<String, Boolean>> iterator_Set = individualSet
					.entrySet().iterator();
			while (iterator_Set.hasNext()) {
				Map.Entry<String, Boolean> entry = (Entry<String, Boolean>) iterator_Set
						.next();
				String key = (String) entry.getKey();
				boolean value = (Boolean) entry.getValue();
				if (value == false) {
					String indivudualName = key;
					Individual individual = this.model.getIndividual(baseXmlns
							+ indivudualName);
					individual.remove();
					Log.getLogger(this.getClass()).debug("删除FAQ库中:" +individual.getLocalName());
				}
			}
			this.save();
			Log.getLogger(this.getClass()).debug("更新FAQ本体库完成");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		finally{
			if(individualSet!=null)
				individualSet.clear();
		}
		return false;
	}

	public synchronized int reNameFaqIndividual(String oldFaqId, String newFaqId) {
		String baseXmln = this.getBaseXmlns();
		Individual individual = this.model.getIndividual(baseXmln
				+ Configuration.FaqMappingCommon.INDIVIDUAL_HEAD + oldFaqId);
		Property property = this.model.getProperty(baseXmln
				+ Configuration.FaqMappingCommon.Data_URL);

		RDFNode oldUrlNode = individual.getPropertyValue(property);
		String oldUrl = oldUrlNode.toString();
		individual.removeProperty(property, oldUrlNode);
		individual.addProperty(property, oldUrl.replace(oldFaqId, newFaqId));
		if (individual == null)
			return -1;
		ResourceUtils.renameResource(individual, baseXmln
				+ Configuration.FaqMappingCommon.INDIVIDUAL_HEAD + newFaqId);
		this.save();
		return 1;
	}

	public synchronized int createFaqIndividual(String url) {
		int reFlag = createModelIndividual(this.model, new String[] { url },
				baseXmlns);
		this.save();
		return reFlag;
	}

	public synchronized boolean deleteFaqIndividual(String faqId) {
		Individual individual = this.model.getIndividual(baseXmlns
				+ Configuration.FaqMappingCommon.INDIVIDUAL_HEAD + faqId);
		if(individual!=null){
			individual.remove();
			this.save();
			return true;
		}
		else
			return false;
	}

	// 基于关键字的自动分类
	public int AutomaticClassification(OntologyManage manage) {
		String baseXmln = this.getBaseXmlns();
		Property property = this.model.getProperty(baseXmln
				+ Configuration.FaqMappingCommon.Data_URL);
		Iterator iterator = model.listIndividuals();
		int count = 0;
		List<String> ontoClassWords = manage.getAbstractGetInterface()
				.getOntoClassWords();
		if (ontoClassWords == null || ontoClassWords.size() == 0)
			return -2;
		while (iterator.hasNext()) {
			Individual individual = (Individual) iterator.next();
			String url = individual.getPropertyValue(property).toString();
			try {
				String[] html = FAQDataAnalysis.jsoupOneFAQHtml(url);
				if (html == null || html.length == 0)
					continue;
				String[] words = fenciControl.iKAnalysisAndSyn(html[1]).split("/");
				if (words == null)
					continue;
				List<String> wordSets = Arrays.asList(words);
				System.out.println((count++) + ":" + html[1]);
				System.out.println("********************");
				for (String ontoClassWord : ontoClassWords) {
					if (wordSets.contains(ontoClassWord)) {
						System.out.print(ontoClassWord + " : ");
						// this.getAddInter().addOWLFAQType(manage.getOwlModel(),
						// individual.getLocalName(), ontoClassWord);
						try {
							OWLNamedClass namedClass = manage.getOwlModel()
									.getOWLNamedClass(ontoClassWord);
							if (manage.getAbstractQueryInterface()
									.getOntologyIsOthersSyn(ontoClassWord)) {
								Iterator it = namedClass.getEquivalentClasses()
										.iterator();
								namedClass = (OWLNamedClass) it.next();
							}
							recursiveFaqType(manage, namedClass,
									individual.getLocalName());
						} catch (Exception e) {
							// TODO: handle exception
							Log.getLogger(this.getClass()).error(e.getMessage(),e);
						}

					}
				}
				System.out.println("********************");

			} catch (Exception e) {
				// TODO: handle exception
				Log.getLogger().error("AutomaticClassification", e);
				e.printStackTrace();
			}
		}
		this.save();
		return 1;
	}

	private void recursiveFaqType(OntologyManage manage,
			OWLNamedClass namedClass, String individualName) {
		if (namedClass == null)
			return;
		this.getAddInter().addOWLFAQType(manage.getOwlModel(), individualName,
				namedClass.getBrowserText());
		Collection<OWLNamedClass> superClasses = namedClass
				.getSuperclasses(false);// 寻找直接父类
		if (superClasses == null || superClasses.size() == 0)
			return;
		for (OWLNamedClass owlNamedClass : superClasses) {
			if ((owlNamedClass != null && owlNamedClass.getBrowserText()
					.equals(OntologyCommon.OntoOwlRootName))// 这里最顶层父类是owl:Thing
															// ,不知道为什么
					|| manage.getAbstractQueryInterface()
							.getOntologyIsOthersSyn(
									owlNamedClass.getBrowserText()))// 当一个类是另一个类定义的不处理(不然死循环了)
				continue;
			recursiveFaqType(manage, owlNamedClass, individualName);
		}
	}

	// 将FAQ外网地址转换为内网地址
	private String faqNetTransformation(String url) {
		if (true) {
			Pattern pattern = Pattern
					.compile("(http://)([a-zA-Z0-9\\.]*)(:){1}([0-9]+)");
			Matcher m = pattern.matcher(url);
			if (m.find()) {
				String oldUrl = m.group(0);
				//
				//url = url.replace(oldUrl, Configuration.FAQ_URL);
				url = url.replace(oldUrl, Configuration.FAQURL);
			}
		}
		return url;
	}

	/*
	 * public static void main(String[] str) { new FaqMapServices()
	 * .faqNetTransformation(
	 * "Lv:0::http://www.62111929.net:8070/test/faq/0287dae752fd579f65647c849054d96a.htm:"
	 * ); }
	 */

	public boolean initBuildFaqDB() {
		String ontoURL = Configuration.getOntoBaseURL();
		String initBaseXml = ontoURL + "/"
				+ Configuration.FaqMappingCommon.ontoName + ".owl#";
		//OntologyDBServices dbServices = new OntologyDBServices();
		try {
			this.dbServices.deleteOntologyByDB(Configuration.FaqMappingCommon.ontoName);
		} catch (ConnectionPoolException e1) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e1);
			return false;
		}
		int reFlag = dbServices.createOntologyByDB(
				Configuration.FaqMappingCommon.ontoName, ontoURL);
		if (reFlag != 1)
			return false;
		OntModel model = null;
		try {
			model = this.dbServices
					.getModelFromMySQL(Configuration.FaqMappingCommon.ontoName,false);// 生成文件了
			this.createModeOwnDataProperty(model, initBaseXml);
			List<String> urls = JsonFaqHtml.jsoupAllFAQHtml(
					resourcesControl.listAllFaq(), JsonFaqHtml.A_HREF);
			for (String url : urls) {
				// 3月25日，删除问答保存在数据库中
				// String[] strs=analysis.jsoupOneFAQHtml(url);
				String[] strs = new String[1];
				strs[0] = url;
				createModelIndividual(model, strs, initBaseXml);
			}
			/*
			 * dbServices.jenaModelToFile(model, ontoName);
			 * dbServices.jenaFileToMySQL(ontoName, false);
			 */
			model.commit();
			
			//重新初始化[需要修改]
			//this.instance=null;
			//this.getInstance();
			this.init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			/*if (dbServices != null)
				dbServices.close();	*/
		}
		return true;

	}

	public synchronized void save() {
		this.model.commit();
		// dbServices.jenaFileToMySQL(ontoName, false);
	}

	private void createModeOwnDataProperty(OntModel model, String initBaseXml) {
		model.createDatatypeProperty(initBaseXml
				+ Configuration.FaqMappingCommon.Data_URL);
	}

	private int createModelIndividual(OntModel model, String[] strs,
			String initBaseXml) {
		// 文档空间;
		if (strs == null || strs.length == 0)
			return -2;
		String faqId = analysisHrefTogetName(strs[0]);
		if (faqId == null)
			return -3;// 不是标准的URL
		String individualName = Configuration.FaqMappingCommon.INDIVIDUAL_HEAD
				+ faqId;
		Resource resource = model.getOntClass(OWL_THING);
		try {
			model.createIndividual(initBaseXml + individualName, resource);
			// 去除FAQ 问答
			// model.getIndividual(baseXmlns+individualName).addProperty(model.getDatatypeProperty(baseXmlns+Data_Q),
			// strs[1]);
			// model.getIndividual(baseXmlns+individualName).addProperty(model.getDatatypeProperty(baseXmlns+Data_A),
			// strs[2]);

			// 内外网转换
			strs[0] = faqNetTransformation(strs[0]);
			model.getIndividual(initBaseXml + individualName).addProperty(
					model.getDatatypeProperty(initBaseXml
							+ Configuration.FaqMappingCommon.Data_URL), strs[0]);
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		}
	}

	// 从地址中提取文件名
	public static String analysisHrefTogetName(String url) {
		// http://localhost:8070/test/faq/f2ee6fc8c313421e519658c25fa6129d.htm
		String name = null;
		if (url.contains(".") && url.contains("/")) {
			name = url.substring(0, url.lastIndexOf("."));
			name = name.substring(name.lastIndexOf("/") + 1, name.length());
		}
		return name;
	}
	
	
/*	public static List<String> jsoupAllFAQHtml(String html, String type) {
		Document doc = Jsoup.parse(html);
		Iterator<Element> iterator;
		if (type.equals(A_HREF)) {
			iterator = doc.select("a").iterator();
		} else
			iterator = doc.getElementsByClass(type).iterator();
		ArrayList<String> list = new ArrayList<String>();
		int i = 0;
		while (iterator.hasNext()) {
			Element e = iterator.next();
			if (e != null) {
				if (type.equals(A_HREF)) {
					list.add(e.attr("href"));
					// Log.debugSystemOut((i++)+":"+e.attr("href"));
				} else {
					list.add(e.text());
					// Log.debugSystemOut((i++)+":"+e.text());
				}
			}
		}
		return list;
	}
*/
	public static void main(String[] str) throws FileNotFoundException {
		OntologyDBServer dbServices = new OntologyDBServer();
		OntModel model = dbServices
				.getModelFromMySQL(Configuration.FaqMappingCommon.ontoName,false);
		Individual individual = model
				.getIndividual("http://192.168.1.235:8070/ontology/faq.owl#faq_5fea3f728449a22a4b204bfe25ac4084");
		Iterator iterator = individual.listRDFTypes(true);
		while (iterator.hasNext()) {
			Log.getLogger(FaqMapServices.class).debug(iterator.next());
		}
		dbServices.close();

	}

}
