package com.shenji.onto;

public class StateCode {
	public static enum Code {
		Error(-1);
		private int num;

		private Code(int num) {
			this.num = num;
		}

		public int getNum() {
			return num;
		}
	}
}
