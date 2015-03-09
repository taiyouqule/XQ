package com.shenji.search;

import com.shenji.common.log.Log;
import com.shenji.common.util.IniEditor;
import com.shenji.common.util.PathUtil;

public class Parameters {
	// 返回最大的结果数
	public static int maxResult = 200;
	// 更多标签前显示结果数
	public static int showResult = 20;
	// 最大显示字数
	public static int maxTestShow = 200;
	// 最大精准答案数，小于等于4为精准
	public static int maxAccurat = 4;
	public static float maxMatchWeight = 2;
	public static float myIkdictWeight = 3;
	public static float qaProportion = 3;
	// 权重(未启用)
	// private static double[] weight = { 0.5, 0.3, 0.2 };
	// 网页分割线
	public static double[] cutLine = { 0.95, 0.8, 0.7, 0.5, 0.3, 0 };

	public static void resetParameters() {
		// 这里是定义的初始值（经过参数调优）
		maxAccurat = 4;
		maxMatchWeight = 2;
		myIkdictWeight = 3;
		qaProportion = 3;
		maxResult = 200;
		maxTestShow = 200;
		showResult = 20;

	}

	static {
		loadConfig();
	}

	private static void loadConfig() {
		IniEditor config = new IniEditor();
		try {
			String path = PathUtil.getWebInFAbsolutePath();
			config.load(path + Configuration.configFile);
			String parameter_config = Configuration.parameter_config;
			maxResult = Integer.parseInt(config.get(parameter_config,
					"MaxResult"));
			maxTestShow = Integer.parseInt(config.get(parameter_config,
					"MaxTextShow"));
			showResult = Integer.parseInt(config.get(parameter_config,
					"showResult"));
			maxAccurat = Integer.parseInt(config.get(parameter_config,
					"maxAccurat"));

			maxMatchWeight = Float.parseFloat(config.get(parameter_config,
					"maxMatchWeight"));
			myIkdictWeight = Float.parseFloat(config.get(parameter_config,
					"myIkdictWeight"));
			qaProportion = Float.parseFloat(config.get(parameter_config,
					"qaProportion"));
			// 这里只启用FAQ，故只有weight暂时无效
			/*
			 * weight[0] =
			 * Double.parseDouble(config.get(Common.parameter_config, "Faq"));
			 * weight[1] =
			 * Double.parseDouble(config.get(Common.parameter_config, "Learn"));
			 * weight[2] =
			 * Double.parseDouble(config.get(Common.parameter_config, "Web"));
			 */
		} catch (Exception e) {
			Log.getLogger(Parameters.class).error(e.getMessage(),e);
		}
	}

	public static int setConfig(int maxResult, int maxTextShow,
			float maxMatchWeight, float myIkdictWeight, float qaProportion,
			int showResult, int maxAccurat) {
		if (maxResult == -1 && maxTextShow == -1 && maxMatchWeight == -1
				&& myIkdictWeight == -1 && qaProportion == -1
				&& showResult == -1 && maxAccurat == -1) {
			resetParameters();
			return saveConfig(maxResult, maxTextShow, maxMatchWeight,
					myIkdictWeight, qaProportion, showResult, maxAccurat);
		}
		if (maxResult < 0 || maxTextShow < 0 || maxMatchWeight < 1
				|| myIkdictWeight < 1 || qaProportion < 1 || showResult < 1
				|| maxAccurat < 1)
			return -2;
		else {
			int reFlag = saveConfig(maxResult, maxTextShow, maxMatchWeight,
					myIkdictWeight, qaProportion, showResult, maxAccurat);
			if (reFlag > 0) {
				loadConfig();
			}
			return reFlag;
		}
	}

	private static int saveConfig(int maxResult, int maxTextShow,
			float maxMatchWeight, float myIkdictWeight, float qaProportion,
			int showResult, int maxAccurat) {
		IniEditor config = new IniEditor();
		try {
			String path = PathUtil.getWebInFAbsolutePath();
			config.load(path + Configuration.configFile);
			config.set(Configuration.parameter_config, "MaxResult",
					Integer.toString(maxResult));
			config.set(Configuration.parameter_config, "MaxTextShow",
					Integer.toString(maxTextShow));
			config.set(Configuration.parameter_config, "maxMatchWeight",
					Float.toString(maxMatchWeight));
			config.set(Configuration.parameter_config, "myIkdictWeight",
					Float.toString(myIkdictWeight));
			config.set(Configuration.parameter_config, "qaProportion",
					Float.toString(qaProportion));
			config.set(Configuration.parameter_config, "showResult",
					Integer.toString(showResult));
			config.set(Configuration.parameter_config, "maxAccurat",
					Integer.toString(maxAccurat));
			config.save(path + Configuration.configFile);
			return 1;
		} catch (Exception e) {
			Log.getLogger(Parameters.class).error(e.getMessage(),e);
			return -1;
		}
	}

	/**
	 * 查看配置文件
	 * 
	 * @return 配置文件字符串
	 */
	public static String getConfig() {
		IniEditor config = new IniEditor();
		String result = "";
		try {
			String path = PathUtil.getWebInFAbsolutePath();
			config.load(path + Configuration.configFile);
			result += config.get(Configuration.parameter_config, "MaxResult")
					+ ";";
			result += config.get(Configuration.parameter_config, "MaxTextShow")
					+ ";";
			result += config.get(Configuration.parameter_config,
					"maxMatchWeight") + ";";
			result += config.get(Configuration.parameter_config,
					"myIkdictWeight") + ";";
			result += config
					.get(Configuration.parameter_config, "qaProportion") + ";";
			result += config.get(Configuration.parameter_config, "showResult")
					+ ";";
			result += config.get(Configuration.parameter_config, "maxAccurat");
		} catch (Exception e) {
			Log.getLogger(Parameters.class).error(e.getMessage(),e);
		}
		return result;
	}

}
