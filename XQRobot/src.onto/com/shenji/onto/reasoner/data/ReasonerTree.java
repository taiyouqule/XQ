package com.shenji.onto.reasoner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;

import com.hp.hpl.jena.ontology.OntClass;
import com.shenji.common.log.Log;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.reasoner.inter.IReasonerDataOpeare;

/**
 * 推理树
 * 
 * @author zhq
 * 
 */
public abstract class ReasonerTree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String ROOTNAME = "Thing";
	private int treeSize = 0;
	private int treeHight = 0;
	private ReasonerNode rootNode;
	private int routeNum = 1;

	public ReasonerTree() {
		ReasonerNodeData data = new ReasonerNodeData(ROOTNAME);
		rootNode = new ReasonerNode(data, 0);
	}

	protected ReasonerTree(ReasonerNodeData data) {
		rootNode = new ReasonerNode(data, 0);
	}

	public int getRouteNum() {
		return this.routeNum;
	}

	public void reduceRouteNum() {
		this.routeNum--;
	}

	public void updataRouteNum() {
		this.routeNum++;
	}

	public int getTreeHight() {
		List<ReasonerRoute> routes = this.getReasonerRoutes();
		for (ReasonerRoute route : routes) {
			if (route.size() > treeHight) {
				treeHight = route.size();
			}
			route.clear();
		}
		routes.clear();
		return this.treeHight - 1;
	}

	public void updateTreeSize() {
		this.treeSize++;
	}

	public Set<ReasonerNode> getButtomNode() {
		Set<ReasonerNode> set = new HashSet<ReasonerNode>();
		IReasonerDataOpeare inter = new IReasonerDataOpeare() {
			@Override
			public void method(Object... obj) {
				// TODO Auto-generated method stub
				ReasonerNode node = (ReasonerNode) obj[1];
				HashSet<ReasonerNode> set = (HashSet<ReasonerNode>) obj[0];
				if (node.getFirstChilds() == null) {
					set.add(node);
				}
			}
		};
		preOrder(rootNode, inter, new Object[] { set, rootNode });
		return set;
	}

	public int getTreeSize() {
		return this.treeSize;
	}

	public boolean isNullTree() {
		if (this.getRootNode().getFirstChilds() == null)
			return true;
		else
			return false;
	}

	public ReasonerNode getRootNode() {
		return rootNode;
	}

	// 未完成
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ReasonerTree))
			return false;
		/*
		 * List<ReasonerRoute> list = ((ReasonerTree) obj).getReasonerRoutes();
		 * for (ReasonerRoute route : list) { if (isExitRoute(route)) return
		 * true; }
		 */
		if (this.toString().equals(obj.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * @param otherTree
	 * @return obj[0] 负数为子树 0-1为非子树相似度 -1-0为该对象是传入参数的子树 -2-（-1）是传入参数是该对象的子树
	 *         +-1为理想状态，说明推理路径相等
	 */
	public Object[] relevanceAndMaxRoute(ReasonerTree otherTree) {
		ReasonerRoute[] routes = new ReasonerRoute[2];
		double relevance = relevance(otherTree, routes);
		Object[] object = new Object[3];
		object[0] = relevance;
		object[1] = routes[0];
		object[2] = routes[1];
		return object;
	}

	public int isSubTree(ReasonerTree otherTree) {
		if (this.isNullTree() || otherTree.isNullTree())
			return -1;
		double relevance = this.relevanceSubNum(otherTree);
		if (Math.abs(relevance) == 1)
			return 3;
		else if (relevance < 0 && relevance > -1) {
			return 2;
		} else if (relevance < -1 && relevance > -2) {
			return 1;
		} else
			return -1;
	}

	/**
	 * @param otherTree
	 * @return obj[0] 负数为子树 0-1为非子树相似度 -1-0为该对象是传入参数的子树 -2-（-1）是传入参数是该对象的子树
	 *         +-1为理想状态，说明推理路径相等
	 */
	public double relevance(ReasonerTree otherTree) {
		return relevance(otherTree, null);
	}

	private double relevance(ReasonerTree otherTree, Object... obj) {
		if (this == otherTree)
			return 1;
		if (otherTree == null)
			return 0;
		double max = -2;

		for (ReasonerRoute route : this.getReasonerRoutes()) {
			for (ReasonerRoute otherRoute : otherTree.getReasonerRoutes()) {
				double relevance = route.relevance(otherRoute);
				double temp = relevance;
				if (temp < -1) {
					// -1---（-2）时转换为整数
					temp = temp + 2;
				}
				if (Math.abs(temp) > max) {
					max = relevance;
					if (obj != null) {
						obj[0] = route;
						obj[1] = otherRoute;
					}
				}
			}
		}
		return max;
	}

	private double relevanceSubNum(ReasonerTree otherTree) {
		if (this == otherTree)
			return 1;
		if (otherTree == null)
			return 0;
		double max = -2;
		for (ReasonerRoute route : this.getReasonerRoutes()) {
			for (ReasonerRoute otherRoute : otherTree.getReasonerRoutes()) {
				double relevance = route.relevance(otherRoute);
				double temp = relevance;
				if (temp >= 0 && temp != 1)
					continue;
				if (temp < -1) {
					// -1---（-2）时转换为整数
					temp = temp + 2;
				}
				if (Math.abs(temp) > max) {
					max = relevance;
				}
			}
		}
		return max;
	}

	private boolean relevanceSub(ReasonerTree otherTree) {
		if (this == otherTree)
			return false;
		if (otherTree == null)
			return false;

		for (ReasonerRoute route : this.getReasonerRoutes()) {
			for (ReasonerRoute otherRoute : otherTree.getReasonerRoutes()) {
				double temp = route.relevance(otherRoute);
				if (temp >= -1 && temp < 0) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isExitRoute(ReasonerRoute otherRoute) {
		List<ReasonerRoute> list = this.getReasonerRoutes();
		if (list.contains(otherRoute)) {
			list.clear();
			return true;
		} else {
			list.clear();
			return false;
		}

	}

	public int hashCode() {
		// 尽量采用不同hash值，避免在MAP里面效率低
		return this.toString().hashCode();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		IReasonerDataOpeare inter = new IReasonerDataOpeare() {
			@Override
			public void method(Object... obj) {
				// TODO Auto-generated method stub
				((StringBuilder) obj[0]).append(((ReasonerNode) obj[1])
						.getNodeData().getNodeName());
				((StringBuilder) obj[0]).append("#");
			}
		};
		preOrder(rootNode, inter, new Object[] { sb, rootNode });
		return sb.toString();
	}

	protected void preOrder(ReasonerNode node, IReasonerDataOpeare inter,
			Object... obj) {
		if (node == null)
			return;
		inter.method(obj);
		obj[1] = node.getFirstChilds();
		preOrder(node.getFirstChilds(), inter, obj);
		obj[1] = node.getNextSibling();
		preOrder(node.getNextSibling(), inter, obj);
	}

	protected void preOrderSubTrees(ReasonerNode node, Object... obj)
			throws CloneNotSupportedException {
		if (node == null)
			return;
		ReasonerRoute route = ((ReasonerRoute) obj[2]);
		route.add(node);
		obj[1] = node.getFirstChilds();
		preOrderSubTrees(node.getFirstChilds(), obj);

		obj[1] = node.getNextSibling();
		if (obj[1] == null)
			return;
		List<ReasonerRoute> routesList = (List<ReasonerRoute>) obj[0];
		ReasonerRoute routeClone = (ReasonerRoute) route.clone();
		// 移除公共部分
		int start = routeClone.findId(node.getNodeData().getNodeName());
		routeClone.remove(start, routeClone.size());
		/*
		 * //克隆出来的也要设置初始ID routeClone.setInitId(routeClone.toString());
		 */
		routesList.add(routeClone);
		obj[2] = routeClone;
		preOrderSubTrees(node.getNextSibling(), obj);
	}

	public List<ReasonerRoute> getReasonerRoutes() {
		long start = System.currentTimeMillis();
		List<ReasonerRoute> routesList = new ArrayList<ReasonerRoute>();
		ReasonerRoute route = new ReasonerRoute();
		routesList.add(route);
		try {
			preOrderSubTrees(this.getRootNode(), new Object[] { routesList,
					this.getRootNode(), route });
			/*
			 * //初始化完成，设置初始ID，为了以后剪枝的时候区分 route.setInitId(route.toString());
			 */
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}

		for (ReasonerRoute reasonerRoute : routesList) {
			reasonerRoute.setInitId(reasonerRoute.toString());
		}
		long end = System.currentTimeMillis();
		// Log.debugSystemOut("getReasonerRoutes Time:"+(end-start));
		return routesList;
	}

	protected void LevelOrder(ReasonerNode node,
			IReasonerDataOpeare orderInter, Object... obj) {
		if (node == null)
			return;
		orderInter.method(obj);
		List<ReasonerNode> childsList = node.getDirectlyAllChilds();
		if (childsList == null || childsList.size() == 0)
			return;
		for (ReasonerNode childNode : childsList) {
			obj[1] = childNode;
			LevelOrder(childNode, orderInter, obj);
		}
	}

	protected void afterOrder(ReasonerNode node,
			IReasonerDataOpeare orderInter, Object... obj) {
		obj[1] = node.getFirstChilds();
		afterOrder(node.getFirstChilds(), orderInter, obj);
		if (node == null)
			return;
		orderInter.method(obj);
		obj[1] = node.getNextSibling();
		afterOrder(node.getNextSibling(), orderInter, obj);

	}

	public void clear() {
		rootNode.clear(rootNode);
		System.gc();
	}

	/*
	 * private void preOrderClear(ReasonerNode node) { if (node == null) return;
	 * preOrderClear(node.getFirstChilds());
	 * preOrderClear(node.getNextSibling()); node.clear(); }
	 */

	public ReasonerNode find(ReasonerRoute otherRoute, String nodeName,
			int nodeNameIndex) {
		if (otherRoute == null)
			return null;
		ReasonerNode reNode = null;
		List<ReasonerRoute> orcRoutes = this.getReasonerRoutes();
		try {
			for (ReasonerRoute route : orcRoutes) {
				if (route.toString().startsWith(otherRoute.toString())) {
					/*
					 * for (ReasonerNode node : route.getRouteList()) { if
					 * (node.getNodeData().getNodeName().equals(nodeName)) {
					 * reNode = node; return reNode; } }
					 */
					try {
						if ((reNode = route.get(nodeNameIndex)).getNodeData()
								.getNodeName().equals(nodeName)) {
							return reNode;
						} else
							reNode = null;
					} catch (Exception e) {
						// TODO: handle exception
						Log.getLogger(this.getClass()).error(e.getMessage(),e);
						continue;
					}

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			for (ReasonerRoute route : orcRoutes) {
				route.clear();
				route = null;
			}
			if (orcRoutes != null)
				orcRoutes.clear();
		}
		return reNode;

	}

	public List<ReasonerNode> find(String nodeName) {
		if (nodeName == null)
			return null;
		List<ReasonerNode> list = new ArrayList<ReasonerNode>();
		IReasonerDataOpeare inter = new IReasonerDataOpeare() {
			@Override
			public void method(Object... obj) {
				// TODO Auto-generated method stub
				if ((obj[2].toString()).equals(((ReasonerNode) obj[1])
						.getNodeData().getNodeName()))
					((List<ReasonerNode>) obj[0]).add((ReasonerNode) obj[1]);
			}
		};
		preOrder(rootNode, inter, new Object[] { list, rootNode, nodeName });
		return list;
	}

	/*
	 * public ReasonerNode find(String nodeName, String superName) { if
	 * (nodeName == null) return null; ReasonerNode reNode = null;
	 * ReasonerDataOpeareInter inter = new ReasonerDataOpeareInter() {
	 * 
	 * @Override public void method(Object... obj) { // TODO Auto-generated
	 * method stub if ((obj[3].toString()).equals(((ReasonerNode) obj[1])
	 * .getNodeData().getNodeName()) &&obj[2]!=null&&
	 * (obj[4].toString()).equals(((ReasonerNode) obj[2])
	 * .getNodeData().getNodeName())) { obj[0] = obj[1]; }
	 * 
	 * } }; preOrder(rootNode, inter, new Object[] { reNode, rootNode,null
	 * ,nodeName,superName }); return reNode; }
	 */

	/**
	 * <?xml version="1.0" encoding="GBK"?> <OntoName
	 * url="http://192.168.0.111:8080/ontology/OntoName.owl#"> <Thing> <A /> <B>
	 * <B1> <B11 /> </B1> <B2> <B22/> </B2> </B> </Thing> <OntoName>
	 */
	public abstract String toXmlString(String code);

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
		nodeSuperElement.addContent(element);
		recursiveXmlQueue(firstNode, element, queue);

	}

	protected class ReasonerNodeElement {
		private ReasonerNode node;
		private Element element;

		public ReasonerNodeElement(ReasonerNode node, Element element) {
			this.node = node;
			this.element = element;
		}

		public ReasonerNode getNode() {
			return node;
		}

		public Element getElement() {
			return element;
		}
	}

}
