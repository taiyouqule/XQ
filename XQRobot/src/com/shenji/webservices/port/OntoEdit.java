package com.shenji.webservices.port;

import com.shenji.onto.Configuration;
import com.shenji.onto.editer.server.OntologyDBServer;
import com.shenji.onto.editer.server.OntologyManage;
import com.shenji.onto.editer.server.OntologyServer;
import com.shenji.onto.reasoner.server.ReasonerServer;

public class OntoEdit {
	private OntologyServer ontologyServer;
	private Search searchServer;

	public OntoEdit() {
		this.ontologyServer = new OntologyServer();
		this.searchServer = new Search();
	}

	/**
	 * 创建新新的本体文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @param url
	 * @return
	 */
	public int createNewOntologyFile(String filePath, String fileName,
			String url) {
		// TODO Auto-generated method stub
		return ontologyServer.createNewOntology(filePath, fileName, url);
	}

	/**
	 * 打开一个本体文件【通过文件方式】
	 * 
	 * @param openFileName
	 * @param userName
	 * @return
	 */
	public String openOntologyFile(String openFileName, String userName) {
		// TODO Auto-generated method stub
		return ontologyServer.openOntologyFromFile(openFileName, userName);
	}

	/**
	 * 打开一个本体文件【通过数据库】
	 * 
	 * @param ontoName
	 * @param userName
	 * @return
	 */
	public String openOntologyFromDB(String ontoName, String userName) {
		// TODO Auto-generated method stub
		return ontologyServer.openOntologyFromDB(ontoName, userName);
	}

	/**
	 * 删除一个本体文件
	 * 
	 * @param fileName
	 * @return
	 */
	public int deleteOntologyFile(String fileName) {
		// TODO Auto-generated method stub
		return ontologyServer.deleteOntology(fileName);
	}

	/**
	 * 保存本体文件
	 * 
	 * @param token
	 * @param newOpenFileName
	 * @return
	 */
	public int saveOntologyFile(String token, String newOpenFileName) {
		// TODO Auto-generated method stub
		return ontologyServer.saveOntology(token, newOpenFileName);
	}

	public int addDescriptionOntologyObject(String token, String ontoConceptA,
			String ontoConceptB, int ontoType, int descType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractAddInterface()
				.addDescriptionOntologyObject(ontoConceptA, ontoConceptB,
						ontoType, descType);
		String msg = null;
		if ((msg = ontologyServer.createDescriptionNotifyMessage(descType,
				descType, OntologyServer.ADD)) != null)
			ontologyServer.notifyAll(reFlag, manage, msg,
					OntologyManage.getUserName(token));
		return reFlag;
	}

	public int addDescriptionOntologyObject_(String token, String ontoConceptA,
			String ontoProperty_desc, String values_desc,
			int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum, int descType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractAddInterface()
				.addDescriptionOntologyObject(ontoConceptA, ontoProperty_desc,
						values_desc, ontoProperty_descType, values_descType,
						cardinalityType, cardinalityNum, descType);
		// ontologyServer.notifyAll(reFlag,
		// manage,"A@303",ontologyServer.getUserName(token));
		return reFlag;
	}

