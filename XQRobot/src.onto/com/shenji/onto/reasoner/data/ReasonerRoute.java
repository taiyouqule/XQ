package com.shenji.onto.reasoner.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import com.hp.hpl.jena.util.OneToManyMap.Entry;
import com.shenji.common.log.Log;
import com.shenji.common.util.StringMatching;
import com.shenji.onto.mapping.FaqMapUtil;
import com.shenji.onto.reasoner.data.ResultMap.Iiterator;
import com.shenji.onto.reasoner.inter.IReasonerDataOpeare;


public class ReasonerRoute implements Cloneable {
	private List<ReasonerNode> routeList;
	private String initId;

	public ReasonerRoute() {	
		this.routeList = new ArrayList<ReasonerNode>();
	}
	public String getInitId(){
		return initId;
	}

	public void setInitId(String initId){
		this.initId=initId;
	}

	public void clear() {
		this.routeList.clear();
		this.initId=null;
	}

	public int size() {
		return this.routeList.size();
	}
	//public String toStringByDefin)

	public String toString() {
		IReasonerDataOpeare inter = new IReasonerDataOpeare() {
			@Override
			public void method(Object... obj) {
				// TODO Auto-generated method stub
				/*((StringBuilder) obj[0]).append(((ReasonerNode) obj[1])
						.getNodeData().toString() +"@"+((ReasonerNode) obj[1]).toString().split("@")[1]+"  #  ");*/
				((StringBuilder) obj[0]).append(((ReasonerNode) obj[1])
						.getNodeData().toString()+"#");
			}
		};
		return toPerString(inter);
	}

	private String toPerString(IReasonerDataOpeare inter) {
		StringBuilder builder = new StringBuilder();
		for (ReasonerNode node : routeList) {
			inter.method(new Object[] { builder, node });
		}
		return builder.toString();
	}
	
	

	public static ReasonerRoute doPrunning(ReasonerRoute routeA,
			ReasonerRoute routeB) throws Exception {
		int countA = 0;
		int countB = 0;
		double countD_A=0;
		double countD_B=0;
		for (ReasonerNode node : routeA.getRouteList()) {
			if (!((UserReasonerNodeData) node.getNodeData()).isAbstract())
				countA++;
			countD_A+=((UserReasonerNodeData) node.getNodeData()).getPossibility();
		}
		for (ReasonerNode node : routeB.getRouteList()) {
			if (!((UserReasonerNodeData) node.getNodeData()).isAbstract())
				countB++;
			countD_B+=((UserReasonerNodeData) node.getNodeData()).getPossibility();
		}
		if (countA > countB) {
			return routeB;
		} else if (countA == countB) {
			if(countD_A>countD_B){
				return routeB;
			}
			else if(countD_A<countD_B)
				return routeA;
			return null;
		} else
			return routeA;
	}

	public int isBrotherPruning(ReasonerRoute otherRoute) {
		String orcRouteCode = this.coding("");
		String otherRouteCode = otherRoute.coding("");
		char[] orcChars = orcRouteCode.toCharArray();
		char[] otherChars = otherRouteCode.toCharArray();
		int reFlag = 0;
		/*//最后一个节点不同，不需要剪枝(这里不应该)
		if (orcChars[orcChars.length - 1] != otherChars[otherChars.length - 1]) {
			reFlag = -1;
			return reFlag;
		}*/
		//找到最后一个相等节点 T-E-A-C-D   T-E-B-C-D
		for (int i = 0; i <orcChars.length|| i <otherChars.length; i++) {
			if(i>orcChars.length-1||i>otherChars.length-1)
				break;
			if (orcChars[i] == otherChars[i]) {
				reFlag++;
			} else
				break;
		}
		return reFlag;
	}
	
	public int getNunAbstractNodeNum(){
		int count=0;
		for(ReasonerNode node:this.getRouteList()){
			if(!(node.getNodeData() instanceof UserReasonerNodeData)){
				return -1;
			}
			else{
				if(((UserReasonerNodeData)node.getNodeData()).isAbstract())
					count++;
			}
		}
		return count;
	}

	public List<ReasonerNode> getRouteList() {
		return this.routeList;
	}
	
	public ReasonerNode get(int index){
		return this.routeList.get(index);
	}

	public void add(ReasonerNode node) {
		this.routeList.add(node);
	}

	public void remove(ReasonerNode node) {
		if (this.routeList.contains(node))
			this.routeList.remove(node);
	}

