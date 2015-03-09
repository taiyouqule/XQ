package com.shenji.onto.editer.impl;

import com.shenji.onto.editer.inter.IOntoGet;
import com.shenji.onto.editer.owl.OWLOntoGet;
import com.shenji.onto.editer.server.OntologyManage;

/**
 * 本体获取类
 * @author zhq
 * 获得本体相关
 */
public class OntoGet extends OWLOntoGet implements IOntoGet{
	private String token=null;
	private OntologyManage manage=null;
	public OntoGet(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		super(manage);
		this.manage=manage;
		this.token=manage.getToken();
	}				
}
