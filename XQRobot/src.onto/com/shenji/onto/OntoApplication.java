package com.shenji.onto;

import com.shenji.common.log.Log;
import com.shenji.common.proxy.CglibProxy;
import com.shenji.common.proxy.TimeCalculateProxy;
import com.shenji.common.proxy.TimeCalculateProxy.Model;
import com.shenji.onto.reasoner.server.ReasonerServer;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;

public class OntoApplication {
	public static boolean STATE = false;
	static {
		Log.getLogger(OntoApplication.class).info(
				"------------启动程序正在加载------------");
		/*
		 * ((FaqMapServices)
		 * CglibProxy.createProxy(FaqMapServices.getInstance(), new
		 * TimeCalculateProxy(Model.INFO, "FAQ映射模型"))).start();
		 */
		/*((ReasonerServer) CglibProxy.createProxy(ReasonerServer.getInstance(),
				new TimeCalculateProxy(Model.INFO, "本体推理jenam模型"))).start();
		((ReasonerTreeServer) CglibProxy.createProxy(ReasonerTreeServer
				.getInstance(), new TimeCalculateProxy(Model.INFO, "本体推理树模型")))
				.start();
		ReasonerTreeServer.getInstance().start();
		OntModelAutoRefresh.getInstance().start();*/

		Log.getLogger(OntoApplication.class).info(
				"------------启动程序正在加载完成------------");
	}

	public static void load() {
	}
}
