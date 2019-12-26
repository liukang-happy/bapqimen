package com.jm.bapkp.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 发货单接口
 * 
 * @author kang
 *
 */
public interface DeliveryOrderService {

	String confirm(JSONObject jsonData);

}
