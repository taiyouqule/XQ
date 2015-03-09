package com.shenji.onto.editer.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

import javax.xml.crypto.Data;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.util.FileUtils;
import com.shenji.common.log.Log;
import com.shenji.common.util.FileUtil;
import com.shenji.common.util.MD5Util;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.impl.OntoAdd;
import com.shenji.onto.editer.impl.OntoDelete;
import com.shenji.onto.editer.impl.OntoGet;
import com.shenji.onto.editer.impl.OntoQuery;
import com.shenji.onto.editer.impl.OntoSet;
import com.shenji.onto.editer.impl.OntoUpdate;
import com.shenji.onto.editer.inter.IOntoAdd;
import com.shenji.onto.editer.inter.IOntoDelete;
import com.shenji.onto.editer.inter.IOntoGet;
import com.shenji.onto.editer.inter.IOntoQuery;
import com.shenji.onto.editer.inter.IOntoSet;
import com.shenji.onto.editer.inter.IOntoUpdate;
import com.shenji.wordclassification.FAQWordSetServices;
import com.shenji.wordclassification.WordPrepertyBean;


import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGVocabulary;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

/**
 * @author able
 * 本体的管理类
 */
public class OntologyManage extends SocketObservable{
	private String token=null;
	private String tempPath=null;	
	private String openFileName=null;
	private String orcPureFileName=null;
	private JenaOWLModel owlModel=null;
	
	private IOntoAdd abstractAddInterface=null;	
	private IOntoSet abstractSetInterface=null;
	private IOntoGet abstractGetInterface=null;
	private IOntoUpdate abstractUpdateInterface=null;
	private IOntoDelete abstractDeleteInterface=null;
	private IOntoQuery abstractQueryInterface=null;
	private LockedPointServer lockedPointServer;
	private Date tempDate;
	//为了不修改writeTemp，权宜之计
	private boolean isInitManage=true;
	
