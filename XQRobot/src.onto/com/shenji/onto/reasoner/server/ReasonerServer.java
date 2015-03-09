package com.shenji.onto.reasoner.server;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.shenji.common.log.Log;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyDBServer;

import edu.stanford.smi.protegex.owl.model.RDFResource;

public class ReasonerServer {
	public static String segmentation = "#";
	private static ReasonerServer instance = new ReasonerServer();
	private OntologyDBServer dbServices = null;
	private HashMap<String, OntModel> ontoMap = new HashMap<String, OntModel>();
	private Set<String> ontWordsSet;

	protected ReasonerServer() {
		// Log.debugSystemOut("ReasonerServer创建了");
		dbServices = new OntologyDBServer();
		ontWordsSet = new HashSet<String>();
		initOntModel();
	}

	public void close() {
		if (this.dbServices != null)
			this.dbServices.close();
		if (this.ontoMap != null)
			this.ontoMap.clear();
		if (ontWordsSet != null)
			this.ontWordsSet.clear();
	}

	public static ReasonerServer getInstance() {
		return instance;
	}

	public void start() {
		Log.getLogger(this.getClass()).info("本体推理jena模型服务启动！");
	}

	public synchronized HashMap<String, OntModel> getOntoMap() {
		return this.ontoMap;
	}

	public OntModel getOntModel(String ontoName) {
		if (this.ontoMap.containsKey(ontoName))
			return this.ontoMap.get(ontoName);
		else
			return null;
	}

	public OntClass getOntClass(String ontoName, String ontClassName) {
		OntModel model = this.getOntModel(ontoName);
		if (model == null)
			return null;
		else
			return model.getOntClass(model.getNsPrefixURI("") + ontClassName);
	}

	public OntClass getRootOntClass(String ontoName) {
		OntModel model = this.getOntModel(ontoName);
		if (model == null)
			return null;
		else
			return model.getOntClass("http://www.w3.org/2002/07/owl#Thing");
	}

	// 这里假设只有一个图谱，就得到唯一图谱(以后可以摒弃)
	public OntModel getOnlyOntMode() {
		Iterator<Map.Entry<String, OntModel>> iterator = ReasonerServer
				.getInstance().getOntoMap().entrySet().iterator();
		while (iterator.hasNext()) {
			return iterator.next().getValue();
		}
		return null;
	}

	public void removeOntModel(String ontoName) {
		this.ontoMap.remove(ontoName);
	}

	private void initOntModel() {
		// OntologyDBServices dbServices=null;
		try {
			String[] ontoSets = this.dbServices.getOntologyNamesFromMySQL();
			for (String ontoName : ontoSets) {
				// 假设现在只有一个图谱是我们所需的未来，可以有多个
				if (ontoName.equals(Configuration.Onto_Name)) {
					OntModel model;
					model = dbServices.getModelFromMySQL(ontoName, false);
					this.dataFormat(model);
					this.ontoMap.put(ontoName, model);
					Log.getLogger(this.getClass()).debug(
							"【Log】此时加载的图谱为:" + ontoName);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			instance = null;
		} finally {
			// 不能关闭，不然模型断开
			/*
			 * if(dbServices!=null) dbServices.close();
			 */
		}
	}

	public void dataFormat(OntModel model) {
		// OntModel model = getKnowledgeOntMode();
		Iterator<OntClass> iterator = model.listClasses();
		// 得到模型根节点(root)
		OntClass rootOntClass = model
				.getOntClass("http://www.w3.org/2002/07/owl#Thing");
		while (iterator.hasNext()) {
			OntClass ontClass = iterator.next();
			if (!ontClass.hasSuperClass() && ontClass.getVersionInfo() == null
					&& !ontClass.hasSubClass(rootOntClass)) {
				ontClass.addSuperClass(rootOntClass);
			}
			ontClass = null;
		}
	}

	public Set<String> getOntoWords(OntModel model, boolean isNeedReFresh) {
		// TODO Auto-generated method stub
		// long startTime=System.currentTimeMillis();
		if (!isNeedReFresh) {
			return this.ontWordsSet;
		}
		this.ontWordsSet.clear();
		Iterator iteratorObj = model.listObjects();
		Iterator iteratorSub = model.listSubjects();
		listStatement(iteratorObj);
		listStatement(iteratorSub);
		return this.ontWordsSet;
	}

	private void listStatement(Iterator<Object> iterator) {
		while (iterator.hasNext()) {
			Object node = iterator.next();
			if (node instanceof Resource) {
				ontWordsSet.add(((Resource) node).getLocalName());
			} else if (node instanceof Literal) {
				ontWordsSet.add(((Literal) node).getString());
			} else {
				continue;
			}
		}
	}

	public static void main(String[] s) {
		ReasonerServer server = new ReasonerServer();
		Set<String> sb = server.getOntoWords(server.getOnlyOntMode(), true);
		
	}

}
