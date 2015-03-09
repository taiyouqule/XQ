package com.shenji.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class PathUtil {
	/**
	 * 
	 * @return Web-Inf目录的绝对路径
	 */
	public static String getWebInFAbsolutePath() {
		String path = null;
		String folderPath;
		try {
			folderPath = PathUtil.class.getClassLoader().getResource("")
					.toURI().getPath();
		} catch (URISyntaxException e) {
			folderPath = PathUtil.class.getProtectionDomain().getCodeSource()
					.getLocation().getPath();
			try {
				folderPath = URLDecoder.decode(path, "utf-8");
			} catch (UnsupportedEncodingException ex) {
				folderPath = folderPath.replaceAll("%20", " ");
			}
		}
		if (folderPath.indexOf("WEB-INF") > 0) {
			path = folderPath.substring(0,
					folderPath.indexOf("classes"));
		}
		return path;
	}
	
	/**
	 * 
	 * @return WebRoot目录的绝对路径
	 */
	public static String getWebRootAbsolutePath() {
		String path = null;
		String folderPath;
		try {
			folderPath = PathUtil.class.getClassLoader().getResource("")
					.toURI().getPath();
		} catch (URISyntaxException e) {
			folderPath = PathUtil.class.getProtectionDomain().getCodeSource()
					.getLocation().getPath();
			try {
				folderPath = URLDecoder.decode(path, "utf-8");
			} catch (UnsupportedEncodingException ex) {
				folderPath = folderPath.replaceAll("%20", " ");
			}
		}
		if (folderPath.indexOf("WEB-INF") > 0) {
			path = folderPath.substring(0,
					folderPath.indexOf("WEB-INF/classes"));
		}
		return path;
	}

	public static void main(String[] str) throws URISyntaxException {
		System.out.println(PathUtil.getWebInFAbsolutePath());
	}
}
