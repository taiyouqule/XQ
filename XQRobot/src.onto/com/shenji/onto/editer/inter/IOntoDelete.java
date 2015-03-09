package com.shenji.onto.editer.inter;

public interface IOntoDelete {
	/**
	 * 删除本体成员
	 * 
	 * @param str
	 *            本体成员名
	 * @param type
	 *            本体成员类型
	 * @return
	 */
	public int deleteOntologyObject(String ontoConcept, int ontoType);

	/**
	 * 删除本体成员描述
	 * 
	 * @param str
	 *            本体成员
	 * @param str2
	 *            本体成员描述名
	 * @param type
	 *            本体成员类型
	 * @param descriptionType
	 *            描述类型
	 * @return
	 */
	public int deleteDescriptionOntologyObject(String ontoConceptA,
			String ontoConceptB, int ontoType, int desType);

	/**
	 * 删除本体类成员的复合描述
	 * 
	 * @param objectName
	 *            类名
	 * @param property_description
	 *            描述属性名
	 * @param values_description
	 *            描述的值
	 * @param property_descriptionType
	 *            描述属性的类型
	 * @param values_descriptionType
	 *            描述值的类型
	 * @param cardinalityType
	 *            关系类型
	 * @param cardinalityNum
	 *            关系约束值
	 * @param descriptionType
	 *            描述类型
	 * @return
	 */
	public int deleteDescriptionOntologyObject(String ontoConcept,
			String ontoProperty_desc, String values_desc,
			int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum, int descType);

	/**
	 * 删除实例的类型（映射）
	 * 
	 * @param className
	 *            类名
	 * @param individualName
	 *            实例名
	 * @return
	 */
	public int deleteIndividualType(String ontoClass, String ontoIndividual);

	/**
	 * 删除实例的类型（映射）
	 * 
	 * @param className
	 *            类名
	 * @param individualName
	 *            实例名
	 * @param deleteIndividual
	 *            当无映射时是否删除实例
	 * @return
	 */
	public int deleteIndividualType(String ontoClass, String ontoIndividual,
			boolean isDelIndividual);

	/**
	 * 删除本体属性限制
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
	public int deleteLimit(String ontoProperty, int ontoPropertyType,
			String limit, int limitType);

	/**
	 * 删除本体属性限制
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
	public int deleteLimit(String ontoProperty, int ontoPropertyType,
			int limitType, String ontoProperty_desc, String values_desc,
			int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum);

	/**
	 * 删除属性的特性
	 * 
	 * @param ontologyProperty
	 *            本体属性
	 * @param ontologyPropertyType
	 *            本体属性的类型
	 * @param characteristicsType
	 *            特性类型
	 * @return
	 */
	public int deleteCharacteristics(String ontolProperty,
			int ontoPropertyType, int characteristicsType);

	/**
	 * 删除实例的对象属性（两实例关系）
	 * 
	 * @param individualName_1
	 *            实例1
	 * @param individualName_2
	 *            实例2
	 * @param relation
	 *            关系
	 * @return
	 */
	public int deleteIndividualObjectProPerty(String ontoIndividualA,
			String ontoIndividualB, String relation);

	/**
	 * 删除实例的数据属性
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
	public int deleteIndividualDataProperty(String ontoIndividual,
			String ontoProperty, String propertyValue, String propertyValueType);

	public int deleteAnnotations(String ontoConcept, int ontoType,
			String annotations, String annotationsType);
}
