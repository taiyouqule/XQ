package com.shenji.common.action;

import java.io.File;
import java.io.IOException;

import com.shenji.common.log.Log;
import com.shenji.common.util.IniEditor;
import com.shenji.common.util.PathUtil;

public class Configuration {
	static {
		init();
	}
	public static void load() {
	}
	public final static String configFile = "conf/common_conf.ini";
	public final static String system_config = "system_config";

	public static String TomcatVirtualPath;

	private static void init() {
		IniEditor editor = new IniEditor();
		String path;
		try {
			path = PathUtil.getWebInFAbsolutePath();
			if (new File(path + configFile).exists()) {
				editor.load(path + configFile);
				TomcatVirtualPath = editor.get(system_config,
						"TomcatVirtualPath");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(Configuration.class).error(e.getMessage(),e);
		}
	}
	
	public static void main(String[] str){
		try{
			int i=1/0;
		}catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(Configuration.class).error(e.getMessage(),e);
		}
	
	}
}
