package com.shenji.nlp.inter;

import org.apache.axis2.AxisFault;

public interface IChineseWordSegmentation {
	public String[] segment(String str) throws Exception;
}
