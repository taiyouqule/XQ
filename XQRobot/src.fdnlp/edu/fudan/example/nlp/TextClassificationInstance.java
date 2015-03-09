package edu.fudan.example.nlp;

import java.io.File;

import org.fnlp.app.tc.TextClassifier;

import edu.fudan.data.reader.FileReader;
import edu.fudan.data.reader.Reader;

/**
 * 文本分类示例
 * @author xpqiu
 *
 */

public class TextClassificationInstance {

	/**
	 * 训练数据路径
	 */
	private static String trainDataPath = "./example-data/text-classification/";
	private TextClassifier tc;
	private static TextClassificationInstance instance=new TextClassificationInstance();
	private TextClassificationInstance(){
		tc = new TextClassifier();
		//用不同的Reader读取相应格式的文件
		Reader reader = new FileReader(trainDataPath,"UTF-8",".data");
		try {
			tc.train(reader, modelFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static TextClassificationInstance getInstance(){
		return instance;
	}
	/**
	 * 模型文件
	 */
	private static String modelFile = "./example-data/text-classification/model.gz";

	public String train(String str){
		/**
		 * 分类器使用
		 */
		String label = (String) tc.classify(str).getLabel(0);
		return label;
		
	}
	
	
}