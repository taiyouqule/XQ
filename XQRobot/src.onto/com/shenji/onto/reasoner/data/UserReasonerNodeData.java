package com.shenji.onto.reasoner.data;

public class UserReasonerNodeData extends ReasonerNodeData {
	private boolean isAbstract = true;
	private double possibility=0;

	
	public UserReasonerNodeData(String nodeName) {
		super(nodeName);
		// TODO Auto-generated constructor stub
	}
	public void clear(){
		super.clear();
	}
	
	public UserReasonerNodeData(String nodeName, boolean isAbstract) {
		super(nodeName);
		this.isAbstract = isAbstract;
		// TODO Auto-generated constructor stub
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public double getPossibility() {
		return possibility;
	}
	public void setPossibility(double possibility) {
		this.possibility = possibility;
	}
	
	//暂时不重写了
	/*public String toString(){
		return super.toString()+"&"+this.isAbstract;
	}*/

}
