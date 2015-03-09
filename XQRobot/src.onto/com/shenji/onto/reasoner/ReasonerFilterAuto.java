package com.shenji.onto.reasoner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.shenji.common.exception.OntoReasonerException;
import com.shenji.common.log.HtmlLog;
import com.shenji.common.log.Log;
import com.shenji.onto.reasoner.ReasonerComplex.ListCompara;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.onto.reasoner.data.SearchOntoBean;
import com.shenji.onto.reasoner.server.ReasonerTreeServer;
import com.shenji.search.bean.SearchBean;

public class ReasonerFilterAuto extends ReasonerStrategy {
	public enum DoOneFaqResult {
		Continue, Break, Return;
	}

	public boolean doAfterReasoning(Object... obj) {
		/*List<String> reFaqList = (List<String>) obj[0];
		if (reFaqList != null && reFaqList.size() > 0
				&& reFaqList.get(0) != null) {
			ReasonerComplex.ListCompara compara = new ListCompara();
			Collections.sort(reFaqList, compara);
			return true;
		} else
			return false;*/
		//排序不做了
		return true;
	}

	@Override
	public List<SearchOntoBean> reasoning(Object... obj) throws OntoReasonerException {
		// TODO Auto-generated method stub
		List<SearchBean> beans = null;
		List<SearchOntoBean> reBeans = null;
		ReasonerTree[] userTrees = null;
		try {
			String sentence = (String) obj[0];
			beans = (List<SearchBean>) obj[1];
			reBeans = new ArrayList<SearchOntoBean>();
			if (beans == null || beans.size() == 0)
				return null;
			// 复合推理是没有交互，暂时没有加用户名
			userTrees = ReasonerTreeServer.getInstance().getUserReasonerTree(
					sentence);
			// 用户推理树为空或者没有
			if (userTrees == null || userTrees.length == 0)
				// 推理树没有返回空
				return null;
			// 用户推理树只有THING节点，没有分出来类
			// Log.promptMsg("树高："+userTrees[0].getTreeHight());
			if (userTrees[0].getTreeHight() == 0) {
				throw new OntoReasonerException("UserTree is Null!",
						OntoReasonerException.ErrorCode.UserTreeIsNull);
			}
			HtmlLog.info("getUserReasonerTree", new Object[]{sentence}, userTrees[0].toXmlString("utf-8"),true);
			Set<String> filterSet = ReasonerTreeServer.getInstance().filter(
					userTrees[0]);

			for (SearchBean bean : beans) {
				if (filterSet.contains(bean.getFaqId())) {
					SearchOntoBean ontoBean = new SearchOntoBean(bean);
					ontoBean.setOntoDimension(1);// 这里默认都先设置为1
					reBeans.add(ontoBean);
				}
			}
			doAfterReasoning(new Object[] {reBeans});
			return reBeans;
			/*
			 * if (reFaqList != null && reFaqList.size() != 0) { String[] reStrs
			 * = reFaqList .toArray(new String[reFaqList.size()]);
			 * 
			 * for(String str:reStrs){ Log.debugSystemOut(str); }
			 * 
			 * // return reStrs; return null; } else return null;
			 */
		} finally {
			// 用户树要清除，占内存
			if (userTrees != null && userTrees.length != 0) {
				for (ReasonerTree tree : userTrees) {
					tree.clear();
				}
			}
			if (beans != null && beans.size() > 0) {
				beans.clear();
				beans = null;
			}

		}

	}
}
