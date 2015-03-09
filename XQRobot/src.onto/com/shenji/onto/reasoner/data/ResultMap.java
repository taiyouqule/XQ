package com.shenji.onto.reasoner.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.stylesheets.LinkStyle;

import com.shenji.common.log.Log;


public class ResultMap<K, V> {
	private LinkedHashMap<K, List<V>> linkedHashMap;

	public ResultMap() {
		linkedHashMap = new LinkedHashMap<K, List<V>>();
	}

	public ResultMap(int initialCapacity) {
		linkedHashMap = new LinkedHashMap<K, List<V>>(initialCapacity);
	}

	public ResultMap(int initialCapacity, float loadFactor) {
		linkedHashMap = new LinkedHashMap<K, List<V>>(initialCapacity,
				loadFactor);
	}

	public void clear() {
		for (List<V> list : this.linkedHashMap.values()) {
			list.clear();
		}
		linkedHashMap.clear();
	}

	public void put(K key, V value) {
		List<V> list = (List<V>) linkedHashMap.get(key);// 根据key得到存放value 的list
		if (list == null)
			list = new ArrayList<V>();
		list.add(value);
		linkedHashMap.put(key, list);
	}

	public List<V> getKey(Object key) {
		return this.linkedHashMap.get(key);
	}

	public boolean contains(Object key) {
		if (this.linkedHashMap.containsKey(key))
			return true;
		else
			return false;
	}

	public int size() {
		return this.linkedHashMap.size();
	}

	public Iterator<Map.Entry<K, List<V>>> iterator()
			throws NullPointerException {
		try {
			return new Iiterator(this);
		} catch (Exception e) {
			// TODO: handle exception
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			return null;
		}

	}

	public class Iiterator implements Iterator<Map.Entry<K, List<V>>> {
		private ResultMap<K, V> map;

		// private int position=0;
		public Iiterator(ResultMap<K, V> map) throws NullPointerException {
			this.map = map;
			this.keyIterator = this.map.linkedHashMap.entrySet().iterator();

		}

		public Iterator<Map.Entry<K, List<V>>> keyIterator;

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return keyIterator.hasNext();
		}

		@Override
		public Map.Entry<K, List<V>> next() {
			// TODO Auto-generated method stub
			return keyIterator.next();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

	}
}