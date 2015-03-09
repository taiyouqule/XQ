package com.shenji.search.strategy;

import java.util.Comparator;

import com.shenji.search.bean.SearchBean;

public class ScoreComparator<T extends SearchBean> implements Comparator<T> {
	public int compare(T o1, T o2) {
		SearchBean p1 = (SearchBean) o1;
		SearchBean p2 = (SearchBean) o2;
		if (p1.getScore() - p2.getScore() > 0)
			return -1;
		else if (p1.getScore() - p2.getScore() < 0)
			return 1;
		else
			return 0;
	}
}
