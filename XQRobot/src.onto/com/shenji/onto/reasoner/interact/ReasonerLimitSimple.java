package com.shenji.onto.reasoner.interact;

import com.shenji.onto.reasoner.inter.IReasonerLimit;

public class ReasonerLimitSimple implements IReasonerLimit {
	private final static double[] scores = { 0.4, 0.5, 0.6,0.7};
	private final static double[] similiary = { 0.4, 0.5, 0.65,0.8};

	private static double getTypeNum(Type type, int i) {
		if (type == Type.SCORE) {
			return scores[i];
		} else if (type == Type.SIMILIARY) {
			return similiary[i];
		}
		return -1;
	}

	public static double getLevelNum(Type type, Level level) {
		switch (level) {
		case Level_1:
			getTypeNum(type, 0);
			break;
		case Level_2:
			getTypeNum(type, 1);
			break;
		case Level_3:
			getTypeNum(type, 2);
			break;
		case Level_4:
			getTypeNum(type, 3);
			break;
		default:
			break;
		}

		return -1;
	}

	@Override
	public double getScore(Object... parameters) {
		// TODO Auto-generated method stub
		if (!(parameters[0] instanceof Level)) {
			return -1;
		}
		return getLevelNum(Type.SCORE, (Level) parameters[0]);

	}

	@Override
	public double getSimiliraty(Object... parameters) {
		// TODO Auto-generated method stub
		if (!(parameters[0] instanceof Level)) {
			return -1;
		}
		return getLevelNum(Type.SIMILIARY, (Level) parameters[0]);

	}

}
