package com.shenji.onto.reasoner.data;

import java.util.LinkedList;
import java.util.Queue;


import org.jdom.Document;
import org.jdom.Element;


import com.hp.hpl.jena.ontology.OntClass;
import com.shenji.common.util.StringUtil;


public class FaqReasonerTree extends ReasonerTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url = null;
	private String ontoName = null;

	public FaqReasonerTree(String ontoName, String url) {
		super();
		this.ontoName = ontoName;
		this.url = url;
		// TODO Auto-generated constructor stub
	}

	public FaqReasonerTree(String ontoName,String url, ReasonerNodeData data) {
		super(data);
		this.ontoName = ontoName;
		this.url = url;
		// TODO Auto-generated constructor stub
	}

	public String getOntoName() {
		return this.ontoName;
	}

	public String getURL() {
		return this.url;
	}

	@Override
	public String toXmlString(String code) {
		// TODO Auto-generated method stub
		Document document = new Document();
		Element titleElement = new Element(this.getOntoName());
		titleElement.setAttribute("url", this.getURL());
		Element rootElement = new Element(super.getRootNode().getNodeData()
				.getNodeName());
		Queue<ReasonerNodeElement> queue = new LinkedList<ReasonerTree.ReasonerNodeElement>();
		recursiveXmlQueue(super.getRootNode(), rootElement, queue);
		document.addContent(titleElement);
		titleElement.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document, code);
		xmlStr = StringUtil.changeCharset(xmlStr, code);
		return xmlStr;
	}

}