	public boolean remove(int start, int end) {
		if ((start < 0 || start >= this.routeList.size())
				|| (end < 0 || end > this.routeList.size()) || end < start)
			return false;
		if (this.routeList.size() != 0) {
			for (int i = end - 1; i >= start; i--) {
				this.routeList.remove(this.routeList.get(i));
			}
			return true;
		} else
			return false;
	}

	public ReasonerNode find(String nodeName) {
		for (ReasonerNode node : routeList) {
			if (nodeName != null
					&& nodeName.equals(node.getNodeData().getNodeName()))
				return node;
		}
		return null;
	}

	public int findId(String nodeName) {
		ReasonerNode node = this.find(nodeName);
		if (this.routeList.contains(node))
			return this.routeList.indexOf(node);
		return -1;
	}

	/**
	 * @param obj
	 * @return 负数为子树 0-1为非子树相似度 -1-0为该对象是传入参数的子树 -2-（-1）是传入参数是该对象的子树
	 *         +-1为理想状态，说明推理路径相等
	 */
	public double relevance(ReasonerRoute obj) {
		if (this == obj)
			return 1;
		if (obj == null)
			return 0;
		// ReasonerRoute maxRoute = this.size() >= obj.size() ? this : obj;
		// ReasonerRoute minRoute = this.size() < obj.size() ? this : obj;
		String thisCode = this.coding(null);
		String objCode = obj.coding(null);

		if (isSubRoute(this, obj)) {
			// obj 是 this子树
			if (hasSubRoute(obj, this))
				return -(StringMatching.getSimilarity(thisCode, objCode));
			else
				return -2 + (StringMatching.getSimilarity(thisCode, objCode));
		} else {
			// 这里应该考虑逐层不同，这里先这样吧
			return StringMatching.getSimilarity(thisCode, objCode);
		}
	}

	private String coding(final String nonCodingSet) {
		IReasonerDataOpeare inter = new IReasonerDataOpeare() {
			@Override
			public void method(Object... obj) {
				// TODO Auto-generated method stub
				StringBuilder builder = (StringBuilder) obj[0];
				ReasonerNode node = (ReasonerNode) obj[1];
				// 根节点不参与编码
				if (node.equals(routeList.get(0)))
					return;
				codingOrder(node, nonCodingSet, builder, 0);
			}
		};
		return toPerString(inter);
	}

	private String codingStr(String str) {
		char[] chs = str.toCharArray();
		char ch = 0;
		int count = 0;
		for (char c : chs) {
			ch = (char) (ch + c);
			if (count > 0)
				ch = (char) (ch / 2);
			count++;
		}
		return String.valueOf(ch);
	}

	private void codingOrder(ReasonerNode node, String nonCodingSet,
			StringBuilder builder, int start) {
		String code = null;
		try {
			code = codingStr(node.getNodeData().getNodeName());
			if (builder.indexOf(code) < 0
					&& (nonCodingSet == null || !nonCodingSet.contains(code)))
				builder.append(code);
			else {
				// 遍历完还没有找到合适编码
				char ch = 'a';
				while (true) {
					if (builder.indexOf(String.valueOf(ch)) < 0
							&& (nonCodingSet == null || !nonCodingSet
									.contains(String.valueOf(ch)))) {
						code = String.valueOf(ch);
						break;
					} else
						ch = (char) (ch + 1);
				}
				builder.append(code);

			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}

	}

	public static boolean isSubRoute(ReasonerRoute routeA, ReasonerRoute routeB) {
		if (routeA == null || routeB == null)
			return false;
		if (routeA.size() > routeB.size())
			return routeA.toString().contains(routeB.toString());
		else
			return routeB.toString().contains(routeA.toString());
	}

	public static boolean hasSubRoute(ReasonerRoute routeA, ReasonerRoute routeB) {
		if (routeA == null || routeB == null)
			return false;
		return routeA.toString().contains(routeB.toString());
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReasonerRoute otherRoute = (ReasonerRoute) obj;
		if (this.hashCode() == otherRoute.hashCode())
			return true;
		else
			return false;
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * 克隆操作
	 * 
	 * @return 自身对象的一个副本
	 * @throws CloneNotSupportedException
	 */
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		// 先调用父类的克隆方法进行克隆操作
		ReasonerRoute route = (ReasonerRoute) super.clone();
		// 对于克隆后出的对象route,如果其成员routeList为null,则不能调用clone(),否则出空指针异常
		if (this.routeList != null)
			route.routeList = (List<ReasonerNode>) ((ArrayList<ReasonerNode>) this.routeList)
					.clone();
		return route;
	}
}
