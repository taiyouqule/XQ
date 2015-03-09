package com.shenji.onto.reasoner;

import java.util.List;

import com.shenji.onto.reasoner.data.SearchOntoBean;



public class ReasonerFactory {
	public final static int SIMPLE=0;
	public final static int MULTILAYER=1;
	public final static int INTERACT=2;
	public final static int AUTOCOMPLEX=3;
	public ReasonerFactory(){}	
	
	public static ReasonerStrategy createReasoner(int type) throws RuntimeException{
		ReasonerStrategy strategy;
		switch (type) {
		case SIMPLE:
			strategy=new ReasonerSimple();
			break;
		case MULTILAYER:
			strategy=new ReasonerFilter();
			break;		
		case INTERACT:
			strategy=new ReasonerInteract();
			break;
		case AUTOCOMPLEX:
			strategy=new ReasonerFilterAuto();
			break;
		default:
			throw new RuntimeException("Reasoner Objcet Create Error!");
		}	
		return strategy;
		
	}
}
