package com.shenji.onto;

import java.util.Timer;
import java.util.TimerTask;

import com.shenji.common.log.Log;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.reasoner.server.ReasonerServer;

public class OntModelAutoRefresh {
	private static OntModelAutoRefresh instance = new OntModelAutoRefresh();
	private Timer timer;
	private boolean isFrishInit = false;

	private OntModelAutoRefresh() {
		timer = new Timer();
		isFrishInit = true;
	}

	public static OntModelAutoRefresh getInstance() {
		return instance;
	}

	public void start() {
		if (!isFrishInit)
			return;
		long delay = 3000;
		// 四小时定时刷新
		long period = 14400000;
		isFrishInit = false;
		timer.schedule(getInstance().new myTimerTask(), delay, period);
	}

	public class myTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 随便掉一下两个接口，确保连接不断开
			ReasonerServer.getInstance().getOnlyOntMode()
					.getOntClass("http://www.w3.org/2002/07/owl#Thing");
			/*FaqMapServices.getInstance().getOntModel()
					.getOntClass("http://www.w3.org/2002/07/owl#Thing");*/
			Log.getLogger(this.getClass()).info("【重要】定时调用，确保内存模型jena不断开都在内存中！");

		}
	}
}
