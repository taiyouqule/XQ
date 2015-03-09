package com.shenji.onto.reasoner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.shenji.common.inter.IComReasonerServer;
import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.reasoner.data.SearchOntoBean;
import com.shenji.onto.reasoner.server.ReasonerServer;

public abstract class ReasonerStrategy implements IComReasonerServer{	
	/**
	 * 得到所给的词集合中存在于图谱中的词
	 * @param words
	 * @return
	 */
	protected List<String[]> getOntoClass(List<String> words){
		//得到可供迭代的所有图谱
		Iterator<Map.Entry<String, OntModel>> iterator=ReasonerServer.getInstance().getOntoMap().entrySet().iterator();
		List<String[]> list=new ArrayList<String[]>();
		while(iterator.hasNext()){
			Map.Entry<String, OntModel> entry=iterator.next();
			OntModel model=entry.getValue();
			String path=model.getNsPrefixURI("");
			OntClass ontClass=null;
			List<String> filterList=new ArrayList<String>();
			for(String word:words){
				if((ontClass=model.getOntClass(path+word))!=null){										
					int level=0;
					String[] strs=new String[4];
					//被其他同义词定义的词，得用定义其的词进行搜索
					String synWord=null;
					if(ontClass.getVersionInfo()!=null){
						synWord=ontClass.getEquivalentClass().getLocalName();
						if(words.contains(synWord))
							continue;
					}
					strs[0]=path;
					strs[1]=word;
					strs[2]=synWord;
					level=getMaxOntoClassLevelFromTree(ontClass);
					Log.getLogger(this.getClass()).debug("LEVEL::"+level);
					strs[3]=String.valueOf(level);
					list.add(strs);
				}
			}				
		}
		return list;
	}
	
	/**
	 * 求某个类的在图谱中的最大高度
	 * @param ontClass
	 * @return
	 */
	protected int getMaxOntoClassLevelFromTree(OntClass ontClass) {
		int maxLevel = 0;
		if(ontClass==null||ontClass.getLocalName().equals(OntologyCommon.OntoRootName))
			return maxLevel;		
		Iterator<OntClass> iterator=ontClass.listSuperClasses();	
		if(iterator==null)
			return maxLevel;
		List<Integer> subLevelList=new ArrayList<Integer>();
		while(iterator.hasNext()){
			OntClass superClass=iterator.next();
			int level=getMaxOntoClassLevelFromTree(superClass);
			subLevelList.add(level);
		}
		Collections.sort(subLevelList,new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o2-o1;
			}
		
		});
		if(subLevelList!=null&&subLevelList.size()!=0)
			maxLevel=subLevelList.get(0);
		subLevelList.clear();
		return maxLevel+1;

	}

}
