package com.shenji.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import com.shenji.common.log.Log;

public class ConfigurantionUtil {
	private Properties properties = null;
	private FileInputStream inputStream = null;
	private FileOutputStream outputStream = null;
	private File file;
	private String filepath;

	public ConfigurantionUtil(File file) {
		try {
			this.file = file;
			inputStream = new FileInputStream(file);
			this.properties = new Properties();
			if (null != inputStream) {
				this.properties.load(inputStream);
			}
		} catch (FileNotFoundException e) {
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getValue(String key) {
		String value = null;
		if (this.properties.containsKey(key)) {
			value = properties.getProperty(key);
		}
		return value;
	}

	public HashMap<Object, Object> getValues() {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		Enumeration<?> en = this.properties.propertyNames();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			Object Property = Integer.parseInt(this.properties
					.getProperty((String) key));
			map.put(key, Property);
		}
		return map;
	}

	public void clear() {
		this.properties.clear();
	}

	public void setValue(String key, String value) {
		properties.setProperty(key, value);
	}

	public void savaFile(String description) {
		try {
			outputStream = new FileOutputStream(file);
			properties.store(outputStream, description);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	public void savaFile(String filename, String description) {
		try {
			filepath = file.getAbsolutePath() + filename;
			outputStream = new FileOutputStream(filepath);
			properties.store(outputStream, description);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
		}
	}

	public void save() {
		// TODO Auto-generated method stub
		this.savaFile(null);
	}
}
