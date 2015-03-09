package com.shenji.onto.reasoner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.server.ReasonerServer;

public class ReasonerLabel {
	private static ReasonerLabel instance = new ReasonerLabel();
	private HashMap<String, String> labelMap;

	public ReasonerLabel() {
		this.labelMap = new HashMap<String, String>();
	}

	public static ReasonerLabel getInstance() {
		return instance;
	}

	public synchronized void inintLabelMap() {
		// long start=System.currentTimeMillis();
		OntModel model = ReasonerServer.getInstance().getOnlyOntMode();
		Iterator iterator = model.listResourcesWithProperty(RDFS.label);
		while (iterator.hasNext()) {
			OntClass ontClass = null;
			try {
				ontClass = (OntClass) iterator.next();
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}
			Iterator<RDFNode> labelIt = ontClass.listLabels("");
			while (labelIt.hasNext()) {
				RDFNode node = labelIt.next();
				String ontClassName = ontClass.getLocalName();
				String label = node.asLiteral().getString();
				if (this.labelMap.containsKey(label)) {
					String value = this.labelMap.get(label);
					value = value + "#" + ontClassName;
					this.labelMap.put(label, value);
				} else
					this.labelMap.put(label, ontClassName);
				// Log.debugSystemOut(ontClass.getLocalName()+":"+node.asLiteral().getString());
			}
		}
		// Log.debugSystemOut("初始化标签MAP:" + (end - start) + "ms");
	}

	public String[] getOntClassesByLabel(String label) {
		// inintLabelMap();
		if (this.labelMap.get(label) == null) {
			return null;
		} else {
			String ontClasses = this.labelMap.get(label);
			if (ontClasses.contains("#"))
				return ontClasses.split("#");
			else
				return new String[] { ontClasses };
		}
	}

	public String[] wordsLabelExpand(String[] orcWords, boolean isInitLabelMap,
			boolean isWithPossibility) {
		List<String> wordsList = new ArrayList<String>();
		if (isInitLabelMap)
			this.inintLabelMap();
		if (orcWords == null)
			return null;
		for (String word : orcWords) {
			String temp = word;
			if (isWithPossibility) {
				temp = word.split("#")[0];
			}
			String[] labels = this.getOntClassesByLabel(temp);
			if (labels != null && labels.length != 0)
				for (String label : labels) {
					if (isWithPossibility) {
						label = label + "#" + 1.5;// 比普通词滴点
					}
					wordsList.add(label);
				}
			wordsList.add(word);
		}
		String[] words = wordsList.toArray(new String[wordsList.size()]);
		wordsList.clear();
		return words;
	}

	public String[] wordsLabelExpand(String[] orcWords) {
		return wordsLabelExpand(orcWords, true, false);
	}

}
