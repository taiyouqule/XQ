package com.shenji.common.log;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.shenji.common.proxy.CglibProxy;
import com.shenji.common.util.ConfigurantionUtil;
import com.shenji.common.util.PathUtil;

/**
 * @author sj 单例静态日志类
 */
public class HtmlLog {
	private static Logger logger;

	static {
		logger = Logger.getLogger("[操作记录Html]");
		logger.removeAllAppenders();
		// 不能低于debug
		logger.setLevel(Level.DEBUG);
		logger.setAdditivity(true);
		FileAppender errAppender = getFileAppender();
		logger.addAppender(errAppender);
	}

	private HtmlLog() {
	}

	public static String getRootPath(){
		String logConf="conf/log4j.properties";
		ConfigurantionUtil util=new ConfigurantionUtil(new File(PathUtil.getWebInFAbsolutePath()+logConf));
		String rootPath=util.getValue("log4j.portLog.path");
		return rootPath;
	}
	
	public static String getFilePath(){
		String logConf="conf/log4j.properties";
		ConfigurantionUtil util=new ConfigurantionUtil(new File(PathUtil.getWebInFAbsolutePath()+logConf));
		String filePath=util.getValue("log4j.portLog.file");
		return filePath;
	}
	
	

	private static FileAppender getFileAppender() {
		DailyRollingFileAppender appender = new DailyRollingFileAppender();
		HTMLLayout layout = new CustomHtmlLayout();
		// log的输出形式
		// String conversionPattern = "[%d{yyyy-MM-dd HH:mm:ss}] %p [%t] %m%n";
		// layout.setTitle()tConversionPattern(conversionPattern);
		appender.setLayout(layout);
		// log输出路径
		// 这里使用了环境变量[catalina.home]，只有在tomcat环境下才可以取到
		//String path = PathUtil.getWebRootAbsolutePath() + logFileName;
		File file = new File(getRootPath());
		if (!file.exists())
			file.mkdirs();
		appender.setFile(getFilePath());
		// log的文字码
		appender.setEncoding("UTF-8");
		// true:在已存在log文件后面追加 false:新log覆盖以前的log
		appender.setAppend(true);
		appender.setDatePattern("yyyy-MM-dd'.html'");
		// 适用当前配置
		appender.activateOptions();
		return appender;
	}

	public static void info(Object method, Object[] params, Object result) {
		info(method, params, result, false);
	}

	public static void info(Object method, Object[] params, Object result,
			boolean isNoFormat) {
		StringBuilder sb = new StringBuilder();
		String separator = "<br/>";
		String className = method.toString();
		sb.append("<table border=\"1\" cellpadding=\"6\" width=\"100%\" class=\""
				+ className + "\"" + ">");
		sb.append("<tr>");
		sb.append("<td width=\"60\">" + "方法名：" + "</td>");
		sb.append("<td class=\"method\">");
		if (isNoFormat) {
			// 标记转换一下
			separator = "\r\n";
			sb.append("<xmp>");
		}
		sb.append(method.toString());
		if (isNoFormat)
			sb.append("</xmp>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td width=\"60\">" + "参数：" + "</td>");
		sb.append("<td class=\"params\">");
		if (isNoFormat)
			sb.append("<xmp>");
		CglibProxy.getParams(params, sb, separator);
		if (isNoFormat)
			sb.append("</xmp>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td width=\"60\">" + "返回值：" + "</td>");
		sb.append("<td class=\"result\">");
		if (isNoFormat)
			sb.append("<xmp>");
		CglibProxy.getResult(result, sb, separator);
		if (isNoFormat)
			sb.append("</xmp>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		logger.debug(sb.toString());
	}

}
