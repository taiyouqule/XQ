package com.shenji.onto.mapping.inter;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;

public interface IFaqMapDelete {
	public int deleteOWLFaqDataProperty(String importUrl,String individualName,String importPropertyName,String importPropertyValue);
	public int deleteOWLFAQType(String importUrl,String individualName,String importClassName);
}
