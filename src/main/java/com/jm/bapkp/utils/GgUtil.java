package com.jm.bapkp.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cfca.sm2rsa.common.PKIException;
import cfca.util.CertUtil;
import cfca.util.EnvelopeUtil;
import cfca.util.KeyUtil;
import cfca.util.SignatureUtil2;
import cfca.util.cipher.lib.JCrypto;
import cfca.util.cipher.lib.Session;
import cfca.x509.certificate.X509Cert;
import cfca.x509.certificate.X509CertHelper;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 公共util
 * 
 * 请大家一起完善，修改后及时提交以免冲突。
 * 
 * @author Administrator
 * 
 */
public class GgUtil {
	public static Session session = null;
	private static Log log = LogFactory.getLog(GgUtil.class);
	// public static X509Cert x509Certls;
    private static BASE64Encoder encoder = new BASE64Encoder();
    private static BASE64Decoder decoder = new BASE64Decoder();
    // 加密  
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	/**
     * 获取指定格式的当前日期
     * 
     * @param format
     * @return
     */
	public static String getCurrentDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	public static byte[] base64Decode(String pContent) throws IOException {
		BASE64Decoder base64 = new BASE64Decoder();
		return base64.decodeBuffer(pContent);
	}

	public static String encoderBase64(byte input[]) throws Exception {
		return encoder.encode(input);
	}

	public static byte[] decoderBase64(String str) throws Exception {
		return decoder.decodeBuffer(str);
	}

    // 读取配置文件的值--根据key读取value
	public static String getProperties(String fileName, String key) {
		Properties props = new Properties();
		try {
			InputStream in = GgUtil.class.getClassLoader().getResourceAsStream(fileName);
			props.load(in);
			String value = props.getProperty(key);
			return value;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
     * base64编码
     * 
     * @param pBytes
     * @return
     */
	public static String base64Encode(byte[] pBytes) {
		BASE64Encoder base64 = new BASE64Encoder();
		return base64.encode(pBytes);
	}

	public static String base64Encode(String str) {
		try {
			return base64Encode(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * webservice调用方法
	 * 
	 * @param xml       请求报文
	 * @param url       电商webservice地址
	 * @param namespace 命名空间
	 * @param method    方法名
	 * @return
	 * @throws Exception
	 */
	/*
	 * public static String callWebService(String xml, String url, String namespace,
	 * String method) throws Exception { String recieveData = ""; try {
	 * RPCServiceClient serviceClient = new RPCServiceClient(); Options options =
	 * serviceClient.getOptions(); options.setTransportInProtocol("SOAP");
	 * options.setAction(method); EndpointReference targetEPR = new
	 * EndpointReference(url); options.setTo(targetEPR);
	 * 
	 * int delay = Integer.valueOf("20000").intValue();
	 * options.setTimeOutInMilliSeconds(delay);
	 * System.setProperty("sun.net.client.defaultConnectTimeout",
	 * String.valueOf(delay));// （单位：毫秒）
	 * System.setProperty("sun.net.client.defaultReadTimeout",
	 * String.valueOf(delay)); // （单位：毫秒）
	 * 
	 * QName opAddEntry = new QName(namespace, method); recieveData = (String)
	 * serviceClient.invokeBlocking(opAddEntry, new Object[] { xml }, new Class[] {
	 * String.class })[0]; } catch (Exception e) { // log.error("", e); throw e; }
	 * return recieveData; }
	 */

	public static String signature(String srcData, String pfxPath, String password)
			throws UnsupportedEncodingException, PKIException, FileNotFoundException {
		Session session = null;
		JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
		session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
		// 1.从PFX文件获取电商私钥
		PrivateKey priKey = KeyUtil.getPrivateKeyFromPFX(pfxPath, password);
		// 2.调用 signUtil.p7SignMessageAttach()进行签名
		// X509Cert cert = CertUtil.getCertFromPfx(pfxPath, pwd);
		// //--从PFX文件获取X509证书
		// 选择进行签名的算法
		SignatureUtil2 signUtil = new SignatureUtil2();
		byte[] signature = signUtil.p1SignMessage("SHA1withRSAEncryption", srcData.getBytes("UTF8"), priKey, session);
		String qmz = new String(signature);
		return qmz;
	}

	public static boolean verifySign(String srcData, String qmz, String cerPath) {
		Session session = null;
		boolean flag = false;
		try {
			JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
			session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
			SignatureUtil2 signUtil = new SignatureUtil2();
//			String cerPath = GgUtil.class.getResource("/CA").getPath() + "/picc.cer";
			cerPath = URLDecoder.decode(cerPath, "UTF-8");
			X509Cert cert = X509CertHelper.parse(cerPath);
			PublicKey pubKey = cert.getPublicKey();// 获取公钥验签

			if (signUtil.p1VerifyMessage("SHA1withRSAEncryption", srcData.getBytes("UTF8"), qmz.getBytes(), pubKey,
					session)) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * CFCA进行加密
	 * 
	 * @param srcData
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws PKIException
	 * @throws FileNotFoundException
	 */
	public static String encryCfca(String srcData, String cerPath)
			throws UnsupportedEncodingException, PKIException, FileNotFoundException {
		JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
		Session session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
		// 5.调用EnvelopeUtil.envelopeMessage()，对原文调用平台公钥进行加密
		String[] certPaths = cerPath.split(";");
		X509Cert[] certs = new X509Cert[certPaths.length];
		for (int i = 0; i < certPaths.length; i++) {
			X509Cert cert1 = new X509Cert(new FileInputStream(certPaths[i]));
			certs[i] = cert1;
		}
		byte[] encryptedData = EnvelopeUtil.envelopeMessage(srcData.getBytes("utf-8"), "RC4", certs);
		String jm = new String(encryptedData);
		return jm;
	}

	/**
	 * CFCA进行解密
	 * 
	 * @param srcData
	 * @return
	 * @throws PKIException
	 * @throws IOException
	 */
	public static String deEncryCfca(String srcData, String pfxPath, String pwd) {
		try {
			JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
			Session session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
//			String pfxPath = GgUtil.class.getResource("/CA").getPath() + "/tungkongca.pfx";
//			String pwd = "1";
			// 2.调用 EnvelopeUtil.openEvelopedMessage()进行解密
			PrivateKey priKeypfx = (PrivateKey) KeyUtil.getPrivateKeyFromPFX(pfxPath, pwd);
			X509Cert certpfx = CertUtil.getCertFromPfx(pfxPath, pwd);
			byte[] sourceData = EnvelopeUtil.openEvelopedMessage(srcData.getBytes("utf-8"), priKeypfx, certpfx, session);
			String ybwString = new String(sourceData, "utf-8");
//			System.out.println("解密后的报文是---" + ybwString);
			return ybwString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
