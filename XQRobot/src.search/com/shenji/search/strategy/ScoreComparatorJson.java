package com.shenji.search.strategy;

import java.util.Comparator;

import com.shenji.web.bean.OneResultBean;

public class ScoreComparatorJson <T extends OneResultBean> implements Comparator<T> {
	public int compare(T o1, T o2) {
		OneResultBean p1 = (OneResultBean) o1;
		OneResultBean p2 = (OneResultBean) o2;
		if (p1.getScore() - p2.getScore() > 0)
			return -1;
		else if (p1.getScore() - p2.getScore() < 0)
			return 1;
		else
			return 0;
	}
}
