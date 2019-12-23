package com.jm.bapkp.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jm.bapkp.utils.AESUtil;
import com.jm.bapkp.utils.FileUtils;
import com.jm.bapkp.utils.GgUtil;
import com.jm.bapkp.utils.HttpUtil;
import com.jm.bapkp.utils.PropertiesUtils;
import com.jm.bapkp.utils.XmlUtil;

import cfca.sm2rsa.common.PKIException;

@Controller
@RequestMapping(value = "/Invoice")
public class FpController {
	// 请求对象
	@Autowired
	private HttpServletRequest request;

	private static final Logger log = Logger.getLogger(FpController.class);

	private static final String url = "http://111.202.226.69:9026/zxkp/eInvoiceApi";// 测试环境-post方式

	// 请求发票查询
	@ResponseBody
	@RequestMapping(value = "Query", produces = { "application/json;charset=UTF-8" }, method = RequestMethod.POST)
	public String fpQuery() throws Exception {

		return fpAction("FPCX");

	}

	// 请求发票查询
	@ResponseBody
	@RequestMapping(value = "Draw", produces = { "application/json;charset=UTF-8" }, method = RequestMethod.POST)
	public String fpInvoice() throws Exception {

		return fpAction("REQUEST_E_FAPIAO_KJ");

	}

	private String fpAction(String interfaceCode)
			throws UnsupportedEncodingException, PKIException, FileNotFoundException, Exception, IOException {
		Map<String, String> requestInfo = (Map<String, String>) request.getAttribute("requestInfo");
		// JSONObject param = JSONObject.parseObject(requestInfo.get("FREQUEST"));

		String requestCode = PropertiesUtils.findPropertiesKey("kpConfig.properties", "kp.requestCode");// 1234567890c,91370100163157467J"1234567890c";
		// String interfaceCode = "FPCX";// 发票开具"FPCX";
		String secret = PropertiesUtils.findPropertiesKey("kpConfig.properties", "kp.secret");// "1234567890123456";

		String jmfs = "ca";// 加密方式，ca、aes、base64
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		System.out.println();// new Date()为获取当前系统时间

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<interface>" + "<globalInfo>"
				+ "<appId>dzfp</appId>" + "<interfaceCode>" + interfaceCode + "</interfaceCode>" + "<requestCode>"
				+ requestCode + "</requestCode>" + "<requestTime>" + df.format(new Date()) + "</requestTime>"
				+ "<responseCode>tunkong</responseCode>" + "</globalInfo>" + "<returnStateInfo>"
				+ "<returnCode></returnCode>" + "<returnMessage></returnMessage>" + "</returnStateInfo>";
		xml = xml + "<data>" + "<content><![CDATA[";

		String ywXml = requestInfo.get("FREQUEST");

		String jmStr = "";
		// 我们要走CA加密

		if (jmfs.equals("ca")) {
			String tungkongCertPath = FpController.class.getResource("/tungkongca.cer").getPath();
			jmStr = GgUtil.encryCfca(ywXml, tungkongCertPath);
		} else if (jmfs.equals("aes")) {
			jmStr = AESUtil.AES_Encrypt(secret, ywXml);
		} else if (jmfs.equals("base64")) {
			jmStr = GgUtil.base64Encode(ywXml);
		}

		String signStr = "";
		xml = xml + jmStr + "]]>" + "</content><signature>" + signStr + "</signature></data>" + "</interface>";
//    	String ret = GgUtil.callWebService(xml, url, "http://impl.service.tax.inspur","doService");
//    	String ret = new WlkpServiceImpl().doService(xml);
		String ret = HttpUtil.doPost(url, new HashMap(), xml);
		log.info("返回---：" + ret);
		return resolveRetXml(ret, interfaceCode, jmfs, secret);
	}

	public static String readXml(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		String line = "";
		StringBuffer xml = new StringBuffer();
		while ((line = br.readLine()) != null) {
			xml.append(line).append('\n');
		}
		return xml.toString();
	}

	public static String resolveRetXml(String ret, String interfaceCode, String jmfs, String secret) throws Exception {
		Map retMap = XmlUtil.getMapFromXml(ret, "returnStateInfo");
		log.info("retMap--:" + retMap);
		String returnCode = retMap.get("returnCode").toString();
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		if (returnCode.equals("0000")) {
			String retContent = XmlUtil.getMapFromXml(ret, "data").get("content").toString();
			// Element content = element.getChild("data").getChild("content");
//    		log.info("retContent--:"+retContent);
			// 我们用CA解密
			boolean flg = false;
			if (jmfs.equals("base64")) {
				// base64解码
				retContent = new String(GgUtil.base64Decode(retContent), "UTF-8");
				// content.setContent(new
				// CDATA(retContent.trim()));//setText("<![CDATA["+retContent.trim()+ "]]>");
				log.info("retContent--base64Decode:" + retContent);
				flg = true;
			} else if (jmfs.equals("ca")) {
				// ca解码
				String pfxPath = FpController.class.getResource("/1234567890c.pfx").getPath();
				retContent = GgUtil.deEncryCfca(retContent, pfxPath, "000000");
				// content.setContent(new CDATA(retContent.trim()));
				log.info("retContent--ca解密:" + retContent);
				flg = true;
			} else if (jmfs.equals("aes")) {
				// aes解码
				retContent = AESUtil.AES_Decrypt(secret, retContent);
				// content.setContent(new CDATA(retContent.trim()));
				log.info("retContent--aes解密:" + retContent);
				flg = true;
			}
			if (flg) {
				SAXBuilder builder = new SAXBuilder();
				Document document = builder.build(new StringReader(retContent));
				Element element = document.getRootElement();
				element.addContent(new Element("returnCode").setText(returnCode));
				element.addContent(new Element("returnMessage").setText(retMap.get("returnMessage").toString()));
				Format format = Format.getCompactFormat();
				format.setEncoding("UTF-8");// 设置xml文件的字符为UTF-8，解决中文问题
				XMLOutputter xmlout = new XMLOutputter();
				xmlout.output(document, bo);
			}
			if (interfaceCode.equals("GETPDF")) {
				String pdfBase64 = XmlUtil.getMapFromXml(retContent, "RESPONSE_COMMON_GETPDF").get("PDF_TYPE")
						.toString();
				FileUtils.writeFile("c:/test.pdf", GgUtil.base64Decode(pdfBase64));
			}
		} else {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder
					.build(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><BUSINESS></BUSINESS>"));
			Element element = document.getRootElement();
			element.addContent(new Element("returnCode").setText(returnCode));
			element.addContent(new Element("returnMessage").setText(retMap.get("returnMessage").toString()));
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");// 设置xml文件的字符为UTF-8，解决中文问题
			XMLOutputter xmlout = new XMLOutputter();
			xmlout.output(document, bo);
		}
		return bo.toString().trim();
	}
}
