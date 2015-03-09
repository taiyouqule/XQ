package com.shenji.search.services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.shenji.common.util.FileUse;



public class HtmlService{
	/**
	 * 读取一个HTML文档。得到整个html标签内容
	 * @param path
	 * @return
	 */
	public static String getHtml(String path){		
		String html=null;
		File file=new File(path);
		List<String> list=FileUse.read(file);	
		html=list.toString();
		html=html.substring(1,html.length()-1);
		//System.out.println(html);
		return html;
		
	}
	/**
	 * 读取一个HTML文档。得到Meta标签内容
	 * @param path
	 * @return
	 */
	public static String getMeta(String path){
		String html=HtmlService.getHtml(path);
		String meta=null;
		int start=html.indexOf("<meta");
		html=html.substring(start);
		int end=html.indexOf(">");
		meta=html.substring(0, end+1);
		//System.out.println(meta);
		return meta;
		
	}
	
	/**
	 * 读取一个HTML文档。得到Body标签内容
	 * @param path
	 * @return
	 */
	public static String getBody(String path){
		String html=HtmlService.getHtml(path);
		String body=null;
		int start=html.indexOf("<body>");
		int end=html.lastIndexOf("</body>")+"</body>".length()-1;
		body=html.substring(start, end+1);
		//System.out.println(body);
		return body;
		
	}
	
	public static String getContextFromBody(String path){
		String body=HtmlService.getBody(path);
		String context=body.replace("<body>", "");
		context=context.replace("</body>", "");
		System.out.println(context);
		return context;
	}
	
	public static String changeHtmlMeta(String path,String newMeta){	
		String html=HtmlService.getHtml(path);	
		String oldMeta=HtmlService.getMeta(path);
		html=html.replace(oldMeta, newMeta);
		//System.out.println(html);					
		return html;
	}
	
	/**
	 * 得到meta标签内所有评分
	 * @param path
	 * @return
	 */
	public static String[] getAllScore(String path){
		List<String> list=new ArrayList<String>();
		String meta=HtmlService.getMeta(path);
		String type="score";
	    if(!meta.contains(type))
	    	return null;
	    String[] temp=meta.split(type);
	    for(int i=1;i<temp.length;i++){
	    	char[] chars=temp[i].toCharArray();
	    	int count=0;
	    	int start=0;
	    	int end=0;
			for(int j=0;j<chars.length;j++){
				if(chars[j]=='"'&&count==0){
					start=j;
					count++;
					continue;
				}
				if(chars[j]=='"'&&count==1){
					end=j;
					break;
				}			
			}
			String name=temp[i].substring(start+1,end);
			String content=getContentFromMeta(path, name, type);
			System.out.println(content);
			list.add(content);
	    }	
		
		return (String[])list.toArray(new String[list.size()]);
	}
	
	public static String getContentFromMeta(String path,String name,String type){
		String content=null;
		String meta=getMeta(path);
		int start=meta.indexOf(type+"=\"")+type.length();
		meta=meta.substring(start);		
		start=meta.indexOf("\""+name+"\"");
		if(start==-1)
			return null;
		start=start+name.length()+2;
		meta=meta.substring(start,meta.length());	
		start=meta.indexOf("content")+"content".length();
		meta=meta.substring(start);	
		char[] temp=meta.toCharArray();
		int count=0;
		int end=0;
		for(int i=0;i<temp.length;i++){
			if(temp[i]=='"'&&count==0){
				start=i;
				count++;
				continue;
			}
			if(temp[i]=='"'&&count==1){
				end=i;
				break;
			}					
		}
		content=meta.substring(start+1,end);		
		//System.out.println(content);
		return content;
		
	}
	/**
	 * 过时的方法
	 * @param path
	 * @param name
	 * @return
	 */
	private static String getContent(String path,String name){
		String content=null;
		String meta=getMeta(path);
		String[] temp=meta.split(name);		
		if(temp.length<2)
			return null;
		String tempMeta=meta.split(name)[1];		
		int start=tempMeta.indexOf("content")+"content".length();
		int end=0;
		char[] chars=tempMeta.toCharArray();
		int count=0;
		for(int i=start;i<chars.length;i++){
			if(chars[i]=='"'&&count==0){
				start=i+1;
				count++;
				continue;
			}
			if(chars[i]=='"'&&count==1){
				end=i+1;
				break;
			}			
		}
		content=tempMeta.substring(start, end-1);
		System.err.println(content);
		return content;
	}
	
	public static void main(String[] strings) throws IOException{
		//new HtmlService().getBody("C:\\Users\\sj\\Desktop\\e53a0a2978c28872a4505bdb51db06dc.htm");
		//new HtmlService().getContentFromMeta("C:\\Users\\sj\\Desktop\\e53a0a2978c28872a4505bdb51db06dc.htm","endTime","name");
		//new HtmlService().getMeta("C:\\Users\\sj\\Desktop\\e53a0a2978c28872a4505bdb51db06dc.htm");
		new HtmlService().getAllScore("C:\\Users\\sj\\Desktop\\e53a0a2978c28872a4505bdb51db06dc.htm");
		//new HtmlService().getContent(new HtmlService().getMeta("C:\\Users\\sj\\Desktop\\e53a0a2978c28872a4505bdb51db06dc.htm"), "");
	}
}	


	
	
	
	
	

