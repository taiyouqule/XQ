package com.shenji.onto.editer.inter;

public interface IOntoSet {
	/**
	 * 设置本体属性限制
	 * 
	 * @param ontologyProperty
	 *            本体属性
	 * @param ontologyPropertyType
	 *            本体属性类型
	 * @param limit
	 *            限制
	 * @param limitType
	 *            限制类型
	 * @return
	 */
	public int setLimit(String ontoProperty, int ontoPropertyType,
			String limit, int limitType);

	/**
	 * 设置本体属性限制
	 * 
	 * @param ontologyProperty
	 *            本体属性
	 * @param ontologyPropertyType
	 *            本体属性类型
	 * @param limitType
	 *            限制类型
	 * @param property_description
	 *            限制的属性描述
	 * @param values_description
	 *            限制的值描述
	 * @param property_descriptionType
	 *            限制的属性的描述类型
	 * @param values_descriptionType
	 *            限制的值的描述类型
	 * @param cardinalityType
	 *            关系类型
	 * @param cardinalityNum
	 *            关系约束值
	 * @return
	 */
	public int setLimit(String ontoProperty, int ontoPropertyType,
			int limitType, String ontoProperty_desc, String values_desc,
			int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum);

	/**
	 * 设置属性的特性
	 * 
	 * @param ontologyProperty
	 *            本体属性
	 * @param ontologyPropertyType
	 *            本体属性的类型
	 * @param characteristicsType
	 *            特性类型
	 * @return
	 */
	public int setCharacteristics(String ontoProperty, int ontoPropertyType,
			int characteristicsType);

	/**
	 * 设置实例的对象属性（两实例关系）
	 * 
	 * @param individualName_1
	 *            实例1
	 * @param individualName_2
	 *            实例2
	 * @param relation
	 *            关系
	 * @return
	 */

	public int setIndividualDataProperty(String ontoIndividual,
			String ontoProperty, String propertyValues, String propertyValuesType);

	/**
	 * 设置实例的数据属性
	 * 
	 * @param individualName
	 *            实例名
	 * @param propertyName
	 *            属性名
	 * @param propertyValue
	 *            属性值
	 * @param propertyValueType
	 *            属性值的类型
	 * @return
	 */
	public int setIndividualObjectProPerty(String ontoIndividualA,
			String ontoIndividualB, String relation);
}
