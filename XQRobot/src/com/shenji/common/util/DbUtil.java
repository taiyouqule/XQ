package com.shenji.common.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.shenji.common.log.Log;

public class DbUtil {
	public static String systemPath = null;
	public Connection connection = null;
	public static String url = null;
	public static String url_short = null;
	public static String user = null;
	public static String password = null;
	public static String dbname = null;
	public static String driver = null;
	public static String code = null;
	public static final String databaseFile = "conf/database.properties";
	static {
		systemPath = PathUtil.getWebInFAbsolutePath();
		initProperty();
	}

	public static String getUser() {
		return user;
	}

	public static String getPassword() {
		return password;
	}

	public static String getDriver() {
		return driver;
	}

	public static String getUrl() {
		return url;
	}

	public DbUtil() {
		try {
			// ConfigurantionUtil con=new ConfigurantionUtil(null);
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isClosed()) {
				System.out.println("Successed connecting to the Database!");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	private static void initProperty() {
		File file = new File(systemPath + FileUtil.separator + databaseFile);
		ConfigurantionUtil configurantionUtil = new ConfigurantionUtil(file);
		url_short = configurantionUtil.getValue("url_short");
		user = configurantionUtil.getValue("user");
		password = configurantionUtil.getValue("password");
		driver = configurantionUtil.getValue("driver");
		dbname = configurantionUtil.getValue("databaseName");
		code = configurantionUtil.getValue("code");
		url = url_short + dbname + code;
	}

	public Connection getConnection() {
		return connection;
	}

	public void close() {
		try {
			if (!connection.isClosed()) {
				connection.close();
				System.out.println("datebase close success!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

	public static void main(String[] str) {
		new DbUtil();
	}

}
