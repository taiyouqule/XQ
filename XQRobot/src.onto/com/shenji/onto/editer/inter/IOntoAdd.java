package com.shenji.onto.editer.inter;

public interface IOntoAdd {

	/**
	 * 添加本体概念的描述
	 * 
	 * @param ontoConceptA
	 *            本体概念
	 * @param ontoConceptB
	 *            本体概念
	 * @param ontoType
	 *            本体概念类型（DataType）
	 * @param descType
	 *            描述类型（Description）
	 * @return 1-成功 其他-失败
	 */
	public int addDescriptionOntologyObject(String ontoConceptA,
			String ontoConceptB, int ontoType, int descType);

	public int addDescriptionOntologyObject(String ontoConceptA,
			String ontoProperty_desc, String values_desc,
			int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum, int descType);

	/**
	 * 添加本体实例
	 * 
	 * @param ontoClass
	 *            命名类
	 * @param ontoIndividual
	 *            实例
	 * @return 1-成功 其他-失败
	 */
	public int addIndividualType(String ontoClass, String ontoIndividual);

	public int addOntologyObject(String ontoConcept, String superOntoConcept,
			int ontoType);

	/**
	 * 添加本体概念的声明
	 * 
	 * @param ontoConcept
	 *            本体概念
	 * @param ontoType
	 *            本体概念类型
	 * @param annotations
	 *            声明内容
	 * @param annotationsType
	 *            声明类型
	 * @return
	 */
	public int addAnnotations(String ontoConcept, int ontoType,
			String annotations, String annotationsType);

	/**
	 * 添加用户自定义的声明类型
	 * 
	 * @param userAnnotationsType
	 *            用户自定义的声明类型
	 * @return
	 */
	public int addUserAnnotation(String userAnnotationsType);

	/**
	 * 添加本体概念的等价概念 注意：ontoConcept已存在，equiOntoConcept通过ontoConcept添加，
	 * 所以新添加的概念为equiOntoConcept
	 * 
	 * @param ontoConcept
	 *            本体概念
	 * @param superOntoConcept
	 *            本体概念的父概念
	 * @param equiOntoConcept
	 *            等价的本体概念
	 * @param ontoType
	 *            本体概念的类型
	 * @return
	 */
	public int addObjectByEquivalent(String ontoConcept,
			String ontoConcept_super, String ontoConcept_equivalent,
			int ontoType);

}
