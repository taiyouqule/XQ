package com.shenji.common.action;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.MD5Util;
import com.shenji.web.bean.UserBean;

public class DBUserManager {
	private java.sql.Connection connection;

	public DBUserManager() throws ConnectionPoolException {
		this.connection = ConnectionPool.getInstance().getConnection();
	}

	public void close() {
		try {
			if (this.connection != null)
				connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public boolean isExistUserName(String userName) {
		PreparedStatement preparedStatement = null;
		String sql = "select * from tb_user where name=?";
		ResultSet resultSet = null;
		try {
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			resultSet.last();
			if (resultSet.getRow() > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return true;

	}

	// 1成功，-1用户名不存在，-2密码错误,-3该用户已经登陆
	public int login(String userName, String passWord) {
		int reFlag = -4;
		PreparedStatement preparedStatement = null;
		String sql = "select * from tb_user where name=?";
		ResultSet resultSet = null;
		try {
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			resultSet.last();
			if (resultSet.getRow() > 0) {
				resultSet.first();
				String passWordDB = null;
				passWordDB = resultSet.getString("password");
				if (passWordDB != null) {
					String md5PassWord = MD5Util.md5(passWord);
					if (md5PassWord.equals(passWordDB))
						return 1;
					else
						return -2;
				}
			} else {
				return -1;
			}
		} catch (SQLException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return reFlag;

	}

	public int register(UserBean bean) {
		String sql = "insert into tb_user(name,password,email,level) values(?,?,?,?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setString(1, bean.getName());
			preparedStatement.setString(2, bean.getPassWord());
			preparedStatement.setString(3, bean.getEmail());
			preparedStatement.setInt(4, 1);
			int row = preparedStatement.executeUpdate();
			if (row > 0) {
				return 1;
			}

		} catch (SQLException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}

		return -1;
	}

	public static void main(String[] str) throws ConnectionPoolException {
		System.out.println(new DBUserManager().login("1", "1"));
	}
}