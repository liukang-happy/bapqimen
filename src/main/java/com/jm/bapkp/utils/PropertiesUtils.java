package com.jm.bapkp.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * 属性文件的操作
 */
public class PropertiesUtils {

	// 类加载器
	public static ClassLoader loader = Thread.currentThread().getContextClassLoader();

	/**
	 * 获取属性文件的数据 根据key获取值
	 * 
	 * @param fileName 文件名 (注意：加载的是src下的文件,如果在某个包下．请把包名加上)
	 * @param key
	 * @return
	 */
	public static String findPropertiesKey(String fileName, String key) {
		try {
			Properties prop = getProperties(fileName);
			return prop.getProperty(key);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 返回 Properties
	 * 
	 * @param fileName 文件名 (注意：加载的是src下的文件,如果在某个包下．请把包名加上)
	 * @param
	 * @return
	 */
	public static Properties getProperties(String fileName) {
		Properties prop = new Properties();
		InputStream in = null;
		// 以下方法读取属性文件会缓存问题
//      InputStream in = TaskController.class  
//              .getResourceAsStream("/config.properties");  
		try {
			String savePath = loader.getResource(fileName).getPath();
			// 读取文件内容
			in = new BufferedInputStream(new FileInputStream(URLDecoder.decode(savePath, "UTF-8")));
			// 转化成Properties对象，并返回该对象
			prop.load(in);
		} catch (Exception e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	/**
	 * 写入properties信息
	 * 
	 * @param key   名称
	 * @param value 值
	 */
	public static boolean modifyProperties(String fileName, String key, String value) {
		try {
			// 从输入流中读取属性列表（键和元素对）
			Properties prop = getProperties(fileName);
			// 设置键值
			prop.setProperty(key, value);
			// 获取文件路径
			String path = loader.getResource(fileName).getPath();
			// 更新文件信息
			FileOutputStream outputFile = new FileOutputStream(path);
			prop.store(outputFile, "modify");
			outputFile.flush();
			outputFile.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
