package com.shenji.onto.reasoner.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.shenji.common.util.MD5Util;
import com.shenji.onto.reasoner.data.UserReasonerTree;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;


public class InteractServer {
	
	private static InteractServer instance;
	public static InteractServer getInstance(){
		return instance;
	}

	private InteractServer() {
		//map = new HashMap<String, InteractContext>();
	}

	private String crateUId(String question, Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
		String time = format.format(date);
		String md5 = MD5Util.md5(question);
		String uid = time + md5;
		return uid;
	}

	public String firstQuery(String question, String[] words) {
		UserReasonerTree[] trees = (UserReasonerTree[]) ReasonerTreeServer
				.getInstance().getUserReasonerTree(words);
		Date date = new Date();
		String uid = this.crateUId(question, date);
		InteractContext context = new InteractContext(uid, date, trees);
		//this.map.put(uid, context);
		return uid;
	}
}
