package com.shenji.onto.reasoner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.sparql.function.library.now;
import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.inter.IReasonerDataOpeare;

/**
 * 推理节点
 * 
 * @author zhq
 * 
 */
public class ReasonerNode implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9021712231280515037L;
	/**
	 * 节点数据结构
	 */
	private ReasonerNodeData nodeData;
	/**
	 * 所处层次
	 */
	private int level = -1;
	// 指向第一个儿子
	private ReasonerNode firstChilds = null;
	// 指向下一个兄弟
	private ReasonerNode nextSibling = null;

	// private Set<ReasonerNode> parents = null;

	public ReasonerNode(ReasonerNodeData nodeData, int level) {
		this.nodeData = nodeData;
		this.level = level;
		// this.parents = new HashSet<ReasonerNode>();
	}

	public void clear() {
		this.nodeData.clear();
		this.firstChilds = null;
		this.nextSibling = null;
	}

	public void clear(ReasonerNode node) {
		// 清理数据域
		if (node.firstChilds != null)
			clear(node.firstChilds);
		if (node.nextSibling != null)
			clear(node.nextSibling);
		/*Log.debugSystemOut("【清理】" + node.getNodeData().toString() + " # "
				+ node.toString().split("@")[1]);*/
		node.clear();
		// System.gc();
	}

	public ReasonerNodeData getNodeData() {
		return this.nodeData;
	}

	public void addFirstChilds(ReasonerNode ch) {
		this.firstChilds = ch;
	}

	public void addNextSibling(ReasonerNode ch) {
		this.nextSibling = ch;
	}

	public int addChilds(ReasonerNode ch) {
		if (this.getFirstChilds() == null){
			this.addFirstChilds(ch);
			return -1;
		}
		else {
			ReasonerNode nextSiblingNode = this.getFirstChilds();
			while (nextSiblingNode.getNextSibling() != null) {
				nextSiblingNode = nextSiblingNode.getNextSibling();
			}
			nextSiblingNode.addNextSibling(ch);
			return 1;
		}
	}


	public boolean isChild(ReasonerNode ch) {
		ReasonerNode node = this.firstChilds;
		while (node != null) {
			if (node.equals(ch)) {
				return true;
			}
			node = node.getNextSibling();
		}
		return false;
	}

	/**
	 * 清除子节点及所有子节点的所有子节点
	 * 
	 * @param ch
	 */
	public void deleteChilds(ReasonerNode ch) {
		if (ch == null || this.firstChilds == null)
			return;
		ReasonerNode nowNode = this.firstChilds;
		ReasonerNode perNode = this;
		while (nowNode != null) {
			if (nowNode.equals(ch)) {
				if (nowNode.getNextSibling() != null) {
					perNode.nextSibling = nowNode.getNextSibling();
				} else
					perNode.nextSibling = null;
				// 清楚该节点的所有子节点
				this.clear(nowNode.getFirstChilds());
				// 清楚自身
				this.clear();
				return;
			}
			perNode = nowNode;
			nowNode = nowNode.getNextSibling();

		}
	}

	/*
	 * public void deleteChilde(ReasonerNode ch){ if (ch == null ||
	 * this.firstChilds == null) return; ReasonerNode nowNode =
	 * this.firstChilds; while (nowNode != null) {
	 * 
	 * } }
	 */

	public void deleteChildsByRoute(ReasonerRoute route, int startIndex) {
		// 起始节点为Thing,故startIndex必须大于0
		/*
		 * if (route == null || route.size() == 0 || startIndex > route.size() -
		 * 1 || startIndex <= 0 || route.get(startIndex - 1) != this ||
		 * this.firstChilds == null) return;
		 */
		if (route == null || route.size() == 0 || startIndex == route.size()
				|| startIndex <= 0)// || route.get(startIndex - 1) != superNode)
			return;
		for (int i = route.getRouteList().size() - 1; i >= startIndex; i--) {
			ReasonerNode node = route.get(i);
			// 没有字节点的再继续走，不然就退出
			if (node.getFirstChilds() == null) {
				ReasonerNode superNode = route.get(i - 1);
				/****** 找到node的父节点 *****/
				ReasonerNode subNode = superNode.getFirstChilds();
				// ReasonerNode preNode = superNode;
				while (subNode != null) {
					if (subNode.equals(node)) {
						break;
					}
					superNode = subNode;
					subNode = superNode.getNextSibling();
				}
				/***********************/
				// 要删除的节点有兄弟
				if (node.getNextSibling() != null) {
					/*if (superNode.getFirstChilds() == null) {
						superNode.nextSibling = null;
					} else {
						superNode.firstChilds = node.getNextSibling();
					}*/
					if (node.equals(superNode.getFirstChilds())) {
						superNode.firstChilds = node.getNextSibling();
					} else if (node.equals(superNode.nextSibling)) {
						superNode.nextSibling = node.getNextSibling();
					} else {
						continue;
					}
				}
				// 要删除的节点没有兄弟
				else {
					if (node.equals(superNode.getFirstChilds())) {
						superNode.firstChilds = null;
					} else if (node.equals(superNode.nextSibling)) {
						superNode.nextSibling = null;
					} else {
						continue;
					}
					/*
					 * if(superNode.getFirstChilds()==null){
					 * superNode.nextSibling=null; } else
					 * superNode.firstChilds=null;
					 */

				}
				node.clear();
			}
		}
		/*
		 * deleteChildsByRoute(route.get(startIndex),route,startIndex+1);
		 * ReasonerNode nowNode=route.get(startIndex);
		 * if(nowNode.getNextSibling()==null){
		 * 
		 * } else{
		 * 
		 * }
		 */
		// System.out.println(nowNode.getNodeData().toString());
		/*
		 * if(nowNode.getNextSibling()!=null){
		 * superNode.addChilds(nowNode.getNextSibling()); nowNode.clear(); }
		 * if(nowNode.getAllChildCount())
		 */

		// deleteChildsByRoute(,route,startIndex+1);

	}

	/*
	 * public void addParents(ReasonerNode parent) { this.parents.add(parent); }
	 * 
	 * public Set<ReasonerNode> getParent() { return parents; }
	 */

	public ReasonerNode getFirstChilds() {
		return firstChilds;
	}

	public ReasonerNode getNextSibling() {
		return nextSibling;
	}

	public int getLevel() {
		return level;
	}

	public List<ReasonerNode> getDirectlyAllChilds() {
		if (firstChilds == null)
			return null;
		List<ReasonerNode> childs = new ArrayList<ReasonerNode>();
		childs.add(firstChilds);
		ReasonerNode sibNode = firstChilds.getNextSibling();
		while (sibNode != null) {
			childs.add(sibNode);
			sibNode = sibNode.getNextSibling();
		}
		return childs;
	}
	public void getAllChilds(List<ReasonerNode> list,ReasonerNode node){
		if(node==null)
			return;
		if (node.firstChilds != null)
			getAllChilds(list,node.firstChilds);
		if (node.nextSibling != null)
			getAllChilds(list,node.nextSibling);
		list.add(node);
	}

	public int getAllChildCount() {
		int count = 0;
		if (firstChilds == null)
			return 0;
		count++;
		ReasonerNode sibNode = firstChilds.getNextSibling();
		while (sibNode != null) {
			count++;
			sibNode = sibNode.getNextSibling();
		}
		return count;
	}

	/*
	 * private void preClone(ReasonerNode node) throws
	 * CloneNotSupportedException { if (node == null) return; node.clone();
	 * preClone(node.getFirstChilds()); preClone(node.getNextSibling()); }
	 */

	/**
	 * @return 创建并返回此对象的一个副本。
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException {
		// 直接调用父类的clone()方法,返回克隆副本,这里不需要深层拷贝
		ReasonerNode cloneNode = (ReasonerNode) super.clone();
		return cloneNode;
	}

	/**
	 * 新客隆 只克隆节点值，不克隆节点指针
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	private Object newClone() throws CloneNotSupportedException {
		ReasonerNode cloneNode = new ReasonerNode(
				(ReasonerNodeData) nodeData.clone(), this.getLevel());
		return cloneNode;
	}

	/**
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Object deepNewClone() throws CloneNotSupportedException {
		ReasonerNode cloneRootNode = (ReasonerNode) newClone();
		ReasonerNode thisSubNode = this.firstChilds;
		ReasonerNode cloneSubNode = null;
		ReasonerNode cloneSuperNode = cloneRootNode;
		while (thisSubNode != null) {
			cloneSubNode = (ReasonerNode) thisSubNode.newClone();
			cloneSuperNode.addFirstChilds(cloneSubNode);
			thisSubNode = thisSubNode.getFirstChilds();
			cloneSuperNode = cloneSubNode;
		}
		return cloneRootNode;
	}

	public static void main(String[] str) throws CloneNotSupportedException {
		ReasonerNode nodeA = new ReasonerNode(new ReasonerNodeData("A"), 0);
		ReasonerNode nodeb = new ReasonerNode(new ReasonerNodeData("B"), 0);
		nodeA.addChilds(nodeb);
		ReasonerNode nodeC = (ReasonerNode) nodeA.deepNewClone();
	}

}