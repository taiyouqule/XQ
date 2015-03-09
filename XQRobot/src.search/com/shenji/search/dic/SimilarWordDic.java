package com.shenji.search.dic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.search.engine.SimilarWordEngine;
import com.shenji.search.exception.EngineException;
import com.shenji.search.old.SimilarWordDict;

public class SimilarWordDic implements SimilarWordEngine {
	private static double a = 0.65, b = 0.8, c = 0.9, d = 0.96, e = 0.5,
			f = 0.1;
	private static double PI = 3.1415926;
	private Connection connection = null;

	public SimilarWordDic() throws EngineException {
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			throw new EngineException("SimilarWordEngine init Exception", e);
		}
	};

	public void close() {
		if (this.connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
	}

	public double getSimilar(String word1, String word2) {
		double maxSimilar = 0.1;
		String wordCodeStr1 = SimilarWordDict.getInstance().getWordCode(word1);
		String wordCodeStr2 = SimilarWordDict.getInstance().getWordCode(word2);
		// 词不存在
		if (wordCodeStr1 == null || !wordCodeStr1.contains(";"))
			return -1;
		if (wordCodeStr2 == null || !wordCodeStr2.contains(";"))
			return -2;
		String[] wordCode1 = wordCodeStr1.split(";");
		String[] wordCode2 = wordCodeStr2.split(";");
		for (String code1 : wordCode1) {
			if (code1 == null || code1.length() < 7)
				continue;
			for (String code2 : wordCode2) {
				if (code2 == null || code2.length() < 7)
					continue;
				double tempSimilar = getSimilarByCode(code1, code2);
				if (tempSimilar > maxSimilar)
					maxSimilar = tempSimilar;
			}
		}
		return maxSimilar;
	}

	private double getSimilarByCode(String wordCode1, String wordCode2) {
		double similar = 0.0;
		int n = 1;
		int k = 0;
		char[] ch1 = wordCode1.toCharArray();
		char[] ch2 = wordCode2.toCharArray();

		if (ch1[0] != ch2[0])
			return similar;
		if (ch1[1] != ch2[1]) {
			k = Math.abs(ch1[1] - ch2[1]);
			n = SimilarWordDict.getInstance().TreeBranchNum(
					String.valueOf(ch1[0]), 1);
			similar = a * Math.cos(n * PI / 180) * ((double) (n - k + 1) / n);
			return similar;
		}
		String s1 = wordCode1.substring(2, 4);
		String s2 = wordCode2.substring(2, 4);
		if (!s1.endsWith(s2)) {
			k = Math.abs(Integer.valueOf(s1) - Integer.valueOf(s2));
			n = SimilarWordDict.getInstance().TreeBranchNum(
					wordCode1.substring(0, 2), 2);
			similar = b * Math.cos(n * PI / 180) * ((double) (n - k + 1) / n);
			return similar;
		}
		if (ch1[4] != ch2[4]) {
			k = Math.abs(ch1[4] - ch2[4]);
			n = SimilarWordDict.getInstance().TreeBranchNum(
					wordCode1.substring(0, 4), 1);
			similar = c * Math.cos(n * PI / 180) * ((double) (n - k + 1) / n);
			return similar;
		}
		s1 = wordCode1.substring(5, 7);
		s2 = wordCode2.substring(5, 7);
		if (!s1.endsWith(s2)) {
			k = Math.abs(Integer.valueOf(s1) - Integer.valueOf(s2));
			n = SimilarWordDict.getInstance().TreeBranchNum(
					wordCode1.substring(0, 5), 2);
			similar = d * Math.cos(n * PI / 180) * ((double) (n - k + 1) / n);
			return similar;
		}
		return 1.0;
	}

	public int TreeBranchNum(String ch, int count) {
		// List list=SimilarWordDict.getCodeList();
		int num = 0;
		String sql = "select * from words_code where code like ?";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = this.connection.prepareStatement(sql);
			if (count == 1)
				statement.setString(1, ch + "_");
			else
				statement.setString(1, ch + "__");

			resultSet = statement.executeQuery();
			resultSet.last();
			num = resultSet.getRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return num;
	}

	public String getWordCode(String word) {
		String code = null;
		String sql = "select code from words_word where word=?";
		// DBUtil dbUtil=new DBUtil();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = this.connection.prepareStatement(sql);
			statement.setString(1, word);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				code = resultSet.getString("code");
				return code;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
		return code;
	}

	private void initCodeDB(String path) {
		String sql_create = "create table words_code (id int not null auto_increment ,code VARCHAR(10) not null unique, primary key(id))engine=innodb default charset=utf8;";
		String sql_insert = "insert into words_code(code) values(?)";
		File file = new File(path + "同义词编码.txt");

		PreparedStatement statement_create = null;
		PreparedStatement statement_insert = null;
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			List<String> codeList = FileUtils.readLines(file);
			statement_create = connection.prepareStatement(sql_create);
			try {
				statement_create.execute();
			}
			// 这个异常不写日志，如果表存在，后面不做了
			catch (com.mysql.jdbc.exceptions.MySQLSyntaxErrorException e) {
				Log.getLogger(this.getClass()).debug("words_code重复表不创建！");
				return;
			}
			statement_insert = connection.prepareStatement(sql_insert);

			int end = 0;
			for (int i = 0; i < 5; i++) {
				end = end + 1;
				if (i == 2 || i == 4)
					end++;
				for (String str : codeList) {
					String temp = str.substring(0, end);
					statement_insert.clearParameters();
					statement_insert.setString(1, temp);
					try {
						statement_insert.executeUpdate();
						System.out.println(temp);
					}
					// 这个异常不写日志，如果重复数据，不插入
					catch (com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException e) {
						Log.getLogger(this.getClass()).debug("重复数据，不插入！");
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (statement_create != null)
					statement_create.close();
				if (statement_insert != null)
					statement_insert.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	private void initWordDB(String path) {
		String sql_create = "create table words_word (id int not null auto_increment ,word VARCHAR(20) not null unique,codeNum int not null,code VARCHAR(200), primary key(id))engine=innodb default charset=utf8;";
		String sql_insert = "insert into words_word(word,codeNum,code) values(?,?,?)";
		File file = new File(path + "同义词词林.txt");
		Connection con = null;
		PreparedStatement statement_create = null;
		PreparedStatement statement_insert = null;
		try {
			List<String> codeList = FileUtils.readLines(file);
			con = ConnectionPool.getInstance().getConnection();
			statement_create = con.prepareStatement(sql_create);
			try {
				statement_create.execute();
			}
			// 这个异常不写日志，如果表存在，后面不做了
			catch (com.mysql.jdbc.exceptions.MySQLSyntaxErrorException e) {
				System.err.println("words_word重复表不创建！");
				return;
			}
			statement_insert = con.prepareStatement(sql_insert);

			int end = 0;
			for (String str : codeList) {
				String[] temp = str.split(" ");
				statement_insert.clearParameters();
				statement_insert.setString(1, temp[0]);
				statement_insert.setString(2, temp[1]);
				String code = "";
				for (int i = 2; i < Integer.parseInt(temp[1]) + 2; i++) {
					code = code + temp[i] + ";";
				}
				statement_insert.setString(3, code);
				try {
					statement_insert.executeUpdate();
					System.out.println(temp[0] + ":" + code);
				}
				// 这个异常不写日志，如果重复数据，不插入
				catch (com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException e) {
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (statement_create != null)
					statement_create.close();
				if (statement_insert != null)
					statement_insert.close();
				if (con != null)
					con.close();
			} catch (Exception e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}

	}

	/**
	 * @param args
	 * @throws EngineException
	 */
	public static void main(String[] args) throws EngineException {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		SimilarWordEngine engine = new SimilarWordDic();
		//for (int i = 0; i < 100; i++) {
			System.out.println("Similar----------:"
					+ engine.getSimilar("中国", "宁夏"));
		//}
		long end = System.currentTimeMillis();
		engine.close();
		System.err.println("time-------------!!:" + (end - start));

	}

}
