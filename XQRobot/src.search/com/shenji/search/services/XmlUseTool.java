package com.shenji.search.services;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.shenji.common.log.Log;
import com.shenji.search.bean.XmlDialogueBean;

public class XmlUseTool {	
	
	/**
	 * @param args
	 */
	public static XmlDialogueBean Dom4jRead(String str){
		XmlDialogueBean dialogueBean=new XmlDialogueBean();
		SAXReader saxReader=new SAXReader();
		try {
			//读取一个xml串
			Document document=DocumentHelper.parseText(str);
			Element root=document.getRootElement();
			for(Iterator iterator=root.elementIterator();iterator.hasNext();){
				Element element=(Element)iterator.next();
				if(element.getName().equals("sessionid"))
					dialogueBean.setSessionId(element.getText());
				if(element.getName().equals("starttime"))
					dialogueBean.setStartTime(element.getText());
				if(element.getName().equals("endtime"))
					dialogueBean.setEndTime(element.getText());
				if(element.getName().equals("record")){
					String speaker=null;
					String time=null;
					String content=null;
					for(Iterator iteratorInner=element.elementIterator();iteratorInner.hasNext();){
						Element elementInner = (Element) iteratorInner.next();  
						if(elementInner.getName().equals("speaker"))
							speaker=elementInner.getText();
						if(elementInner.getName().equals("time"))
							time=elementInner.getText();
						if(elementInner.getName().equals("content"))
							content=elementInner.getText();
					}
					dialogueBean.addRocord(speaker, time, content);
				}
			}
			
			
		} catch (DocumentException e) {
			Log.getLogger(XmlUseTool.class).error(e.getMessage(),e);
			return null;
		}		
		return dialogueBean;	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str="<dialog>" +
				"<sessionid></sessionid>" +
				"<starttime>2013-09-13 09:08:32</starttime>" +
				"<endtime>2013-09-13 09:18:32</endtime>" +
				"<record><speaker>s</speaker><time>123132</time><content>4444</content></record>" +
				"<record><speaker></speaker><time>12312312</time><content>4444</content></record>" +
				"</dialog> ";
		XmlUseTool tool=new XmlUseTool();
		XmlDialogueBean bean;
		bean=tool.Dom4jRead(str);
		
		System.out.println(bean.getRocords().get(0).getSpeaker());
	}

}
