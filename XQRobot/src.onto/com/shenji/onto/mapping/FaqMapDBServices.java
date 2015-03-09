package com.shenji.onto.mapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.DbUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyDBServer;


public class FaqMapDBServices {
	private java.sql.Connection connection;
	//private DbUtil dbUtil;
	private static String MAPPING_TYPE = "Uv::http://www.w3.org/1999/02/22-rdf-syntax-ns#type:";
	private static String ONTO_HEAD = "Uv::";
	private static String ONTO_END = ":";

	public FaqMapDBServices() throws ConnectionPoolException {
		//this.dbUtil = new DbUtil();
		this.connection = ConnectionPool.getInstance().getConnection();
	}

	public void close() {
		if(this.connection!=null)
			try {
				this.connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static String getFaqID(String faqIndividualName) {
		// 去掉其他
		String tempFaqId = faqIndividualName
				.split(Configuration.FaqMappingCommon.INDIVIDUAL_HEAD)[1];
		// 去掉末尾冒号
		String faqId = tempFaqId.substring(0, tempFaqId.length() - 1);
		return faqId;
	}

	public List<String> getFaqByType(String path, String ontoClassName) {
		int faqTableId = OntologyDBServer
				.getGraphId(Configuration.FaqMappingCommon.ontoName);
		String faqTableName = "jena_g" + faqTableId + "t1_stmt";
		String sql = "select Subj from " + faqTableName
				+ " where Prop=? and Obj=?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<String> list = new ArrayList<String>();
		try {
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setString(1, MAPPING_TYPE);
			String obj = ONTO_HEAD + path + ontoClassName + ONTO_END;
			preparedStatement.setString(2, obj);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String subj = resultSet.getString("Subj");
				list.add(subj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public void reNameOntology(String ontoUrl, String oldName, String newName,
			int type) {
		int faqTableId = OntologyDBServer
				.getGraphId(Configuration.FaqMappingCommon.ontoName);
		String faqTableName = "jena_g" + faqTableId + "t1_stmt";
		PreparedStatement preparedStatement = null;
		String oldStr = ONTO_HEAD + ontoUrl + oldName + ONTO_END;
		String newStr = oldStr.replace(oldName, newName);
		String sql = null;
		switch (type) {
		case OntologyCommon.DataType.namedClass:
		case OntologyCommon.DataType.individual:
			sql = "update " + faqTableName + " set Obj=? where Obj=?";
			break;
		case OntologyCommon.DataType.dataTypeProperty:
		case OntologyCommon.DataType.objectProperty:
			sql = "update " + faqTableName + " set Prop=? where Prop=?";
			break;
		}
		try {
			if (sql == null)
				return;
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setString(1, newStr);
			preparedStatement.setString(2, oldStr);
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

	public void delete(String ontoUrl, String name, int type) {
		int faqTableId = OntologyDBServer
				.getGraphId(Configuration.FaqMappingCommon.ontoName);
		String faqTableName = "jena_g" + faqTableId + "t1_stmt";
		String str = ONTO_HEAD + ontoUrl + name + ONTO_END;
		PreparedStatement preparedStatement = null;
		String sql = null;
		switch (type) {
		case OntologyCommon.DataType.namedClass:
		case OntologyCommon.DataType.individual:
			sql = "delete from " + faqTableName + " where Obj=?";
			break;
		case OntologyCommon.DataType.dataTypeProperty:
		case OntologyCommon.DataType.objectProperty:
			sql = "delete from " + faqTableName + " where Prop=?";
			break;
		}
		try {
			if (sql == null)
				return;
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setString(1, str);
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

	public static void main(String[] str) throws ConnectionPoolException {
		long s = System.currentTimeMillis();
		// new FaqMapDBServices().reName("http://www.21esn.com/ceshi.owl#",
		// "data", "vvv",12);
		new FaqMapDBServices().delete("http://www.21esn.com/ceshi.owl#",
				"ddddd", 11);
		long e = System.currentTimeMillis();
		System.err.println(e - s);

	}
}
