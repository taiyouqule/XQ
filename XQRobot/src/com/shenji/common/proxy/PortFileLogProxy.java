/**
 * 
 */
package com.shenji.common.proxy;

import java.lang.reflect.Method;

import com.shenji.common.log.HtmlLog;
import com.shenji.common.log.Log;
import com.shenji.common.util.MD5Util;
import com.shenji.search.old.LogHtmlWebServer;

/**
 * @author zhq
 * 
 */
public class PortFileLogProxy extends CglibProxy {

	public PortFileLogProxy() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	@Override
	public void doBefore(Object proxy, Method method, Object[] params,
			Object result) {
		// TODO Auto-generated method stub
		Log.getLogger(this.getClass()).debug(
				"this doBefore " + method.getName() + " Method is runing!");
	}

	
	@Override
	public void doAfter(Object proxy, Method method, Object[] params,
			Object result) {
		HtmlLog.info(method, params, result);
		Log.getLogger(this.getClass()).debug(
				"this doAfter " + method.getName() + " Method is runing!");
	}


}
