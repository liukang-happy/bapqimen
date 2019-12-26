package com.jm.bapkp.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * 返回给奇门的响应
 * 
 * @author kang
 *
 */
@Setter
@Getter
@XmlRootElement(name = "response")
@XmlType(propOrder = { "flag", "code", "message" })
public class BapQmResponse {
	// 响应标志，默认成功
	private String flag = "success";
	// 响应码
	private String code = "";
	// 响应信息
	private String message = "";
}
