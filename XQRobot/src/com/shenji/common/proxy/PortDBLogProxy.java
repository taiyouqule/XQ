package com.shenji.common.proxy;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;

public class PortDBLogProxy extends CglibProxy {
	private String sql = "insert into tb_log (Time,Operation,Parameter,Result) values(?,?,?,?)";

	public PortDBLogProxy() {
		super();
	}

	@Override
	public void doBefore(Object proxy, Method method, Object[] params,
			Object result) {
		// TODO Auto-generated method stub
		Log.getLogger(this.getClass()).debug(
				"this doBefore " + method.getName() + " Method is runing!");
	}

	@Override
	public void doAfter(Object proxy, Method method, Object[] params,
			Object result) {
		// TODO Auto-generated method stub
		Log.getLogger(this.getClass()).debug(
				"this doAfter " + method.getName() + " Method is runing!");
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("获得数据库连接失败!", e);
		}
		PreparedStatement statement = null;
		StringBuilder sb = new StringBuilder();
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String time = df.format(new Date());
			statement = conn.prepareStatement(sql);
			statement.setString(1, time);
			statement.setString(2, method.getName());
			getParams(params, sb, ";");
			statement.setString(3, sb.toString());
			sb.setLength(0);
			getResult(result, sb, ";");
			statement.setString(4, sb.toString());
			statement.execute();
		} catch (Exception e) {
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
				if (sb != null)
					sb = null;
			} catch (SQLException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(), e);
			}
		}
	}
}
