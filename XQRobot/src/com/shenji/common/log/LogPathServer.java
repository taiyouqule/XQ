package com.shenji.common.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.shenji.common.util.ConfigurantionUtil;
import com.shenji.common.util.FileUtil;
import com.shenji.common.util.IniEditor;
import com.shenji.common.util.PathUtil;
import com.shenji.onto.Configuration;

public class LogPathServer {
	public static void start() {
		Log.getLogger(LogPathServer.class).info("日志web查看服务启动!");
	}

	static {
		try {
			createLogWebXml();
			openLogFileList();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(LogPathServer.class).error(e.getMessage(),e);
		}
	}

	private static void openLogFileList() throws FileNotFoundException {
		File oldFile = new File(PathUtil.getWebInFAbsolutePath()
				+ "conf/log.xml");
		if (!oldFile.exists())
			throw new FileNotFoundException("log.xml文件不存在!");
		if (new File(com.shenji.common.action.Configuration.TomcatVirtualPath
				+ File.separator + "log.xml").exists())
			return;
		FileUtil.copyFile(PathUtil.getWebInFAbsolutePath() + "conf", "log.xml",
				com.shenji.common.action.Configuration.TomcatVirtualPath,
				"log.xml");
	}

	private static void createLogWebXml() throws FileNotFoundException {
		String path = getRootPath() + File.separator + "WEB-INF";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			File webFile = new File(path + File.separator + "web.xml");
			if (!webFile.exists()) {
				File oldFile = new File(PathUtil.getWebInFAbsolutePath()
						+ "conf/logWebConf.xml");
				if (!oldFile.exists())
					throw new FileNotFoundException("logWebConf.xml文件不存在!");
				FileUtil.copyFile(PathUtil.getWebInFAbsolutePath() + "conf",
						"logWebConf.xml", path, "web.xml");
			}
		}
	}

	private static String getRootPath() {
		String logConf = "conf/log4j.properties";
		ConfigurantionUtil util = new ConfigurantionUtil(new File(
				PathUtil.getWebInFAbsolutePath() + logConf));
		String rootPath = util.getValue("log4j.path");
		return rootPath;
	}

	public static void main(String[] str) {
		System.out.println("");
	}
}
