package com.shenji.common.proxy;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.shenji.common.log.Log;

public class TimeCalculateProxy extends CglibProxy {
	private long startTime;
	private long endTime;
	private String message = null;
	private Model model = Model.DEBUG;

	public enum Model {
		DEBUG, INFO
	}

	public TimeCalculateProxy() {
		// TODO Auto-generated constructor stub
		super();
	}

	public TimeCalculateProxy(String message) {
		// TODO Auto-generated constructor stub
		super();
		this.message = message;
	}

	public TimeCalculateProxy(Model model, String message) {
		// TODO Auto-generated constructor stub
		super();
		this.message = message;
		this.model = model;
	}

	@Override
	public void doBefore(Object proxy, Method method, Object[] params,
			Object result) {
		// TODO Auto-generated method stub
		Log.getLogger(this.getClass()).debug(
				"this doBefore " + method.getName() + " Method is runing!");
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void doAfter(Object proxy, Method method, Object[] params,
			Object result) {
		// TODO Auto-generated method stub
		this.endTime = System.currentTimeMillis();
		Log.getLogger(this.getClass()).debug(
				"this doAfter " + method.getName() + " Method is runing!");
		long time = (this.endTime - this.startTime);
		if (model.equals(Model.DEBUG))
			if (message != null)
				Log.getLogger().debug(
						"【Time】" + message + " 共耗时:" + time + "ms");
			else
				Log.getLogger().debug(
						"【Time】" + proxy.getClass() + "类--" + method.toString()
								+ "方法" + "，共耗时: " + time + "ms");
		else {
			if (message != null)
				Log.getLogger()
						.info("【Time】" + message + " 共耗时:" + time + "ms");
			else
				Log.getLogger().info(
						"【Time】" + proxy.getClass() + "类--" + method.toString()
								+ "方法" + "，共耗时: " + time + "ms");
		}

	}
}
