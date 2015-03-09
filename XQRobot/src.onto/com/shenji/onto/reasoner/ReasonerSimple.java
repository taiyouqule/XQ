package com.shenji.onto.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyDBServer;
import com.shenji.onto.mapping.FaqMapDBServices;
import com.shenji.onto.mapping.FaqMapUtil;
import com.shenji.onto.reasoner.data.SearchOntoBean;
import com.shenji.onto.reasoner.server.ReasonerServer;


public class ReasonerSimple extends ReasonerStrategy{
	@Override
	public List<SearchOntoBean> reasoning(Object... obj) {
		// TODO Auto-generated method stub
		List<String[]> ontoClassList=null;
		List<String> reList=new ArrayList<String>();
		try{
			FaqMapDBServices dbServices=new FaqMapDBServices();			
			ontoClassList=super.getOntoClass(Arrays.asList((String[])obj[0]));
			for(String[] strs:ontoClassList){
				//0是path,1是词,2是定义其的同义词，3是level
				String word=strs[1];
				String synWord=strs[2];
				String level=strs[3];
				List<String> faqlist=null;
				if(synWord==null)
					faqlist=dbServices.getFaqByType(strs[0], word);
				else
					faqlist=dbServices.getFaqByType(strs[0], synWord);
				for(String faq:faqlist){
					String faqId=FaqMapDBServices.getFaqID(faq);
					reList.add(level+ReasonerServer.segmentation+
							word+ReasonerServer.segmentation+faqId);
				}	
				faqlist.clear();
			}
		}catch (Exception e) {
			// TODO: handle exception		
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		}
		finally{
			if(ontoClassList!=null)
				ontoClassList.clear();
		}
		//return reList.toArray(new String[reList.size()]);	
		return null;
	}
	
	


	
	
	
}
