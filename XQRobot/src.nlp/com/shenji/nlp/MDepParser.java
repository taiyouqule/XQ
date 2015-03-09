package com.shenji.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.shenji.common.log.Log;
import com.shenji.nlp.inter.IChineseWordSegmentation;

import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.nlp.parser.dep.JointParser;
import edu.fudan.util.exception.LoadModelException;

/**
 * 依存句法分析
 * @author zhq
 *
 */
public class MDepParser extends JointParser{
	private static String DepMatcher="动词|主语|宾语"; 
	private POSTagger posmodel;
	private IChineseWordSegmentation segmentation; 
	public MDepParser(String modelfile,POSTagger posmodel,IChineseWordSegmentation segmentation) throws LoadModelException {
		super(modelfile);
		this.posmodel=posmodel;
		this.segmentation=segmentation;
		// TODO Auto-generated constructor stub
	}
	
	public DependencyTree parse(String sentence){		
		String[] words=null;
		try {
			words = segmentation.segment(sentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.getLogger().error(e.getMessage(),e);
			e.printStackTrace();
			return null;
		}
		String[] speechs=posmodel.tagSeged(words);
		DependencyTree tree = super.parse2T(words,speechs);
		return tree;
	}
	
	public String extractTrunk(String sentence){
		StringBuilder sb=new StringBuilder();
		List<DependencyTree> list=new ArrayList<DependencyTree>();
		String[] reStrs=null;
		DependencyTree tree=this.parse(sentence);
		recursiveTree(tree,list);
		for(DependencyTree node:list){
			String relation=node.relation;
			if(relation==null)
				relation="谓语";
			sb.append(node.word+"/"+relation+" ");
		}
		return sb.toString();
		
	}
	
	private void recursiveTree(DependencyTree tree,List<DependencyTree> list){
		for (int i = 0; i < tree.leftChilds.size(); i++) {
			recursiveTree(tree.leftChilds.get(i),list);
		}		
		if(tree.relation==null||tree.relation.matches(DepMatcher)){
			list.add(tree);
		}
		for (int i = 0; i < tree.rightChilds.size(); i++) {
			recursiveTree(tree.rightChilds.get(i),list);
		}
		
	}


	
	

}
