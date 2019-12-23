package com.jm.bapkp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * gzip压缩解压缩，抄的，没什么解释的
 */
public class ZipUtils {

	// 压缩
	public static byte[] compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		byte[] buffer = new byte[1024];
		int n;
		while ((n = is.read(buffer)) >= 0) {
			gzip.write(buffer, 0, n);
		}
		gzip.close();
		return out.toByteArray();
	}

	/**
	 * 解压成string
	 * 
	 * @param by
	 * @return
	 * @throws IOException
	 */
	public static String uncompress(byte[] by) throws IOException {
		if (by == null || by.length == 0) {
			return "";
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream is = new ByteArrayInputStream(by);
		GZIPInputStream gunzip = new GZIPInputStream(is);
		byte[] buffer = new byte[1024];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString("UTF-8")
		gunzip.close();
		return out.toString("UTF-8");
	}

	/**
	 * 解压成byte[]
	 * 
	 * @param by
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompressToByte(byte[] by) throws IOException {
		if (by == null || by.length == 0) {
			return by;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream is = new ByteArrayInputStream(by);
		GZIPInputStream gunzip = new GZIPInputStream(is);
		byte[] buffer = new byte[1024];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		gunzip.close();
		return out.toByteArray();
	}

	// string解压缩成string
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
		GZIPInputStream gunzip = new GZIPInputStream(is);
		byte[] buffer = new byte[1024];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
		gunzip.close();
		return out.toString("UTF-8");
	}
}
