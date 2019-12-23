package com.jm.bapkp.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jm.bapkp.exception.MyException;
import com.jm.bapkp.utils.IpUtils;
import com.jm.bapkp.utils.StringUtils;

public class RequestInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws MyException {
		// 请求信息
		Map<String, String> requestInfo = new LinkedHashMap<String, String>();
		// 生成一个guid放在request中，记录返回日志时，取这个guid来一一对应
		String guid = StringUtils.getUUID();

		requestInfo.put("FMID", guid);
		requestInfo.put("FCREATETIME", String.valueOf(new Date().getTime()));

		// 请求的类型
		String requestType = "";
		// 请求的字符串
		String requestStr = "";
		// 请求的IP
		String host = IpUtils.getRequestIp(request);
		// 请求的接口地址，不包括问号后面的参数
		String url = request.getServletPath();
		// 这里控制不能get请求吧
//				String param = request.getParameterMap().toString();

		requestInfo.put("FHOST", host);
		requestInfo.put("FURL", url);

		// 如果是POST请求，并且不是微信支付宝的支付回调，就获取请求报文
		if ("POST".equals(request.getMethod().toUpperCase()) && !(url != null && url.startsWith("/payCallBack"))) {
			InputStream is = null;
			// 获取请求流
			try {
				is = request.getInputStream();
			} catch (IOException e) {
				throw MyException.getErr("00007", e.getMessage());
			}
			// 将请求流转为字符串
			requestStr = StringUtils.inToStr(is).trim();

			// 判断xml还是json是以字符串开头来判断的，以xml的<来判断好像有问题，忘了
			if (requestStr.startsWith("{") || requestStr.startsWith("[")) {
				requestType = "json";
			} else {
				requestType = "xml";
			}

			if ("json".equals(requestType)) {
				requestStr = requestStr.replace("PAGEINDEX", "FPAGEINDEX");
			}
		}

		requestInfo.put("FTYPE", requestType);
		// 请求类型，post
		requestInfo.put("FMETHOD", request.getMethod().toUpperCase());
		// 请求的报文
		requestInfo.put("FREQUEST", requestStr);
		// 将请求的信息存在request对象中，后面还会用到
		request.setAttribute("requestInfo", requestInfo);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
