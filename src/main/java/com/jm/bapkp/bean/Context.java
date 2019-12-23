package com.jm.bapkp.bean;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Context {

	@JSONField(name = "enable_compn")
	private String enableCompn;

	private String token;

	private String version;

	private String from;

	private String mchid;

	private String appid;

	private String timestamp;

}
