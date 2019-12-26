package com.jm.bapkp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jm.bapkp.bean.BapkpRequest;
import com.jm.bapkp.bean.Context;
import com.jm.bapkp.bean.SQLBuilderItem;
import com.jm.bapkp.service.StockOutService;
import com.jm.bapkp.util.ConfigUtil;
import com.jm.bapkp.util.DateUtil;
import com.jm.bapkp.util.HttpClientHelper;

/**
 * 出库单接口实现
 * 
 * @author kang
 *
 */
@Service
public class StockOutServiceImpl implements StockOutService {

	private HttpClientHelper httpClientHelper = HttpClientHelper.getInstance();

	public String confirm(JSONObject jsonData) {
		JSONObject deliveryOrder = (JSONObject) jsonData.get("DELIVERYORDER");
		JSONArray orderLines = (JSONArray) jsonData.get("ORDERLINES");
		JSONArray packages = (JSONArray) jsonData.get("PACKAGES");

		// 唯一码FMID
		String fmid = UUID.randomUUID().toString().toUpperCase();

		// 转发的请求
		BapkpRequest req = new BapkpRequest();

		Context context = new Context();
		context.setToken("C7E4F107-14D7-4E02-BAB4-274B67C17EDC");
		context.setVersion("1.0.165-beta");
		context.setFrom("3");
		context.setMchid("T001");
		context.setAppid("2E855DFE-1987-4889-81C3-AAF92579ABC3");
		context.setTimestamp(DateUtil.format(DateUtil.getNow(), "yyyyMMddHHmmssms"));

		List<SQLBuilderItem> items = new ArrayList<>();
		SQLBuilderItem itemMain = new SQLBuilderItem();
		itemMain.setSqlBuilderId("{808F15B4-2AB2-4007-B2C7-1C1FC5697539}");
		itemMain.setTableName("BN_MID_DELIVERYORDER");
		itemMain.setCaption("主表");
		itemMain.setEnabled(true);
		deliveryOrder.put("FMID", fmid);
		deliveryOrder.put("RowState", "4");
		itemMain.getSaveList().add(deliveryOrder);
		items.add(itemMain);

		SQLBuilderItem itemPack = new SQLBuilderItem();
		itemPack.setSqlBuilderId("{5F48E764-F42A-4F34-9BFF-0B97CD1B399D}");
		itemPack.setTableName("BN_MID_PACKAGE");
		itemPack.setCaption("物流");
		itemPack.setEnabled(true);
		for (int i = 0; i < packages.size(); i++) {
			JSONObject packObj = (JSONObject) packages.get(i);
			// 遍历参数，去掉数组类型的属性值
			Iterator<Entry<String, Object>> iterator = packObj.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				if (entry.getValue() instanceof JSONArray) {
					iterator.remove();
				}
			}
			packObj.put("FMID", UUID.randomUUID().toString().toUpperCase());
			packObj.put("FPID", fmid);
			packObj.put("RowState", "4");
			itemPack.getSaveList().add(packObj);
		}
		items.add(itemPack);

		SQLBuilderItem itemDetail = new SQLBuilderItem();
		itemDetail.setSqlBuilderId("{238FF6E8-9992-4434-B1EC-56AC9EBB61D5}");
		itemDetail.setTableName("BN_MID_ORDERLINES");
		itemDetail.setCaption("明细");
		itemDetail.setEnabled(true);
		for (int i = 0; i < orderLines.size(); i++) {
			JSONObject orderLineObj = (JSONObject) orderLines.get(i);
			orderLineObj.put("FMID", UUID.randomUUID().toString().toUpperCase());
			orderLineObj.put("FPID", fmid);
			orderLineObj.put("RowState", "4");
			itemDetail.getSaveList().add(orderLineObj);
		}
		items.add(itemDetail);

		SQLBuilderItem itemExecute = new SQLBuilderItem();
		itemExecute.setSqlBuilderId("{120A3537-57BC-4DD3-9BE8-86B0925FE677}");
		itemExecute.setTableName("");
		itemExecute.setCaption("出库单确认接口执行脚本");
		itemExecute.setEnabled(true);
		Map<String, Object> exe1 = new HashMap<>();
		exe1.put("FMID", fmid);
		itemExecute.getExecuteList().add(exe1);
		items.add(itemExecute);

		req.setContext(context);
		req.setSqlBuilderItems(items);

		// 转发请求
		String url = ConfigUtil.get("url");
		return httpClientHelper.sendJsonHttpPost(url, JSON.toJSONString(req));
	}

}
