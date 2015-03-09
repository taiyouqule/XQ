package com.shenji.onto.mapping.inter;

import java.io.IOException;

public interface IFaqMapQuery {
	public String[] getAllFaqIndividual();
	public String[] getAllFaqIndividualAndQ(int treeHight);
	public String[] getFaqIndividualByClass(String path,String ontoClassName,int treeHight);
	public String[][] getDataProperty(String individualName);
	public String[][] getFAQContext(String individualName) throws IOException;
	public String[] getRDFType(String individualName);
}
