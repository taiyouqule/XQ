package com.shenji.webservices.port;

import com.shenji.webservices.action.ActionServer;

import edu.stanford.smi.protege.util.Log;

public class CommonServer {
	public final static Object CommonServer_LOCK = new Object();// 全局锁
	public int Login(String userName, String passWord) {
		// TODO Auto-generated method stub
		return new ActionServer().login(userName, passWord);
	}
	
	public int reBuildAllIndex(String passWord){
		if(!passWord.equals("sictsj@sict123")){
			return -2;
		}
		synchronized(CommonServer_LOCK){
			boolean b=new Search().rebuildIndex("0");
			b=b&new OntoReasoning().reBuildFaqReasonerTree();
			Log.getLogger(this.getClass()).info("重建所有索引与本体树:"+b);
			if(b)
				return 1;
			else
				return -1;
		}
	}
	
	

}
