package com.jm.bapkp.exception;

import com.jm.bapkp.utils.PropertiesUtils;

/**
 * <p>Description: 自定义的异常类</p>
 * @author zhou
 * @date 2018年5月11日 下午8:00:02
 */
public class MyException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyException() {
		super();
	}
	
	public MyException (String errMsg) {
		super(errMsg);
	}
	
	/**
	 * 根据code找到对应的错误信息，错误信息统一放在配置文件errMsg中
	 * @param code
	 * @return
	 */
	public static MyException getErr(String code) {
		String errMsg = PropertiesUtils.findPropertiesKey("errMsg.properties", code);
		return new MyException(errMsg == null ? "" : code + ", " + errMsg);
	}
	
	/**
	 * 除了配置文件中的错误信息，还有一个系统报错的错误信息
	 * @param code
	 * @param baseMsg
	 * @return
	 */
	public static MyException getErr(String code, String baseMsg) {
		String errMsg = PropertiesUtils.findPropertiesKey("errMsg.properties", code);
		return new MyException(errMsg == null ? baseMsg : code + ", " + errMsg + "\r\n" + baseMsg);
	}
}
