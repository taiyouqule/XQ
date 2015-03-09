package com.shenji.onto.editer.server;

public interface SocketObserver {
	public static final int CLASS_CHANGE=0;
	public static final int DATA_PROPERTY_CHANGE=1;
	public static final int OBJECT_PROPERTY_CHANGE=2;
	public static final int INDIVAUL_CHANGE=3;
	public void update(Object obj);

}
