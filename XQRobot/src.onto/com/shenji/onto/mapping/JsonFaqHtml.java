package com.shenji.onto.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.shenji.common.log.Log;

public class JsonFaqHtml {
	public static final String A_HREF = "a[href]";

	public static List<String[]> jsoupAllFAQHtml_UrlAndQ(String html) {
		Document doc = Jsoup.parse(html);
		Iterator<Element> iteratorURL;
		Iterator<Element> iteratorQ;
		iteratorURL = doc.select("a").iterator();
		iteratorQ = doc.getElementsByClass("q").iterator();
		List<String[]> list = new ArrayList<String[]>();
		int count = 0;
		while (iteratorURL.hasNext() && iteratorQ.hasNext()) {
			Element eU = iteratorURL.next();
			Element eQ = iteratorQ.next();
			/*
			 * if(eU==null) Log.debugSystemOut(eU); if(eQ==null)
			 * Log.debugSystemOut(eQ);
			 */
			if (eU != null && eQ != null) {
				String[] strs = new String[2];
				strs[0] = eU.attr("href");
				strs[1] = eQ.text();
				list.add(strs);
			}
			count++;
		}
		return list;
	}

	public static List<String> jsoupAllFAQHtml(String html, String type) {
		Document doc = Jsoup.parse(html);
		Iterator<Element> iterator;
		if (type.equals(A_HREF)) {
			iterator = doc.select("a").iterator();
		} else
			iterator = doc.getElementsByClass(type).iterator();
		ArrayList<String> list = new ArrayList<String>();
		int i = 0;
		while (iterator.hasNext()) {
			Element e = iterator.next();
			if (e != null) {
				if (type.equals(A_HREF)) {
					list.add(e.attr("href"));
					// Log.debugSystemOut((i++)+":"+e.attr("href"));
				} else {
					list.add(e.text());
					// Log.debugSystemOut((i++)+":"+e.text());
				}
			}
		}
		return list;
	}
}
