package com.shenji.onto.editer.inter;

public interface IOntoQuery {
	/**
	 * 获得本体文件传
	 * @param tempPath 缓存文件名
	 * @return 本体文件序列化字符串
	 */
	public String getOntologyOWLString(String tempPath);	
	/**
	 * 得到本体的成员的描述成员
	 * @param objectName 本体成员
	 * @param type 成员类型
	 * @param transitive 传递性
	 * @param descriptionType 描述类型
	 * @return
	 */
	public String[] getOntologyDescriptionObject(String ontoConcept, int ontoType, boolean transitive, int descType,boolean isUsePackage);
	public String getIndividuals();
	public String[] getNamedClassIndividual(String ontoClass);	
	public String[] getOntologyDataType();
	public String getAnnotations(String ontoConcept,
			int ontoType);
	public String[] getAnnotationsType();
	public String getCharacteristics( String ontoProperty,
			int ontoPropertyType);
	public String getIndividualType(String ontoIndividual);
	public String[] getUserAnnotationsType();
	public String getIndividualObjectProperty(String ontoIndivual);
	public String getIndividualDataProperty(String ontoIndividual);
	public String getBasicInformation();
	public String getBaseNameSpeaceUrl();
	public boolean getOntologyIsOthersSyn(String ontoConcept);
    public String getOntologyNamedClassXmlList(String code);
	
	
}
