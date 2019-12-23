package com.jm.bapkp.utils;


import java.io.IOException;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESUtil {
	private static final String AESTYPE ="AES/ECB/PKCS5Padding"; 
	 
    public static String AES_Encrypt(String keyStr, String plainText) throws Exception { 
        byte[] encrypt = null; 
        try{ 
            Key key = generateKey(keyStr); 
            Cipher cipher = Cipher.getInstance(AESTYPE); 
            cipher.init(Cipher.ENCRYPT_MODE, key); 
            encrypt = cipher.doFinal(plainText.getBytes("UTF-8"));     
            return new String(Base64.encodeBase64(encrypt)); 
        }catch(Exception e){ 
        	throw e; 
        }
        
    } 
 
    public static String AES_Decrypt(String keyStr, String encryptData) throws Exception {
        byte[] decrypt = null; 
        try{ 
            Key key = generateKey(keyStr); 
            Cipher cipher = Cipher.getInstance(AESTYPE); 
            cipher.init(Cipher.DECRYPT_MODE, key); 
            decrypt = cipher.doFinal(base642byte(encryptData)); 
          //  decrypt = "";
            return new String(decrypt,"UTF-8").trim(); 
        }catch(Exception e){ 
        	throw e; 
        } 
       
    } 
    
	/**
	 * 解码
	 * 
	 * @param str
	 * @return string
	 */
	public static byte[] base642byte(String str) {
		byte[] bt = null;
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			bt = decoder.decodeBuffer(str);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bt;
	}
    private static Key generateKey(String key)throws Exception{ 
        try{            
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES"); 
            return keySpec; 
        }catch(Exception e){ 
            e.printStackTrace(); 
            throw e; 
        } 
 
    } 

	
	public static void main(String[] args) throws Exception {
		String str="{\"fpdm\":\"我124578767654\",\"fphm\":\"87976428\",\"jym\":\"97368916390973738987\",\"timestamp\":\"143200830000\"}";
//		String str="124578767654,87976428,97368916390973738987,90999923233233";
		/*String key=genKeyAES();
		System.out.println("key:"+key);
		String encryptStr=encryptAES(str,key);
		System.out.println("encryptStr:"+encryptStr+"��");
		String decryptStr=decryptAES(encryptStr, key);
		System.out.println("decryptStr:"+decryptStr);*/
		String key = "UITN25LMUQC436IM";
//		String encryptStr="fxct0pD+5TEpQHULEjM89KnX9I2AlDVnD7gASvBsQoGn6ZPzIY/8obwbmidgcBR9yh1Gd+0w8EGCIshuQzP3pA8aovb53WdOwqgQiHDBgP34LiPY91VEiR+pdI7rftt3+gfR7YHYnO2aYzjL6K8cgQ==";
		String encryptStr = AESUtil.AES_Encrypt(key, str);
		System.out.println("encryptStr==:"+encryptStr);
		String yw = AESUtil.AES_Decrypt(key, encryptStr);
		System.out.println("yw==:"+yw);
	}
}
