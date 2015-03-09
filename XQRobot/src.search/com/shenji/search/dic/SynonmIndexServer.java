package com.shenji.search.dic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.common.util.PathUtil;
import com.shenji.search.Configuration;
import com.shenji.search.exception.EngineException;

public class SynonmIndexServer {
	public boolean createIndex() {
		boolean b = false;
		if (IndexServer.deleteIndexDir(Configuration.synFolder)) {
			CommonSynonymIndexServer commonSynonymIndexServer = new CommonSynonymIndexServer();
			b = commonSynonymIndexServer.createIndex();
			commonSynonymIndexServer.close();
		}
		if (IndexServer.deleteIndexDir(Configuration.mySynFolder)) {
			CustomSynonymIndexServer customSynonymIndexServer = new CustomSynonymIndexServer();
			b = customSynonymIndexServer.createIndex() && b;
			customSynonymIndexServer.close();
		}
		return b;
	}

	public int addNewSynonmWord(String word, String[] synonmWords) {
		String path;
		int result;
		if (synonmWords.length == 0)
			return -1;
		path = PathUtil.getWebInFAbsolutePath();
		File file = new File(path + Configuration.synonmDict);
		String[] oldSynonmWords = readOldSynonmWords(file, word);
		String[] newSynonWords = null;
		List<String> newSynonmList = new ArrayList<String>();
		if (oldSynonmWords != null) {
			for (String s : synonmWords) {
				boolean isEquals = false;
				for (String t : oldSynonmWords) {
					if (s.equalsIgnoreCase(t)) {
						isEquals = true;
						continue;
					}
				}
				if (isEquals == false)
					newSynonmList.add(s.toLowerCase());
			}
			synonmWords = newSynonmList
					.toArray(new String[newSynonmList.size()]);
			newSynonmList.clear();
		}

		if (oldSynonmWords != null) {
			newSynonWords = new String[oldSynonmWords.length
					+ synonmWords.length];
			for (int i = 0; i < oldSynonmWords.length; i++)
				newSynonWords[i] = oldSynonmWords[i];
			for (int j = 0; j < synonmWords.length; j++)
				newSynonWords[oldSynonmWords.length + j] = synonmWords[j]
						.toLowerCase();
		} else
			newSynonWords = synonmWords;
		ArrayList<String> list = modifyFile(file, word, oldSynonmWords,
				newSynonWords);
		result = FileUse.write(file, list);
		CommonSynonymIndexServer server = new CommonSynonymIndexServer();
		synchroWithMySynonmDict(word, synonmWords);
		try {
			server.addIndex(word, synonmWords, newSynonWords);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		finally{
			if(server!=null)
				server.close();
		}		
		return result;
	}

	public int modifySynonmWord(String word, String[] newSynonmWords) {
		if (newSynonmWords == null)
			return -1;
		String path;
		int result;
		path = PathUtil.getWebInFAbsolutePath();
		File file = new File(path + Configuration.synonmDict);
		String[] oldSynonmWords = readOldSynonmWords(file, word);
		ArrayList<String> list = modifyFile(file, word, oldSynonmWords,
				newSynonmWords);
		if (list == null)
			return -1;
		result = FileUse.write(file, list);
		CommonSynonymIndexServer server = new CommonSynonymIndexServer();
		try {
			server.modifyIndex(word, oldSynonmWords, newSynonmWords);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		finally{
			if(server!=null)
				server.close();
		}	
		return result;
	}

	public int synchroWithMySynonmDict(String word, String[] synonmWords) {
		String path;
		int result;
		path = PathUtil.getWebInFAbsolutePath();
		File file = new File(path + Configuration.mySynonmDict);
		String[] oldSynonmWords = readOldSynonmWords(file, word);
		String[] newSynonWords = null;
		List<String> newSynonmList = new ArrayList<String>();
		if (oldSynonmWords != null) {
			for (String s : synonmWords) {
				boolean isEquals = false;
				for (String t : oldSynonmWords) {
					if (s.equals(t)) {
						isEquals = true;
						continue;
					}
				}
				if (isEquals == false)
					newSynonmList.add(s);
			}
			synonmWords = newSynonmList
					.toArray(new String[newSynonmList.size()]);
			newSynonmList.clear();
		}

		if (oldSynonmWords != null) {
			newSynonWords = new String[oldSynonmWords.length
					+ synonmWords.length];
			for (int i = 0; i < oldSynonmWords.length; i++)
				newSynonWords[i] = oldSynonmWords[i];
			for (int j = 0; j < synonmWords.length; j++)
				newSynonWords[oldSynonmWords.length + j] = synonmWords[j];
		} else
			newSynonWords = synonmWords;
		ArrayList<String> list = modifyFile(file, word, oldSynonmWords,
				newSynonWords);
		result = FileUse.write(file, list);
		CustomSynonymIndexServer server = new CustomSynonymIndexServer();
		server.createIndex();
		server.close();
		return result;
	}

	public static String[] readOldSynonmWords(File file, String word) {
		String[] strs = null;
		ArrayList<String> list = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			while ((str = bufferedReader.readLine()) != null) {
				if (str.split(" ")[0].endsWith(word)) {
					strs = str.split(" ");
					for (int i = 1; i < strs.length; i++) {
						list.add(strs[i]);
					}
				}
			}
		} catch (Exception e) {
			Log.getLogger(SynonmIndexServer.class).error(e.getMessage(), e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(SynonmIndexServer.class).error(e.getMessage(), e);
			}
		}

		if (list.size() == 0)
			return null;
		return (String[]) list.toArray(new String[list.size()]);
	}

	public static ArrayList<String> modifyFile(File file, String word,
			String[] oldSynonmWords, String[] newSynonmWords) {
		word = word.trim();
		if (newSynonmWords == null || newSynonmWords.length == 0)
			return null;
		if (oldSynonmWords != null) {
			for (int i = 0; i < oldSynonmWords.length; i++) {
				oldSynonmWords[i] = oldSynonmWords[i].trim();
			}
		}
		for (int i = 0; i < newSynonmWords.length; i++) {
			newSynonmWords[i] = newSynonmWords[i].trim();
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		String str = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		Set<String> set_old = new TreeSet<String>();
		Set<String> set_new = new TreeSet<String>();

		if (oldSynonmWords != null) {
			for (String s : oldSynonmWords) {
				set_old.add(s);
			}
		}
		for (String s : newSynonmWords) {
			set_new.add(s);
		}
		try {
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(
					fileInputStream, "UTF-8"));
			boolean isExit = false;
			while ((str = bufferedReader.readLine()) != null) {
				String[] strings = str.split(" ");
				// 修改同义词全部替换为新词
				if (strings[0].equals(word)) {
					str = null;
					str = word + " ";
					for (String s : newSynonmWords) {
						str = str + s + " ";
					}
					isExit = true;
				}

				// 从旧词里面删掉包含的新词
				boolean q = set_old.add(strings[0]);
				if (q)
					set_old.remove(strings[0]);
				else if (!q) {
					List<String> list = new ArrayList<String>();
					for (String s : strings) {
						if (!s.equals(word)) {
							list.add(s);
						}
					}
					strings = (String[]) list.toArray(new String[list.size()]);
					str = null;
					for (String s : strings) {
						if (str == null)
							str = s + " ";
						else
							str = str + s + " ";
					}
				}

				// 新词添加word进去
				boolean b = set_new.add(strings[0]);
				if (b)
					set_new.remove(strings[0]);
				else if (!b) {
					set_new.remove(strings[0]);
					str = str + word;
				}
				arrayList.add(str);
				// System.err.println(str);
			}
			if (set_new.size() != 0) {
				Iterator<String> iterator = set_new.iterator();
				while (iterator.hasNext()) {
					str = iterator.next();
					arrayList.add(str + " " + word + " ");
					// System.err.println(str+" "+word);
				}
				// System.err.println(set_new.size());
			}
			if (oldSynonmWords == null && isExit == false) {
				str = null;
				str = word + " ";
				for (String s : newSynonmWords) {
					str = str + s + " ";
				}
				arrayList.add(str);
				// System.err.println(str);
			}
			set_old.clear();
			set_new.clear();
		} catch (Exception e) {
			Log.getLogger(SynonmIndexServer.class).error(e.getMessage(), e);
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				Log.getLogger(SynonmIndexServer.class).error(e.getMessage(), e);
			}
		}
		return arrayList;
	}

}
