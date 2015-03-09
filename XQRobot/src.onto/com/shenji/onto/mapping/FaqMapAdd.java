package com.shenji.onto.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import org.apache.http.impl.conn.Wire;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.mapping.inter.IFaqMapAdd;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatypeFactory;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

public class FaqMapAdd implements IFaqMapAdd {
	private OntModel model;

	public FaqMapAdd(OntModel model) {
		this.model = model;
	}

	public int addOWLFaqDataProperty(JenaOWLModel importModels,
			String individualName, String importPropertyName,
			String importPropertyValue) {
		RDFProperty importProperty = null;
		if ((importProperty = importModels.getRDFProperty(importPropertyName)) == null) {
			System.err.println("不存在该属性");
			return -2;
		}
		String importXmlns = importModels.getNamespaceManager()
				.getDefaultNamespace();
		Property property = importModels.getOntModel().getProperty(
				importXmlns + importPropertyName);
		Individual individual = this.model.getIndividual(FaqMapServices
				.getInstance().getBaseXmlns() + individualName);
		if (individual == null)
			return -3;
		individual.addProperty(property, importPropertyValue);
		FaqMapServices.getInstance().save();
		return 1;
	}

	public int addOWLFAQType(JenaOWLModel importModels, String individualName,
			String importClassName) {
		RDFSNamedClass namedClass = null;
		if ((namedClass = importModels.getRDFSNamedClass(importClassName)) == null) {
			return -2;
		}
		// 导入文档空间
		String importXmlns = importModels.getNamespaceManager()
				.getDefaultNamespace();
		Individual individual = this.model.getIndividual(FaqMapServices
				.getInstance().getBaseXmlns() + individualName);
		if (individual == null)
			return -3;
		individual.addRDFType(importModels.getOntModel().getResource(
				importXmlns + importClassName));

		FaqMapServices.getInstance().save();

		return 1;
	}

}
