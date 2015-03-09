package com.shenji.onto.editer.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.FileUtil;
import com.shenji.common.util.MD5Util;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.Configuration;
import com.shenji.onto.OntologyCommon;

public class OntologyServer {
	public static final int ADD = 0;
	public static final int DELETE = 1;
	// 文件打开列表
	public static OpenFileList openFileList;
	private static String filePootPath_convert = "..云端";

	// 本体文件根目录(现在废弃了)
	private static String fileRootPath = null;

	static {
		// System.err.println("!!!!!!!!!!!!!!!!!!"+Runtime.getRuntime().maxMemory());
		openFileList = OpenFileList.getInstance();
	}

	/**
	 * 构造函数
	 */
	public OntologyServer() {
		fileRootPath = Configuration.TempPath;
	}

	public OpenFileList getOpenFileList() {
		return openFileList;
	}

	public void notifyAll(int reflag, OntologyManage manage, String message,
			String userName) {
		if (reflag > 0)
			manage.notifyObservers(message, userName);
	}

	public void notifyMySelf(int reflag, OntologyManage manage, String message,
			String userName) {
		if (reflag > 0)
			manage.notifyMySelf(message, userName);
	}

	/**
	 * 得到用户操作的本体管理对象
	 * 
	 * @param token
	 *            连接许可（相同用户持有该token进行本体修改操作）
	 * @return 本体管理对象
	 */
	public OntologyManage getManage(String token) {
		// 11月28修改
		String openFileName = openFileList.getFileNameByToken(token);
		// Log.debugSystemOut(openFileName);
		if (openFileName == null)
			return null;
		return openFileList.getOntologyManage(openFileName);
	}

	/**
	 * 创建用户token
	 * 
	 * @param openFileName
	 * @return
	 */
	private String createMD5Token(String openFileName) {
		String token = null;
		token = MD5Util.md5(openFileName);
		// token=token+"#"+Thread.currentThread().getId();
		return token;
	}

	/**
	 * 创建本体文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @return 1成功 -1失败
	 */
	public int createNewOntology(String filePath, String fileName, String url) {
		if (filePath != "" && !filePath.contains(fileRootPath)) {
			if (!filePath.startsWith("/"))
				filePath = fileRootPath + "/" + filePath;
		} else if (filePath == "") {
			filePath = fileRootPath + "/" + filePath;
		}
		System.err.println(filePath + "  " + fileName);
		return OntologyUtil.createNewOntology(filePath, fileName, url);
	}

	public int createNewOntologyByDB(String ontoName, String url) {
		OntologyDBServer dbServices = new OntologyDBServer();
		int flag = dbServices.createOntologyByDB(ontoName, url);
		dbServices.close();
		return flag;
	}

	/*
	 * public int Login(String userName,String passWord){ UserDBManage
	 * baseManage=new UserDBManage(); int reFlag=baseManage.Login(userName,
	 * passWord); baseManage.close(); return reFlag;
	 * 
	 * }
	 */

