package com.shenji.search.engine;

import java.util.Map;

public interface RelatedWordEngine {
	public static final double minRalatedNum = 0.6;

	public void close();

	public Map<String, Double> getRelatedWord(String word);
}
