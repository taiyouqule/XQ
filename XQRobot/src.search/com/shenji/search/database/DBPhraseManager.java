package com.shenji.search.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;

public class DBPhraseManager {
	private String sql_select = "select * from tb_faq where Question=?";
	private String sql_selectAll = "select * from tb_faq";
	private String sql_add = "insert into tb_faq values(?,?)";
	private String sql_delete = "delete from tb_faq where Question=?";
	private String sql_update = "update tb_faq set Answer=? where Question=?";
	private String ANSWER = "Answer";
	private String QUESTION = "Question";

	public DBPhraseManager() {
	}

	public static void main(String[] str) throws ConnectionPoolException {
		System.out.println(new DBPhraseManager().listAllQA().size());
	}

	// 获取回答
	public String getAnswer(String question) throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		PreparedStatement statement = null;
		String answer = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql_select);
			statement.setString(1, question);
			rs = statement.executeQuery();
			if (rs.next()) {
				answer = rs.getString(ANSWER);
			}
			return answer;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	// 增加常用语
	public int addQA(String question, String answer)
			throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		PreparedStatement statementA = null;
		PreparedStatement statementB = null;
		ResultSet rs = null;
		try {
			statementA = connection.prepareStatement(sql_select);
			statementA.setString(1, question);
			rs = statementA.executeQuery();
			// 如果常用语已经存在，返回0
			if (rs.next()) {
				return 0;
			} else {
				statementB= connection.prepareStatement(sql_add);
				statementB.setString(1, question);
				statementB.setString(2, answer);
				statementB.execute();
			}
			return 1;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statementA != null)
					statementA.close();
				if (statementB != null)
					statementB.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	// 删除常用语
	public int delQA(String question) throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setString(1, question);
			if (statement.executeUpdate() == 1)
				return 1;
			else
				return 0;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	// 修改常用语
	public int modifyQA(String question, String answer)
			throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql_update);
			statement.setString(1, question);
			statement.setString(2, answer);
			if (statement.executeUpdate() == 1)
				return 1;
			else
				return 0;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return -1;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	public Map<String, String> listAllQA() throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			statement = connection.prepareStatement(sql_selectAll);
			rs = statement.executeQuery();
			while (rs.next()) {
				map.put(rs.getString(QUESTION), rs.getString(ANSWER));
			}
			return map;
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}
	
	public void modifyLog(String operation, String parameter){
		
	}

	/*
	 * // 修改信息日志 public void ModifyLog(String operation, String parameter) {
	 * ConnectionPool connectionPool = null; Connection connection = null;
	 * Statement statement = null; try { connectionPool =
	 * ConnectionPool.getInstance(); connection =
	 * connectionPool.getConnection(); statement = connection.createStatement();
	 * String sql = "insert into tb_log (Operation,Parameter) values('" +
	 * operation + "','" + parameter + "')"; statement.execute(sql); } catch
	 * (Exception e) { WriteLog writeLog = new WriteLog();
	 * writeLog.Write(e.getMessage(), DBPhraseManager.class.getName()); }
	 * finally { try { if (statement != null) statement.close(); if (connection
	 * != null) connectionPool.setConnection(connection); } catch (SQLException
	 * e) { WriteLog writeLog = new WriteLog(); writeLog.Write(e.getMessage(),
	 * DBPhraseManager.class.getName()); } } }
	 */
}
