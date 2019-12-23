package com.jm.bapkp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件工具类
 * 
 */
public class PropertiesUtil {

	public static Properties load(InputStream inputStream) {
		return load(new Properties(), inputStream);
	}

	public static Properties load(Properties props, InputStream inputStream) {
		try {
			if (inputStream != null) {
				props.load(inputStream);
			}
			return props;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}

}
