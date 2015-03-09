package com.shenji.search.old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.shenji.common.log.HtmlLog;
import com.shenji.common.log.Log;
import com.shenji.common.util.PathUtil;

public class LogHtmlWebServer {

	private final static String logName = "log";
	static {
		File file = new File(PathUtil.getWebInFAbsolutePath() + logName);
		if (!file.exists())
			file.mkdirs();
	}

	public int copyHtmlFile(String oldFileName) {
		Log.getLogger().debug(HtmlLog.getRootPath());
		Log.getLogger().debug(oldFileName);
		Log.getLogger().debug(PathUtil.getWebInFAbsolutePath() + logName);
		File file = new File(PathUtil.getWebInFAbsolutePath() + logName
				+ File.separator + oldFileName);
		Log.getLogger().debug(file.exists() + ":" + file.getAbsolutePath());
		if (file.exists())
			return -1;
		copyFile(HtmlLog.getRootPath(), oldFileName,
				PathUtil.getWebInFAbsolutePath() + logName);
		return 1;
	}

	public static String[] getFileList() {
		String ahread = "<a href=\"";
		String tailStart = "\" onclick=\"loadFile('";
		String tailEnd = "')\">";
		String path = HtmlLog.getRootPath();
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
			return null;
		List<String> list = new ArrayList<String>();
		File[] files = dir.listFiles();
		for (File f : files) {
			String fileName;
			try {
				fileName = "./" + logName + "/" + f.getName();
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}
			String s = ahread + fileName + tailStart + f.getName() + tailEnd
					+ f.getName() + "<br/>";
			list.add(s);
			Log.getLogger(LogHtmlWebServer.class).debug("目录:" + s);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public static void copyFile(String oldPath, String oldFileName,
			String newPath) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int byteread = 0;
			File oldfile = new File(oldPath + File.separator + oldFileName);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldfile); // 读入原文件
				fs = new FileOutputStream(newPath + File.separator
						+ oldFileName);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			Log.getLogger(LogHtmlWebServer.class).error(e);
		} finally {

			try {
				if (inStream != null)
					inStream.close();
				if (fs != null)
					fs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(LogHtmlWebServer.class).error(e);
			}

		}
	}

	public static void main(String[] str) {
		// getFileList();
		new LogHtmlWebServer().copyHtmlFile("log_.HTM");
	}
}
