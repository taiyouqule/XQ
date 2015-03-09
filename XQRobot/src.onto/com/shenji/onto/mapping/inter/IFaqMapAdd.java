package com.shenji.onto.mapping.inter;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;

public interface IFaqMapAdd {
	public int addOWLFaqDataProperty(JenaOWLModel importModels,String individualName,String import_propertyName,String import_propertyValue);
	public int addOWLFAQType(JenaOWLModel importModels,String individualName,String importClassName);
}
