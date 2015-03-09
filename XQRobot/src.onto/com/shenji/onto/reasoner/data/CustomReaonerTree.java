package com.shenji.onto.reasoner.data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.shenji.common.log.Log;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.Configuration;
import com.shenji.onto.mapping.FaqMapUtil;
import com.shenji.onto.reasoner.data.ReasonerTree.ReasonerNodeElement;
import com.shenji.wordclassification.FAQDataAnalysis;

public class CustomReaonerTree extends FaqReasonerTree {

	private static final long serialVersionUID = 1L;
	private final static String segment = "\n";

	public CustomReaonerTree(String ontoName, String url) {
		super(ontoName, url);
		// TODO Auto-generated constructor stub
	}

	public CustomReaonerTree(String ontoName, String url, ReasonerNodeData data) {
		super(ontoName, url, data);
		// TODO Auto-generated constructor stub
	}

	private String getQuestion(String FaqId) {
		String question = null;
		String url = Configuration.FAQURL + File.separator + FaqId + ".htm";
		try {
			question = FAQDataAnalysis.jsoupOneFAQHtml(url, new Object[] {
					Configuration.FaqMappingCommon.Data_A,
					Configuration.FaqMappingCommon.Data_URL })[1];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// 不输出错误了，可能FAQ格式有问题
			Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
		}
		return question;
	}

	@Override
	public String toXmlString(String code) {
		// TODO Auto-generated method stub
		Document document = new Document();
		Element titleElement = new Element(this.getOntoName());
		titleElement.setAttribute("url", this.getURL());
		Element rootElement = new Element(super.getRootNode().getNodeData()
				.getNodeName());
		Set<String> faqIdSet = ((FaqReasonerNodeData) super.getRootNode()
				.getNodeData()).getFaqIdList();
		StringBuilder sb = new StringBuilder();
		for (String faqId : faqIdSet) {
			String question = getQuestion(faqId);
			if (question != null)
				sb.append(question + segment);
		}
		rootElement.setText(sb.toString());
		Queue<ReasonerNodeElement> queue = new LinkedList<ReasonerTree.ReasonerNodeElement>();
		recursiveXmlQueue(super.getRootNode(), rootElement, queue);
		document.addContent(titleElement);
		titleElement.addContent(rootElement);
		String xmlStr = StringUtil.outputToString(document, code);
		xmlStr = StringUtil.changeCharset(xmlStr, code);
		if (queue != null)
			queue.clear();
		return xmlStr;
	}

	protected void recursiveXmlQueue(ReasonerNode node, Element superElement,
			Queue<ReasonerNodeElement> queue) {
		if (node == null || queue == null)
			return;
		List<ReasonerNode> list = node.getDirectlyAllChilds();
		if (list != null && list.size() != 0) {
			for (ReasonerNode tN : list) {
				queue.offer(new ReasonerNodeElement(tN, superElement));
			}
		}
		ReasonerNodeElement nodeElement = queue.poll();
		if (nodeElement == null)
			return;
		ReasonerNode firstNode = nodeElement.getNode();
		// 得到其父节点
		Element nodeSuperElement = nodeElement.getElement();
		Element element = new Element(firstNode.getNodeData().getNodeName());
		// Log.debugSystemOut(element.getName());
		Set<String> faqIdSet = ((FaqReasonerNodeData) firstNode.getNodeData())
				.getFaqIdList();
		StringBuilder sb = new StringBuilder();
		for (String faqId : faqIdSet) {
			String question = getQuestion(faqId);
			if (question != null)
				sb.append(question + segment);
		}
		element.setText(sb.toString());
		nodeSuperElement.addContent(element);
		recursiveXmlQueue(firstNode, element, queue);

	}

}
