package com.shenji.onto.editer.inter;

import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

public interface IOntoGet {	
	/**
	 * 得到本体类
	 * @param className 类名
	 * @return 类对象
	 */
	public OWLNamedClass getOWLNamedClass(String className);
	/**
	 * 得到本体属性
	 * @param property 属性名
	 * @param propertyType 属性类型
	 * @return 属性对象
	 */
	public OWLProperty getOWLProperty(String property,int propertyType);
	/**
	 * 得到本体实例
	 * @param individualName 实例名
	 * @return 实例对象
	 */
	public OWLIndividual getOWLIndividual(String individualName);		
	/**
	 * 得到复合描述类(如objectProperty some class)
	 * @param property_description 属性
	 * @param values_description 值
	 * @param property_descriptionType 属性类型
	 * @param values_descriptionType 值类型
	 * @param cardinalityType 关系
	 * @param cardinalityNum 关系的约束值
	 * @return
	 */
	public RDFSClass getCardinality(String property_description, String values_description,
			int property_descriptionType,int values_descriptionType,int cardinalityType,int cardinalityNum);
	/**
	 * 得到复合描述数据 (如int 1)
	 * @param value 值
	 * @param valueType 值类型
	 * @return 复合描述数据
	 */
	public RDFSLiteral getRDFSLiteral(String value,String valueType);
	public List<String> getALLOntologyWords();
	public List<String> getOntoClassWords();
}
