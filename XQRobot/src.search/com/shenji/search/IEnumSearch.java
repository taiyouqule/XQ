package com.shenji.search;


public class IEnumSearch {
	
	public static enum SearchConditionType {
		Basics(1),Ordinary(2),FilterByOnto(3),Interaction(4);
		private int value;

		private SearchConditionType(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static SearchConditionType valueOf(int value) throws IllegalArgumentException{ // 手写的从int到enum的转换函数
			for (SearchConditionType t : SearchConditionType.values()) {
				if (t.value() == value) {
					return t;
				}
			}
			throw new IllegalArgumentException("Enum SearchConditionType Argument is Error!");
		}
	}
	
	public static enum ResultCode {
		Exact(101),NunExact(102),//101是确定回答，102是答案比较多的
		NoSearchResult(301),NoOntoResult(302),
		IllegalArgumentException(401),SystemError(402);
		
		private int value;

		private ResultCode(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static ResultCode valueOf(int value) { 
			for (ResultCode t : ResultCode.values()) {
				if (t.value() == value) {
					return t;
				}
			}
			return null;
		}
	}

	public static enum SearchRelationType {
		OR_SEARCH(1),AND_SEARCH(2);
		private int value;

		private SearchRelationType(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static SearchRelationType valueOf(int value) throws IllegalArgumentException{ // 手写的从int到enum的转换函数
			for (SearchRelationType t : SearchRelationType.values()) {
				if (t.value() == value) {
					return t;
				}
			}
			throw new IllegalArgumentException("Enum SearchRelationType Argument is Error!");
		}
	}

	public static enum Fenci{
		MAX_SYN(3), MORE_SYN(4), MAX_NOSYN(
				1), MORE_NOSYN(2);
		private int value;

		private Fenci(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static Fenci valueOf(int value) throws IllegalArgumentException{ // 手写的从int到enum的转换函数
			for (Fenci t : Fenci.values()) {
				if (t.value() == value) {
					return t;
				}
			}
			throw new IllegalArgumentException("Enum FenciType Argument is Error!");
		}
	}

	public static enum WordPossibility {
		sysWord(0.5), word(1), maxWord(2);
		private double num;

		private WordPossibility(double num) {
			this.num = num;
		}

		public double getNum() {
			return num;
		}
	}

}
