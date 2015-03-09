package com.shenji.test;

import java.net.URISyntaxException;
import java.util.ArrayList;

import com.shenji.common.util.FileUse;
import com.shenji.search.SearchControl;

public class TestMain {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws URISyntaxException {
		// TODO Auto-generated method stub
//		String[] words={"水电费","朱大庆"};
		//SearchLogic logic=new SearchLogic();
		//logic.AddNewWords("朱大庆");
		//logic.AddNewWords("朱大放庆");
		//logic.deleteWords(words);
		TestMain logic=new TestMain();
		logic.testSearch();
		//System.err.println(logic.IKAnalysis("单反的"));
		//System.err.println(logic.deleteWords(words));
		//System.err.println(logic.IKAnalysis(""));
		//testIsChinaWord();
		
	}
	public void testSearch(){
//		String str="电子报税中汇算清缴中，汇算清缴报表如何填写";
//		SearchLogic logic=new SearchLogic();
////		System.out.println(logic.Search(str));
//		logic.Search(str);
	}
/*	public void test() throws URISyntaxException{
		String path = this.getClass().getClassLoader().getResource("").toURI().getPath();
		System.err.println(path);
	}
	public static void testIsChinaWord(){
		System.err.println(FileUse.isChineseWord("浼"));
		System.err.println(FileUse.isChineseWord("s"));
		System.err.println(FileUse.isChineseWord("3"));
	}
	
	public static void testFileUse(){
		//long start=System.currentTimeMillcom.shenji.search.SearchLogiccom.shenji.search.SearchLogic();
		//SearchLogic s2= new SearchLogic();
		
	   // s.AddNewWords("锟斤拷锟斤拷锟斤拷式");
		//System.out.println(s.IKAnalysis("锟斤拷锟斤拷锟斤拷式锟斤拷师"));
		FileUse fileUse=new FileUse();
		ArrayList<String> arrayList=fileUse.modify("D:\\FAQ\\WebService\\build\\classes\\", 
				"mydict.dic", "鍦伴�姝绘柟", "鍦伴�姝籨鏂�);
		String[] strings={"鐨勬垜寮�","鐨勫憙寮�"};
		//ArrayList<String> arrayList=fileUse.delete("D:\\FAQ\\WebService\\build\\classes\\", "mydict.dic", strings);
		//fileUse.write("D:\\FAQ\\WebService\\build\\classes\\", "mydict.dic", arrayList)	;
		for(String string:arrayList){
			System.err.println(string);
		}
		long end=System.currentTimeMillis();
		//System.out.println(end-start);
	}*/

}
