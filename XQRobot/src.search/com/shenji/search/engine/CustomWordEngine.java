package com.shenji.search.engine;

import com.shenji.search.exception.EngineException;

public interface CustomWordEngine {
	public boolean isCustomWord(String word);
	public String[] mixCuttingEnCh(String content);
	public void close();
	public void reset() throws EngineException;
}
