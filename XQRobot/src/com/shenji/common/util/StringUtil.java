package com.shenji.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.hp.hpl.jena.reasoner.rulesys.builtins.Regex;
import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;

public class StringUtil {
	/**
	 * 这个方法将JDom对象转换字符串.
	 * 
	 * @param document
	 *            将要被转换的JDom对象
	 * @param encoding
	 *            输出字符串使用的编码
	 * @return String xml对象保存到的字符串
	 */
	public static String outputToString(Document document, String encoding) {
		ByteArrayOutputStream byteRep = new ByteArrayOutputStream();
		Format format = Format.getPrettyFormat();
		format.setEncoding(encoding);
		String xmlStr = null;
		XMLOutputter docWriter = new XMLOutputter(format);
		try {
			docWriter.output(document, byteRep);
			xmlStr = byteRep.toString();
		} catch (Exception e) {
			Log.getLogger(StringUtil.class).error(e.getMessage(),e);
		} finally {
			try {
				if (byteRep != null)
					byteRep.close();
			} catch (IOException e) {
				Log.getLogger(StringUtil.class).error(e.getMessage(),e);
			}
		}
		return xmlStr;
	}

	public static boolean isAllLetter(String str) {
		String regex = "^[a-zA-Z]+$";
		return str.matches(regex);
	}

	public static String changeCharset(String str, String newCharset) {
		if (str != null) {
			byte[] bs = null;
			bs = str.getBytes();
			// 用新的字符编码生成字符串
			try {
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Log.getLogger(StringUtil.class).error(e.getMessage(),e);
			}
		}
		return null;
	}

	public static boolean wordVerification(String str) {
		String supportSymbol = "";
		for (String s : Configuration.supportSymbolSet) {
			supportSymbol = supportSymbol + s;
		}
		System.out.println(supportSymbol);
		String matchStr = "^[a-zA-Z0-9\u4e00-\u9fa5" + supportSymbol + "]+$";
		return str.matches(matchStr);
	}

	public static void main(String[] str) {
		System.err.println(wordVerification("葛超是sbd22c_c@"));
	}

}
