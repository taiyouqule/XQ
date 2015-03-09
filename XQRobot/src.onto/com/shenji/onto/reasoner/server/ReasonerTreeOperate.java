package com.shenji.onto.reasoner.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.xpath.axes.SubContextList;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.reasoner.ReasonerLabel;
import com.shenji.onto.reasoner.data.CustomReaonerTree;
import com.shenji.onto.reasoner.data.FaqReasonerNodeData;
import com.shenji.onto.reasoner.data.FaqReasonerTree;
import com.shenji.onto.reasoner.data.ReasonerNode;
import com.shenji.onto.reasoner.data.ReasonerNodeData;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.data.UserReasonerNodeData;
import com.shenji.onto.reasoner.data.UserReasonerTree;

public class ReasonerTreeOperate {
	private static enum TreeType{
		FAQTREE,
		USERTREE,
		BIGFAQTREE;
	}
	private static int FAQTREE = 0;
	private static int USERTREE = 1;
	private static int BIGFAQTREE = 2;

	public static ReasonerTree[] getFaqReasonerTree(String individualName,
			boolean isNeedReFresh,boolean isWithPossibility) {
		// 从模型中得到所有显示类型（URL#NAME）
		String[] typesList = FaqMapServices.getInstance().getQueryInter()
				.getRDFType(individualName);
		long start = System.currentTimeMillis();
		ReasonerTree[] trees = getReasonerTree(typesList, FAQTREE,
				isNeedReFresh,isWithPossibility);
		long end = System.currentTimeMillis();
		// Log.debugSystemOut("【Time】得到一个Faq推理树花费的时间:"+(end - start)+"ms");
		return trees;
	}

	public static ReasonerTree[] getBigFaqReasonerTree(boolean isWithPossibility) {
		return getReasonerTree(null, BIGFAQTREE, false,isWithPossibility);
	}

	public static ReasonerTree[] getUserReasonerTree(String[] words,
			boolean isNeedReFresh,boolean isWithPossibility) {
		return getReasonerTree(words, USERTREE, isNeedReFresh,isWithPossibility);
	}

	private static ReasonerTree[] getReasonerTree(String[] words, int type,
			boolean isNeedReFresh,boolean isWithPossibility) {
		/*
		 * if ((words == null || words.length == 0) && (type != BIGFAQTREE))
		 * return null;
		 */

		// 获得所有可用于推理的图谱集合
		Iterator<Map.Entry<String, OntModel>> iterator = ReasonerServer
				.getInstance().getOntoMap().entrySet().iterator();
		// 推理树集合
		List<ReasonerTree> list = new ArrayList<ReasonerTree>();
		// 所有知识图谱迭代（这里可能只有一个）
		while (iterator.hasNext()) {
			Map.Entry<String, OntModel> entry = iterator.next();
			// 知识图谱内存模型
			OntModel model = entry.getValue();
			//Log.promptMsg("【Log】此时使用的图谱名："+entry.getKey());
			// 便利所有显示词，得到改图谱中OntClass
			/*
			 * for (String word : words) { if (type == USERTREE) { //
			 * 拼接成（URL#NAME） word = model.getNsPrefixURI("") + word; } OntClass
			 * ontClass = model.getOntClass(word);
			 * ontClassesSet.add(ontClass);// 用map没什么用，其实set就行 }
			 */
			if (type == BIGFAQTREE) {
				addBigFaqReasonrTree(entry.getKey(), model, list);
			} else {
				Set<String> ontWordsSet = ReasonerServer.getInstance()
						.getOntoWords(model, isNeedReFresh);
				// 用于存储该条FAQ的显示。（在该图谱中）
				HashMap<OntClass,Double> ontClassesMap = new HashMap<OntClass,Double>();
				if (words != null) {
					for (String word : words) {
						// USER树
						double possiblity=0;
						if(isWithPossibility){
							possiblity=Double.parseDouble(word.split("#")[1]);
							word=word.split("#")[0];						
						}
						if (type == USERTREE) { // 拼接成（URL#NAME）							
							if (ontWordsSet.contains(word)) {
								word = model.getNsPrefixURI("") + word;
								OntClass ontClass = model.getOntClass(word);
								ontClassesMap.put(ontClass,possiblity);
							}
						}
						// FAQ树
						else {
							OntClass ontClass = model.getOntClass(word);
							ontClassesMap.put(ontClass,possiblity);
						}

					}
				}
				if (type == FAQTREE)
					addFaqReasonrTree_(entry.getKey(), model, ontClassesMap,
							list);
				else if (type == USERTREE)
					// 这里用户名还没
					addUserReasonrTree("test", entry.getKey(), model,
							ontClassesMap, list);
				ontClassesMap.clear();
			}

		}
		return list.toArray(new ReasonerTree[list.size()]);

	}

