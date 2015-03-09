package com.shenji.onto.editer.impl;

import org.jdom.Document;
import org.jdom.Element;

import com.shenji.common.util.StringUtil;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.editer.inter.IOntoQuery;
import com.shenji.onto.editer.owl.OWLOntoQuery;
import com.shenji.onto.editer.server.OntologyManage;

/**
 * 本体查询接口
 * 
 * @author zhq
 */
public class OntoQuery extends OWLOntoQuery implements
		IOntoQuery {
	private String token = null;
	private OntologyManage manage = null;

	/**
	 * 构造函数
	 * 
	 * @param manage
	 *            本体管理对象
	 */
	public OntoQuery(OntologyManage manage) {
		// TODO Auto-generated constructor stub
		super(manage);
		this.manage = manage;
		this.token = manage.getToken();
	}

	public String getOntologyNamedClassXmlList(String code) {
		Document document = new Document();	
		// 跟节点信息
		Element rootElement = new Element(OntologyCommon.OntoRootName);
		rootElement.setAttribute("url", this.manage.getOwlModel()
				.getNamespaceManager().getDefaultNamespace());
		recursiveXmlQueue(OntologyCommon.OntoRootName, rootElement);
		document.addContent(rootElement);
		// 转换编码
		String xmlStr = StringUtil.outputToString(document, code);
		xmlStr = StringUtil.changeCharset(xmlStr, code);
		return xmlStr;
	}

	/*
	 * private class ReasouerceElementNode{ private RDFResoner }
	 */

	protected void recursiveXmlQueue(String namedClassName,
			Element superElement) {
		String[] subList = this.manage.getAbstractQueryInterface()
				.getOntologyDescriptionObject(namedClassName, OntologyCommon.DataType.namedClass, false,
						OntologyCommon.Description.sub_, false);
		if (subList != null && subList.length != 0) {
			for (String str:subList) {
				if(!this.manage.getAbstractQueryInterface().getOntologyIsOthersSyn(str)){
					Element element = new Element(str);
					
					String[] synList=this.manage.getAbstractQueryInterface().getOntologyDescriptionObject(str, OntologyCommon.DataType.namedClass, false,
							OntologyCommon.Description.equivalent_, false);
					if(synList!=null&&synList.length!=0){
						element.setAttribute("syn", Boolean.TRUE.toString());					
						StringBuilder sb=new StringBuilder();
						for(String syn:synList){
							sb.append(syn+"#");
						}						
						element.setText(sb.substring(0, sb.length()-1));				
					}
					String locked="";
					if(this.manage.getLockedPointServer().checkIsLocked(str,OntologyManage.getUserName(this.manage.getToken()), OntologyCommon.DataType.namedClass))
						locked=Boolean.TRUE.toString();
					else
						locked=Boolean.FALSE.toString();
					element.setAttribute("locked", locked);
					superElement.addContent(element);
					recursiveXmlQueue(str, element);
				}
			}
		}
			
		
	}
	

}
