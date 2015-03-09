package com.shenji.onto.reasoner.data;

public class ReasonerNodeData implements Cloneable{
	private String nodeName = null;
	public void clear(){
		this.nodeName=null;
	}
	public ReasonerNodeData(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}
	public String toString(){
		return this.nodeName;
	}
	
	public Object clone() throws CloneNotSupportedException {
		// 直接调用父类的clone()方法,返回克隆副本,这里不需要深层拷贝	
		return 	super.clone();
	}
}
