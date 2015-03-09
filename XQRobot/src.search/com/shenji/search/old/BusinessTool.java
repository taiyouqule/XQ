package com.shenji.search.old;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.shenji.common.log.Log;
import com.shenji.common.util.FileUse;
import com.shenji.search.Configuration;

public class BusinessTool {

	/**
	 * @param args
	 */
	
	
	public boolean addNewWord(String word,float weight){
		try {
			String path = this.getClass().getClassLoader().getResource("").toURI().getPath();
			File file=new File(path+Configuration.businessDict);
		/*	if(!file.exists()){
				createFile();
			}*/			
			String s=word+" "+weight;
			ArrayList<String> list=new ArrayList<String>();
			list.add(s);				
			FileUse.write(path, Configuration.businessDict, list);			
		} catch (URISyntaxException e) {
			Log.getLogger(this.getClass()).error(e);
			return false;
		}
		return true;
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BusinessTool businessTool=new BusinessTool();
		businessTool.addNewWord("fsdf", 100);
	}

}
