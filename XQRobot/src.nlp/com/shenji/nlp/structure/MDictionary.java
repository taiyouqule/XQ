package com.shenji.nlp.structure;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.shenji.common.log.Log;
import com.shenji.nlp.common.Common;

import edu.fudan.ml.types.Dictionary;

public class MDictionary extends Dictionary {
	private MDictionary(String[] path) throws IOException{
		super();
		super.setAmbiguity(false);
		ArrayList<String[]> al = this.loadDict(path);		
		add(al); 
		super.createIndex();
	}
	protected ArrayList<String[]> loadDict(String[] paths) throws IOException {
		ArrayList<String[]> al = new ArrayList<String[]>();
		for(String path:paths){
			Scanner scanner = new Scanner(new FileInputStream(path), "utf-8");	
			while(scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if(line.length() > 0) {
					String[] s = line.split("\\s");//任意空白字符，\s需转义
					al.add(s);
				}
			}
			scanner.close();
		}		
		return al;
	}
	private static MDictionary dict;
	public static MDictionary getInstance() throws IOException{
        if(dict==null){
            synchronized(MDictionary.class){        
                String[] paths={Common.UserSpeechDic,Common.QuestionWordsDic};
				dict = new MDictionary(paths);				
            }
        }
		return dict;
	}
}
