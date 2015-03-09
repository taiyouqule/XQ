package com.shenji.webservices.demo;

import com.shenji.search.IEnumSearch;
import com.shenji.webservices.port.Search;

public class DSearch {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		dSearchNum();
		//dRebuildIndex();
//		 daddNewFAQ();  //增加
//		 drebuildIndex();  //重建
		//dFenci();				//分词
		//dSearch();
		//daddPhrase();
	}

	
	
	public static void dRebuildIndex() {
		Search search = new Search();
		search.rebuildIndex("0");
	}

	public static void dFenci() {
		Search search = new Search();
		String str = "证书更新后，登录电子报税或网上认证时，提示：登记信息税局验证失败。ca证书错误,为什么？";
		System.out.println(search.fenCi(str,
				IEnumSearch.Fenci.MORE_NOSYN.value()));
		System.out.println(search.fenCi(str,
				IEnumSearch.Fenci.MAX_NOSYN.value()));
		
		System.out.println(search.fenCi(str,
				IEnumSearch.Fenci.MORE_SYN.value()));
		System.out.println(search.fenCi(str,
				IEnumSearch.Fenci.MAX_SYN.value()));
	}

	public static void dSearch() {
		Search search = new Search();
		String html = search.search("网上认证开票",
				IEnumSearch.SearchRelationType.OR_SEARCH.value(),
				IEnumSearch.SearchConditionType.Ordinary.value());
		System.out.println(html);
	}

	public static void daddNewFAQ() {
		Search search = new Search();
		System.out.println(search.addNewFAQ(new String[] { "郭宏伟是傻逼吗？" },
				new String[] { "郭宏伟不是，他是大帅比！" }));
	}
	
	
	public static void daddPhrase() {
		Search search = new Search();
		System.out.println(search.addPhrase("郭宏伟", "大帅比"));
	}

	public static void drebuildIndex() {
		Search search = new Search();
		search.rebuildIndex("0");
	}

	public static void dSearchNum() {
		Search search = new Search();
		String[] reStrs = search.searchNum("网上认证开票", 3, 1, 3);
		for (String s : reStrs) {
			System.out.println(s);
		}
		System.out.println(reStrs.length);
	}

}
