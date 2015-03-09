package com.shenji.onto.reasoner.inter;

public interface IReasonerLimit {
	public static enum Type {
		SCORE, SIMILIARY;
	}

	public static enum Level {
		Level_1, Level_2, Level_3, Level_4;

	}

	public double getScore(Object... parameters);

	public double getSimiliraty(Object... parameters);

}
