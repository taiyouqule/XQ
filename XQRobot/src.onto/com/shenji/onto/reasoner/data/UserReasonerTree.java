package com.shenji.onto.reasoner.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.shenji.common.log.Log;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.reasoner.inter.IReasonerDataOpeare;


public class UserReasonerTree extends FaqReasonerTree {
	private HashMap<String, Integer> usedNodeNameMap;
	private String userName;

	public UserReasonerTree(String userName, String ontoName, String url) {
		super(ontoName, url, new UserReasonerNodeData(ROOTNAME, false));
		this.userName = userName;
		this.usedNodeNameMap = new HashMap<String, Integer>();
		// TODO Auto-generated constructor stub
	}

	public HashMap<String, Integer> getUsedNodeNameMap() {
		return this.usedNodeNameMap;
	}

	/**
	 * 剪枝算法
	 */
	public void doPruning() {
		// 当推理路径只有一条或者没有
		List<ReasonerRoute> routes = this.getReasonerRoutes();
		/*
		 * Log.debugSystemOut("----【剪枝前路径Start】----"); for (ReasonerRoute route
		 * : routes) { Log.debugSystemOut(route.toString()); }
		 * Log.debugSystemOut("----【剪枝前路径End】----");
		 */
		if (routes.size() <= 1)
			return;
		// 完成剪枝的集合
		Set<String> finishPruningSet = new HashSet<String>();
		Map<ReasonerRoute, Integer> distCardRouteMap = new HashMap<ReasonerRoute, Integer>();
		PruningServer pruningServer = new PruningServer();
		for (ReasonerRoute route : routes) {
			if (finishPruningSet.contains(route.getInitId()))
				continue;
			for (ReasonerRoute otherRoute : routes) {
				// Log.debugSystemOut("$$"+otherRoute.getInitId());
				if (finishPruningSet.contains(route.getInitId())
						|| finishPruningSet.contains(otherRoute.getInitId()))
					continue;
				// 剪枝节点位置
				if (route.getInitId() == (otherRoute.getInitId()))
					continue;
				//第一层剪枝不要暂时不要了
				/*if (pruningServer.firstLevelPurning(route, otherRoute,
						distCardRouteMap)) {*/
					if(pruningServer.ordinaryPurning(route, otherRoute, distCardRouteMap));
				//}
				/*pruningServer.virtualPurning(route, distCardRouteMap);
				pruningServer.virtualPurning(otherRoute, distCardRouteMap);*/
				if(distCardRouteMap.size()==0)
					continue;
				Iterator<Entry<ReasonerRoute, Integer>> iterator=distCardRouteMap.entrySet().iterator();
				while(iterator.hasNext()){
					Map.Entry<ReasonerRoute, Integer> entry=iterator.next();
					ReasonerRoute discardRoute = entry.getKey();
					int pruningLocationNum =entry.getValue();	
					pruningTree(discardRoute,pruningLocationNum,finishPruningSet);
				}
				distCardRouteMap.clear();
			}
		}
		if (finishPruningSet != null)
			finishPruningSet.clear();
		if (distCardRouteMap != null) {
			distCardRouteMap.clear();
		}
		if (routes == null)
			return;
		for (ReasonerRoute route : routes) {
			route.clear();
		}

		routes.clear();
	}
	
	private void pruningTree(ReasonerRoute discardRoute,int pruningLocationNum,Set<String> finishPruningSet){
		ReasonerNode discardSuperNode = discardRoute
				.get(pruningLocationNum);
		// Log.debugSystemOut("--" + discardRoute.toString() +
		// "--");
		discardSuperNode.deleteChildsByRoute(discardRoute,
				pruningLocationNum + 1);
		finishPruningSet.add(discardRoute.getInitId());
		// 推理路径减少了一条
		super.reduceRouteNum();
	}

