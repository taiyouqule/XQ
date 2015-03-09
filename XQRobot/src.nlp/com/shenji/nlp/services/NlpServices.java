package com.shenji.nlp.services;

import java.io.IOException;

import org.apache.axis2.AxisFault;

import com.shenji.common.inter.IComFenciServer;
import com.shenji.common.log.Log;
import com.shenji.nlp.MAnaphora;
import com.shenji.nlp.MSearchFenci;
import com.shenji.nlp.MDepParser;
import com.shenji.nlp.MNERTagger;
import com.shenji.nlp.MPOSTagger;
import com.shenji.nlp.MQuestionClassification;
import com.shenji.nlp.MTimeNormalizer;
import com.shenji.nlp.common.Common;
import com.shenji.nlp.inter.IChineseWordSegmentation;
import com.shenji.nlp.structure.MDictionary;

import edu.fudan.example.nlp.QuestionClassification;
import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.anaphora.Anaphora;
import edu.fudan.nlp.cn.ner.TimeNormalizer;
import edu.fudan.nlp.cn.tag.NERTagger;
import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.util.exception.LoadModelException;

public class NlpServices {
	/**
	 * 中文分词器
	 */
	private IChineseWordSegmentation chineseWordSegmentation;
	/**
	 * 词性分析器
	 */
	private MPOSTagger posTagger;
	/**
	 * 词性词典
	 */
	private MDictionary dict;
	/**
	 * 指代消除分析器
	 */
	private MAnaphora anaphora;
	/**
	 * 时间表达式识别
	 */
	private MTimeNormalizer timeNormalizer;	
	/**
	 * 命名实体识别
	 */
	private MNERTagger nerTagger;
	/**
	 * 问题分类预测
	 */
	private MQuestionClassification classification;			
	/**
	 * 依存句法分析
	 */
	private MDepParser depParser;

	/**
	 * 服务状态码
	 */
	int[] serviceCode=new int[8];
	//private static NlpServices nlpServices=new NlpServices();
	public NlpServices(IChineseWordSegmentation chineseWordSegmentation){
		this.chineseWordSegmentation=chineseWordSegmentation;
		this.loadServices();
	}
	/*public static NlpServices getInstance(){
		return nlpServices;
	}*/	
	/**
	 * 加载服务
	 * @return 服务状态集 int[]
	 * （0-分词 1-词性词典 2-词性分析  3-指代消除  4-句法分析 5-命名实体 6-时间表达是 7-句子分类）
	 */
	private int[] loadServices(){	
		for(int i=0;i<serviceCode.length;i++)
			serviceCode[i]=1;
		try {
			try {
				dict=MDictionary.getInstance();
				try {
					posTagger=new MPOSTagger(Common.POS_FILE,dict,chineseWordSegmentation);
					try {
						anaphora=new MAnaphora(Common.ARModel,chineseWordSegmentation, posTagger);							
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.getLogger().error("指代消除服务加载失败!", e);
						e.printStackTrace();
						serviceCode[3]=0;	
					}
					try {
						depParser=new MDepParser(Common.DepParser, posTagger, chineseWordSegmentation);							
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.getLogger().error("依存句法分析服务加载失败!", e);
						e.printStackTrace();
						serviceCode[4]=0;	
					}					
					//命名实体	
					nerTagger=new MNERTagger(posTagger, chineseWordSegmentation);
				} catch (LoadModelException e) {
					// TODO Auto-generated catch block
					Log.getLogger().error("词性服务加载失败!", e);
					e.printStackTrace();
					for(int i=2;i<6;i++)
						serviceCode[i]=0;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger().error("词性词典加载失败!", e);
				e.printStackTrace();
				for(int i=1;i<6;i++)
					serviceCode[i]=0;
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("分词服务加载失败!", e);
			e.printStackTrace();
			for(int i=0;i<6;i++)
				serviceCode[i]=0;
		}	
		
		try {
			timeNormalizer=new MTimeNormalizer(Common.TimeNorMalizerModel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("时间识别分析服务加载失败!", e);		
			e.printStackTrace();
			serviceCode[6]=0;
		}		
		
		try {
			classification=new MQuestionClassification(Common.QTrainDataPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error("句子分类预测服务加载失败!", e);
			e.printStackTrace();
			serviceCode[7]=0;
		}	 		
		return serviceCode;			
	}
	
	public IChineseWordSegmentation getChineseWordSegmentation() {
		return chineseWordSegmentation;
	}
	public MPOSTagger getPosTagger() {
		return posTagger;
	}
	public MDictionary getDict() {
		return dict;
	}
	public MAnaphora getAnaphora() {
		return anaphora;
	}
	public MTimeNormalizer getTimeNormalizer() {
		return timeNormalizer;
	}
	public MNERTagger getNerTagger() {
		return nerTagger;
	}
	public MQuestionClassification getClassification() {
		return classification;
	}	
	public MDepParser getDepParser() {
		return depParser;
	}
	public int[] getServiceCode() {
		return serviceCode;
	}
}