	public int addIndividualType(String token, String ontoClass,
			String ontoIndividual) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractAddInterface().addIndividualType(
				ontoClass, ontoIndividual);
		// ontologyServer.notifyAll(reFlag, manage, 9);
		return reFlag;
	}

	public int addOntologyObject(String token, String ontoConcept,
			String superOntoConcept, int ontoType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractAddInterface().addOntologyObject(
				ontoConcept, superOntoConcept, ontoType);
		if (reFlag == 1) {
			// 添加自定义分词
			searchServer.addNewWords(ontoConcept);
			manage.resetWordSet();
			ontologyServer.notifyAll(reFlag, manage, "A"
					+ Configuration.messageSeparator + "301"
					+ Configuration.messageSeparator + ontoType,
					OntologyManage.getUserName(token));
		}
		return reFlag;
	}

	public int deleteOntologyObject(String token, String ontoConcept,
			int ontoType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractDeleteInterface().deleteOntologyObject(
				ontoConcept, ontoType);
		if (reFlag == 1) {
			ontologyServer.notifyAll(reFlag, manage, "D"
					+ Configuration.messageSeparator + "404"
					+ Configuration.messageSeparator + ontoType,
					OntologyManage.getUserName(token));
		}
		return reFlag;
	}

	public int deleteDescriptionOntologyObject(String token,
			String ontoConceptA, String ontoConceptB, int ontoType, int desType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractDeleteInterface()
				.deleteDescriptionOntologyObject(ontoConceptA, ontoConceptB,
						ontoType, desType);
		String msg = null;
		if ((msg = ontologyServer.createDescriptionNotifyMessage(desType,
				ontoType, OntologyServer.DELETE)) != null)
			ontologyServer.notifyAll(reFlag, manage, msg,
					OntologyManage.getUserName(token));
		return reFlag;
	}

	public int deleteDescriptionOntologyObject_(String token,
			String ontoConcept, String ontoProperty_desc, String values_desc,
			int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum, int descType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface()
				.deleteDescriptionOntologyObject(ontoConcept,
						ontoProperty_desc, values_desc, ontoProperty_descType,
						values_descType, cardinalityType, cardinalityNum,
						descType);
	}

	public int deleteIndividualType(String token, String ontoClass,
			String ontoIndividual) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface().deleteIndividualType(
				ontoClass, ontoIndividual);
	}

	public int deleteIndividualType_(String token, String ontoClass,
			String ontoIndividual, boolean isDelIndividual) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface().deleteIndividualType(
				ontoClass, ontoIndividual, isDelIndividual);
	}

	public int deleteLimit(String token, String ontoProperty,
			int ontoPropertyType, String limit, int limitType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface().deleteLimit(ontoProperty,
				ontoPropertyType, limit, limitType);
	}

	public int deleteLimit_(String token, String ontoProperty,
			int ontoPropertyType, int limitType, String ontoProperty_desc,
			String values_desc, int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface().deleteLimit(ontoProperty,
				ontoPropertyType, limitType, ontoProperty_desc, values_desc,
				ontoProperty_descType, values_descType, cardinalityType,
				cardinalityNum);
	}

	public int deleteCharacteristics(String token, String ontolProperty,
			int ontoPropertyType, int characteristicsType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface().deleteCharacteristics(
				ontolProperty, ontoPropertyType, characteristicsType);
	}

	public int deleteIndividualObjectProPerty(String token,
			String ontoIndividualA, String ontoIndividualB, String relation) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface()
				.deleteIndividualObjectProPerty(ontoIndividualA,
						ontoIndividualB, relation);
	}

	public int deleteIndividualDataProperty(String token,
			String ontoIndividual, String ontoProperty, String propertyValue,
			String propertyValueType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface()
				.deleteIndividualDataProperty(ontoIndividual, ontoProperty,
						propertyValue, propertyValueType);
	}

	public int renameOntologyObject(String token, String ontoConcept_old,
			String ontoConcept_new, int ontoType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int flag = manage.getAbstractUpdateInterface().renameOntologyObject(
				ontoConcept_old, ontoConcept_new, ontoType);
		if (flag == 1) {
			ontologyServer.notifyAll(flag, manage, "M"
					+ Configuration.messageSeparator + "501"
					+ Configuration.messageSeparator + ontoType,
					OntologyManage.getUserName(token));
		}
		return flag;
	}

	public int updateDescriptionOntologyObject(String token,
			String ontoConcept, String ontoConcept_desc_old,
			String ontoConcept_decs_new, int ontoType, int descType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface()
				.updateDescriptionOntologyObject(ontoConcept,
						ontoConcept_desc_old, ontoConcept_decs_new, ontoType,
						descType);
	}

	public int updateDescriptionOntologyObject_(String token,
			String ontoConcept, String ontoProperty_desc_old,
			String values_desc_old, String ontoPropery_desc_new,
			String values_desc_new, int ontoProperty_descType_old,
			int values_descType_old, int ontoProperty_descType_new,
			int values_descType_new, int cardinalityType_old,
			int cardinalityNum_old, int cardinalityType_new,
			int cardinalityNum_new, int descType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface()
				.updateDescriptionOntologyObject(ontoConcept,
						ontoProperty_desc_old, values_desc_old,
						ontoPropery_desc_new, values_desc_new,
						ontoProperty_descType_old, values_descType_old,
						ontoProperty_descType_new, values_descType_new,
						cardinalityType_old, cardinalityNum_old,
						cardinalityType_new, cardinalityNum_new, descType);
	}

	public int updateLimit(String token, String ontoProperty,
			int ontoPropertyType, String limit_old, int limitType_old,
			String limit_new, int limitType_new) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface().updateLimit(ontoProperty,
				ontoPropertyType, limit_old, limitType_old, limit_new,
				limitType_new);
	}

	public int updateLimit_(String token, String ontoProperty,
			int ontoPropertyType, int limitType_old,
			String ontoProperty_desc_old, String values_desc_old,
			int ontoProperty_descType_old, int values_descType_old,
			int cardinalityType_old, int cardinalityNum_old, int limitType_new,
			String ontoProperty_desc_new, String values_desc_new,
			int ontoProperty_descType_new, int values_descType_new,
			int cardinalityType_new, int cardinalityNum_new) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface().updateLimit(ontoProperty,
				ontoPropertyType, limitType_old, ontoProperty_desc_old,
				values_desc_old, ontoProperty_descType_old,
				values_descType_old, cardinalityType_old, cardinalityNum_old,
				limitType_new, ontoProperty_desc_new, values_desc_new,
				ontoProperty_descType_new, values_descType_new,
				cardinalityType_new, cardinalityNum_new);
	}

	public int updateIndividualDataProperty(String token,
			String ontoIndividual, String ontoProperty_old,
			String propertyValues_old, String propertyValuesType_old,
			String ontoProperty_new, String propertyValues_new,
			String propertyValuesType_new) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface()
				.updateIndividualDataProperty(ontoIndividual, ontoProperty_old,
						propertyValues_old, propertyValuesType_old,
						ontoProperty_new, propertyValues_new,
						propertyValuesType_new);
	}

	public int updateIndividualObjectProPerty(String token,
			String ontoIndivualA, String ontoIndivualB_old,
			String relation_old, String ontoIndivualB_new, String relation_new) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface()
				.updateIndividualObjectProPerty(ontoIndivualA,
						ontoIndivualB_old, relation_old, ontoIndivualB_new,
						relation_new);
	}

	public int updateIndividualType(String token, String ontoIndivual,
			String ontoClass_old, String ontoClass_new) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractUpdateInterface().updateIndividualType(
				ontoIndivual, ontoClass_old, ontoClass_new);
	}

	public int setLimit(String token, String ontoProperty,
			int ontoPropertyType, String limit, int limitType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractSetInterface().setLimit(ontoProperty,
				ontoPropertyType, limit, limitType);
	}

	public int setLimit_(String token, String ontoProperty,
			int ontoPropertyType, int limitType, String ontoProperty_desc,
			String values_desc, int ontoProperty_descType, int values_descType,
			int cardinalityType, int cardinalityNum) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractSetInterface().setLimit(ontoProperty,
				ontoPropertyType, limitType, ontoProperty_desc, values_desc,
				ontoProperty_descType, values_descType, cardinalityType,
				cardinalityNum);
	}

	public int setCharacteristics(String token, String ontoProperty,
			int ontoPropertyType, int characteristicsType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractSetInterface().setCharacteristics(
				ontoProperty, ontoPropertyType, characteristicsType);
	}

	public int setIndividualDataProperty(String token, String ontoIndividual,
			String ontoProperty, String propertyValues,
			String propertyValuesType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractSetInterface().setIndividualDataProperty(
				ontoIndividual, ontoProperty, propertyValues, propertyValues);
	}

	public int setIndividualObjectProPerty(String token,
			String ontoIndividualA, String ontoIndividualB, String relation) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractSetInterface().setIndividualObjectProPerty(
				ontoIndividualA, ontoIndividualB, relation);
	}

	public String getOntologyOWLString(String token) {
		// [有问题]
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return null;
	}

	public String[] getOntologyDescriptionObject(String token,
			String ontoConcept, int ontoType, boolean transitive, int descType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		String[] reStrs = manage.getAbstractQueryInterface()
				.getOntologyDescriptionObject(ontoConcept, ontoType,
						transitive, descType, true);
		if (reStrs != null && reStrs.length > 0) {
			for (int i = 0; i < reStrs.length; i++) {
				// 锁信息
				reStrs[i] = ontologyServer.createLockedDescription(token,
						reStrs[i]);
			}
		}
		return reStrs;
	}

	public String[] getOntologyFileList(String dirPath) {
		// TODO Auto-generated method stub
		return ontologyServer.getFileList(dirPath);
	}

	public String[] getOntologyDirList(String dirPath) {
		// TODO Auto-generated method stub
		return ontologyServer.getDirectoryList(dirPath);
	}

	public int deleteOntologyDirectory(String path) {
		// TODO Auto-generated method stub
		return ontologyServer.deleteDirectory(path);
	}

	public int renameOntologyDirectory(String path, String newName) {
		// TODO Auto-generated method stub
		return ontologyServer.renameDirectory(path, newName);
	}

	public int createOntologyDirectory(String path) {
		// TODO Auto-generated method stub
		return ontologyServer.createDirectory(path);
	}

	public int moveOntologyFile(String oldFilePath, String newFilePath) {
		// TODO Auto-generated method stub
		return ontologyServer.moveFile(oldFilePath, newFilePath);
	}

	public void releaseToken(String[] tokens) {
		// TODO Auto-generated method stub
		ontologyServer.releaseToken(tokens);
	}

	public int closeOntologyFile(String token) {
		// TODO Auto-generated method stub
		return ontologyServer.closeOntology(token);
	}

	public int addAnnotations(String token, String ontoConcept, int ontoType,
			String annotations, String annotationsType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractAddInterface().addAnnotations(ontoConcept,
				ontoType, annotations, annotationsType);
	}

	public String[] getOntologyDataType(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getOntologyDataType();
	}

	public String getAnnotations(String token, String ontoConcept, int ontoType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getAnnotations(ontoConcept,
				ontoType);
	}

	public String[] getAnnotationsType(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getAnnotationsType();
	}

	public String getCharacteristics(String token, String ontoProperty,
			int ontoPropertyType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getCharacteristics(
				ontoProperty, ontoPropertyType);
	}

	public String getIndividuals(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		String individualsXml = manage.getAbstractQueryInterface()
				.getIndividuals();
		return ontologyServer.createLockdedIndividuals(token, individualsXml);
	}

	public String getIndividualType(String token, String ontoIndividual) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		String xml = manage.getAbstractQueryInterface().getIndividualType(
				ontoIndividual);
		if (xml == null)
			return null;
		xml = ontologyServer.createLockdedIndividualType(token, xml);
		return xml;
	}

	public int addUserAnnotation(String token, String userAnnotationsName) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractAddInterface().addUserAnnotation(
				userAnnotationsName);
	}

	public String[] getUserAnnotationsType(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getUserAnnotationsType();
	}

	public int deleteAnnotations(String token, String ontoConcept,
			int ontoType, String annotations, String annotationsType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		return manage.getAbstractDeleteInterface().deleteAnnotations(
				ontoConcept, ontoType, annotations, annotationsType);
	}

	public String[] getNamedClassIndividual(String token, String ontoClass) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getNamedClassIndividual(
				ontoClass);
	}

	public String getIndividualObjectProperty(String token, String ontoIndivual) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getIndividualObjectProperty(
				ontoIndivual);
	}

	public String getIndividualDataProperty(String token, String ontoIndividual) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getIndividualDataProperty(
				ontoIndividual);
	}

	public String getBasicInformation(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getBasicInformation();
	}

	public String getBaseNameSpeaceUrl(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getAbstractQueryInterface().getBaseNameSpeaceUrl();
	}

	public String[] getMyWordSet(String token) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return null;
		return manage.getMyWordSet();
	}

	public int addObjectByEquivalent(String token, String ontoConcept,
			String ontoConcept_super, String ontoConcept_equivalent,
			int ontoType) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return -10;
		int reFlag = manage.getAbstractAddInterface().addObjectByEquivalent(
				ontoConcept, ontoConcept_super, ontoConcept_equivalent,
				ontoType);
		if (reFlag == 1) {
			manage.resetWordSet();
		}
		return reFlag;
	}

	public boolean isOtherSynOntoConcept(String token, String ontoConcept) {
		// TODO Auto-generated method stub
		OntologyManage manage = null;
		if ((manage = ontologyServer.getManage(token)) == null)
			return false;
		return manage.getAbstractQueryInterface().getOntologyIsOthersSyn(
				ontoConcept);
	}

	public String[] getOntologyNamesFromMySQL() {
		// TODO Auto-generated method stub
		OntologyDBServer dbServices = new OntologyDBServer();
		String[] strs = dbServices.getOntologyNamesFromMySQL();
		if (dbServices != null)
			dbServices.close();
		return strs;
	}

	public int createNewOntologyByDB(String ontoName, String url) {
		// TODO Auto-generated method stub
		return ontologyServer.createNewOntologyByDB(ontoName, url);
	}

	public int deleteOntologyFromDB(String ontoName, String passWord) {
		// TODO Auto-generated method stub
		if (!passWord.equals(Configuration.testPassWord)) {
			// 密码错误
			return -20;
		}
		int reFlag = ontologyServer.deleteOntologyFromDB(ontoName);
		ReasonerServer.getInstance().removeOntModel(ontoName);
		return reFlag;
	}

}
