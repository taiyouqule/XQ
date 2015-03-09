package com.shenji.common.log;

import java.text.SimpleDateFormat;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class CustomHtmlLayout extends HTMLLayout {
	protected final int BUF_SIZE = 256;
	protected final int MAX_CAPACITY = 1024;
	static String TRACE_PREFIX = "<br>    ";
	private StringBuffer sbuf = new StringBuffer(BUF_SIZE);
	private String title = "日志文件";

	public static final String TITLE_OPTION = "Title";

	boolean locationInfo = true;

	public String format(LoggingEvent event) {
		if (sbuf.capacity() > MAX_CAPACITY) {
			sbuf = new StringBuffer(BUF_SIZE);
		} else {
			sbuf.setLength(0);
		}
		sbuf.append(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);
		sbuf.append("<td width=\"200\">");
		sbuf.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date()));
		sbuf.append("</td>" + Layout.LINE_SEP);

		sbuf.append("<td title=\"信息\">");
		sbuf.append(event.getRenderedMessage());
		sbuf.append("</td>" + Layout.LINE_SEP);
		sbuf.append("</tr>" + Layout.LINE_SEP);

		return sbuf.toString();
	}

	/**
	 * Returns appropriate HTML headers.
	 */
	public String getHeader() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
				+ Layout.LINE_SEP);
		sbuf.append("<html>" + Layout.LINE_SEP);
		sbuf.append("<head>" + Layout.LINE_SEP);
		sbuf.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ Layout.LINE_SEP);
		sbuf.append("<title>" + title + "</title>" + Layout.LINE_SEP);
		sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
		sbuf.append("<!--" + Layout.LINE_SEP);
		sbuf.append("body, table {font-family: '??',arial,sans-serif; font-size: 12px;}"
				+ Layout.LINE_SEP);
		sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}"
				+ Layout.LINE_SEP);
		sbuf.append("-->" + Layout.LINE_SEP);
		sbuf.append("</style>" + Layout.LINE_SEP);
		sbuf.append("</head>" + Layout.LINE_SEP);
		sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">"
				+ Layout.LINE_SEP);

		sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">"
				+ Layout.LINE_SEP);
		sbuf.append("<tr>" + Layout.LINE_SEP);

		sbuf.append("<th>时间:</th>" + Layout.LINE_SEP);
		sbuf.append("<th>信息：</th>" + Layout.LINE_SEP);
		sbuf.append("</tr>" + Layout.LINE_SEP);
		sbuf.append("<br></br>" + Layout.LINE_SEP);
		return sbuf.toString();
	}
}
