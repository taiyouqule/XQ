package com.shenji.common.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public abstract class CglibProxy implements MethodInterceptor {
	public static final String startSeparator = "#";

	public static Object createProxy(Object target, CglibProxy proxy) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());// 设置代理目标
		enhancer.setCallback(proxy);// 设置回调
		enhancer.setClassLoader(target.getClass().getClassLoader());
		return enhancer.create();
	}

	/**
	 * 在代理实例上处理方法调用并返回结果
	 * 
	 * @param proxy
	 *            代理类
	 * @param method
	 *            被代理的方法
	 * @param params
	 *            该方法的参数数组
	 * @param methodProxy
	 */
	@Override
	public Object intercept(Object proxy, Method method, Object[] params,
			MethodProxy methodProxy) throws Throwable {
		// TODO Auto-generated method stub
		Object result = null;
		// 调用之前
		doBefore(proxy, method, params, result);
		// 调用原始对象的方法
		result = methodProxy.invokeSuper(proxy, params);
		// 调用之后
		doAfter(proxy, method, params, result);
		return result;
	}

	public abstract void doBefore(Object proxy, Method method, Object[] params,
			Object result);

	public abstract void doAfter(Object proxy, Method method, Object[] params,
			Object result);

	public static void getParams(Object[] params, StringBuilder sb,
			String separator) {
		if (separator == null)
			separator = "";
		for (Object obj : params) {
			if (obj.getClass().isArray()) {
				int count = 0;
				for (Object o : (Object[]) obj) {
					sb.append(startSeparator + o.getClass().getSimpleName()
							+ "[" + (count++) + "]" + ":" + o.toString()
							+ separator);
				}
			} else {
				sb.append(startSeparator + obj.getClass().getSimpleName() + ":"
						+ obj.toString() + separator);
			}
		}
	}

	public static void getResult(Object result, StringBuilder sb,
			String separator) {
		if (separator == null)
			separator = "";
		if (result.getClass().isArray()) {
			int count = 0;
			for (Object o : (Object[]) result) {
				sb.append(startSeparator + o.getClass().getSimpleName() + "["
						+ (count++) + "]" + ":" + o.toString() + separator);
			}
		} else {
			sb.append(startSeparator + result.getClass().getSimpleName() + ":"
					+ result.toString() + separator);
		}
	}

}
