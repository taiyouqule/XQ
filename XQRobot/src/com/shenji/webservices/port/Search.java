package com.shenji.webservices.port;

import java.util.List;

import com.shenji.common.data.ResultShowBean;
import com.shenji.common.exception.OntoReasonerException;
import com.shenji.common.log.Log;
import com.shenji.common.proxy.CglibProxy;
import com.shenji.common.proxy.PortDBLogProxy;
import com.shenji.common.proxy.PortFileLogProxy;
import com.shenji.onto.reasoner.ReasonerFactory;
import com.shenji.onto.reasoner.data.OntoDimensionComparator;
import com.shenji.search.DicControl;
import com.shenji.search.FenciControl;
import com.shenji.search.IEnumSearch;
import com.shenji.search.IEnumSearch.Fenci;
import com.shenji.search.IEnumSearch.ResultCode;
import com.shenji.search.IEnumSearch.SearchConditionType;
import com.shenji.search.IEnumSearch.SearchRelationType;
import com.shenji.search.Parameters;
import com.shenji.search.ResourcesControl;
import com.shenji.search.SearchControl;
import com.shenji.search.dic.BusinessDic;
import com.shenji.search.exception.EngineException;
import com.shenji.search.exception.SearchProcessException;
import com.shenji.webservices.action.StandardAnswer;

public class Search {
	public final static Object Search_LOCK = new Object();// 全局锁

	public int setConfig(int maxResult, int maxTextShow, float maxMatchWeight,
			float myIkdictWeight, float qaProportion, int showResult,
			int maxAccurat) {
		return Parameters.setConfig(maxResult, maxTextShow, maxMatchWeight,
				myIkdictWeight, qaProportion, showResult, maxAccurat);
	}

	public String getConfig() {
		return Parameters.getConfig();
	}

