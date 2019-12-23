package com.jm.bapkp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.jm.bapkp.exception.MyException;

/**
 * 一些字符串操作的工具类
 * 
 * @author zhou
 */
public class StringUtils {

	/**
	 * 输入流转化为字符串 这里有解压
	 */
	public static String inToStr(InputStream is) throws MyException {
		// 解压
		byte[] streamByte = null;
		try {
			// 先转化为byte数组
			streamByte = input2byte(is);
		} catch (IOException e1) {
			throw MyException.getErr("00001", e1.getMessage());
		}

		// 请求流中，如果是以这几个字节开头，就认为是压缩过的，需要解压
		if (streamByte.length > 100) {
			byte[] header = { 31, -117, 8, 0, 0, 0, 0, 0 };
			for (int i = 0; i < 8; i++) {
				if (streamByte[i] != header[i]) {
					break;
				}
				if (i == 7) {
					try {
						// 返回解压的结果
						return ZipUtils.uncompress(streamByte);
					} catch (IOException e) {
						throw MyException.getErr("00001", e.getMessage());
					}
				}
			}
		}
		// 不解压的话就直接返回了
		try {
			return new String(streamByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw MyException.getErr("00001", e.getMessage());
		}
	}

	/**
	 * 流到字节
	 */
	public static byte[] input2byte(InputStream is) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		// 缓冲区
		byte[] buffer = new byte[4096];
		int n = 0;
		// 读取
		while (-1 != (n = is.read(buffer))) {
			output.write(buffer, 0, n);
		}
		is.close();
		return output.toByteArray();
	}

	/**
	 * 产生UUID，不含大括号和横杠
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").replace("{", "").replace("}", "");
	}
}
