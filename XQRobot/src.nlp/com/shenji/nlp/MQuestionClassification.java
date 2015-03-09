package com.shenji.nlp;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.fnlp.app.tc.TextClassifier;

import com.shenji.nlp.common.Common;


import edu.fudan.data.reader.FileReader;
import edu.fudan.data.reader.Reader;
import edu.fudan.example.nlp.TextClassificationInstance;

/**
 * 问题分类
 * @author zhq
 *
 */
public class MQuestionClassification {
	private TextClassifier tc;
	private final static String CODE="UTF-8";
	private final static String TYPE=".data";
	private String modelPath=null;

	public MQuestionClassification(String src) throws Exception{
		tc = new TextClassifier();
		//用不同的Reader读取相应格式的文件
		Reader reader = new FileReader(src,CODE,TYPE);
		tc.train(reader, Common.QClassificationModelFile);
		this.modelPath=src;
	}
	
	public String predict(String str){
		/**
		 * 分类器使用
		 */
		String label = (String) tc.classify(str).getLabel(0);
		return label;
		
	}
	
	public String[] getSupportedTags(){
		List<String> list=new ArrayList<String>();
		File file=new File(modelPath);
		if(!file.isDirectory())
			return null;
		File[] fileList=file.listFiles(new FileFilter() {		
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().endsWith(TYPE);
			}
		});
		for(File f:fileList){
			list.add(f.getName().replace(TYPE, ""));
		}
		return list.toArray(new String[list.size()]);
	}
	
}