	/**
	 * 剪枝算法
	 */
	/*
	 * public void doPruning() { // 当推理路径只有一条或者没有 List<ReasonerRoute> routes =
	 * this.getReasonerRoutes(); Log.debugSystemOut("----【剪枝前路径Start】----"); for
	 * (ReasonerRoute route : routes) { Log.debugSystemOut(route.toString()); }
	 * Log.debugSystemOut("----【剪枝前路径End】----"); if (routes.size() <= 1) return;
	 * // 完成剪枝的集合 Set<String> finishPruningSet = new HashSet<String>(); for
	 * (ReasonerRoute route : routes) { if
	 * (finishPruningSet.contains(route.getInitId())) continue; for
	 * (ReasonerRoute otherRoute : routes) { //
	 * Log.debugSystemOut("$$"+otherRoute.getInitId()); if
	 * (finishPruningSet.contains(route.getInitId()) ||
	 * finishPruningSet.contains(otherRoute.getInitId())) continue; // 剪枝节点位置 if
	 * (route.getInitId() == (otherRoute.getInitId())) continue; boolean
	 * routeFirstLevel = ((UserReasonerNodeData) route.get(1)
	 * .getNodeData()).isAbstract(); boolean otherRouteFirstLevel =
	 * ((UserReasonerNodeData) otherRoute .get(1).getNodeData()).isAbstract();
	 * int pruningLocationNum = -1;; ReasonerRoute discardRoute = null;
	 * 
	 * if (routeFirstLevel && otherRouteFirstLevel || (!routeFirstLevel &&
	 * !otherRouteFirstLevel)) { pruningLocationNum =
	 * route.isBrotherPruning(otherRoute); if (pruningLocationNum == -1) { //
	 * 第一层词出现要却确保 discardRoute = null; } else { try {
	 * 
	 * discardRoute = ReasonerRoute.doPrunning(route, otherRoute); } catch
	 * (Exception e) { // 类型转换异常不剪枝了 e.printStackTrace(); }
	 * 
	 * } } else { //整条路径不要了 if (!routeFirstLevel) { discardRoute = otherRoute;
	 * pruningLocationNum=0; } else if (!otherRouteFirstLevel) { discardRoute =
	 * route; pruningLocationNum=0; } else discardRoute = null; } // 做剪枝 if
	 * (discardRoute != null) {
	 * 
	 * ReasonerNode discardSuperNode = discardRoute .get(pruningLocationNum);
	 * //Log.debugSystemOut("--" + discardRoute.toString() + "--");
	 * discardSuperNode.deleteChildsByRoute(discardRoute, pruningLocationNum +
	 * 1); finishPruningSet.add(discardRoute.getInitId()); //推理路径减少了一条
	 * super.reduceRouteNum();
	 * 
	 * // 自己被减掉了，跳出循环 if (discardRoute.hashCode()==route.hashCode()) break;
	 * 
	 * } } } if (finishPruningSet != null) finishPruningSet.clear(); if (routes
	 * == null) return; for (ReasonerRoute route : routes) { route.clear(); }
	 * 
	 * routes.clear(); }
	 */

	/*
	 * private void pruningReasonerRoute(ReasonerRoute route,int
	 * pruningLocationNum){ ReasonerNode
	 * superNode=route.get(pruningLocationNum); ReasonerNode
	 * subNode=route.get(pruningLocationNum+1); }
	 */

	@Override
	public String toXmlString(String code) {
		// TODO Auto-generated method stub
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

	public String toAbstractString() {
		StringBuilder sb = new StringBuilder();
		IReasonerDataOpeare inter = new IReasonerDataOpeare() {
			@Override
			public void method(Object... obj) {
				// TODO Auto-generated method stub
				((StringBuilder) obj[0]).append(((ReasonerNode) obj[1])
						.getNodeData().getNodeName()
						+ " "
						+ ((UserReasonerNodeData) ((ReasonerNode) obj[1])
								.getNodeData()).isAbstract());
				((StringBuilder) obj[0]).append("#");
			}
		};
		super.preOrder(this.getRootNode(), inter,
				new Object[] { sb, this.getRootNode() });
		return sb.toString();
	}

	public void clear() {
		super.clear();
		this.usedNodeNameMap.clear();
	}

	public void addUsedNodeName(String nodeName) {
		if (!this.usedNodeNameMap.containsKey(nodeName))
			this.usedNodeNameMap.put(nodeName, 1);
		else {
			this.usedNodeNameMap.put(nodeName,
					this.usedNodeNameMap.get(nodeName) + 1);
		}
	}

	public int getUsedNodeNum(String nodeName) {
		int num = -1;
		if (this.usedNodeNameMap.containsKey(nodeName))
			num = this.usedNodeNameMap.get(nodeName);
		return num;
	}

}
