package com.shenji.search.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import com.shenji.common.log.Log;

public class DBFeedBackManager {
	private Connection connection=null;
	private Statement statement=null;
	private ResultSet resultSet=null;
	/**
	 * ���캯��
	 * @param connection
	 */
	public DBFeedBackManager(Connection connection){	
		try {
			this.connection=connection;
			this.statement=connection.createStatement();			
		} catch (SQLException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}
	
	
	private int getDate() {
		Calendar now= Calendar.getInstance();
		return (now.get(Calendar.YEAR)*366+now.get(Calendar.MONTH)*31+now.get(Calendar.DAY_OF_MONTH))/7;
	}
	
   
    
    public void ExecuteUpdate(String sql) {
		try {
			statement.executeUpdate(sql);
		} catch (Exception e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
	}

    public int modify(String url,String which,String count){
    	int date = getDate();
    	String[] temp=url.split(which);
    	if(temp.length<2)
    		return -1;
    	url=url.split(which)[1];
    	url=which+url;
    	url=url.replace("//", "/");
    	url=url.replace("\\", "/");
    	int myCount=Integer.valueOf(count);
        String sql = "update "+which+" set count=count+"+myCount+" where url ='"+url+"';";
        try {
            if(statement.executeUpdate(sql)<1)
            	return Add(url,which);
        }catch (Exception e){
        	Log.getLogger(this.getClass()).error(e.getMessage(),e);
            return -1;
        }
        return 1;
    }
    
   /* public int SearchInt(String sql) {
		try {
			statement = conn.createStatement();  
			ResultSet rs = statement.executeQuery(sql);
			if(rs.next())
			{
				return rs.getInt("Score");
			}
			else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}*/
    public void close(){	
		try {
			if(statement!=null)
				statement.close();
		} catch (SQLException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
    }
    private int Add(String url,String which) {
    	int date = getDate();
    	String sql = "insert into "+which+" (url,count,createtime) values('"+url+"',1,now())";
    	ExecuteUpdate(sql);
    	return 1;
	}
    
}
