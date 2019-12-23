package com.jm.bapkp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.XML;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jm.bapkp.bean.Context;
import com.jm.bapkp.bean.SQLBuilderItem;
import com.jm.bapkp.util.DateUtil;

@RestController
@RequestMapping(value = "/returnorder")
public class ReturnOrderController {

//	private String url = "";

	@ResponseBody
	@PostMapping(value = "/confirm", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String query(@RequestBody String postData) throws Exception {
		String postJsonStr = XML.toJSONObject(postData).toString();
		JSONObject postJson = JSONObject.parseObject(postJsonStr);
		JSONObject request = JSONObject.parseObject(postJson.get("request").toString());
		JSONObject deliveryOrder = JSON.parseObject(request.get("deliveryOrder").toString());
		JSONObject orderLines = JSON.parseObject(request.get("orderLines").toString());
		JSONArray orderLine = JSON.parseArray(orderLines.get("orderLine").toString());

		JSONObject data = new JSONObject();

		Context context = new Context();
		context.setEnableCompn("1");
		context.setToken("C7E4F107-14D7-4E02-BAB4-274B67C17EDC");
		context.setVersion("1.0.165-beta");
		context.setFrom("1");
		context.setMchid("T001");
		context.setAppid("2E855DFE-1987-4889-81C3-AAF92579ABC3");
		context.setTimestamp(DateUtil.format(DateUtil.getNow(), "yyyyMMddHHmmssms"));

		List<SQLBuilderItem> items = new ArrayList<>();
		SQLBuilderItem item = new SQLBuilderItem();
		item.setSqlBuilderId("{E16D4FCC-5830-4ABE-955D-B1B4BB6168A1}");
		item.setTableName("BN_BIZ_SA_MA");
		item.setCaption("主表");
		item.setEnabled(true);
		item.getSaveList().add(deliveryOrder);
		items.add(item);

		SQLBuilderItem item2 = new SQLBuilderItem();
		item2.setSqlBuilderId("{7AF74427-3E05-4A3D-89AE-3DBF8DE93A56}");
		item2.setTableName("BN_BIZ_SA_DE");
		item2.setCaption("明细");
		item2.setEnabled(true);
		for (int i = 0; i < orderLine.size(); i++) {
			item2.getSaveList().add(orderLine.get(i));
		}
		items.add(item2);

		SQLBuilderItem item3 = new SQLBuilderItem();
		item3.setSqlBuilderId("{2D279CC4-5FF7-4ADA-8D34-6E480ED5B720}");
		item3.setTableName("BN_BIZ_SA_MA");
		item3.setCaption("销售单增量同步脚本");
		item3.setEnabled(true);
		Map<String, Object> exe1 = new HashMap<>();
		exe1.put("FMID", "{E97B79CD-5D2B-4125-CA4E-C1B490BC0C49}");
		exe1.put("FTID", "{0548F19A-2F7F-CC51-6121-5C1622D6DF3F}");
		exe1.put("FUNIT_ID", "{352ABEA3-BFB8-4C81-87DF-9FB554E99DDE}");
		exe1.put("FUNIT_NAME", "标准收银");
		exe1.put("FACTION_ID", "");
		exe1.put("FACTION_NAME", "");
		exe1.put("FUID", "C7E4F107-14D7-4E02-BAB4-274B67C17EDC");
		exe1.put("FOPERATORID", "C7E4F107-14D7-4E02-BAB4-274B67C17EDC");
		exe1.put("FOPERATOR", "系统管理员");
		item3.getExecuteList().add(exe1);
		items.add(item3);

		data.put("Context", context);
		data.put("SQLBuilderItem", items);
		return data.toJSONString();
	}

}
