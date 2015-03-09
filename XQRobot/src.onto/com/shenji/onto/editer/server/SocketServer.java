package com.shenji.onto.editer.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;

public class SocketServer implements Runnable, PointLockedHander {
	private ServerSocket serverSocket;
	private boolean running = true;
	private OntologyManage ontologyManage;
	private int port;
	private static String END_CODE = "END";
	private static int END_CODE_INT = 0;
	private static int LOCK = 0;
	private static int RELEASE = 1;
	private ExecutorService executorService;
	// 锁节点服务对象
	private LockedPointServer lockedPointServer;

	public SocketServer(int port, OntologyManage ontologyManage,
			ExecutorService executorService, LockedPointServer lockedPointServer) {
		try {
			this.ontologyManage = ontologyManage;
			this.port = port;
			this.executorService = executorService;
			this.serverSocket = new ServerSocket(port);
			this.lockedPointServer = lockedPointServer;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("socket对象创建失败", e);
			e.printStackTrace();
		}
	}

	private synchronized boolean getRunning() {
		return this.running;
	}

	public void close() {
		this.running = false;
	}

	public String getClientIP(Socket socket) {
		String clientIP = socket.getInetAddress() + ":" + socket.getPort();
		return clientIP;
	}

	private void clear() {
		try {
			if (this.serverSocket != null)
				serverSocket.close();
			if (this.lockedPointServer != null)
				lockedPointServer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("socket关闭异常", e);
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.getLogger(this.getClass()).debug("Socket服务启动！端口号：" + this.port);
		while (true) {
			if (getRunning() == false)
				break;
			Socket socket = null;
			try {
				// 阻塞方法，等待用户进行socket连接
				socket = serverSocket.accept();
				Handler handler = new Handler(socket);
				verifyAuthority(handler);
				this.ontologyManage.addObserver(handler);
				Thread thread = new Thread(handler);
				executorService.execute(thread);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger().error("socket服务时出现问题", e);
				e.printStackTrace();
				this.clear();
			}
		}
		this.clear();
	}

	// 没有用
	private boolean verifyAuthority(Handler handler) {
		String msg = null;
		try {
			// Log.debugSystemOut("接受客户端消息:"+msg);
			msg = handler.getReader().readLine();
			handler.setClientName(msg);
			// Log.debugSystemOut("测试这里是不是也阻塞了！:"+msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("服务端接收不到信息", e);
			e.printStackTrace();
		}
		return true;

	}

	public class Handler implements Runnable, SocketObserver {
		private Socket socket;
		private boolean running = true;
		private OutputStream outputStream;
		private OutputStreamWriter outputStreamWriter;
		private BufferedWriter bufferedWriter;

		private InputStream inputStream = null;
		private InputStreamReader inputStreamReader = null;
		private BufferedReader bufferedReader = null;
		private String clientIP;
		private String clientName;

		public String getClientIP() {
			return clientIP;
		}

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		public Handler(Socket socket) {
			this.socket = socket;
			this.clientIP = SocketServer.this.getClientIP(socket);
		}

		public void close() {
			this.running = false;
		}

		private synchronized boolean getRunning() {
			return this.running;
		}

		// 读socket
		private synchronized BufferedReader getReader() {
			if (inputStream == null) {
				try {
					inputStream = this.socket.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (inputStream == null)
					return null;
				try {
					inputStreamReader = new InputStreamReader(inputStream,
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bufferedReader = new BufferedReader(inputStreamReader);
			}
			return bufferedReader;
		}

		private synchronized BufferedWriter getWriter() {
			if (outputStream == null) {
				try {
					outputStream = this.socket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (outputStream == null)
					return null;
				try {
					outputStreamWriter = new OutputStreamWriter(outputStream,
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bufferedWriter = new BufferedWriter(outputStreamWriter);
			}
			return bufferedWriter;
		}

		public void run() {
			try {
				Log.getLogger(this.getClass()).debug(
						"一个新的连接产生了:" + socket.getInetAddress() + ":"
								+ socket.getPort());
				Log.getLogger(this.getClass()).debug(
						"连接名" + this.getClientName());
				while (true) {
					/*
					 * if(!this.getRunning()) break;
					 */
					// System.out.println(socket.getInetAddress()+"我一直在跑！我为什么这么屌！qian");
					String msg = this.getReader().readLine();
					System.out.println("服务器接收数据!!: " + msg);
					// System.out.println(socket.getInetAddress()+"我一直在跑！我为什么这么屌！hou");
					if (msg.equals(END_CODE)) {
						// 删除观察者
						int message = Integer.parseInt(String
								.valueOf(END_CODE_INT));
						this.getWriter().write(message);
						this.getWriter().flush();
						ontologyManage.deleteObserver(this);
						break;
					} else {
						String[] messageSeparator = msg
								.split(Configuration.messageSeparator);
						String lockedType = messageSeparator[0];
						String pointName = messageSeparator[1];
						int pointType = Integer.valueOf(messageSeparator[2]);
						String pointHolder = messageSeparator[3];
						List<String> msgList = new ArrayList<String>();
						if (lockedType.equals("L")) {
							// 加入锁队列
							msgList.clear();
							fetchCopePoint(pointName, pointType, pointHolder,
									LOCK, msgList);
						} else {
							// 移除出锁队列
							msgList.clear();
							fetchCopePoint(pointName, pointType, pointHolder,
									RELEASE, msgList);
						}
						if (msgList != null) {
							for (String str : msgList) {
								locked(str, this.getClientName());
							}
						}
					}

				}
			} catch (Exception e) {
				// 当socket断开时异常处理
				Log.getLogger(this.getClass()).debug("socket连接断开", e);
				Log.getLogger(this.getClass()).debug("socket连接断开");
				// ontologyManage.get
				OntologyServer.openFileList.closeOpenFileBean(
						ontologyManage.getOpenFileName(),
						ontologyManage.getToken());
				ontologyManage.deleteObserver(this);
				// 删除历史节点记录
				String[] historyMessages = lockedPointServer
						.removeHistoryByHolder(this.getClientName());
				for (String message : historyMessages) {
					locked(message, this.getClientName());
				}
			} finally {
				try {
					if (socket != null)
						socket.close();
					if (bufferedWriter != null)
						bufferedWriter.close();
					if (outputStreamWriter != null)
						outputStreamWriter.close();
					if (outputStream != null)
						outputStream.close();
					if (bufferedReader != null)
						bufferedReader.close();
					if (inputStreamReader != null)
						inputStreamReader.close();
					if (inputStream != null)
						inputStream.close();
					String[] historyMessages = lockedPointServer
							.removeHistoryByHolder(this.getClientName());
					for (String message : historyMessages) {
						locked(message, this.getClientName());
					}
				} catch (IOException e) {
					Log.getLogger().error("Socket资源释放异常", e);
					e.printStackTrace();
				}
			}
		}

		@Override
		public synchronized void update(Object obj) {
			// TODO Auto-generated method stub
			try {
				this.getWriter().write(obj.toString() + "\n");
				this.getWriter().flush();
				Log.getLogger(this.getClass()).debug(
						this.toString() + "页面回调更新：" + obj.toString() + ":"
								+ socket.getPort());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void locked(Object obj, String observersName) {
		// TODO Auto-generated method stub
		this.ontologyManage.notifyObservers(obj, observersName);
		// Log.debugSystemOut("locked"+obj.toString());
	}

	private void fetchCopePoint(String ontologyName, int ontologyType,
			String pointHolder, int copeType, List<String> msgList) {
		if (copeType == LOCK) {
			if (lockedPointServer.add(ontologyName, pointHolder, ontologyType)) {
				// 消息队列
				msgList.add(LockedPointServer.createLockPointMessage(
						ontologyType, ontologyName, pointHolder));
			}
		} else if (copeType == RELEASE) {
			if (lockedPointServer.release(ontologyName, ontologyType,
					pointHolder)) {
				// 消息队列
				msgList.add(LockedPointServer.createReleasePointMessage(
						ontologyType, ontologyName, pointHolder));
			}
		}
		String[] subs = ontologyManage.getAbstractQueryInterface()
				.getOntologyDescriptionObject(ontologyName, ontologyType,
						false, OntologyCommon.Description.sub_, false);
		if (subs == null)
			return;
		for (String str : subs) {
			if (str == null)
				break;
			fetchCopePoint(str, ontologyType, pointHolder, copeType, msgList);
		}

	}

}
