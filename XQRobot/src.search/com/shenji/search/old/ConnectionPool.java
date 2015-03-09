package com.shenji.search.old;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shenji.common.log.Log;
public class ConnectionPool {
//	private static Object LOCK = ConnectionPool.class;
	String driverClassName = "com.mysql.jdbc.Driver";
    String url = "";
    String user = "";
    String password = "";
    Driver driver = null;
	static private ConnectionPool instance;//唯一实例
	private int clients = 0;
	private int maxConn = 5;
	private List<Connection> conns = new ArrayList<Connection>();
	
	private ConnectionPool(){
		try {
			setDB();
			driver = (Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(driver);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e);
		}
	}
	
    private void setDB(){
  	  String dbName = "";
  	  String dbIP = "";
  	  Map map = new HashMap();
  	  try {
  		  BufferedReader bufReader = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("").toURI().getPath()+"/system.ini"));
  		  String line = null;
  	  		while((line = bufReader.readLine())!=null){
  	  			line = line.trim();
  	  			if(line.equals("[dbconfig]")){
  	  				while((line = bufReader.readLine())!=null){
  	  					if(line.matches("\\[.*\\]"))
  	  						break;
  	  					String [] s = line.split("=");
  	  					if(s.length==2){
  	  						map.put(s[0], s[1]);
  	  					}
  	  				}
  	  				break;
  	  			}
  	  		}
  	  		bufReader.close();
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e);
		}
		if(map.get("user")!=null)
			user = map.get("user").toString();
		if(map.get("password")!=null)
			password = map.get("password").toString();
		if(map.get("dbName")!=null)
			dbName = map.get("dbName").toString();
		if(map.get("dbIP")!=null)
			dbIP = map.get("dbIP").toString();
		url = "jdbc:mysql://"+dbIP+"/"+dbName+"?useUnicode=true&characterEncoding=utf8";
    }
	
	public Connection createConnection(){
		Connection con = null;
		try {
			con = DriverManager.getConnection(url, user,password);
			return con;
		}catch (SQLException e) {
			Log.getLogger(this.getClass()).error(e);
			return null;
		}
	}
	static synchronized public ConnectionPool getInstance(){
		if(instance==null){
			instance = new ConnectionPool();
		}
		return instance;
	}
	public Connection getConnection(){
		Connection connection = null;
		long start = System.currentTimeMillis();
		long now;
		synchronized (this) {
			while(clients==maxConn){
				try {
					now = System.currentTimeMillis();
					if((now-start)>5000){
						clients--;
						break;
					}
					//唤醒后接着wait后面的代码执行
					this.wait(1000);
				} catch (InterruptedException e) {
					return null;
				}
			}
			while(conns.size()>0){
				connection = conns.get(0);
				conns.remove(0);
				try {
					if(connection==null||connection.isClosed()||!connection.isValid(1)){
						connection = null;
						continue;
					}
					if(connection.isValid(1)){
						clients++;
						return connection;
					}
				} catch (SQLException e) {
					Log.getLogger(this.getClass()).error(e);
				}
			}
			if(clients<maxConn){
				connection = createConnection();
				clients++;
				return connection;
			}
			return null;
		}

	}
	
	public void setConnection(Connection connection){
		synchronized (this) {
			if(clients>0){
				if(clients==maxConn)
					this.notifyAll();
				clients--;
				conns.add(connection);
			}else{
				try{
					if(!connection.isClosed())
						connection.close();
					connection=null;
				} catch (SQLException e) {
					Log.getLogger(this.getClass()).error(e);
				}
			}
		}
	}
}
