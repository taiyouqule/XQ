package com.shenji.common.action;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.logicalcobwebs.proxool.ConnectionInfoIF;
import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.admin.SnapshotIF;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.PathUtil;

/**
 * 数据库连接池
 * 
 * @author zhq
 * 
 */
public class ConnectionPool {
	private final static String ProxoolFile = "conf/proxool.xml";
	private final static String driver = "org.logicalcobwebs.proxool.ProxoolDriver";
	private final static String proXooolName = "proxool.mysql";
	private final static String sqlName = "mysql";
	private static ConnectionPool instance;
	private static int activeCount = 0;// 当前连接数

	public static ConnectionPool getInstance() throws ConnectionPoolException {
		if (instance == null) {
			synchronized (ConnectionPool.class) {
				if (instance == null)
					instance = new ConnectionPool();
			}
		}
		return instance;
	}

	private ConnectionPool() throws ConnectionPoolException {
		try {
			init();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new ConnectionPoolException("Connection Init Exception!", e);
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			throw new ConnectionPoolException("Connection Init Exception!", e);
		}
	}

	private void init() throws URISyntaxException, ProxoolException {
		String systemPath = PathUtil.getWebInFAbsolutePath();
		JAXPConfigurator.configure(systemPath + ProxoolFile, false);
	}

	public Connection getConnection() throws ConnectionPoolException {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new ConnectionPoolException(
					"Connection Driver load Exception!", e);
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(proXooolName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new ConnectionPoolException("Connection get Exception!", e);
		}
		return conn;
	}

	public String getPoolInformation() {
		StringBuilder sb = new StringBuilder();
		ConnectionPoolDefinitionIF cpd = null;
		try {
			cpd = ProxoolFacade.getConnectionPoolDefinition(sqlName);
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		SnapshotIF snapshotIF = null;
		try {
			snapshotIF = ProxoolFacade.getSnapshot(sqlName, true);
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}
		int curActiveCount = snapshotIF.getActiveConnectionCount();// 获得活动(正在使用的busy)连接数
		if (curActiveCount != activeCount)// 当活动连接数变化时输出信息
		{
			System.out.println("activeCount:" + activeCount);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int availableCount = snapshotIF.getAvailableConnectionCount();// 获得可得到的(空闲的free)连接数
			int maxCount = snapshotIF.getMaximumConnectionCount();// 获得总连接数(最大的)
			int offlineCount = snapshotIF.getOfflineConnectionCount();// 获得不在线的连接数(释放的)
			long refusedCount = snapshotIF.getRefusedCount();// How many
																// connections
			long servedCount = snapshotIF.getServedCount();// How many
															// connections
			Date snapshotDate = snapshotIF.getSnapshotDate();
			ConnectionInfoIF[] connIF = snapshotIF.getConnectionInfos();
			sb.append("\n\n----------------连接数信息------------------\n");
			sb.append("监控时间:" + sdf.format(snapshotDate) + "\n");
			sb.append("busy连接数:" + curActiveCount + "\n");
			sb.append("free连接数:" + availableCount + "\n");
			sb.append("max连接数:" + maxCount + "\n");
			sb.append("offlineCount连接数:" + offlineCount + "\n");
			sb.append("refusedCount连接数:" + refusedCount + "\n");
			sb.append("servedCount连接数:" + servedCount + "\n");
			/*
			 * System.err.println("trace:" + cpd.isTrace());
			 * System.err.println("trace:" + cpd.getStatisticsLogLevel());
			 * System.err.println(curActiveCount + "(active)  " + availableCount
			 * + "(available)  " + maxCount + "(max)");
			 */
			sb.append("----------------连接信息-----------------\n");
			for (int i = 0; i < connIF.length; i++) {
				ConnectionInfoIF connInfo = connIF[i];
				sb.append("ConnectionId号:" + connInfo.getId() + "\n");
				sb.append("Connection初始化时间:"
						+ sdf.format(new Date(connInfo.getBirthTime())) + "\n");
				sb.append("Connection年龄(mis):" + connInfo.getAge() + "\n");
				sb.append("Connection状态(active-2;available-1;null-0;offline-3):"
						+ connInfo.getStatus() + "\n");
				sb.append("Connection最后一次活动开始时间:"
						+ sdf.format(new Date(connInfo.getTimeLastStartActive()))
						+ "\n");
				sb.append("Connection最后一次活动结束时间:"
						+ sdf.format(new Date(connInfo.getTimeLastStopActive()))
						+ "\n");
				sb.append("Connection最后一次活动占用时长(mis):"
						+ (connInfo.getTimeLastStopActive() - connInfo
								.getTimeLastStartActive()) + "\n");
				String[] sqlCallsStrs = connInfo.getSqlCalls();
				for (int j = 0; i < sqlCallsStrs.length; j++) {
					sb.append("---------该连接执行的sql语句----------");
					sb.append(sqlCallsStrs[j]);
					sb.append("-----------------------------------");
				}
			}
			activeCount = curActiveCount;
		}
		return sb.toString();
	}

	public static void main(String[] str) {
		try {
			for (int i = 0; i < 10; i++)
				ConnectionPool.getInstance().getConnection();
			System.out.println(ConnectionPool.getInstance()
					.getPoolInformation());
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
