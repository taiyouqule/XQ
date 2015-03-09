package com.shenji.search.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;

import com.shenji.common.log.Log;
 
public class FileService { 
	
	private static char md5Chars[] =
		 { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		   'e', 'f' };
	
    public String read(String fileName, String encoding) { 
        StringBuffer fileContent = new StringBuffer(); 
        try { 
            FileInputStream fis = new FileInputStream(fileName); 
            InputStreamReader isr = new InputStreamReader(fis, encoding); 
            BufferedReader br = new BufferedReader(isr); 
            String line = null; 
            while ((line = br.readLine()) != null) { 
                fileContent.append(line+"\n"); 
            } 
            br.close(); 
            isr.close(); 
            fis.close(); 
            return fileContent.toString(); 
        } catch (Exception e) { 
        	Log.getLogger(this.getClass()).error(e.getMessage(),e);
            return null;
        } 
    } 

    public boolean write(String fileContent, String fileName, String encoding) { 
        try { 
            File f = new File(fileName);
            if(f.exists())
            	return false;
            FileOutputStream fos = new FileOutputStream(fileName); 
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding); 
            osw.write(fileContent); 
            osw.flush(); 
            osw.close(); 
            fos.close(); 
            return true;
        } catch (Exception e) { 
        	Log.getLogger(this.getClass()).error(e.getMessage(),e);
            return false;
        } 
    } 

	/*��ȡһ���ַ��md5�� */
	 public String getStringMD5String(String str)
	 {
		 try{
			 MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			 messagedigest.update(str.getBytes()); 
			 return bufferToHex(messagedigest.digest());
		 }catch(Exception e){
			 e.printStackTrace();
			 return null;
		 }
	 }
	 
	 private String bufferToHex(byte bytes[])
	 {
		 return bufferToHex(bytes, 0, bytes.length);
	 }
	 
	 private String bufferToHex(byte bytes[], int m, int n)
	 {
		 StringBuffer stringbuffer = new StringBuffer(2 * n);
		 int k = m + n;
		 for (int l = m; l < k; l++)
		 {
			 appendHexPair(bytes[l], stringbuffer);
		 }
		 return stringbuffer.toString();
	 }
	 
	 private void appendHexPair(byte bt, StringBuffer stringbuffer)
	 {
		 char c0 = md5Chars[(bt & 0xf0) >> 4];
		 char c1 = md5Chars[bt & 0xf];
		 stringbuffer.append(c0);
		 stringbuffer.append(c1);
	 }
} 
 