	private String[] getArraryByResultShowBean(ResultShowBean bean) {
		ResultCode code = bean.getCode();
		List<String> list = bean.getResult();
		list.add(0, Integer.toString(code.value()));
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * 查询
	 * 
	 * @param args
	 *            问题
	 * @param number
	 *            问答对数
	 * @param relationType
	 *            关系类型（1或查询，2与查询,-1默认）
	 * @param conditionType
	 *            条件类型（1基础、2普通、3图谱过滤，-1默认）
	 * @return 问答对
	 */
	public String[] searchNum(String args, int number, int relationType,
			int conditionType) {
		Search search = ((Search) CglibProxy.createProxy(this,
				new PortFileLogProxy()));
		return search.searchNum_NoProxy(args, number, relationType,
				conditionType);
	}

	protected String[] searchNum_NoProxy(String args, int number,
			int relationType, int conditionType) {
		// 这里必须protected，不然cglib反射找不到，如果public则会发布出去，不合理
		Object[] searchEnums = null;
		String[] reStrs = null;
		ResultShowBean bean = null;
		try {
			searchEnums = this.getSearchEnum(relationType, conditionType);
		} catch (IllegalArgumentException e) {
			return new String[] {
					String.valueOf(ResultCode.IllegalArgumentException.value()),
					"参数错误!" };
		}
		IEnumSearch.SearchRelationType rType = (IEnumSearch.SearchRelationType) searchEnums[0];
		IEnumSearch.SearchConditionType cType = (IEnumSearch.SearchConditionType) searchEnums[1];
		try {
			switch (cType) {
			case Basics:// 基础查询
				bean = new SearchControl().searchBasicNum(args, number, rType);
				break;
			case Ordinary:// 标准查询
				bean = new SearchControl().searchOrdinaryNum(args, number,
						rType);
				break;
			case FilterByOnto:// 图谱推理过滤查询
			{
				try {
					bean = new SearchControl()
							.searchFilterByOntoNum(
									args,
									number,
									rType,
									ReasonerFactory
											.createReasoner(ReasonerFactory.AUTOCOMPLEX),
									new OntoDimensionComparator());
					break;
				} catch (OntoReasonerException e) {
					// TODO Auto-generated catch block
					return new String[] {
							String.valueOf(ResultCode.NoOntoResult.value()),
							StandardAnswer.str_noOnto };// 推理没有定位
				}
			}
			default:
				return new String[] {
						String.valueOf(ResultCode.IllegalArgumentException
								.value()), "参数错误!" };
			}
		} catch (SearchProcessException e) {
			// TODO Auto-generated catch block
			if (e.getErrorCode().equals(
					SearchProcessException.ErrorCode.NoSearchResult)) {
				return new String[] {
						String.valueOf(ResultCode.NoSearchResult.value()),
						"无法找到答案！" };
			} else
				Log.getLogger(this.getClass()).fatal("出大事了，搜索奔溃了！赶紧重启!", e);
			return new String[] {
					String.valueOf(ResultCode.SystemError.value()),
					"系统发生错误！请联系管理员!" };

		}
		reStrs = this.getArraryByResultShowBean(bean);
		System.out.println(reStrs.length);
		return reStrs;
	}

	private Object[] getSearchEnum(int relationType, int conditionType)
			throws IllegalArgumentException {
		Object[] searchEnums = new Object[2];
		IEnumSearch.SearchRelationType rType;
		IEnumSearch.SearchConditionType cType;
		// 默认参数构造（-1，-1）(或查询，图谱过滤查询)
		if (relationType == -1 && conditionType == -1) {
			rType = SearchRelationType.OR_SEARCH;
			cType = SearchConditionType.FilterByOnto;
		} else {
			try {
				rType = IEnumSearch.SearchRelationType.valueOf(relationType);
				cType = IEnumSearch.SearchConditionType.valueOf(conditionType);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				throw e;
			}
		}
		searchEnums[0] = rType;
		searchEnums[1] = cType;
		return searchEnums;
	}

	public String search(String args, int relationType, int conditionType) {
		Object[] searchEnums = null;
		try {
			searchEnums = this.getSearchEnum(relationType, conditionType);
		} catch (IllegalArgumentException e) {
			return "IllegalArgument!";
		}
		IEnumSearch.SearchRelationType rType = (IEnumSearch.SearchRelationType) searchEnums[0];
		IEnumSearch.SearchConditionType cType = (IEnumSearch.SearchConditionType) searchEnums[1];

		String reStr = null;
		try {
			switch (cType) {
			case Basics:// 基础查询
				reStr = new SearchControl().searchBasic(args, rType);
				break;
			case Ordinary:// 标准查询
				reStr = new SearchControl().searchOrdinary(args, rType);
				break;
			case FilterByOnto:// 图谱推理过滤查询
			{
				try {
					reStr = new SearchControl()
							.searchFilterByOnto(
									args,
									rType,
									ReasonerFactory
											.createReasoner(ReasonerFactory.AUTOCOMPLEX),
									new OntoDimensionComparator());
					break;
				} catch (OntoReasonerException e) {
					// TODO Auto-generated catch block
					return StandardAnswer.str_noOnto;// 推理没有定位
				}
			}
			default:
				break;
			}
			return reStr;
		} catch (SearchProcessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (e.getErrorCode().equals(
					SearchProcessException.ErrorCode.NoSearchResult)) {
				return "无法找到答案!";
			} else {
				Log.getLogger(this.getClass()).fatal("出大事了，搜索奔溃了！赶紧重启!",e);
				return "系统发生错误！请联系管理员!";
			}
		}

	}

	public String listAllFaq() {
		String reStr = null;
		synchronized (Search_LOCK) {
			reStr = new ResourcesControl().listAllFaq();
		}
		return reStr;
	}

	public String fenCi(String args, int type) {
		IEnumSearch.Fenci fenci;
		try {
			if (type == -1)
				fenci = Fenci.MORE_NOSYN;// 默认分词
			else
				fenci = Fenci.valueOf(type);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			return "IllegalArgument!";
		}
		switch (fenci) {
		case MORE_NOSYN:
			return new FenciControl().iKAnalysis(args);
		case MAX_NOSYN:
			return new FenciControl().iKAnalysisMax(args);
		case MORE_SYN:
			try {
				return new FenciControl().iKAnalysisAndSyn(args);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		case MAX_SYN:
			try {
				return new FenciControl().iKAnalysisMaxAndSyn(args);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		default:
			return "IllegalArgument!";
		}
	}

	public int modifyWords(String oldWord, String newWord) {
		synchronized (Search_LOCK) {
			return new DicControl().modifyWords(oldWord, newWord);
		}
	}

	public int deleteWords(String[] words) {
		synchronized (Search_LOCK) {
			return new DicControl().deleteWords(words);
		}
	}

	public int addNewWords(String words) {
		synchronized (Search_LOCK) {
			DicControl control = (DicControl) CglibProxy.createProxy(
					new DicControl(), new PortDBLogProxy());
			return control.addNewWords(words);
		}
	}

	

	public int addNewFAQ(String question[], String answer[]) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().addNewFAQ(question, answer);
		}
	}

	public int deleteFAQ(String url[]) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().deleteFAQ(url);
		}
	}

	public int changeFAQ(String url[], String q[], String a[]) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().changeFAQ(url, q, a);
		}
	}

	public boolean rebuildIndex(String index) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().rebuildIndex(index);
		}
	}

	public String getAboutWords(String word) {
		return new DicControl().getAboutWords(word);
	}

	public String getMyAllWords() {
		// 这里有点问题，这个方法需要许改进DicControl
		return new DicControl().listMyAllWords();
	}

	public int addNewBusinessWord(String word, float weight) {
		synchronized (Search_LOCK) {
			return new DicControl().addNewBusinessWord(word, weight);
		}
	}

	public int modifyBusinessWord(String oldWord, String newWord,
			float newWeight) {
		synchronized (Search_LOCK) {
			return new DicControl().modifyBusinessWord(oldWord, newWord,
					newWeight);
		}
	}

	public int deleteBusinessWord(String word) {
		synchronized (Search_LOCK) {
			return new DicControl().deleteBusinessWord(word);
		}
	}

	public float getBusinessWord(String word) {
		return new DicControl().getBusinessWord(word);
	}

	public String[] getListBusinessDict() {
		// 这里有点问题，这个方法需要许改进DicControl
		try {
			return BusinessDic.getInstance().listBusinessDict();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public int addNewSynonmWord(String word, String[] synonmWords) {
		synchronized (Search_LOCK) {
			return new DicControl().addNewSynonmWord(word, synonmWords);
		}
	}

	public int modifySynonmWord(String word, String[] newSynonmWords) {
		synchronized (Search_LOCK) {
			return new DicControl().modifySynonmWord(word, newSynonmWords);
		}
	}

	public String getSynonmWords(String word) {
		return new DicControl().getSynonmWords(word);
	}

	public String getNowMyWord(String word) {
		return new DicControl().getNowMyWord(word);
	}

	public float isInMyWord(String word) {
		return new DicControl().isCustomWord(word);
	}

	/*
	 * public boolean isInOntology(String word) { return false; }
	 */

	// 获得常用语
	public String getPhrase(String question) {
		return new ResourcesControl().getPhrase(question);
	}

	// 增加常用语
	public int addPhrase(String question, String answer) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().addPhrase(question, answer);
		}
	}

	// 删除常用语
	public int delPhrase(String question) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().delPhrase(question);
		}
	}

	// 修改常用语
	public int modifyPhrase(String question, String answer) {
		synchronized (Search_LOCK) {
			return new ResourcesControl().modifyPhrase(question, answer);
		}
	}

	public String[][] listAllPhrase() {
		return new ResourcesControl().listAllPhrase();
	}

}
