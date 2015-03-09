package com.shenji.search.dic;

import java.io.File;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.search.Configuration;

public class IndexServer {
	public static boolean deleteIndexDir(String indexDirName) {
		File file = new File(Configuration.indexPath + "/"
				+ indexDirName);
		if (file.exists()) {
			FileUse.deleteDirectory(Configuration.indexPath + "/"
					+ indexDirName);
			if (!file.exists()) {
				Log.getLogger(IndexServer.class).info("删除："+indexDirName+"文件夹成功！");
			} else {
				Log.getLogger(IndexServer.class).info("删除："+indexDirName+"文件夹失败！");
				return false;
			}
		}

		if (!file.exists()) {
			boolean b = createFolder(file);
			Log.getLogger(IndexServer.class).info("创建："+indexDirName+"文件夹成功！");
			if (b == false)
				return false;
		}
		return true;
	}
	
	private static boolean createFolder(File file) {
		if (!file.exists()) {
			file.mkdirs();
			return true;
		} else
			return false;
	}
}
