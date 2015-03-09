package com.shenji.search.engine;

public interface SimilarWordEngine {
	public double getSimilar(String wordA, String wordB);

	public void close();
}
