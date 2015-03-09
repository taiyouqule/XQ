package edu.fudan.example.nlp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import edu.fudan.nlp.pipe.seq.templet.Templet;
import edu.fudan.nlp.pipe.seq.templet.TempletGroup;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		File file=new File("./models/TimeExp.m");
		/*ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(file))));*/
		FileInputStream fileInputStream=new FileInputStream(file);		
		GZIPInputStream gzipInputStream=new GZIPInputStream(fileInputStream);
		BufferedInputStream bufferedInputStream=new BufferedInputStream(gzipInputStream);
		
		/*
		ObjectInputStream in = new ObjectInputStream(bufferedInputStream);
		TempletGroup templets = (TempletGroup) in.readObject();
		Iterator iterator=templets.iterator();
		while(iterator.hasNext()){
			Templet  templet=(Templet) iterator.next();
			System.err.println(templet.toString());
		}*/
		
		/*BufferedReader reader=new BufferedReader(new InputStreamReader(bufferedInputStream,"utf-8"));
		String s;
		while((s=reader.readLine())!=null)
			System.err.println(s);*/
		
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
         byte[] by = new byte[1024];
         int len = 0;
         while ((len = gzipInputStream.read(by)) != -1)
         {
             os.write(by, 0, len);
         }
         String t = new String(os.toByteArray(),"utf-8");
         System.out.println(t);
	
	}
}
