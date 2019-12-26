package com.jm.bapkp.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 出库单接口
 * 
 * @author kang
 *
 */
public interface StockOutService {

	public String confirm(JSONObject jsonData);

}