	private static void addFaqReasonrTree(String modelName, OntModel model,
			HashMap<OntClass,Double> ontClassMap, List<ReasonerTree> list) {
		if (ontClassMap == null || ontClassMap.size() == 0)
			return;
		// 得到模型根节点(root)
		OntClass rootOntClass = model
				.getOntClass("http://www.w3.org/2002/07/owl#Thing");
		// 知识图谱的URL
		String url = model.getNsPrefixURI("");
		// 推理树（假定只有一颗大的推理树）
		ReasonerTree reasonerTree = new FaqReasonerTree(modelName, url);
		// ************* 这边不合理，寻找THING子类是便利所有类，效率太低，其实还好 *************//*
		int subCount = 0;
		int level = 1;
		Iterator<OntClass> iterator = model.listNamedClasses();
		// rootOntClass.listSubClasses();
		while (iterator.hasNext()) {
			OntClass subOntClass = (OntClass) iterator.next();
			if (subOntClass.hasSuperClass(rootOntClass, false)
					|| !subOntClass.hasSuperClass()) {// 没有父类或者父类是THING,这是模型的第一层(是不是jena
				// BUG )
				// if (!subOntClass.hasSuperClass()) {
				if (ontClassMap.containsKey(subOntClass)) {
					ReasonerNode subNode = new ReasonerNode(
							new ReasonerNodeData(subOntClass.getLocalName()),
							level);
					int type = reasonerTree.getRootNode().addChilds(subNode);
					// 增加兄弟节点则让树的推理路径加1
					if (type > 0)
						reasonerTree.updataRouteNum();
					// subNode.addParents(reasonerTree.getRootNode());
					subCount++;
					recursiveFaqSubOntClass(reasonerTree, subNode,
							ontClassMap, (level + 1), url);
				}

			}
		}
		// ************************************************//*
		// recursiveSubOntClass(reasonerTree,reasonerTree.getRootNode(),ontClassesMap,1,url);
		list.add(reasonerTree);
	}

	private static void addFaqReasonrTree_(String modelName, OntModel model,
			HashMap<OntClass,Double> ontClassesMap, List<ReasonerTree> list) {
		/*
		 * if (ontClassesSet == null || ontClassesSet.size() == 0) return;
		 */
		// 得到模型根节点(root)
		OntClass rootOntClass = model
				.getOntClass("http://www.w3.org/2002/07/owl#Thing");
		// 知识图谱的URL
		String url = model.getNsPrefixURI("");
		// 推理树（假定只有一颗大的推理树）
		ReasonerTree reasonerTree = new FaqReasonerTree(modelName, url);
		// ************* 这边不合理，寻找THING子类是便利所有类，效率太低，其实还好 *************//*
		int subCount = 0;
		int level = 1;
		Iterator<OntClass> iterator = rootOntClass.listSubClasses();
		while (iterator.hasNext()) {
			OntClass subOntClass = (OntClass) iterator.next();
			if (ontClassesMap.containsKey(subOntClass)) {
				ReasonerNode subNode = new ReasonerNode(new ReasonerNodeData(
						subOntClass.getLocalName()), level);
				int type = reasonerTree.getRootNode().addChilds(subNode);
				// 增加兄弟节点则让树的推理路径加1
				if (type > 0)
					reasonerTree.updataRouteNum();
				subCount++;
				recursiveFaqSubOntClass(reasonerTree, subNode, ontClassesMap,
						(level + 1), url);
			}
		}
		// ************************************************//*
		// recursiveSubOntClass(reasonerTree,reasonerTree.getRootNode(),ontClassesMap,1,url);
		list.add(reasonerTree);
	}

