package com.shenji.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shenji.common.log.Log;

/**
 * @author sj
 * 
 */
public class FileUse {
	public static int EQUALS_TYPE = 1;
	public static int CONTAINS_TYPE = 2;

	public static ArrayList<String> modify(String path, String fileName,
			String oldStr, String newStr, int type) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			if (fileName == null) {
				fileInputStream = new FileInputStream(path);
			} else
				fileInputStream = new FileInputStream(path + fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			while ((str = bufferedReader.readLine()) != null) {
				if (type == EQUALS_TYPE) {
					str = str.trim();
					if (str.equals(oldStr))
						str = newStr.toLowerCase();
					arrayList.add(str);
				} else if (type == CONTAINS_TYPE) {
					if (str.contains(oldStr))
						str = newStr.toLowerCase();
					arrayList.add(str);
				}
			}
		} catch (Exception e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return arrayList;
	}

	public static ArrayList<String> add(File file, String string)
			throws FileNotFoundException {
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		if (file == null || !file.exists())
			throw new FileNotFoundException("文件不存在");
		try {
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			while ((str = bufferedReader.readLine()) != null) {
				arrayList.add(str);
			}
			arrayList.add(string);
		} catch (Exception e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return arrayList;
	}

	public static ArrayList<String> delete(String path, String fileName,
			String[] strings, int type) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			if (fileName == null) {
				fileInputStream = new FileInputStream(path);
			} else
				fileInputStream = new FileInputStream(path + fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			while ((str = bufferedReader.readLine()) != null) {
				boolean flag = false;
				for (int i = 0; i < strings.length; i++) {
					if (type == EQUALS_TYPE) {
						if (strings[i] != null
								&& str.equals(strings[i].toLowerCase())) {
							flag = true;
							break;
						}
					} else if (type == CONTAINS_TYPE) {
						if (strings[i] != null
								&& str.contains(strings[i].toLowerCase())) {
							flag = true;
							break;
						}
					}

				}
				if (flag != true)
					arrayList.add(str);
			}
		} catch (Exception e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return arrayList;
	}

	public static int write(File file, ArrayList<String> arrayList) {
		if (arrayList.size() == 0) {
			return 0;
		}
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, false), "UTF-8"));
			for (String s : arrayList) {
				bufferedWriter.write(s);
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
			return -1;
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return 1;
	}

	public static int write(String path, String fileName,
			ArrayList<String> arrayList) {
		if (arrayList.size() == 0)
			return -1;
		BufferedWriter bufferedWriter = null;
		try {
			if (fileName == null) {
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(path, false), "UTF-8"));
			} else
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(path + fileName, false), "UTF-8"));
			for (String s : arrayList) {
				if (s != null && !s.equals("")) {
					s = s.toLowerCase();
				}
				bufferedWriter.write(s);
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
			return -1;
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return 1;
	}

	public static boolean isChineseWord(String str) {
		boolean mark = false;
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]");
		Matcher matc = pattern.matcher(str);
		while (matc.find()) {
			mark = true;
		}
		return mark;
	}

	public static boolean isNotChineseWord(String str) {
		boolean mark = true;
		Pattern pattern = Pattern.compile("^[\u4E00-\u9FA5]*$");
		Matcher matc = pattern.matcher(str);
		while (matc.find()) {
			mark = false;
		}
		return mark;
	}

	public static ArrayList<String> read(File file) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			while ((str = bufferedReader.readLine()) != null) {
				arrayList.add(str);
			}
		} catch (Exception e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return arrayList;
	}

	/**
	 * 读取相关
	 * 
	 * @param file
	 * @param s
	 * @return
	 */
	public static ArrayList<String> read(File file, String s) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			while ((str = bufferedReader.readLine()) != null) {
				if (str.contains(s))
					arrayList.add(str);
			}
		} catch (Exception e) {
			Log.getLogger(FileUse.class).error(e.getMessage(),e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(FileUse.class).error(e.getMessage(),e);
			}
		}
		return arrayList;
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		boolean flag;
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag;
		File file;
		flag = false;
		file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static String find(String dir, String fileName) {
		File root = new File(dir);
		String result = null;
		for (File f : root.listFiles()) {
			if (f.isDirectory()) {
				dir = f.getAbsolutePath();
				result = find(dir, fileName);
				if (result != null)
					return result;
			} else {
				if (f.getName().equals(fileName)) {
					result = f.getAbsolutePath();
					break;
				}
			}
		}
		return result;
	}

}
