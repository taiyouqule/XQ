package com.shenji.onto.reasoner.interact;

import java.util.Set;

public class InteractCommon {
	public static enum ResultMark {
		ONLY_THING;
	}

	public static enum ResultClassify {
		Exact, Abstract, NunAbstract
	}

	public static enum ReasultState {
		Exact_SameTree(101), Exact_UserSub(102), Exact_FaqSub(103),

		NAbstract_L1(201), NAbstract_L2(202), NAbstract_L3(203),

		Abstract_L1(301), Abstract_L2(302), Abstract_L3(303);
		private int num;

		private ReasultState(int num) {
			this.num = num;
		}

		public int getNum() {
			return num;
		}

		public static ResultClassify getClassify(int num) {
			if (num > 100 && num < 200)
				return ResultClassify.Exact;
			else if (num > 200 && num < 300)
				return ResultClassify.NunAbstract;
			else if (num > 300 && num < 400)
				return ResultClassify.Abstract;
			else
				return null;
		}
	}

}
