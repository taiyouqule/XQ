package com.shenji.onto.editer.inter;

public interface IOntoUpdate {
	/**
	 * 重命名本体的成员
	 * 
	 * @param oldName
	 *            旧成员名
	 * @param newName
	 *            新成员名
	 * @param type
	 *            成员类型
	 * @return
	 */
	public int renameOntologyObject(String ontoConcept_old,
			String ontoConcept_new, int ontoType);

	/**
	 * 修改本体成员的描述
	 * 
	 * @param objectName_1
	 *            本体成员名
	 * @param oldObjectName_2
	 *            本体成员描述的旧成员名
	 * @param newObjectName_2
	 *            本体成员描述的新成员名
	 * @param type
	 *            本体成员类型
	 * @param descriptionType
	 *            本体成员的描述类型
	 * @return
	 */
	public int updateDescriptionOntologyObject(String ontoConceptA,
			String ontoConceptB_desc_old, String ontoConceptB_decs_new,
			int ontoType, int descType);

	/**
	 * 修改本体成员的描述
	 * 
	 * @param objectName
	 *            本体成员名
	 * @param oldProperty_description
	 *            本体成员的描述的旧属性名
	 * @param oldValues_description
	 *            本体成员的描述的旧值
	 * @param newProperty_description
	 *            本体成员的描述的新属性名
	 * @param newValues_description
	 *            本体成员的描述的新值
	 * @param oldProperty_descriptionType
	 *            本体成员的描述的旧属性的类型
	 * @param oldValues_descriptionType
	 *            本体成员的描述的旧值的类型
	 * @param newProperty_descriptionType
	 *            本体成员的描述的新属性的类型
	 * @param newValues_descriptionType
	 *            本体成员的描述的新值的类型
	 * @param oldCardinalityType
	 *            本体描述旧关系类型
	 * @param oldCardinalityNum
	 *            本体描述的旧关系的约束值
	 * @param newCardinalityType
	 *            本体描述新关系类型
	 * @param newCardinalityNum
	 *            本体描述的新关系的约束值
	 * @param descriptionType
	 *            描述类型
	 * @return
	 */
	public int updateDescriptionOntologyObject(String ontoConcept,
			String ontoProperty_desc_old, String values_desc_old,
			String ontoPropery_desc_new, String values_desc_new,
			int ontoProperty_descType_old, int values_descType_old,
			int ontoProperty_descType_new, int values_descType_new,
			int cardinalityType_old, int cardinalityNum_old,
			int cardinalityType_new, int cardinalityNum_new, int descType);

	/**
	 * 修改本体属性的限制
	 * 
	 * @param ontologyProperty
	 *            本体属性
	 * @param ontologyPropertyType
	 *            本体属性类型
	 * @param oldLimit
	 *            本体属性旧的限制
	 * @param oldLimitType
	 *            本体属性旧限制的类型
	 * @param newLimit
	 *            本体属性新的限制
	 * @param newLimitType
	 *            本体属性新的限制的类型
	 * @return
	 */
	public int updateLimit(String ontoProperty, int ontoPropertyType,
			String limit_old, int limitType_old, String limit_new,
			int limitType_new);

	/**
	 * 修改本体属性的限制
	 * 
	 * @param ontologyProperty
	 *            本体属性
	 * @param ontologyPropertyType
	 *            本体属性的类型
	 * @param oldLimitType
	 *            旧本体属性限制的类型
	 * @param oldProperty_description
	 *            旧本体属性限制的描述属性
	 * @param oldValues_description
	 *            旧本体属性限制的描述值
	 * @param oldProperty_descriptionType
	 *            旧本体属性限制的描述属性的类型
	 * @param oldValues_descriptionType旧本体属性限制的描述值的类型
	 * @param oldCardinalityType
	 *            旧本体描述旧关系类型
	 * @param oldCardinalityNum
	 *            旧本体描述的旧关系的约束值
	 * @param newLimitType
	 *            新本体属性限制的类型
	 * @param newProperty_description
	 *            新本体属性限制的描述属性
	 * @param newValues_description
	 *            新本体属性限制的描述值
	 * @param newProperty_descriptionType
	 *            新本体属性限制的描述属性的类型
	 * @param newValues_descriptionType
	 *            新本体属性限制的描述值的类型
	 * @param newCardinalityType
	 *            新本体描述旧关系类型
	 * @param newCardinalityNum
	 *            新本体描述的旧关系的约束值
	 * @return
	 */
	public int updateLimit(String ontoProperty, int ontoPropertyType,
			int limitType_old, String ontoProperty_desc_old,
			String values_desc_old, int ontoProperty_descType_old,
			int values_descType_old, int cardinalityType_old,
			int cardinalityNum_old, int limitType_new,
			String ontoProperty_desc_new, String values_desc_new,
			int ontoProperty_descType_new, int values_descType_new,
			int cardinalityType_new, int cardinalityNum_new);

	/**
	 * 修改实例的数据属性
	 * 
	 * @param individualName
	 *            实例名
	 * @param oldPropertyName
	 *            旧的属性名
	 * @param oldPropertyValue
	 *            旧的属性值
	 * @param oldPropertyValueType
	 *            旧的属性值类型
	 * @param newPropertyName
	 *            新的属性名
	 * @param newPropertyValue
	 *            新的属性值
	 * @param newPropertyValueType
	 *            新的属性值类型
	 * @return
	 */
	public int updateIndividualDataProperty(String ontoIndividual,
			String ontoProperty_old, String propertyValues_old,
			String propertyValuesType_old, String ontoProperty_new,
			String propertyValues_new, String propertyValuesType_new);

	/**
	 * 修改实例的对象属性
	 * 
	 * @param individualName
	 *            实例名
	 * @param oldIndividualName
	 *            旧的关系实例名
	 * @param oldRelation
	 *            旧的关系
	 * @param newIndividualName
	 *            新的关系实例名
	 * @param newRelation
	 *            新的关系
	 * @return
	 */
	public int updateIndividualObjectProPerty(String ontoIndivualA,
			String ontoIndivualB_old, String relation_old,
			String ontoIndivualB_new, String relation_new);

	/**
	 * 修改实例类型（映射）
	 * 
	 * @param individualName
	 *            实例名
	 * @param oldClassName
	 *            旧类名
	 * @param newClassName
	 *            新类名
	 * @return
	 */
	public int updateIndividualType(String ontoIndivual, String ontoClass_old,
			String ontoClass_new);
}
