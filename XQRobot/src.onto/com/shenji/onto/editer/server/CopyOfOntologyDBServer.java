package com.shenji.onto.editer.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.db.RDFRDBException;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelFactoryBase;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.vocabulary.DB;
import com.mysql.jdbc.Connection;
import com.shenji.common.log.Log;
import com.shenji.common.util.DbUtil;
import com.shenji.common.util.StringUtil;
import com.shenji.onto.Configuration;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.mapping.FaqMapUtil;

import edu.stanford.smi.protege.code.generator.wrapping.OntologyJavaMappingUtil;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.graph.JenaModelFactory;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

public class CopyOfOntologyDBServer {
	private static String databaseType = "MySQL";
	private IDBConnection connection = null;
	private String ontoPath = null;

	public CopyOfOntologyDBServer() {
		this.ontoPath = Configuration.ontoPath;
		initDBConn();
		// 原先是放在类加载路径，现在改为可设置
		/*
		 * try {
		 * systemPath=this.getClass().getClassLoader().getResource("").toURI
		 * ().getPath()+"temp"+File.separator; } catch (URISyntaxException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public int createOntologyByDB(String ontoName, String url) {
		if (!StringUtil.isAllLetter(ontoName))
			return -3;// 数据库只支持纯英文名
		int flag = 1;
		ModelMaker maker = null;
		try {
			maker = ModelFactory.createModelRDBMaker(this.connection);
			boolean hasModel = maker.hasModel(ontoName);
			if (hasModel == true)// 存在则不创建
				return -1;
			flag = OntologyUtil.createNewOntology(ontoPath, ontoName, url);
			if (flag == 1) // 创建文件成功
				flag = this.jenaFileToMySQL(ontoName, false);
			this.insertOntoInDbMapping(ontoName);
			return flag;
		} catch (com.hp.hpl.jena.shared.AlreadyExistsException exception) {
			// 已经存在
			return -1;
		} finally {
			if (maker != null)
				maker.close();
		}
	}

	public int deleteOntologyByDB(String ontoName) {
		ModelMaker maker = null;
		int flag = 1;
		try {
			maker = ModelFactory.createModelRDBMaker(this.connection);
			boolean hasModel = maker.hasModel(ontoName);
			if (hasModel == false)// 不存在则不删除
				return -2;
			maker.removeModel(ontoName);
			if (!OntologyUtil.deleteOntology(ontoPath + File.separator
					+ ontoName + ".owl"))
				return -3;
			// 删缓存
			File file = new File(Configuration.TempPath + "/" + ontoName);
			if (file.exists())
				file.delete();
			this.deleteOntoInDbMapping(ontoName);
			return flag;
		} finally {
			if (maker != null)
				maker.close();
		}
	}

	private IDBConnection initDBConn() {
		try {
			// this.connection=new DBConnection(connection,databaseType);
			this.connection = new DBConnection(DbUtil.getUrl(),
					DbUtil.getUser(), DbUtil.getPassword(), databaseType);
			Class.forName(DbUtil.getDriver());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out
					.println("ClassNotFoundException, Driver is not available...");
			e.printStackTrace();
		} catch (RDFRDBException e) {
			System.out.println("Exceptions occur...");
			e.printStackTrace();
		}
		return this.connection;
	}

	public void close() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
		}
	}

	public String[] getOntologyNamesFromMySQL() {
		List<String> list = new ArrayList<String>();
		ModelMaker maker = ModelFactory.createModelRDBMaker(this.connection);
		Iterator iterator = maker.getGraphMaker().listGraphs();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			list.add(name);
			// System.out.println(iterator.next());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public JenaOWLModel getJenaOWLModelFromMySQL(String ontoName)
			throws FileNotFoundException, UnsupportedEncodingException,
			OntologyLoadException {
		JenaOWLModel jenaOWLModel = null;
		FileInputStream inputStream = null;
		Reader reader = null;
		OntModel ontModel = null;
		try {
			// 先从数据库获得ontModel对象
			ontModel = getModelFromMySQL(ontoName, true);
			inputStream = new FileInputStream(ontoPath + File.separator
					+ ontoName + ".owl");
			reader = new InputStreamReader(inputStream, "UTF-8");
			// Reader reader=new InputStreamReader(inputStream, code);
			// this.owlModel=ProtegeOWL.createJenaOWLModelFromReader(reader);
			jenaOWLModel = ProtegeOWL.createJenaOWLModelFromReader(reader);
		} finally {
			try {
				if (ontModel != null)
					ontModel.close();
				if (reader != null)
					reader.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger().error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
		return jenaOWLModel;
	}

	private OntModelSpec getModelSpec(ModelMaker maker) {
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setImportModelMaker(maker);
		return spec;
	}

	public OntModel getModelFromMySQL(String ontoName, boolean isWriteFile)
			throws FileNotFoundException {
		ModelMaker maker = ModelFactory.createModelRDBMaker(this.connection);
		Model model = maker.getModel(ontoName);
		OntModel ontModel = ModelFactory.createOntologyModel(
				getModelSpec(maker), model);
		// OntModel ontModel=ModelFactory.createOntologyModel();
		FileOutputStream outputStream = null;
		OutputStreamWriter streamWriter = null;
		try {
			if (isWriteFile) {
				File file = new File(ontoPath + File.separator + ontoName
						+ ".owl");
				outputStream = new FileOutputStream(file);
				streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				ontModel.write(streamWriter, FaqMapUtil.RDF_LIVEL);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (streamWriter != null)
					streamWriter.close();
				if (outputStream != null)
					outputStream.close();
				if (maker != null)
					maker.close();
				// model.close()
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ontModel;
	}

	public int JenaOWLModelToMySQL(JenaOWLModel jenaOWLModel, String ontoName,
			boolean isNeedReset) {
		OWLModelWriter modelWriter = null;
		Writer writer = null;
		File file = null;
		int reFlag = -2;
		try {
			file = new File(ontoPath + File.separator + ontoName + ".owl");
			writer = new FileWriter(file);
			if (writer != null) {
				modelWriter = new OWLModelWriter(jenaOWLModel, jenaOWLModel
						.getTripleStoreModel().getActiveTripleStore(), writer);
				modelWriter.write();
			}

			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reFlag = jenaFileToMySQL(ontoName, isNeedReset);
			// jenaOWLModel.getOntModel().commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reFlag;

	}

	public void jenaModelToFile(OntModel model, String ontoName) {
		ModelMaker maker = ModelFactory.createModelRDBMaker(this.connection);
		OntModel ontModel = ModelFactory.createOntologyModel(
				getModelSpec(maker), model);
		FileOutputStream outputStream = null;
		OutputStreamWriter streamWriter = null;
		try {

			File file = new File(ontoPath + File.separator + ontoName + ".owl");
			outputStream = new FileOutputStream(file);
			streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
			ontModel.write(streamWriter, FaqMapUtil.RDF_LIVEL);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (streamWriter != null)
					streamWriter.close();
				if (outputStream != null)
					outputStream.close();
				if (maker != null)
					maker.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class JenaFileToMySQLResetThread implements Runnable {
		private ModelMaker maker;
		private Model defMode;
		private String ontoName;
		private InputStreamReader inputStreamReader;
		public JenaFileToMySQLResetThread(ModelMaker maker, Model defMode,
				String ontoName, InputStreamReader inputStreamReader) {
			// TODO Auto-generated constructor stub
			this.maker=maker;
			this.defMode=defMode;
			this.ontoName=ontoName;
			this.inputStreamReader=inputStreamReader;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (CopyOfOntologyDBServer.class) {
				this.maker.getModel(ontoName).removeAll();
				this.defMode = maker.createModel(ontoName, false);
				this.defMode.read(inputStreamReader, null);
				this.defMode.commit();
			}

		}

	}

	public int jenaFileToMySQL(String ontoName, boolean isNeedReset) {
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		File file = null;
		ModelMaker maker = null;
		Model defMode = null;
		try {
			file = new File(ontoPath + File.separator + ontoName + ".owl");
			inputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			maker = ModelFactory.createModelRDBMaker(this.connection);
			if (isNeedReset) {				
				maker.getModel(ontoName).removeAll();
				defMode = maker.createModel(ontoName, false);
				//不能放线程，会出问题
				//Thread thread=new Thread(new JenaFileToMySQLResetThread(maker, defMode, ontoName, inputStreamReader));
				//thread.start();
			} else {
				defMode = maker.createModel(ontoName);			
			}
			defMode.read(inputStreamReader, null);
			defMode.commit();
			return 1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -3;
		} finally {
			try {
				if (inputStreamReader != null)
					inputStreamReader.close();
				if (inputStream != null)
					inputStream.close();
				if (defMode != null)
					defMode.close();
				if (maker != null)
					maker.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void deleteOntoInDbMapping(String ontoName) {
		DbUtil dbUtil = new DbUtil();
		java.sql.Connection connection = dbUtil.getConnection();
		createDbMappingTable();
		PreparedStatement preparedStatement_delete = null;
		String sql = "delete from jena_mapping where name=?";
		try {
			preparedStatement_delete = connection.prepareStatement(sql);
			preparedStatement_delete.setString(1, ontoName);
			preparedStatement_delete.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement_delete != null)
					preparedStatement_delete.close();
				if (dbUtil != null)
					dbUtil.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void insertOntoInDbMapping(String ontoName) {
		DbUtil dbUtil = new DbUtil();
		java.sql.Connection connection = dbUtil.getConnection();
		createDbMappingTable();
		PreparedStatement preparedStatement_query = null;
		PreparedStatement preparedStatement_insert = null;
		String sql_query = "select * from jena_graph";
		String sql_insert = "insert into jena_mapping values(?,?)";
		ResultSet resultSet = null;
		boolean reFlag = false;
		try {
			preparedStatement_query = connection.prepareStatement(sql_query);
			int rows = 0;
			try {
				resultSet = preparedStatement_query.executeQuery();
			} catch (Exception e) {
				// 表不存在，不处理
			}
			if (resultSet != null) {
				resultSet.last();
				rows = resultSet.getRow();
			}
			preparedStatement_insert = connection.prepareStatement(sql_insert);
			preparedStatement_insert.setInt(1, rows);
			preparedStatement_insert.setString(2, ontoName);
			preparedStatement_insert.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement_query != null)
					preparedStatement_query.close();
				if (preparedStatement_insert != null)
					preparedStatement_insert.close();
				if (dbUtil != null)
					dbUtil.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static int getGraphId(String ontoName) {
		DbUtil dbUtil = new DbUtil();
		java.sql.Connection connection = dbUtil.getConnection();
		PreparedStatement preparedStatement = null;
		String sql = "select id from jena_mapping where name=?";
		ResultSet resultSet = null;
		int id = -1;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, ontoName);
			resultSet = preparedStatement.executeQuery();
			resultSet.first();
			id = resultSet.getInt("id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
				if (dbUtil != null)
					dbUtil.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return id;
	}

	private void createDbMappingTable() {
		DbUtil dbUtil = new DbUtil();
		java.sql.Connection connection = dbUtil.getConnection();
		PreparedStatement preparedStatement = null;
		String sql = "create table if not exists jena_mapping (id int unsigned not null unique,name VARCHAR(30) unique, primary key(id))engine=innodb default charset=utf8;";
		boolean reFlag = false;
		try {
			preparedStatement = connection.prepareStatement(sql);
			reFlag = preparedStatement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (dbUtil != null)
					dbUtil.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
