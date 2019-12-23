package com.jm.bapkp.bean;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BapkpRequest {

	@JSONField(name = "Context")
	private Context context;

	@JSONField(name = "SQLBuilderItem")
	private List<SQLBuilderItem> sqlBuilderItems;

}