	private FAQWordSetServices wordSetServices;
	/**
	 * 构造函数
	 * @param openFileFullName 本体路径
	 * @param token 用户凭证
	 */
	public OntologyManage(String openFileName,String token,LockedPointServer lockedPointServer) throws Exception{
		//orcFileContext=null;
		super();
		try {
			this.tempDate=new Date();
			this.token=token;			
			this.openFileName=openFileName;
			if(openFileName.contains("/")||openFileName.contains(".")){
				this.orcPureFileName=openFileName.substring(openFileName.lastIndexOf("/")+1,openFileName.length());
				this.orcPureFileName=orcPureFileName.substring(0,orcPureFileName.lastIndexOf("."));
			}
			else
				this.orcPureFileName=openFileName;
			this.tempPath=Configuration.TempPath+"/"+orcPureFileName;
			//初始化内存操作实例
			initOWLModel();
			//初始化硬盘缓存
			initTemp();			
			initOntologyManage();
			this.lockedPointServer=lockedPointServer;
			wordSetServices=new FAQWordSetServices(this);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 获得用户凭证
	 * @return
	 */
	public String getToken() {
		return this.token;		
	}
	
	public static String getUserName(String token){
		return token.split(OpenFileList.userSeparator)[1];		
	}
	
	public String getTempPath(){
		return this.tempPath;
	}
	
	public String getOpenFileName(){
		return this.openFileName;
	}
	

	/**
	 * 获得缓存文件
	 * @return
	 */
	/*public synchronized String getTempFile() {
		return tempFile;
	}*/

	public synchronized JenaOWLModel getOwlModel() {
		return owlModel;
	
	}

	
	
	public LockedPointServer getLockedPointServer(){
		return this.lockedPointServer;
	}
	
	/**
	 * 实例化本体管理各接口
	 */
	private void initOntologyManage(){
		abstractAddInterface=new OntoAdd(this);
		abstractSetInterface=new OntoSet(this);
		abstractGetInterface=new OntoGet(this);
		abstractUpdateInterface=new OntoUpdate(this);
		abstractDeleteInterface=new OntoDelete(this);	
		abstractQueryInterface=new OntoQuery(this);
	}
	
	/**
	 * 获得本体添加对象
	 * @return
	 */
	public IOntoAdd getAbstractAddInterface(){
		return this.abstractAddInterface;
	}
	/**
	 * 获得本体修改对象
	 * @return
	 */
	public IOntoSet getAbstractSetInterface() {
		return abstractSetInterface;
	}

	/**
	 * 获得本体查询对象
	 * @return
	 */
	public IOntoGet getAbstractGetInterface() {
		return abstractGetInterface;
	}
	
	public IOntoUpdate getAbstractUpdateInterface() {
		return abstractUpdateInterface;
	}

	public IOntoDelete getAbstractDeleteInterface() {
		return abstractDeleteInterface;
	}
	
	public IOntoQuery getAbstractQueryInterface(){
		return abstractQueryInterface;
	}

	
	/**
	 * 初始化缓存
	 */
	private synchronized void initTemp(){
		//第一次写入缓存
		File file=new File(tempPath);
		if(!file.exists())
			file.mkdir();
		long start=System.currentTimeMillis();
		//初始化保存(暂时)
		writeTemp(false);
		long end=System.currentTimeMillis();
		System.err.println("wirteSQL"+(end-start));
		this.isInitManage=false;
		//通过文件读写，已弃用
		/*File file=new File(tempFile);
		if(FileUtil.createFile(file)==1)
			FileUtil.write(file, orcFileContext);	*/
	}
	
	
	/**
	 * 最低间隔2分钟生成缓存文件
	 * @return
	 */
	private boolean isUseWriteTemp(){
		Date newDate=new Date();
		if(newDate.getDay()==tempDate.getDay()&&
				newDate.getHours()==tempDate.getHours()&&
				(newDate.getMinutes()-tempDate.getMinutes()<=Configuration.TEMP_TIME)){
			return false;
		}
		tempDate=newDate;
		return true;
	}
	
	
	///【废弃】现在直接写入数据和缓存中
	/*public synchronized int writeTemp(){	
		FileOutputStream outputStream_orc=null;
		FileOutputStream outputStream_temp=null;
		try {
			outputStream_orc = new FileOutputStream(this.orcFile);	
			if(isUseWriteTemp()||isInitManage){
				SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
				String tempFile=tempPath+File.separator+format.format(tempDate)+".temp";
				outputStream_temp=new FileOutputStream(tempFile);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(outputStream_orc==null)
			return -1;
		else{
			if(outputStream_temp!=null)
				write(outputStream_temp);
			return write(outputStream_orc);
		}
		
	}*/
	/**
	 * 写入缓存并保存到数据库（不是单纯些缓存了）
	 * @return boolean isNeedRest
	 */
	public synchronized int writeTemp(boolean isNeedReset){	
		FileOutputStream outputStream_temp=null;
		OntologyDBServer dbServices=new OntologyDBServer();
		try {
			int reflag=dbServices.JenaOWLModelToMySQL(this.getOwlModel(),this.orcPureFileName,isNeedReset);
			if(reflag!=1)
				return reflag;
			if(isUseWriteTemp()||isInitManage){
				SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
				String tempFile=tempPath+File.separator+format.format(tempDate)+".temp";
				outputStream_temp=new FileOutputStream(tempFile);
			}
			if(outputStream_temp!=null)
				write(outputStream_temp);
			return 1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		finally{
			if(dbServices!=null)
				dbServices.close();
		}	
	}
	
	/**
	 * 讲OWlModel写到文件，现在用于缓存
	 * @param outputStream
	 * @return
	 */
	private int write(FileOutputStream outputStream){
		Writer writer =null;
		OWLModelWriter modelWriter=null;
		try {
			writer = new OutputStreamWriter(outputStream,Configuration.ONTO_CODE);
			modelWriter=new OWLModelWriter(this.getOwlModel(), owlModel.getTripleStoreModel().getActiveTripleStore(), writer);
			modelWriter.write();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		finally{		
			try {
				if(modelWriter!=null)
					modelWriter=null;
				if(writer!=null)
					writer.close();
				if(outputStream!=null)
					outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 1;
	}
	
	/**
	 * 关闭所有流
	 */
	public void close(){		
		try {
			if(owlModel!=null){				
				owlModel.clearAllListeners();
				owlModel.close();			
			}
			if(this.lockedPointServer!=null)
				this.lockedPointServer.close();
			if(wordSetServices!=null)
				wordSetServices.close();
			//回收一下垃圾
			 Log.getLogger(this.getClass()).debug("释放了owlModel资源"+Runtime.getRuntime().totalMemory());
			Runtime.getRuntime().gc();
		} catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void resetOWLModel()throws Exception{
		this.owlModel.close();
		initOWLModel();
	}
	
	/**
	 * 初始化OWL模型
	 * @throws OntologyLoadException 
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	
	//原来从文件，【暂时不用】
	/*private void initOWLModel() throws Exception{	
		FileInputStream inputStream=null;
		Reader reader=null;
		try {		
			inputStream=new FileInputStream(this.orcFile);
			reader= new InputStreamReader(inputStream,StaticCommon.ONTO_CODE);
			//Reader reader=new InputStreamReader(inputStream, code); 
			//this.owlModel=ProtegeOWL.createJenaOWLModelFromReader(reader);
			this.owlModel=ProtegeOWL.createJenaOWLModelFromReader(reader);
		}
		finally{		
			try {
				if(reader!=null)
					reader.close();
				if(inputStream!=null)
					inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getErrorLogger().error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}*/
	
	private void initOWLModel() throws FileNotFoundException, UnsupportedEncodingException, OntologyLoadException {	
		OntologyDBServer dbServices=new OntologyDBServer();
		this.owlModel=dbServices.getJenaOWLModelFromMySQL(this.orcPureFileName);
		if(dbServices!=null)
			dbServices.close();
	}
	
	
		
	/**
	 * 保存本体
	 * @param orcFileName
	 * @return
	 */
	public int saveOntology(String orcFileName){
		synchronized (this.token) {
			File orcFile=new File(orcFileName);
			if(orcFile.exists())
				orcFile.delete();
			FileOutputStream outputStream=null;
			try {
				outputStream = new FileOutputStream(orcFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(outputStream==null)
				return -1;
			int reFlag=write(outputStream);
			//删除硬盘缓存文件
			//this.deleteTemp();
			return  reFlag;
		}
	}
	
	public String[] getMyWordSet(){
		TreeMap<String, String> map=this.wordSetServices.getMyWordSetMap();
		String[] strs=new String[map.size()];
		Iterator<Map.Entry<String, String>> iterator=map.entrySet().iterator();
		int count=0;
		while(iterator.hasNext()){
			Map.Entry<String, String> entry=iterator.next();
			/*//得到词表的时候全未使用
			String values=entry.getValue().replace("true", "false");*/
			String s=entry.getKey()+FAQWordSetServices.separator+entry.getValue();
			strs[count++]=s;
		}	
		return strs;
	}
	
	public void resetWordSet(){
		//重置的时候将本体里的词汇设为使用
		this.wordSetServices.resetWordSet();
	}
	
	//暂时取消删除缓存文件
	/*public void deleteTemp(){
		this.close();
		File file=new File(this.tempFile);
		boolean b=true;
		if(file.exists()&&file.isFile()&&file.canWrite()){
			b=file.delete();	
		}
	}*/

	
}

