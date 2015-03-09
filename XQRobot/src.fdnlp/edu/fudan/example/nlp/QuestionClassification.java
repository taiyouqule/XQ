package edu.fudan.example.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class QuestionClassification {
	
	public static void main(String[] str) throws JDOMException, IOException{
		new QuestionClassification().questionUsage();
	}

	public void questionUsage() throws JDOMException, IOException{
		String xmlpath="./example-data/text-classification/Fudan Question Bank.xml";
		SAXBuilder builder=new SAXBuilder(false);
		Document doc=builder.build(xmlpath);
		Element root=doc.getRootElement();
		//Set<String> set=new HashSet<String>();
		List<Element> childrenList=root.getChildren();
		HashMap<String,List<String>> map=new HashMap<String, List<String>>();
		HashMap<String,FileWriter> fileMap=new HashMap<String, FileWriter>();
		for(Element e:childrenList){
			String Question=e.getChildTextTrim("Question");
			String QuestionStyle=e.getChildTextTrim("QuestionStyle");
			String AnswerType1=e.getChildTextTrim("AnswerType1");
			String type=QuestionStyle+"#"+AnswerType1;
			//sop(Question);
			//sop(QuestionStyle);
			if(!map.containsKey(type)){
				List<String> list=new ArrayList<String>();
				list.add(Question);
				map.put(type, list);
				File file=new File("./example-data/text-classification/"+type+".data");
				FileWriter writer=new FileWriter(file, true);
				fileMap.put(type, writer);
				sop(type);
			}
			else
			{
				map.get(type).add(Question);
			}
			//set.add(QuestionStyle);
		}
		
		Iterator<Map.Entry<String, List<String>>> iterator=map.entrySet().iterator();
		int count=0;
		while(iterator.hasNext()){
 			Map.Entry<String, List<String>> m=iterator.next();
 			//sop(m.getKey()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
 			List<String> values=m.getValue();
 			FileWriter writer=fileMap.get(m.getKey());
 			if(values==null)
 				continue;
 			for(String s:values){
 				writer.write(s+"\r\n");
 				//sop((count++)+":"+s);
 			}
 			writer.close();
		}
		if(map!=null)
			map.clear();
		if(fileMap!=null)
			fileMap.clear();
		sop("");
	}
	
	public static void sop(Object obj){
		System.out.println(obj);
	}
	
}
