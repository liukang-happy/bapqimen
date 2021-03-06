package com.jm.bapkp.util;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

public class HttpClientHelper {

	private static HttpClientHelper instance = null;

	public HttpClientHelper() {
	}

	public static HttpClientHelper getInstance() {
		if (instance == null) {
			instance = new HttpClientHelper();
		}
		return instance;
	}

	public String sendJsonHttpPost(String url, String json) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseInfo = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/octet-stream;charset=UTF-8");
			ContentType contentType = ContentType.create("application/octet-stream", CharsetUtils.get("UTF-8"));
			httpPost.setEntity(new StringEntity(json, contentType));
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				if (null != entity) {
					responseInfo = EntityUtils.toString(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseInfo;
	}

	public static void main(String[] args) {

	}
}