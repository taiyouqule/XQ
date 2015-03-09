package com.shenji.onto.reasoner.server;

import java.util.Set;

import com.shenji.common.log.HtmlLog;
import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.search.FenciControl;
import com.shenji.search.exception.EngineException;


public class ReasonerTreeServer{
	private final static int MEMORY=0;
	private final static int NONMEMORY=1;
	private final static int MEMORY_USER=2;
	private ReasonerTreeAbsServer treeInter;
	private static ReasonerTreeServer instance=new ReasonerTreeServer();
	
	public void start(){
		Log.getLogger(this.getClass()).info("本体推理树服务启动！");
	}
	protected ReasonerTreeServer(){
		//后面type从配置读取
		int type=MEMORY_USER;	
		switch (type) {
		case MEMORY:
			try {
				this.treeInter=new ReasonerTreeMemoryNoAutoServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				instance=null;
			}
			break;
		case NONMEMORY:
			this.treeInter=new ReasonerTreeNonMemoryServer();
			break;
		case MEMORY_USER:
			try {
				this.treeInter=new ReasonerTreeMemoryAutoServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getLogger().error(e.getMessage(),e);
				e.printStackTrace();
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
				instance=null;
			}
			break;
		default:
			break;
		}				
	}
	
	public static ReasonerTreeServer getInstance() {
		return instance;
	}

	public void deleteFaqTree(String individualName){
		treeInter.deleteFaqTree(individualName);
	}
	
	public void addFaqTree(String individualName){
		treeInter.addFaqTree(individualName);
	}
	
	public void updateFaqTree(String individualName){
		treeInter.updateFaqTree(individualName);
	}
	
	public  Set<String>  filter(ReasonerTree tree){
		return treeInter.filter(tree);
	}

	public boolean reBuildFaqReasonerTree(){
		try {
			((ReasonerTreeMemoryAutoServer)treeInter).rebuildFaqsReasonerTrees();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return false;
		}
	}
	
	public ReasonerTree getBigFaqTree(){
		try {
			return ((ReasonerTreeMemoryAutoServer)treeInter).getFaqOntoTree();
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
	}
	
	public ReasonerTree[] getFaqReasonerTree(String individualName) {
		// TODO Auto-generated method stub
		return treeInter.getFaqReasonerTree(individualName);
	}

	
	public ReasonerTree[] getUserReasonerTree(String[] words) {
		// TODO Auto-generated method stub
		return treeInter.getUserReasonerTree(words);
	}
	
	public ReasonerTree[] getUserReasonerTree(String str) {
		// TODO Auto-generated method stub
		//这里有问题,需要改
		String[] words = null;
		try {
			words = new FenciControl().iKAnalysisWithPossibility(str);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		HtmlLog.info("iKAnalysisWithPossibility", new Object[]{str}, words,true);
		return treeInter.getUserReasonerTree(words);
	}
	
	public static void main(String[] strs){
		System.out.println(ReasonerTreeServer.getInstance().getUserReasonerTree("错误代码：05051错误信息：证书初始化失败！没有设备(0x82040051)")[0].toXmlString("UTF-8"));
		
	}

}
