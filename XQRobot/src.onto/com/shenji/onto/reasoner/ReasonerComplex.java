package com.shenji.onto.reasoner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.shenji.common.exception.OntoReasonerException;
import com.shenji.common.log.Log;
import com.shenji.onto.Configuration;
import com.shenji.onto.mapping.FaqMapUtil;
import com.shenji.onto.reasoner.data.FaqReasonerTree;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.data.SearchOntoBean;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;


public abstract class ReasonerComplex extends ReasonerStrategy {
	public enum DoOneFaqResult {
		Continue, Break, Return;
	}

	public static class ListCompara implements Comparator<String> {
		@Override
		public int compare(String strA, String strB) {
			// TODO Auto-generated method stub
			double scoreA = Double.parseDouble(strA.split("#")[1]);
			double similiaryA = Double.parseDouble(strA.split("#")[2]);
			double scoreB = Double.parseDouble(strB.split("#")[1]);
			double similiaryB = Double.parseDouble(strB.split("#")[2]);
			int releventA = Integer.parseInt(strA.split("#")[3]);
			int releventB = Integer.parseInt(strB.split("#")[3]);
			if (releventA > releventB)
				return -1;
			else if (releventA == releventB) {
				if (scoreA > scoreB)
					return -1;
				else if (scoreA == scoreB) {
					if (similiaryA > similiaryB)
						return -1;
					else if (similiaryA == similiaryA) {
						return 0;
					} else
						return 1;
				} else
					return 1;
			} else
				return 1;
		}

	}
	public abstract DoOneFaqResult doOneFaqReasoning(Object... obj);

	public abstract boolean doAfterReasoning(Object... obj);

	@Override
	public List<SearchOntoBean> reasoning(Object... obj) throws OntoReasonerException{
		// TODO Auto-generated method stub
		List<String> reFaqList = null;
		ReasonerTree[] userTrees=null ;
		boolean reBool = true;
		try {
			String[] orcWords = (String[]) obj[0];
			String[] faqList = (String[]) obj[1];

			reFaqList = new ArrayList<String>();
			String[] words = ReasonerLabel.getInstance().wordsLabelExpand(
					orcWords);

			if (words == null || words.length == 0)
				return null;
			// 复合推理是没有交互，暂时没有加用户名
			userTrees = ReasonerTreeServer.getInstance()
					.getUserReasonerTree(words);
			if (faqList == null || faqList.length == 0)
				return null;
			// 用户推理树为空或者没有
			if (userTrees == null || userTrees.length == 0)
				return null;
			for (String faq : faqList) {
				String individual = null;
				try {
					individual = Configuration.FaqMappingCommon.INDIVIDUAL_HEAD
							+ faq.split("#")[0];
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}
				ReasonerTree[] faqTrees = ReasonerTreeServer.getInstance()
						.getFaqReasonerTree(individual);
				// FAQ推理树为空或者没有
				if (faqTrees == null || faqTrees.length == 0)
					continue;
				DoOneFaqResult doOneFaqResult=doOneFaqReasoning(new Object[] { userTrees, faqTrees,
						reFaqList, faq });
				if (doOneFaqResult==DoOneFaqResult.Continue)
					continue;
				else if(doOneFaqResult==DoOneFaqResult.Break)
					break;
				else if(doOneFaqResult==DoOneFaqResult.Return)
					return null;
			}
			reBool = doAfterReasoning(new Object[] { reFaqList });
			if (reFaqList != null && reFaqList.size() != 0 && reBool)
				//return reFaqList.toArray(new String[reFaqList.size()]);
				return null;
			else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(FaqMapUtil.class).error(e.getMessage(),e);
			return null;
		}
		finally{
			//用户树要清除，占内存
			if(userTrees!=null&&userTrees.length!=0){
				for(ReasonerTree tree:userTrees){
					tree.clear();
				}
			}
		}
		
	}
}
