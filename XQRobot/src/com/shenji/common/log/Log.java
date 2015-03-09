package com.shenji.common.log;

import java.io.FileNotFoundException;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.shenji.common.util.PathUtil;

/**
 * @author sj 单例静态日志类
 */
public class Log {
	private static Logger logger;
	private static boolean debug = true;
	private static boolean consoleShow = true;
	public static final String logFile = "conf/log4j.properties";

	static {
		String path = PathUtil.getWebInFAbsolutePath();
		logger = Logger.getLogger("[XQRoot]");
		PropertyConfigurator.configure(path + logFile);
	}

	private Log() {
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Logger getLogger(Class<? extends Object> clazz) {
		try {
			String name = classNameFormat(clazz);
			return Logger.getLogger("[" + name + " 模块]");
		} catch (Exception e) {
			// TODO: handle exception
			return getLogger();
		}
	}

	private static String classNameFormat(Class<? extends Object> clazz)
			throws PatternSyntaxException {
		String name = null;
		try {
			name = Log.class.getName().split("\\.")[2];
		} catch (PatternSyntaxException e) {
			// TODO: handle exception
			throw e;
		}
		return name;
	}

	public static void main(String[] str){
		try{
			int i=10/0;
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(Log.class).error(Exception.class,e);
		}
		
	}

}
