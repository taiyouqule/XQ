package com.shenji.wordclassification;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.DbUtil;
import com.shenji.onto.Configuration;
import com.shenji.onto.mapping.JsonFaqHtml;
import com.shenji.search.ResourcesControl;

public class FAQDataAnalysis {

	public List<WordPrepertyBean> queryTable(String tableName) {
		String sql = "select * from " + tableName;
		//DbUtil dbUtil = new DbUtil();
		Connection connection;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		PreparedStatement preparedStatement = null;
		List<WordPrepertyBean> list = new ArrayList<WordPrepertyBean>();
		int count = -1;
		try {
			preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				WordPrepertyBean bean = new WordPrepertyBean();
				bean.setWord(resultSet.getString("word"));
				bean.setSpeech_en(resultSet.getString("en"));
				bean.setSpeech_ch(resultSet.getString("ch"));
				bean.setSource(resultSet.getString("source"));
				bean.setCount(resultSet.getInt("count"));
				list.add(bean);
				// System.err.println(bean.getWord());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * @param start
	 *            包含
	 * @param end
	 *            不包含
	 */
	public void excuteInsertTable(String tableName, int start, int end,
			String type) {
		// 需要改
		String allFalHtml = new ResourcesControl().listAllFaq();
		List<String> arrayList = JsonFaqHtml.jsoupAllFAQHtml(allFalHtml, type);
		int count = 0;
		// DbUtil dbUtil = new DbUtil();
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		int startScale = start;
		PreparedStatement preparedStatement = null;
		try {
			connection.setAutoCommit(false);
			String sql = "insert into " + tableName
					+ "(word,en,ch,source) values(?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			if (end == -1)
				end = arrayList.size();
			for (int i = start; i < end; i++) {
				if (i >= (arrayList.size()))
					break;
				if (arrayList.get(i).length() == 0)
					continue;
				ArrayList<WordPrepertyBean> list = faqWordSpeechTagging(arrayList
						.get(i));
				for (WordPrepertyBean bean : list) {
					setInsertPreparedStatement(tableName, preparedStatement,
							bean, connection);
				}
				// System.err.print(end+":"+startScale+":"+count);
				double scale = (double) count / (end - startScale);
				System.out.println("当前执行完：第 " + (count++) + " 条     完成比例"
						+ scale * 100 + "%");
			}
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public boolean excuteCreateTable(String tableName) {
		// if not exists
		String sql = "create table "
				+ tableName
				+ "(id int unsigned not null auto_increment,word VARCHAR(40) unique,en CHAR(2),ch varCHAR(6),source varCHAR(16),count int default 1, primary key(id))engine=innodb default charset=utf8";
		PreparedStatement preparedStatement = null;
		// DbUtil dbUtil = new DbUtil();
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
		} catch (ConnectionPoolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean flag = true;
		try {
			preparedStatement = connection.prepareStatement(sql);
			// preparedStatement.setString(1, "sdd");
			preparedStatement.execute();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	private void excuteUpdateDuplicateCount(String tableName, String word,
			int count, java.sql.Connection connection) {
		String sql = "update " + tableName + " set count=? where word=?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, count + 1);
			preparedStatement.setString(2, word);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private int selectWordCount(String tableName, String word,
			java.sql.Connection connection) {
		String sql = "select count from " + tableName + " where word=?";
		PreparedStatement preparedStatement = null;
		int count = -1;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, word);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			count = resultSet.getInt("count");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}

	private void setInsertPreparedStatement(String tableName,
			PreparedStatement preparedStatement, WordPrepertyBean bean,
			java.sql.Connection connection) {
		try {
			if (bean.getWord().length() == 0)
				return;
			preparedStatement.setString(1, bean.getWord());
			preparedStatement.setString(2, bean.getSpeech_en());
			preparedStatement.setString(3, bean.getSpeech_ch());
			preparedStatement.setString(4, bean.getSource());
			try {
				preparedStatement.executeUpdate();
			} catch (com.mysql.jdbc.MysqlDataTruncation e) {
				Log.getLogger().error(e + "::" + bean.getWord());
				System.err.println("Error:long for column 'word' at row 1"
						+ bean.getWord());
				return;
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
				int count = selectWordCount(tableName, bean.getWord(),
						connection);
				if (count != -1)
					excuteUpdateDuplicateCount(tableName, bean.getWord(),
							count, connection);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// Log.getErrorLogger().error(e.getMessage(),e);
			e.printStackTrace();
		}
	}

	/**
	 * @param url
	 * @return String[] 0:url 1:q 2:a
	 * @throws IOException
	 */
	/**
	 * @param url
	 * @param obj
	 *            过滤参数，如传入StaticCommon.FaqMappingCommon.Data_A则不解析a
	 * @return String[] 0:url 1:q 2:a
	 * @throws IOException
	 */
	public static String[] jsoupOneFAQHtml(String url, Object... objs)
			throws IOException {
		String[] str = new String[3];
		Elements eTemp;
		long start = System.currentTimeMillis();
		Document doc = Jsoup.connect(url).get();
		Set<Object> objSet = null;
		if (objs != null && objs.length != 0) {
			objSet = new HashSet<Object>();
			for (Object obj : objs) {
				objSet.add(obj);
			}
		}
		if (null != doc) {
			if (objSet == null
					|| !objSet
							.contains(Configuration.FaqMappingCommon.Data_URL))
				str[0] = url;
			// 如果问题为空直接这条FAQ格式不正确，直接返回空
			if (objSet == null
					|| !objSet.contains(Configuration.FaqMappingCommon.Data_Q)) {
				if ((eTemp = doc
						.getElementsByClass(Configuration.FaqMappingCommon.Data_Q))
						.size() <= 0) {
					return null;
				}
				str[1] = eTemp.get(0).text();
			}
			if (objSet == null
					|| !objSet.contains(Configuration.FaqMappingCommon.Data_A)) {
				str[2] = doc
						.getElementsByClass(
								Configuration.FaqMappingCommon.Data_A).get(0)
						.text();
			}
		}
		if (objSet != null)
			objSet.clear();
		long end = System.currentTimeMillis();
		// Log.promptMsg("解析URL:(" + url + ")花费时间：" + (end - start));
		return str;
	}

	public ArrayList<WordPrepertyBean> faqWordSpeechTagging(String question) {
		WordPreProcess preProcess = new WordPreProcess();
		// new WordPreProcess().sopArrayList(preProcess.wordPreProcess(question,
		// false));
		return preProcess.wordPreProcess(question, false);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * // TODO Auto-generated method stub ArrayList<String>
		 * list=ExcelUtil.readFile("D:\\11.xls",0,0,100,900); for(String
		 * str:list){ new FAQDataAnalysis().faqWordSpeechTagging(str); }
		 * FAQDataAnalysis analysis=new FAQDataAnalysis(); long
		 * start=System.currentTimeMillis(); ArrayList<String>
		 * urls=analysis.jsoupAllFAQHtml(analysis.getAllFaqHtml(),A_HREF);
		 * for(String s:urls){ //s=s.replace("www.62111929.net:8088",
		 * "localhost:8070"); analysis.jsoupOneFAQHtml(s); } long
		 * end=System.currentTimeMillis(); System.err.println(end-start); String
		 * tableName="speechtagging_a";
		 * if(analysis.excuteCreateTable(tableName))
		 * analysis.excuteInsertTable(tableName,500,-1,"a");
		 * //System.out.print(analysis.getMyDict());
		 */
	}

}
