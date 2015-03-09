package com.shenji.search.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import com.shenji.common.log.Log;
import com.shenji.search.old.TextExtract;


public class WebSearchThread implements Callable<String> {

	private static String GetEncoding(URL u){
		try{
			URLConnection httpUrlConnection=u.openConnection();
			httpUrlConnection.connect();
			String contentEncoding=httpUrlConnection.getContentEncoding();
			String contentType=httpUrlConnection.getContentType();
			InputStream inputStream=httpUrlConnection.getInputStream();
			if(contentEncoding==null){
				int index=contentType.indexOf("charset=");
			if(index==-1){
				//System.out.println("----InputStream----");
				return getEncodingByInputStream(inputStream);
			}else{
				//System.out.println("----contentType----");
				String t=contentType.substring(index+8);
				return t.toUpperCase();
			}
			}else{
				//System.out.println("----contentEncoding----");
				return contentEncoding;
			}
		}catch(Exception e){
			Log.getLogger(WebSearchThread.class).error(e.getMessage(),e);
			return null;
		}
	}
	
	private static String getEncodingByInputStream(InputStream inputStream){
		try {
			StringBuffer sb=new StringBuffer("1234567");
			StringBuffer sb2=new StringBuffer();
			int t;
			while ((t=inputStream.read())!=-1) {
			sb.deleteCharAt(0);
			sb.append((char)t);
			if(sb.toString().toLowerCase().equals("charset")){
			for (int i = 0; i < 10; i++) {
			char c=(char) inputStream.read();
			sb2.append(c);
			}
			break;
			}
	
			}
			String str=sb2.toString();
			int si=str.indexOf("=")+1;
			int ei=str.indexOf("\"");
			String encoding=str.substring(si, ei).trim().toUpperCase();
			return encoding;
		} catch (Exception e) {
			Log.getLogger(WebSearchThread.class).error(e.getMessage(),e);
			return "UTF-8";
		}
	}
    
	private String url;
	
    public WebSearchThread(String url) {
        this.url = url;
    }
    
	private static String getHTML(String urlStr) throws IOException {
		if(urlStr==null||urlStr.equals(""))
			return null;
		URL url = new URL(urlStr);
		//System.setProperty("sun.net.client.defaultConnectTimeout","5000");
		//System.setProperty("sun.net.client.defaultReadTimeout","5000");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),GetEncoding(url)));
		String s = "";
		StringBuilder sb = new StringBuilder("");
		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public String call() throws Exception {
		TextExtract t = new TextExtract();
		return t.parse(getHTML(url));
		//return getHTML(url);
	}

}
