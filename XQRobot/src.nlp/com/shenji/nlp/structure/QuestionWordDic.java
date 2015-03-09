package com.shenji.nlp.structure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.shenji.common.log.Log;


public class QuestionWordDic {
	
	private int trankDicFromHowNet(){
		int reFlag=1;
		String HowNet_FILE="models/hownet.dat"; 
		String QuestionWord_FILE="models/questionWord.dat";
		String QuestionWord_DIC="models/questionWord.dic";
		File file_src=new File(HowNet_FILE);
		File file_org=new File(QuestionWord_FILE);
		File file_dic=new File(QuestionWord_DIC);
		if(file_org.exists()||file_dic.exists()){
			return -2;
		}
		else{
			try {
				file_org.createNewFile();
				file_dic.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger().error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
		FileInputStream fileInputStream=null;
		InputStreamReader inputStreamReader=null;
		BufferedReader bufferedReader=null;
		
		FileOutputStream fileOutputStream=null;
		OutputStreamWriter outputStreamWriter=null;
		BufferedWriter bufferedWriter=null;
		
		FileOutputStream fileOutputStream2=null;
		OutputStreamWriter outputStreamWriter2=null;
		BufferedWriter bufferedWriter2=null;	
		try {
			fileInputStream = new FileInputStream(file_src);
			inputStreamReader=new InputStreamReader(fileInputStream, "GB2312");
			bufferedReader=new BufferedReader(inputStreamReader);
			
			fileOutputStream=new FileOutputStream(file_org);
			outputStreamWriter=new OutputStreamWriter(fileOutputStream,"UTF-8");
			bufferedWriter=new BufferedWriter(outputStreamWriter);
			
			fileOutputStream2=new FileOutputStream(file_dic);
			outputStreamWriter2=new OutputStreamWriter(fileOutputStream2,"UTF-8");
			bufferedWriter2=new BufferedWriter(outputStreamWriter2);
			String lineStr=null;
			while((lineStr=bufferedReader.readLine())!=null){
				if(lineStr.contains("question")){				
					bufferedWriter.write(lineStr);
					bufferedWriter.newLine();
					bufferedWriter.flush();
					bufferedWriter2.write(lineStr.split("\\s")[0]+" "+"疑问代词");
					bufferedWriter2.newLine();
					bufferedWriter2.flush();
				}	
			}	
			if(bufferedWriter!=null)
				bufferedWriter.close();
			if(bufferedWriter2!=null)
				bufferedWriter2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(bufferedReader!=null)
					bufferedReader.close();
				if(inputStreamReader!=null)
					inputStreamReader.close();
				if(fileInputStream!=null)
					fileInputStream.close();
				
				if(outputStreamWriter!=null)
					outputStreamWriter.close();
				if(fileOutputStream!=null)
					fileOutputStream.close();
				if(outputStreamWriter2!=null)
					outputStreamWriter2.close();
				if(fileOutputStream2!=null)
					fileOutputStream2.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return reFlag;
	}
	
	public static void main(String[] str){
		new QuestionWordDic().trankDicFromHowNet();
	}
}