	/**
	 * 打开本体文件
	 * 
	 * @param openFileName
	 *            文件完整路径+文件名+后缀
	 * @return token 连接许可（相同用户持有该token进行本体修改操作） 成功：token 失败：null（获取token失败） file
	 *         aredy open!（文件已经打开过）
	 */
	public synchronized String openOntologyFromFile(String openFileName,
			String userName) {
		System.err.println("start!!!!!!!!!!!!!!!!!!!!!!"
				+ Runtime.getRuntime().totalMemory());
		openFileName = convertDirPath(openFileName, true);
		if (FileUtil.isExist(openFileName)) {
			String token = null;
			String md5Token = createMD5Token(openFileName);
			if (!openFileList.isExist(openFileName)) {
				try {
					token = openFileList.addOpenFileBean(openFileName,
							md5Token, userName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.getLogger().error(e.getMessage(),e);
					e.printStackTrace();
					return null;
				}
			} else {
				if (openFileList.addOpenFileBeanConnectNum(openFileName,
						md5Token, userName) == -1) {
					// 用户存在了
					return null;
				}
				token = openFileList.getTokenByFileName(openFileName, userName);
			}
			System.out.println("用户token:" + token);
			System.err.println("end!!!!!!!!!!!!!!!!!!!!!!"
					+ Runtime.getRuntime().totalMemory());
			return token;
		}
		// 文件不存在
		else
			return null;
	}

	public synchronized String openOntologyFromDB(String ontoName,
			String userName) {
		System.err.println("start!!!!!!!!!!!!!!!!!!!!!!"
				+ Runtime.getRuntime().totalMemory());
		String token = null;
		String md5Token = createMD5Token(ontoName);
		if (!openFileList.isExist(ontoName)) {
			try {
				token = openFileList.addOpenFileBean(ontoName, md5Token,
						userName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getLogger().error(e.getMessage(),e);
				e.printStackTrace();
				// 未知异常
				return null;
			}
		} else {
			if (openFileList.addOpenFileBeanConnectNum(ontoName, md5Token,
					userName) == -1) {
				// 用户存在了
				return null;
			}
			token = openFileList.getTokenByFileName(ontoName, userName);
		}
		System.out.println("用户token:" + token);
		System.err.println("end!!!!!!!!!!!!!!!!!!!!!!"
				+ Runtime.getRuntime().totalMemory());
		return token;
	}

	/**
	 * 删除本体
	 * 
	 * @param token
	 * @return
	 */
	public int deleteOntology(String fileName) {
		/*
		 * String openFileName=openFileList.getFileNameByToken(token);
		 * synchronized (openFileName){ OntologyManage manage=null;
		 * if((manage=this.getManage(token))==null) return -10;
		 * manage.deleteTemp();
		 * //if(openFileList.getOpenFileBean(openFileName).isOpen()&&)
		 * if(OntologyUtil.deleteOntology(openFileName)==true);
		 * openFileList.closeOpenFileBean(openFileName,false); }
		 */
		String converfile = convertDirPath(fileName, true);
		Log.getLogger(this.getClass()).debug(fileName + ":" + converfile);
		if (FileUtil.deleteFile(converfile))
			return 1;
		else
			return -1;
	}

	public int deleteOntologyFromDB(String ontoName) {
		OntologyDBServer dbServices = new OntologyDBServer();
		int flag;
		try {
			flag = dbServices.deleteOntologyByDB(ontoName);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		dbServices.close();
		return flag;
	}

	public int closeOntology(String token) {
		String openFileName = openFileList.getFileNameByToken(token);
		if (openFileName == null)
			return -1;
		synchronized (openFileName) {
			openFileList.closeOpenFileBean(openFileName, token);
			return 1;
		}
	}

	/**
	 * 保存最终操作
	 * 
	 * @param token
	 *            连接许可（相同用户持有该token进行本体修改操作）
	 * @param newOpenFileName
	 *            文件完整路径+文件名+后缀
	 * @return 1保存成功 -1失败
	 */
	public int saveOntology(String token, String newOpenFileName) {
		String oldOpenFileName = openFileList.getFileNameByToken(token);
		if (oldOpenFileName == null)
			return -2;
		int Reflag = -1;
		synchronized (oldOpenFileName) {
			// 缓存删除先不用
			/*
			 * if(oldOpenFileName==null){ //删除缓存文件 String
			 * fileName=this.tempPath+token+".temp";
			 * FileUtil.deleteFile(fileName); return -2; }
			 */
			OntologyManage manage = getManage(token);
			Reflag = manage.saveOntology(newOpenFileName);
		}
		return Reflag;
	}

	/**
	 * 显示释放所有用户持有的许可
	 * 
	 * @param token
	 * @return
	 */
	public void releaseToken(String[] tokens) {
		/*
		 * for(String token:tokens){ String
		 * openFileName=openFileList.getFileNameByToken(token);
		 * if(openFileName==null){ continue; }
		 * openFileList.deleteOntologyManage(openFileName, token);
		 * if(openFileList.isOpen(openFileName)==false){
		 * openFileList.closeOpenFileBean(openFileName,true); } }
		 */
	}

	private String[] convertDirPath(String[] oldPath, boolean realToVirtual) {
		String[] newPath = new String[oldPath.length];
		for (int i = 0; i < oldPath.length; i++) {
			String temp = null;
			if (realToVirtual)
				temp = FileUtil.getFormattedDirectory(oldPath[i]).replace(
						filePootPath_convert, fileRootPath);
			else
				temp = FileUtil.getFormattedDirectory(oldPath[i]).replace(
						fileRootPath, filePootPath_convert);
			newPath[i] = temp;
		}
		return newPath;
	}

	private String convertDirPath(String oldPath, boolean realToVirtual) {
		String tempStr = null;
		if (realToVirtual)
			tempStr = FileUtil.getFormattedDirectory(oldPath).replace(
					filePootPath_convert, fileRootPath);
		else
			tempStr = FileUtil.getFormattedDirectory(oldPath).replace(
					fileRootPath, filePootPath_convert);
		return tempStr;
	}

	public String[] getFileList(String dirPath) {
		return convertDirPath(
				FileUtil.getFileList(this.fileRootPath + FileUtil.separator
						+ dirPath, FileUtil.FILE), false);
	}

	public String[] getDirectoryList(String dirPath) {
		return FileUtil.getFileList(this.fileRootPath + FileUtil.separator
				+ dirPath, FileUtil.DIR);
	}

	public int deleteDirectory(String path) {
		try {
			path = convertDirPath(path, true);
			return FileUtil.deleteDirectory(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.getLogger().error("deleteDirectory", e);
		}
		return -8;
	}

	public int renameDirectory(String path, String newName) {
		path = convertDirPath(path, true);
		return FileUtil.renameDirectory(path, newName);
	}

	public int createDirectory(String path) {
		path = convertDirPath(path, true);
		return FileUtil.createDirectory(path);
	}

	public int moveFile(String oldFilePath, String newFilePath) {
		return FileUtil.moveFile(oldFilePath, newFilePath);
	}

	public String createLockedDescription(String token, String xml) {
		StringReader xmlString = new StringReader(xml);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(xmlString);
		// 创建一个新的SAXBuilder
		SAXBuilder saxb = new SAXBuilder();
		Document doc;
		String xmlStr = null;
		try {
			doc = saxb.build(source);
			Element root = doc.getRootElement();
			if (root.getAttribute("isComplex").equals("true"))
				root.setAttribute("locked", "false");
			else {
				String locked;
				Element subE = (Element) root.getChildren().get(0);
				String ontologyName = subE.getAttributeValue("name");
				int typeNum = Integer.parseInt(subE
						.getAttributeValue("typeNum"));
				if (this.getManage(token)
						.getLockedPointServer()
						.checkIsLocked(ontologyName,
								OntologyManage.getUserName(token), typeNum))
					locked = "true";
				else
					locked = "false";
				root.setAttribute("locked", locked);
			}
			xmlStr = StringUtil.outputToString(doc, Configuration.XML_CODE);
			xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
			// Log.debugSystemOut("转换后的：\n\r"+xmlStr);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 取的根元素
		return xmlStr;
	}

	public String createLockdedIndividuals(String token, String xml) {
		StringReader xmlString = new StringReader(xml);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(xmlString);
		// 创建一个新的SAXBuilder
		SAXBuilder saxb = new SAXBuilder();
		Document doc;
		String xmlStr = null;
		try {
			doc = saxb.build(source);
			Element root = doc.getRootElement();
			List<Element> list = root.getChildren();
			for (Element e : list) {
				String locked;
				String individualName = e.getAttributeValue("name");
				if (this.getManage(token)
						.getLockedPointServer()
						.checkIsLocked(individualName,
								OntologyManage.getUserName(token),
								OntologyCommon.DataType.individual))
					locked = "true";
				else
					locked = "false";
				e.setAttribute("locked", locked);
			}
			xmlStr = StringUtil.outputToString(doc, Configuration.XML_CODE);
			xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
			// Log.debugSystemOut("转换后的：\n\r"+xmlStr);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 取的根元素
		return xmlStr;
	}

	public String createLockdedIndividualType(String token, String xml) {
		StringReader xmlString = new StringReader(xml);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(xmlString);
		// 创建一个新的SAXBuilder
		SAXBuilder saxb = new SAXBuilder();
		Document doc;
		String xmlStr = null;
		try {
			doc = saxb.build(source);
			Element root = doc.getRootElement();
			List<Element> list = root.getChildren();
			for (Element e : list) {
				String locked;
				String className = e.getAttributeValue("name");
				if (this.getManage(token)
						.getLockedPointServer()
						.checkIsLocked(className,
								OntologyManage.getUserName(token),
								OntologyCommon.DataType.namedClass))
					locked = "true";
				else
					locked = "false";
				e.setAttribute("locked", locked);
			}
			xmlStr = StringUtil.outputToString(doc, Configuration.XML_CODE);
			xmlStr = StringUtil.changeCharset(xmlStr, Configuration.XML_CODE);
			// Log.debugSystemOut("转换后的：\n\r"+xmlStr);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 取的根元素
		// Log.debugSystemOut(xmlStr);
		return xmlStr;
	}

	public String createDescriptionNotifyMessage(int ontologyDescriptionType,
			int ontologyType, int operationType) {
		switch (ontologyDescriptionType) {
		case OntologyCommon.Description.super_:
			if (operationType == ADD)
				return "A" + Configuration.messageSeparator + "302"
						+ Configuration.messageSeparator + ontologyType;
			else
				return "D" + Configuration.messageSeparator + "402"
						+ Configuration.messageSeparator + ontologyType;
		default:
			return null;
		}
	}

	public String createAutomaticClassificationNotifyMessage(boolean state) {
		if (state)
			return String.valueOf("601");
		else
			return String.valueOf("602");

	}
}
