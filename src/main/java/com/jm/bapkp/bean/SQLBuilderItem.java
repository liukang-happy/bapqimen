package com.jm.bapkp.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SQLBuilderItem {

	@JSONField(name = "SQLBuilderID")
	private String sqlBuilderId;

	@JSONField(name = "TableName")
	private String tableName;

	@JSONField(name = "Caption")
	private String caption;

	@JSONField(name = "Enabled")
	private Boolean enabled;

	@JSONField(name = "Save")
	private List<Object> saveList = new ArrayList<>();

	@JSONField(name = "Execute")
	private List<Object> executeList = new ArrayList<>();

	@JSONField(name = "Select")
	private Map<String, Object> selectList = new HashMap<>();

}