	private static void addUserReasonrTree(String userName, String modelName,
			OntModel model, HashMap<OntClass,Double> ontClassMap,
			List<ReasonerTree> list) {
		/*
		 * if (ontClassesSet == null || ontClassesSet.size() == 0) return;
		 */
		// 得到模型根节点(root)
		OntClass rootOntClass = model
				.getOntClass("http://www.w3.org/2002/07/owl#Thing");
		// 知识图谱的URL
		String url = model.getNsPrefixURI("");
		// 推理树（假定只有一颗大的推理树）
		ReasonerTree reasonerTree = new UserReasonerTree(userName, modelName,
				url);

		Iterator<Entry<OntClass, Double>> iterator = ontClassMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<OntClass, Double> entry=iterator.next();
			OntClass ontClass = entry.getKey();
			double possibility=entry.getValue();
			if (ontClass == null)
				continue;
			// 被其他同义词定义的词，得用定义其的词进行搜索
			if (ontClass.getVersionInfo() != null) {
				ontClass = ontClass.getEquivalentClass();
			}
			String nodeName = ontClass.getLocalName();
			if (((UserReasonerTree) reasonerTree).getUsedNodeNum(nodeName) > 0) {
				List<ReasonerNode> nodesList = reasonerTree.find(nodeName);
				for (ReasonerNode node : nodesList) {
					((UserReasonerNodeData) node.getNodeData())
							.setAbstract(false);
					((UserReasonerNodeData) node.getNodeData())
					.setPossibility(possibility);
				}
				((UserReasonerTree) reasonerTree).addUsedNodeName(nodeName);
				continue;
			} else {
				UserReasonerNodeData nodeData = new UserReasonerNodeData(
						ontClass.getLocalName());
				nodeData.setAbstract(false);
				nodeData.setPossibility(possibility);
				ReasonerNode rootNode = new ReasonerNode(nodeData, 0);

				((UserReasonerTree) reasonerTree).addUsedNodeName(nodeName);
				recursiveUserSuperOntClass(rootNode, reasonerTree);
			}

		}
		// Log.debugSystemOut("【剪枝前】:"+reasonerTree.toString());
		// 逻辑做剪枝
		((UserReasonerTree) reasonerTree).doPruning();
		list.add(reasonerTree);
	}

	private static void recursiveUserSuperOntClass(ReasonerNode subNode,
			ReasonerTree reasonerTree) {
		String ontoName = ((UserReasonerTree) reasonerTree).getOntoName();
		OntClass ontClass = ReasonerServer.getInstance().getOntClass(ontoName,
				subNode.getNodeData().getNodeName());
		if (ontClass == null)
			return;
		/*
		 * if (subNode.getNodeData().getNodeName().equals("D")) {
		 * Log.debugSystemOut(""); }
		 */
		if (!ontClass.hasSuperClass()
				|| ontClass.hasSuperClass(ReasonerServer.getInstance()
						.getRootOntClass(ontoName))) {// BUG吧，有些第一层节点找不到父类。。。只能认为没有父类的就是顶层
			if (((UserReasonerTree) reasonerTree).getUsedNodeNum(subNode
					.getNodeData().getNodeName()) == 1) {
				int type = reasonerTree.getRootNode().addChilds(subNode);
				// 增加兄弟节点则让树的推理路径加1
				if (type > 0)
					reasonerTree.updataRouteNum();
			}
			return;
		}
		Iterator<OntClass> iterator = ontClass.listSuperClasses();
		if (iterator == null)
			return;
		int superCount = 1;
		ReasonerNode superNode = null;
		while (iterator.hasNext()) {
			OntClass superClass = iterator.next();
			String superNodeName = superClass.getLocalName();
			// 第一次出现
			if (((UserReasonerTree) reasonerTree).getUsedNodeNum(superNodeName) < 1) {
				UserReasonerNodeData superNodeData = new UserReasonerNodeData(
						superClass.getLocalName());
				superNodeData.setAbstract(true);
				superNode = new ReasonerNode(superNodeData, 0);
				if (superCount == 1) {
					if (!superNode.isChild(subNode)) {
						int type = superNode.addChilds(subNode);
						// 增加兄弟节点则让树的推理路径加1
						if (type > 0)
							reasonerTree.updataRouteNum();
					}
				} else {
					ReasonerNode cloneNode;
					try {
						cloneNode = (ReasonerNode) subNode.deepNewClone();
						int type = superNode.addChilds(cloneNode);
						// 增加兄弟节点则让树的推理路径加1
						if (type > 0)
							reasonerTree.updataRouteNum();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						Log.getLogger(ReasonerTreeOperate.class).error(e.getMessage(),e);
						continue;
					}
				}
				((UserReasonerTree) reasonerTree)
						.addUsedNodeName(superNodeName);
				superCount++;
				recursiveUserSuperOntClass(superNode, reasonerTree);
			} else {
				List<ReasonerNode> nodesList = reasonerTree.find(superNodeName);
				if (nodesList != null && nodesList.size() != 0) {
					for (ReasonerNode node : nodesList) {
						if (superCount == 1) {
							if (!node.isChild(subNode)) {
								int type = node.addChilds(subNode);
								// 增加兄弟节点则让树的推理路径加1
								if (type > 0)
									reasonerTree.updataRouteNum();

							}
						} else {
							ReasonerNode cloneNode;
							try {
								cloneNode = (ReasonerNode) subNode
										.deepNewClone();
								int type = node.addChilds(cloneNode);
								// 增加兄弟节点则让树的推理路径加1
								if (type > 0)
									reasonerTree.updataRouteNum();
							} catch (CloneNotSupportedException e) {
								// TODO Auto-generated catch block
								Log.getLogger(ReasonerTreeOperate.class).error(e.getMessage(),e);
								continue;
							}
						}
						// recursiveUserSuperOntClass(node, reasonerTree);
						superCount++;
						// ((UserReasonerTree)
						// reasonerTree).addUsedNodeName(superNodeName);
					}
				}
			}
		}
	}

	private static void recursiveFaqSubOntClass(ReasonerTree reasonerTree,
			ReasonerNode superNode, HashMap<OntClass,Double> ontClassesSet, int level,
			String url) {
		OntClass ontClass = ReasonerServer.getInstance().getOntClass(
				((FaqReasonerTree) reasonerTree).getOntoName(),
				superNode.getNodeData().getNodeName());
		if (ontClass == null)
			return;
		Iterator<OntClass> iterator = ontClass.listSubClasses();
		int subCount = 0;
		while (iterator.hasNext()) {
			OntClass subOntClass = (OntClass) iterator.next();
			if (ontClassesSet.containsKey(subOntClass)) {
				ReasonerNode subNode = new ReasonerNode(new ReasonerNodeData(
						subOntClass.getLocalName()), level);

				/*
				 * Log.debugSystemOut(subNode.getNodeData().getNodeName() + "#"
				 * + subNode.getLevel());
				 */

				if (subCount == 0) {
					superNode.addFirstChilds(subNode);
				} else {
					ReasonerNode nextSiblingNode = superNode.getFirstChilds();
					while (nextSiblingNode.getNextSibling() != null) {
						nextSiblingNode = nextSiblingNode.getNextSibling();
					}
					nextSiblingNode.addNextSibling(subNode);
				}
				int type = superNode.addChilds(subNode);
				// 增加兄弟节点则让树的推理路径加1
				if (type > 0)
					reasonerTree.updataRouteNum();
				// subNode.addParents(superNode);
				subCount++;
				recursiveFaqSubOntClass(reasonerTree, subNode, ontClassesSet,
						(level + 1), url);
			}
		}
		reasonerTree.updateTreeSize();
	}

	private static void addBigFaqReasonrTree(String modelName, OntModel model,
			List<ReasonerTree> list) {
		/*
		 * if (ontClassesSet == null || ontClassesSet.size() == 0) return;
		 */
		// 得到模型根节点(root)
		OntClass rootOntClass = model
				.getOntClass("http://www.w3.org/2002/07/owl#Thing");
		// 知识图谱的URL
		String url = model.getNsPrefixURI("");
		// 推理树（假定只有一颗大的推理树）
		ReasonerTree reasonerTree = new CustomReaonerTree(modelName, url,
				new FaqReasonerNodeData(ReasonerTree.ROOTNAME));
		// ************* 这边不合理，寻找THING子类是便利所有类，效率太低，其实还好 *************//*
		int level = 1;
		Iterator<OntClass> iterator = rootOntClass.listSubClasses(false);
		while (iterator.hasNext()) {
			OntClass subOntClass = (OntClass) iterator.next();
			// Log.debugSystemOut(subOntClass.getLocalName());
			ReasonerNode subNode = new ReasonerNode(new FaqReasonerNodeData(
					subOntClass.getLocalName()), level);
			int type = reasonerTree.getRootNode().addChilds(subNode);
			// 增加兄弟节点则让树的推理路径加1
			if (type > 0)
				reasonerTree.updataRouteNum();
			recursiveBigFaqSubOntClass(reasonerTree, subNode, (level + 1), url);
		}
		list.add(reasonerTree);
	}

	private static void recursiveBigFaqSubOntClass(ReasonerTree reasonerTree,
			ReasonerNode superNode, int level, String url) {
		OntClass ontClass = ReasonerServer.getInstance().getOntClass(
				((FaqReasonerTree) reasonerTree).getOntoName(),
				superNode.getNodeData().getNodeName());
		if (ontClass == null)
			return;
		Iterator<OntClass> iterator = ontClass.listSubClasses(false);
		while (iterator.hasNext()) {
			OntClass subOntClass = (OntClass) iterator.next();
			// Log.debugSystemOut(subOntClass.getLocalName());
			ReasonerNode subNode = new ReasonerNode(new FaqReasonerNodeData(
					subOntClass.getLocalName()), level);
			int type = superNode.addChilds(subNode);
			// 增加兄弟节点则让树的推理路径加1
			if (type > 0)
				reasonerTree.updataRouteNum();
			// subNode.addParents(superNode);
			recursiveBigFaqSubOntClass(reasonerTree, subNode, (level + 1), url);
		}
		reasonerTree.updateTreeSize();
	}
	

}
