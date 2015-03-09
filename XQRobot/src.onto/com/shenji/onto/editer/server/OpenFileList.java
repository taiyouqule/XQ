package com.shenji.onto.editer.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shenji.common.log.Log;

/**
 * @author sj 单例设计模式，内存唯一实例instance 打开文件列表类
 */
public class OpenFileList {
	private static OpenFileList instance = new OpenFileList();
	private List<OpenFileBean> list;
	private static final Object LOCK = OpenFileList.class;
	// 端口号集合，保证端口不重复
	private Set<Integer> portSet = new HashSet<Integer>();
	private ExecutorService executorService;
	private static final int POOL_SIZE = 20;
	public static final String userSeparator = "@";
	public static final String portSeparator = "#";

	private OpenFileList() {
		this.list = new ArrayList<OpenFileList.OpenFileBean>();
		this.executorService = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors() * POOL_SIZE);
	}

	/**
	 * 获得OpenFileList实例
	 * 
	 * @return OpenFileList实例
	 */
	public synchronized static OpenFileList getInstance() {
		return instance;
	}

	// 得到随机端口号 20000-21000
	private synchronized int getRandomPort() {
		int randomPort = 0;
		while (true) {
			randomPort = (int) (Math.random() * 1000 + 20000);
			if (!portSet.contains(randomPort)) {
				portSet.add(randomPort);
				break;
			}
		}
		return randomPort;
	}

	public synchronized void removePort(int port) {
		if (portSet.contains(port))
			this.portSet.remove(port);
	}

	/**
	 * 添加打开文件Bean
	 * 
	 * @param fileAllName
	 *            文件名
	 * @param token
	 *            用户凭证
	 */
	public String addOpenFileBean(String fileName, String md5Token,
			String userName) throws Exception {
		String token = null;
		synchronized (LOCK) {
			OpenFileBean bean = null;
			// 文件打开Bean
			int port = getRandomPort();
			// token=md5值#端口号@用户名
			token = md5Token + portSeparator + port + userSeparator + userName;
			// 异常，throws Exception
			bean = new OpenFileBean(fileName, token, port);
			// 每个bean对应一个manage对象
			bean.addUser(userName);
			bean.addToken(token);
			this.list.add(bean);
		}
		return token;

	}

	public int addOpenFileBeanConnectNum(String fileName, String md5Token,
			String userName) {
		OpenFileBean bean;
		bean = getOpenFileBean(fileName);
		// bean的用户表中已经有该用户
		if (!bean.addUser(userName)) {
			// 这里应该是返回一个值，让说明已经有用户打开了该本体
			return -1;
		} else {
			int port = bean.getSocketServerPort();
			// token=md5值#端口号@用户名
			String token = md5Token + portSeparator + port + userSeparator
					+ userName;
			bean.addUser(userName);
			bean.addToken(token);
			bean.addOpenNum();
		}
		return 1;
	}

	/**
	 * 获得 某用户操作内存对象
	 * 
	 * @param token
	 * @param fillAllName
	 * @return
	 */
	public OntologyManage getOntologyManage(String fileName) {
		OntologyManage ontologyManage = null;
		OpenFileBean bean = this.getOpenFileBean(fileName);
		ontologyManage = bean.getOntologyManage();
		return ontologyManage;
	}

	/**
	 * 关闭 一个打开文件连接
	 * 
	 * @param fileAllName
	 *            文件名
	 * @return
	 */
	public int closeOpenFileBean(String fileName, String token) {
		synchronized (LOCK) {
			OpenFileBean bean;
			if ((bean = getOpenFileBean(fileName)) == null) {
				return -1;
			} else {
				// 连接数自减
				int reFlag = 1;
				bean.reduceOpenNum();
				bean.deleteToken(token);
				bean.deleteUserSet(token.split(userSeparator)[1]);
				if (bean.isOpen() == false) {
					// （2014.11.27） 关闭的时候持久到jena mysql数据库
					if (bean.getOntologyManage().writeTemp(true) != 1)
						reFlag = -9;
					Log.getLogger(this.getClass()).debug(
							"内存实例jena持久化完成！" + bean.getFileName());
					// （2014.11.27）CLOSE这里关闭
					bean.close();
					this.deleteOpenFileBean(bean);
					Log.getLogger(this.getClass()).debug(
							"删除了文件内存实例!" + bean.getFileName());
				}
				return reFlag;
			}
		}
	}

	private void deleteOpenFileBean(OpenFileBean bean) {
		this.list.remove(bean);
		this.portSet.remove(bean.getSocketServerPort());
	}

	/**
	 * 判断文件信息对象是否存在
	 * 
	 * @param fileAllName
	 * @return
	 */
	public boolean isExist(String fileName) {
		if (getOpenFileBeanID(fileName) != -1)
			return true;
		else
			return false;
	}

	/**
	 * 得到文件信息对象ID
	 * 
	 * @param fileAllName
	 * @return
	 */
	private int getOpenFileBeanID(String fileName) {
		for (int i = 0; i < list.size(); i++) {
			if (fileName.equals(list.get(i).getFileName()))
				return i;
		}
		return -1;
	}

	/**
	 * 得到文件信息对象实例
	 * 
	 * @param fileAllName
	 * @return
	 */
	private OpenFileBean getOpenFileBean(String fileAllName) {
		int id;
		if ((id = getOpenFileBeanID(fileAllName)) != -1)
			return this.list.get(id);
		else
			return null;
	}

	/**
	 * 通过token获得用户名
	 * 
	 * @param token
	 * @return
	 */
	public String getFileNameByToken(String token) {
		for (OpenFileBean bean : list) {
			for (String str : bean.getTokenSet())
				if (str.equals(token)) {
					return bean.fileName;
				}
		}
		return null;
	}

	public String getTokenByFileName(String fileName, String userName) {
		OpenFileList.OpenFileBean bean = this.getOpenFileBean(fileName);
		for (String token : bean.getTokenSet()) {
			if (token.split(userSeparator)[1].equals(userName))
				return token;
		}
		return null;
	}

	public int getSocketPortByFileName(String fileName) {
		OpenFileList.OpenFileBean bean = this.getOpenFileBean(fileName);
		return bean.getSocketServerPort();
	}

	// /////////////////////////////////////////////////////////
	/**
	 * @author sj 打开文件信息（单个文件）类
	 */
	private class OpenFileBean {
		// 打开文件名
		private String fileName;
		// 文件连接数
		private int openNum;
		// 是否被打开
		private boolean isOpen;
		// 用户许可
		private Set<String> tokenSet;
		// 用户表
		private Set<String> userSet;
		// 内存持久化文本管理对象
		private OntologyManage ontologyManage;
		// Socket服务端口号
		private int socketServerPort;
		// Socket服务
		private SocketServer socketServer;
		// 锁节点服务
		private LockedPointServer lockedPointServer;

		public OpenFileBean(String fileName, String token, int socketServerPort)
				throws Exception {
			this.fileName = fileName;
			this.openNum = 1;
			this.isOpen = true;
			this.socketServerPort = socketServerPort;
			this.userSet = new HashSet<String>();
			this.tokenSet = new HashSet<String>();
			// 初始化锁节点服务
			this.lockedPointServer = new LockedPointServer();
			OntologyManage manage = null;
			try {
				// 异常：throws Exception
				manage = new OntologyManage(fileName, token, lockedPointServer);
				this.setOntologyManage(manage);
				this.socketServer = new SocketServer(socketServerPort, manage,
						executorService, lockedPointServer);
				executorService.execute(socketServer);
			} finally {
				if (manage != null)
					lockedPointServer.close();
				userSet.clear();
			}

		}

		public int getSocketServerPort() {
			return socketServerPort;
		}

		/**
		 * 得到某用户内存镜像
		 * 
		 * @param token
		 * @return
		 */
		public OntologyManage getOntologyManage() {
			return this.ontologyManage;
		}

		public boolean addUser(String userName) {
			return this.userSet.add(userName);
		}

		public boolean deleteUserSet(String userName) {
			return this.userSet.remove(userName);
		}

		public Set<String> getTokenSet() {
			return tokenSet;
		}

		public boolean addToken(String token) {
			return tokenSet.add(token);
		}

		public void deleteToken(String token) {
			tokenSet.remove(token);
		}

		/**
		 * 设置内存镜像
		 * 
		 * @param token
		 * @param ontologyUpdate
		 */
		public void setOntologyManage(OntologyManage ontologyManage) {
			this.ontologyManage = ontologyManage;
		}

		/**
		 * 删除并关闭内存镜像
		 * 
		 * @param token
		 */
		private void deleteOntologyManage() {
			this.ontologyManage.close();
			this.ontologyManage = null;
		}

		public boolean isOpen() {
			return this.isOpen;
		}

		/**
		 * 添加连接数
		 */
		public void addOpenNum() {
			this.openNum++;
			 Log.getLogger(this.getClass()).debug(this.socketServerPort + "端口的当前连接数:" + openNum);
		}

		/**
		 * 得到源文件名
		 * 
		 * @return
		 */
		public String getFileName() {
			return this.fileName;
		}

		/**
		 * 连接数自减
		 */
		public void reduceOpenNum() {
			if (openNum > 0)
				openNum--;
			 Log.getLogger(this.getClass()).debug(this.getFileName() + "文件的连接数为:" + this.openNum);
			if (openNum == 0) {
				this.isOpen = false;
				// 关闭操作不在这里做了
				// this.close();
			}
		}

		public void close() {
			if (this.ontologyManage != null)
				deleteOntologyManage();
			if (this.userSet != null)
				this.userSet.clear();
			if (socketServer != null)
				socketServer.close();
			if (this.tokenSet != null)
				tokenSet.clear();
			if (this.lockedPointServer != null)
				lockedPointServer.close();
			if (this.ontologyManage != null)
				ontologyManage.close();

		}
	}

}
