package com.shenji.search.engine;

import java.io.IOException;

import com.shenji.search.exception.EngineException;

public interface BusinessEngine {
	// 业务词典接口
	float getWeight(String s) throws EngineException;

	public void close();
}
