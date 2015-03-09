package org.wltea.analyzer.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import com.shenji.search.engine.SynonymEngine;

public class SynonymAnalyzer extends Analyzer {
	private SynonymEngine engine;
	public SynonymAnalyzer(SynonymEngine engine){
		this.engine=engine;
	}
	@Override
	public TokenStream tokenStream(String arg0, Reader arg1) {
		// TODO Auto-generated method stub
		TokenStream stream=new SynonymFilter(new IKTokenizer(arg1, false),engine);				
		return stream;
	/*	TokenStream stream=new SynonymFilter(
				 new StandardFilter(new StandardTokenizer(Version.LUCENE_30,arg1)),engine
				);*/
		/*9月23 改为false是，采用最细粒度切分*/
		
	}

}
