package edu.fudan.example.nlp;
import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.nlp.parser.dep.JointParser;
/**
 * 依存句法分析使用示例
 * @author xpqiu
 *
 */
public class DepParser {

	private static JointParser parser;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		parser = new JointParser("models/dep.m");
		
		//System.out.println("媒体计算研究所成立了，高级数据挖掘很难。乐phone很好！");
		System.out.println(parser.getSupportedTypes());
		
		String word = "我昨天在出口退税系统里做4月份的出口退税申报时，没有导入上个月的正式申报反馈，这要不要紧呀？";
		//String word = "号外：涂总 是不是布置你们每个人一个研究课题啊,刚说要今天每人发一篇相关的文章到微信群里，看你们研究了什么";
		test(word);

	}

	/**
	 * 只输入句子，不带词性
	 * @throws Exception 
	 */
	private static void test(String word) throws Exception {		
		//POSTagger tag = new POSTagger("models/seg.m","models/pos.m");
		POSTagger tag = new POSTagger("models/seg.m","models/pos.m",new Dictionary("./models/userSpeech.dic"));
		long start=System.currentTimeMillis();
		String[][] s = tag.tag2Array(word);		
		try {
			DependencyTree tree = parser.parse2T(s[0],s[1]);
			System.out.println(tree.toString());
			//String stree = parser.parse2String(s[0],s[1],true);
			//System.out.println(stree);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		long end=System.currentTimeMillis();
		System.out.println(end-start);
	}
	private static void test2(String word) throws Exception {		
		//POSTagger tag = new POSTagger("models/seg.m","models/pos.m");
		//POSTagger tag = new POSTagger("models/seg.m","models/pos.m");
		long start=System.currentTimeMillis();
		//String[][] s = tag.tag2Array(word);
		String[] s1={"切尔西", "进出口", "银行" ,"与" ,"中国" ,"银行", "加强" ,"合作" ,"。"};
		String[] s2={"专有词" ,"名词" ,"名词" ,"并列连词","地名","名词","动词","名词","标点"};
		try {
			//DependencyTree tree = parser.parse2T(s1,s2);
			//System.out.println(tree.toString());
			String stree = parser.parse2String(s1,s2,true);
			System.out.println(stree);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		long end=System.currentTimeMillis();
		System.out.println(end-start);
	}

}