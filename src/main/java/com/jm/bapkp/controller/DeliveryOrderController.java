package com.jm.bapkp.controller;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jm.bapkp.bean.BapkpRequest;
import com.jm.bapkp.bean.Context;
import com.jm.bapkp.bean.SQLBuilderItem;
import com.jm.bapkp.util.ConfigUtil;
import com.jm.bapkp.util.DateUtil;
import com.jm.bapkp.util.HttpClientHelper;

@RestController
@RequestMapping(value = "/deliveryorder")
public class DeliveryOrderController {

	private Log logger = LogFactory.getLog(this.getClass());

	private HttpClientHelper httpClientHelper = HttpClientHelper.getInstance();

	@ResponseBody
	@PostMapping(value = "/confirm", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String query(@RequestBody String postData) throws Exception {
		// xml格式数据转成json
		JSONObject jsonData = getJsonDataFromXml(postData);
		JSONObject deliveryOrder = (JSONObject) jsonData.get("deliveryOrder");
		JSONArray orderLines = (JSONArray) jsonData.get("orderLines");
		JSONArray packages = (JSONArray) jsonData.get("packages");

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
		itemExecute.setCaption("发货单确认接口执行脚本");
		itemExecute.setEnabled(true);
		Map<String, Object> exe1 = new HashMap<>();
		exe1.put("FMID", fmid);
		itemExecute.getExecuteList().add(exe1);
		items.add(itemExecute);

		req.setContext(context);
		req.setSqlBuilderItems(items);

		// 转发请求
		String url = ConfigUtil.get("url");
		String sendJsonHttpPost = httpClientHelper.sendJsonHttpPost(url, JSON.toJSONString(req));

		return sendJsonHttpPost;
	}

	/**
	 * xml格式数据转成json
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 * @throws UnsupportedEncodingException
	 */
	private JSONObject getJsonDataFromXml(String xml) throws DocumentException, UnsupportedEncodingException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
		// 获取根节点
		Element root = doc.getRootElement();
		// 获取节点的数据
		JSONObject jsonObject = getData(root);
		logger.info(jsonObject.toJSONString());
		return jsonObject;
	}

	/**
	 * 获取节点的数据
	 * 
	 * @param element
	 * @return
	 */
	private JSONObject getData(Element element) {
		JSONObject json = new JSONObject();
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element subElement = iterator.next();
			String name = subElement.getName();
			Iterator<Element> subIterator = subElement.elementIterator();
			if (getArrayFieldName().contains(name)) {
				JSONArray jsonArr = new JSONArray();
				while (subIterator.hasNext()) {
					Element subSubElement = subIterator.next();
					JSONObject data = getData(subSubElement);
					jsonArr.add(data);
				}
				json.put(name, jsonArr);
			} else if (getArraySubFieldName().contains(name)) {
				JSONObject data = getData(subElement);
				json = data;
			} else {
				if (subIterator.hasNext()) {
					JSONObject data = getData(subElement);
					json.put(name, data);
				} else {
					json.put(name, subElement.getStringValue());
				}
			}
		}
		return json;
	}

	/**
	 * 数组节点名称
	 * 
	 * @return
	 */
	private List<String> getArrayFieldName() {
		return Arrays.asList("orderLines", "packages", "packageMaterialList", "items");
	}

	/**
	 * 数组节点下子节点名称
	 * 
	 * @return
	 */
	private List<String> getArraySubFieldName() {
		return Arrays.asList("orderLine", "package", "packageMaterial", "item");
	}

}
