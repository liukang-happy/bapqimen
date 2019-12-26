package com.jm.bapkp.controller;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jm.bapkp.bean.BapQmResponse;
import com.jm.bapkp.service.DeliveryOrderService;
import com.jm.bapkp.service.StockOutService;
import com.jm.bapkp.util.XmlUtil;

@RestController
public class BaseController {

	private Log logger = LogFactory.getLog(this.getClass());

	// 发货单确认
	private String deliveryOrderConfirm = "deliveryorder.confirm";
	// 出库单确认
	private String stockOutConfirm = "stockout.confirm";

	@Autowired
	private DeliveryOrderService deliveryOrderService;

	@Autowired
	private StockOutService stockOutService;

	@ResponseBody
	@PostMapping(value = "/qimentobap", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String query(String method, @RequestBody String postData) throws Exception {
		BapQmResponse response = new BapQmResponse();
		// xml格式数据转成json
		JSONObject jsonData = getJsonDataFromXml(postData);
		// 先读取扩展属性，确认单据和操作类型
		// JSONObject extendProps = (JSONObject) jsonData.get("extendProps");
		// if (extendProps == null) {
		// response.setFlag("failure");
		// response.setCode("error");
		// response.setMessage("未接收到单据类型参数！");
		// return XmlUtil.toXML(response);
		// }
		// String findex = extendProps.getString("FINDEX");
		// 发货单确认
		if (deliveryOrderConfirm.equals(method)) {
			String data = deliveryOrderService.confirm(jsonData);
			logger.info("调用bap返回结果:" + data);
		}
		// 出库单确认
		else if (stockOutConfirm.equals(method)) {
			String data = stockOutService.confirm(jsonData);
			logger.info("调用bap返回结果:" + data);
		}
		// 未匹配到单据类型
		else {
			response.setFlag("failure");
			response.setCode("error");
			response.setMessage("单据类型不匹配！");
			return XmlUtil.toXML(response);
		}
		return XmlUtil.toXML(response);
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
	@SuppressWarnings("unchecked")
	private JSONObject getData(Element element) {
		JSONObject json = new JSONObject();
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element subElement = iterator.next();
			String name = subElement.getName().toUpperCase();
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
		return Arrays.asList("ORDERLINES", "PACKAGES", "PACKAGEMATERIALLIST", "ITEMS");
	}

	/**
	 * 数组节点下子节点名称
	 * 
	 * @return
	 */
	private List<String> getArraySubFieldName() {
		return Arrays.asList("ORDERLINE", "PACKAGE", "PACKAGEMATERIAL", "ITEM");
	}

}
