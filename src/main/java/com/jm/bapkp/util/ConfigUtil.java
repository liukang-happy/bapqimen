package com.jm.bapkp.util;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 配置文件工具类
 * 
 */
public class ConfigUtil {

	private static Log log = LogFactory.getLog(ConfigUtil.class);

	private static final String FILENAME = "bap.properties";

	protected static Properties props = null;

	static {
		try {
			InputStream inputStream = ConfigUtil.class.getResourceAsStream("/" + FILENAME);
			props = PropertiesUtil.load(inputStream);
			log.info(FILENAME + ":");
			for (Entry<Object, Object> entry : props.entrySet()) {
				log.info(entry.getKey() + ":" + entry.getValue());
			}
		} catch (Exception e) {
			throw new RuntimeException("bap.properties load error!" + e.getMessage());
		}
	}

	public static String get(String key) {
		if (props.getProperty(key) == null) {
			throw new RuntimeException("key:" + key + " not exist!");
		}
		return props.getProperty(key);
	}

	public static String get(String key, String defaultVal) {
		if (props == null || props.getProperty(key) == null) {
			return defaultVal;
		} else {
			return props.getProperty(key);
		}
	}

}
