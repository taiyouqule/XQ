package edu.fudan.example.nlp;

import edu.fudan.nlp.cn.anaphora.Anaphora;

/**
 * 指代消解实例
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class AnaphoraResolution {
	public static void main(String args[]) throws Exception{
		//String str = "美国太平洋司令部司令塞缪尔·洛克利尔承认，尚无法确认该导弹是真品还是仿制品，也难以评价。他同时强调，若朝鲜进行第三次核试验，美军可能对朝鲜核试验基地进行精确打击。";
		String str="中国是个国家,它很美的,这个国家有企鹅。";
		Anaphora aa = new Anaphora("./models/seg.m","./models/pos.m","./models/ar.m");
		System.out.printf(aa.resultToString(str));
		
		/*String str2 = "复旦大学创建于1905年,它位于上海市，这个大学培育了好多优秀的学生。";
		String str3[] = {"复旦","大学","创建","于","1905年","，","它","位于","上海市","，","这个","大学","培育","了","好多","优秀","的","学生","。"};
		String str4[] = {"专有名","名词","动词","介词","时间短语","标点","代词","动词","专有名","标点","限定词","名词","动词","动态助词","数词","形容词","结构助词","名词","标点"};
		String str5[][][] = new String[1][2][str3.length];
		str5[0][0] = str3;
		str5[0][1] = str4;		
		System.out.printf(aa.resultToString(str5, str2));*/
	}

}
