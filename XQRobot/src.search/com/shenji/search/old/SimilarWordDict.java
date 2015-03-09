package com.shenji.search.old;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.util.IniEditor;

public class SimilarWordDict {
	private static SimilarWordDict instance = new SimilarWordDict();

	private SimilarWordDict() {
		try {
			IniEditor editor = new IniEditor();
			String path = editor.getClass().getClassLoader().getResource("")
					.toURI().getPath();
			;
			// initCodeDB(path);
			// initWordDB(path);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static SimilarWordDict getInstance() {
		return instance;
	}

	public int TreeBranchNum(String ch, int count) {
		// List list=SimilarWordDict.getCodeList();
		int num = 0;
		String sql = "select * from words_code where code like ?";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			if (count == 1)
				statement.setString(1, ch + "_");
			else
				statement.setString(1, ch + "__");

			resultSet = statement.executeQuery();
			resultSet.last();
			num = resultSet.getRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return num;
	}

	public String getWordCode(String word) {
		String code = null;
		String sql = "select code from words_word where word=?";
		// DBUtil dbUtil=new DBUtil();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, word);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				code = resultSet.getString("code");
				return code;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return code;
	}

	

	/**
	 * @param args
	 * @throws ConnectionPoolException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException,
			ConnectionPoolException {
		// TODO Auto-generated method stub
		// new SimilarWordDict();
		PreparedStatement statement = ConnectionPool.getInstance()
				.getConnection()
				.prepareStatement("select * from words_word where word=?");
		statement.setString(1, "阿尔巴尼亚");
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			System.out.println(resultSet.getString("word"));

		}

	}

}
