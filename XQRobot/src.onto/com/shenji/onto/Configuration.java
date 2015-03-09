package com.shenji.onto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUtil;
import com.shenji.common.util.IniEditor;
import com.shenji.common.util.PathUtil;

/**
 * @author zhq
 *
 */
/**
 * @author zhq
 * 
 */
public class Configuration {
	public static void load() {
	}
	public static final String configFile = "conf/onto_conf.ini";
	public static final String systemConfig = "system_config";
	public static boolean ServicesState = true;
	/**
	 * 保留的本体概念符号集（本体概念中不容许出现）
	 */
	public final static String[] keepSymbolSet = { "#", "<", ">", "(", ")",
			"!", ":" };
	/**
	 * 支持的本体概念符号集
	 */
	public final static String[] supportSymbolSet = { "_", "." };
	/**
	 * 消息分隔符
	 */
	public final static String messageSeparator = "#";
	/**
	 * XML报文编码
	 */
	public final static String XML_CODE = "GBK";
	/**
	 * 本体编码
	 */
	public final static String ONTO_CODE = "UTF-8";
	/**
	 * 本体配置文件名
	 */
	// private final static String ontoSystemConfig="ontoSystem.ini";
	// 配置文件读取默认值
	/**
	 * 缓存文件最小自动保存时间（所有接口的调用间隔）
	 */
	public static int TEMP_TIME = 2;
	/**
	 * 词性集合 文件名
	 */
	public static String SeechTaggingSetFileName = "speechTaggingSet.properties";
	/**
	 * 哈工大LTP账户名
	 */
	public static String LTPToken = "pass6720632@163.com:JLySgtRV";
	/**
	 * FAQ服务地址
	 */
	/*
	 * public static String FAQ_URL="http://172.30.3.56:8070";
	 */
	/**
	 * 文件缓存地址
	 */
	public static String TempPath = "D:/ontologyServices/Temp";
	/**
	 * 可访问本体文件的存储文件夹名，详见ontoPath
	 */
	public static String OntoDirName = "ontology";
	/**
	 * 本体服务地址（本机地址）
	 */
	public static String Onto_URL = "http://172.30.3.56:8080";

	/**
	 * 本服务采用的本体名(未来可扩充到数组)
	 */
	public static String Onto_Name = "KnowLedge";

	/**
	 * 本体文件的存储物理地址（默认为tomcat下/webapps/$OntoDirName）
	 */
	public static String ontoPath = null;

	/**
	 * 某些接口的密码。防止错误操作！
	 */
	public static String testPassWord = "shenji";

	public static String FAQURL = com.shenji.search.Configuration.IP
			+ File.separator + com.shenji.search.Configuration.notesName
			+ File.separator + com.shenji.search.Configuration.faqFolder;

	/**
	 * FAQ映射常量类
	 * 
	 * @author zhq
	 */
	public class FaqMappingCommon {
		/**
		 * FAQ数据库名
		 */
		public static final String ontoName = "faq";
		/**
		 * FAQ实例名前缀（每条FAQ为一条实例，由于FAQ文件名为MD5加密，开头可能为数字，不符合本体概念非数字开头，故FAQ实例名=FAQ前缀
		 * +文件名）
		 */
		public static final String INDIVIDUAL_HEAD = "faq_";
		/**
		 * URL 地址
		 */
		public static final String Data_URL = "url";
		/**
		 * Q 问
		 */
		public static final String Data_Q = "q";
		/**
		 * A 答
		 */
		public static final String Data_A = "a";
	}

	static {
		initConfig();
	}

	/*
	 * static{ if(initConfig()) if(!OntoApplication.staticLoad()){
	 * ServicesState=false; } else ServicesState=false; }
	 */
	/**
	 * 初始化配置文件
	 */
	private static boolean initConfig() {
		IniEditor config = new IniEditor();
		try {
			String systemPath = PathUtil.getWebInFAbsolutePath();
			String ontoConfigPath = systemPath + FileUtil.separator
					+ configFile;
			config.load(ontoConfigPath);
			TEMP_TIME = Integer.parseInt(config.get(systemConfig, "TEMP_TIME"));
			SeechTaggingSetFileName = config.get(systemConfig,
					"SeechTaggingSetFileName");
			LTPToken = config.get(systemConfig, "LTPToken");
			TempPath = config.get(systemConfig, "TempPath");
			OntoDirName = config.get(systemConfig, "OntoDirName");
			Onto_URL = config.get(systemConfig, "Onto_URL");
			Onto_Name = config.get(systemConfig, "Onto_Name");
			createRootPath(TempPath);
			File file = new File(systemPath);
			ontoPath = file.getPath().split("webapps")[0] + "webapps"
					+ File.separator + OntoDirName;
			createRootPath(ontoPath);
			Log.getLogger(Configuration.class).info("[本体模块配置文件读取结束]");
			return true;
		} catch (FileNotFoundException e) {
			Log.getLogger(Configuration.class).error("配置文件不存在", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(Configuration.class).error("读配置文件异常", e);
		}
		return false;
	}

	/**
	 * 创建根目录
	 * 
	 * @param path
	 *            目录
	 */
	private static void createRootPath(String path) {
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}

	/**
	 * 得到本体可访问网络地址
	 * 
	 * @return
	 */
	public static String getOntoBaseURL() {
		return Onto_URL + "/" + OntoDirName;
	}

	/**
	 * 空方法，测试的时候方便加载该类
	 */
	public static void start() {
	}

}
