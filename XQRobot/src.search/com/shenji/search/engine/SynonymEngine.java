package com.shenji.search.engine;

import java.io.IOException;

import com.shenji.search.exception.EngineException;

public interface SynonymEngine {
	public static String WORD="word";
	public static String SYNONM="syn";
	
	String[] getSynonyms(String s) throws EngineException;

	public void close();
}
