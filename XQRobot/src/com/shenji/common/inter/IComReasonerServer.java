package com.shenji.common.inter;

import java.util.List;

import com.shenji.common.exception.OntoReasonerException;
import com.shenji.onto.reasoner.data.SearchOntoBean;

public interface IComReasonerServer {
	public List<SearchOntoBean> reasoning(Object... obj)
			throws OntoReasonerException;
}
