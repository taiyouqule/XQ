package com.shenji.search;

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

	public final static String configFile = "conf/search_conf.ini";
	public final static String parameter_config = "parameter_config";
	public final static String system_config = "system_config";
	public final static String dic_config = "dic_config";
	//public final static String database_config = "database_config";

	public static String IP;
	public static String notesName;

	public static String notesPath;
	public static String indexPath;
	public static String faqFolder;
	public static String learnFolder;
	public static String webFolder;
	public static String synFolder;
	public static String mySynFolder;
	public static String relatedWordFolder;

	public static String myDict;
	public static String businessDict;
	public static String synonmDict;
	public static String mySynonmDict;

/*	public static String dbIP;
	public static String dbName;
	public static String user = "root";
	public static String password = "";*/

	public static void init() {
		IniEditor editor = new IniEditor();
		String path;
		try {
			path = PathUtil.getWebInFAbsolutePath();
			if (new File(path + configFile).exists()) {
				editor.load(path + configFile);
				IP = editor.get(system_config, "IP");
				notesName = editor.get(system_config, "notesName");
				notesPath = editor.get(system_config, "notesPath");
				// notesPath=notesPath.replace("\\\\", "\\");
				indexPath = editor.get(system_config, "indexPath");
				// indexPath=indexPath.replace("\\\\", "\\");
				faqFolder = editor.get(system_config, "faqFolder");
				learnFolder = editor.get(system_config, "learnFolder");
				webFolder = editor.get(system_config, "webFolder");
				synFolder = editor.get(system_config, "synFolder");
				mySynFolder = editor.get(system_config, "mySynFolder");
				relatedWordFolder = editor.get(system_config,
						"relatedWordFolder");

				myDict = editor.get(dic_config, "myDict");
				businessDict = editor.get(dic_config, "businessDict");
				synonmDict = editor.get(dic_config, "synonmDict");
				mySynonmDict = editor.get(dic_config, "MySynonmDict");

			/*	dbIP = editor.get(database_config, "dbIP");
				dbName = editor.get(database_config, "dbName");
				user = editor.get(database_config, "user");
				password = editor.get(database_config, "password");*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(Configuration.class).error(e.getMessage(),e);
		}
	}

	/*
	 * public static String[] searchDir = {indexPath+"/"+faqFolder,
	 * indexPath+"/"+learnFolder, indexPath+"/"+webFolder};
	 */
	public static String[] searchDir = { indexPath + "/" + faqFolder };
	public static String webPath = "http://" + IP + "/" + notesName;
	public static String faqwebPath = "http://" + IP + "/axis2/test/faq";

}
