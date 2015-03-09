package com.shenji.common.log;

import org.apache.log4j.HTMLLayout;

public class MailLayout extends HTMLLayout {
	public String getContentType() {
		return "text/html;charset=UTF-8";
	}
}