package com.shenji.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.shenji.common.log.Log;
import com.shenji.search.old.LogHtmlWebServer;

public class FileUtil {
	public static final int FILE = 0;
	public static final int DIR = 1;
	public static final String separator = "/";

	/**
	 * 判断文件是否存在
	 * 
	 * @param pathname
	 * @return true 存在 false不存在
	 */
	public static boolean isExist(String pathname) {
		File file = new File(pathname);
		if (file.exists()) {
			return true;
		} else
			return false;
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		boolean b = true;
		if (file.exists() && file.isFile() && file.canWrite()) {
			b = file.delete();

		}
		return b;
	}

	public static int createFile(File file) {
		// File file=new File(pathname);
		if (!file.exists()) {
			try {
				if (file.createNewFile())
					return 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(FileUtil.class).error(e.getMessage(),e);
				return -1;
			}
			return -1;
		} else
			return -1;

	}
	

	public static void copyFile(String oldPath, String oldFileName,
			String newPath,String newFileName) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int byteread = 0;
			File oldfile = new File(oldPath + File.separator + oldFileName);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldfile); // 读入原文件
				fs = new FileOutputStream(newPath + File.separator
						+ newFileName);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			Log.getLogger(LogHtmlWebServer.class).error(e.getMessage(),e);
		} finally {

			try {
				if (inStream != null)
					inStream.close();
				if (fs != null)
					fs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(LogHtmlWebServer.class).error(e.getMessage(),e);
			}

		}
	}

	public static ArrayList<String> read(String pathname) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		File file = new File(pathname);
		try {
			if (!file.exists())
				return null;
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			while ((str = bufferedReader.readLine()) != null) {
				arrayList.add(str);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FileUtil.class).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FileUtil.class).error(e.getMessage(),e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
				if (inputStreamReader != null)
					inputStreamReader.close();
				if (fileInputStream != null)
					fileInputStream.close();
				file = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(FileUtil.class).error(e.getMessage(),e);
			}
		}
		return arrayList;
	}

	public static int write(File file, List<Object> arrayList) {
		if (arrayList.size() == 0) {
			return 0;
		}
		BufferedWriter bufferedWriter = null;
		OutputStreamWriter outputStreamWriter = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream,
					"utf-8");
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			for (Object obj : arrayList) {

				bufferedWriter.write(obj.toString());
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(FileUtil.class).error(e.getMessage(),e);
			return -1;
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
				if (outputStreamWriter != null)
					outputStreamWriter.close();
				if (fileOutputStream != null)
					fileOutputStream.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(FileUtil.class).error(e.getMessage(),e);
			}
		}
		return 1;
	}

	public static String getFormattedDirectory(String path) {
		String formattedPath = path;
		formattedPath = formattedPath.replace("//", "/");
		formattedPath = formattedPath.replace("\\\\", "/");
		formattedPath = formattedPath.replace("\\", "/");
		if (formattedPath.endsWith("/") || formattedPath.endsWith("\\"))
			formattedPath = formattedPath.substring(0,
					formattedPath.length() - 1);
		if (formattedPath.endsWith("\\\\") || formattedPath.endsWith("//"))
			formattedPath = formattedPath.substring(0,
					formattedPath.length() - 2);
		return formattedPath;
	}

	public static String[] getFileList(String path, int type) {
		File file = new File(path);
		if (!file.exists())
			return null;
		File[] files = null;
		if (type == FILE) {
			files = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.endsWith(".owl");
				}
			});
		} else if (type == DIR) {
			files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					// TODO Auto-generated method stub
					return file.isDirectory();
				}
			});
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			if (files == null)
				return null;
			for (File fileStr : files) {
				if (type == FILE)
					arrayList.add(fileStr.getPath());
				else if (type == DIR)
					arrayList.add(fileStr.getName());
			}
			return (String[]) arrayList.toArray(new String[arrayList.size()]);
		} finally {
			if (arrayList != null)
				arrayList.clear();
		}
	}

	public static int moveFile(String oldFilePath, String newFilePath) {
		File oldFile = new File(oldFilePath);
		if (!oldFile.exists())
			return -1;
		File newFile = new File(newFilePath);
		if (!newFile.getParentFile().exists())
			return -2;
		oldFile.renameTo(newFile);
		oldFile.delete();
		return 1;
	}

	public static int createDirectory(String path) {
		File file = new File(path);
		if (file.exists() && file.isDirectory())
			return 0;
		else if (!file.exists() || file.isFile()) {
			file.mkdir();
			return 1;
		}
		return -1;
	}

	public static int renameDirectory(String path, String newName) {
		File file = new File(path);
		String newFilePath = file.getParentFile().getPath() + separator
				+ newName;
		File newFile = new File(newFilePath);
		if (file.exists() && file.isDirectory()) {
			if (file.renameTo(newFile) == true)
				return 1;
			else
				return -1;
		} else
			return 0;
	}

	// 删除文件夹
	// param path 文件夹完整绝对路径

	public static int deleteDirectory(String path) throws Exception {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory())
			return -1;
		delAllFile(path); // 删除完里面所有内容
		String filePath = path;
		filePath = filePath.toString();
		File myFilePath = new File(filePath);
		myFilePath.delete(); // 删除空文件夹
		return 1;

	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	private static boolean delAllFile(String path) throws Exception {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)
					|| path.endsWith(FileUtil.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + FileUtil.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + separator + tempList[i]);// 先删除文件夹里面的文件
				deleteDirectory(path + separator + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public static void main(String[] str) throws Exception {
		deleteDirectory("D:\\dsf");
	}
}
