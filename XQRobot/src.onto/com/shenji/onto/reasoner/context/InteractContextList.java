package com.shenji.onto.reasoner.context;

import java.util.HashMap;

public class InteractContextList {
	private static InteractContextList instance;
	public HashMap<String, InteractContext> map;

	private InteractContextList() {
		this.map = new HashMap<String, InteractContext>();
	}

	public static InteractContextList getInstance() {
		return instance;
	}

	public boolean addContext(String UId, InteractContext context) {
		if (this.map.containsKey(UId)) {
			return false;
		} else {
			this.map.put(UId, context);
			return true;
		}
	}

}
