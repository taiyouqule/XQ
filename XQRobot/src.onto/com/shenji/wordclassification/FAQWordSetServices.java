package com.shenji.wordclassification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.shenji.common.action.ConnectionPool;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.onto.editer.server.OntologyManage;


public class FAQWordSetServices {
	
	private TreeMap<String,String> myWordSetMap=new TreeMap<String,String>();
	private static String sql="select * from speechtagging";
	private OntologyManage manage;
	public final static String separator=" ";
	public FAQWordSetServices(OntologyManage manage) throws ConnectionPoolException{
		this.manage=manage;
		//this.fileStr=manage.getTempPath()+File.separator+fileName;
		this.myWordSetMap=this.getSystemWordSet();
		resetWordSet();
	}
	
	public TreeMap<String,String> getMyWordSetMap(){
		return myWordSetMap;
	}
	public void close(){
		if(this.myWordSetMap!=null)
			this.myWordSetMap.clear();
	}
	
	
	private TreeMap<String,String> getSystemWordSet() throws ConnectionPoolException{	
		//DbUtil dbUtil=new DbUtil();
		TreeMap<String,String> map=new TreeMap<String,String>();
		Connection connection=ConnectionPool.getInstance().getConnection();
		ResultSet resultSet=this.query(connection,sql, null);
		int count=0;
		try {
			if(resultSet==null)
				return null;
			while(resultSet.next()){
				WordPrepertyBean bean=new WordPrepertyBean();
				bean.setWord(resultSet.getString("word"));
				bean.setSpeech_en(resultSet.getString("en"));
				bean.setSpeech_ch(resultSet.getString("ch"));
				bean.setSource(resultSet.getString("source"));
				bean.setCount(resultSet.getInt("count"));
				map.put(bean.getWord(),bean.getSpeech_en()+separator+
						bean.getSpeech_ch()+separator+bean.getSource()+separator+
						bean.getCount()+separator+bean.isUsed());
				//System.out.println(bean.getWord()+" "+map.get(bean.getWord()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{	
			try {
				if(resultSet!=null)
					resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	
		return map;
	}
	
	
	public void resetWordSet(){
		Iterator<Map.Entry<String, String>> iterator=myWordSetMap.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, String> entry=iterator.next();
			myWordSetMap.put(entry.getKey(), entry.getValue().replace("true", "false"));
		}	
		for(String str:this.manage.getAbstractGetInterface().getALLOntologyWords()){		
			if(myWordSetMap.containsKey(str)){
				String values=myWordSetMap.get(str);
				values=values.replace("false", "true");
				myWordSetMap.put(str, values);
			}	
		}	
	}
	
	private ResultSet query(Connection connection,String str,Object[] obj){	
		String sql=str;
		PreparedStatement preparedStatement=null;
		try {
			//不能关闭，所以用全局pre
			preparedStatement=connection.prepareStatement(sql);
			if(obj!=null){
				for(int i=0;i<obj.length;i++){
					preparedStatement.setObject(i, obj[i]);
				}
			}
			return preparedStatement.executeQuery();		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	public static void main(String[] str){
		/*long startTime=System.currentTimeMillis();
		new FAQWordSetServices(null).getSystemWordSet();
		long endTime=System.currentTimeMillis();
		System.err.println(endTime-startTime);*/
	}
	
	
	
}
