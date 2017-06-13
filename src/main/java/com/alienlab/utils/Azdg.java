package com.alienlab.utils;

import java.io.IOException;
import java.security.MessageDigest;

import com.alienlab.utils.Azdg;
import com.alienlab.utils.PropertyConfig;



public class Azdg {
	//密钥，可自定义
	String key = new PropertyConfig("sysConfig.properties").getValue("syskey");
    public static void main(String[] args) {
       Azdg azdg = new Azdg();
       

       String clearText = "http://localhost:8080/WXMarketing/admin/index.jsp";//明文
       String cipherText = "";//密文
       String cleanText = "";//解密后的
     
   

       System.out.println("明文为：" + clearText);
       cipherText = azdg.encrypt("123456");
       System.out.println("加密后为："+cipherText);
       cleanText = azdg.decrypt(cipherText);
       System.out.println("解密后为："+cleanText);

    }

    

    /**
     * 加密算法
     * @param txt
     * @return
     */
    public String encrypt(String txt) {
       String encrypt_key = new PropertyConfig("sysConfig.properties").getValue("syskey");
       int ctr = 0;
       String tmp = "";
       for (int i = 0; i < txt.length(); i++) {
    	   ctr = (ctr == encrypt_key.length()) ? 0 : ctr;
           tmp = tmp + encrypt_key.charAt(ctr) + (char)(txt.charAt(i) ^ encrypt_key.charAt(ctr));
           ctr++;

       }
       return base64_encode(key(tmp, key));

    }

    
    /**
     * 解密算法
     * @param cipherText
     * @return
     */
    public  String decrypt(String cipherText) {
       // base64解码
       cipherText = base64_decode(cipherText);
       cipherText = key(cipherText, key);
       String tmp = "";

       for (int i = 0; i < cipherText.length(); i++) {
           int c = cipherText.charAt(i) ^ cipherText.charAt(i + 1);
           String x = "" + (char) c;
           tmp += x;
           i++;

       }

       return tmp;

    }

 

    public String key(String txt, String encrypt_key) {
       encrypt_key = strMD5(encrypt_key);
       int ctr = 0;
       String tmp = "";
       for (int i = 0; i < txt.length(); i++) {
           ctr = (ctr == encrypt_key.length()) ? 0 : ctr;
           int c = txt.charAt(i) ^ encrypt_key.charAt(ctr);
           String x = "" + (char) c;
           tmp = tmp + x;
           ctr++;
       }

       return tmp;

    }

 

    public String base64_encode(String str) {

       return new sun.misc.BASE64Encoder().encode(str.getBytes());

    }

    

    public String base64_decode(String str) {
       sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
       if (str == null)
           return null;
       try {
           return new String(decoder.decodeBuffer(str));
       } catch (IOException e) {
           e.printStackTrace();
           return null;
       }

    }

 

    public static final String strMD5(String s) {
       char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
              'a', 'b', 'c', 'd', 'e', 'f','h','o','t'};
       try {
           byte[] strTemp = s.getBytes();
           MessageDigest mdTemp = MessageDigest.getInstance("MD5");
           mdTemp.update(strTemp);
           byte[] md = mdTemp.digest();
           int j = md.length;
           char str[] = new char[j * 2];
           int k = 0;
           for (int i = 0; i < j; i++) {
              byte byte0 = md[i];
              str[k++] = hexDigits[byte0 >>> 4 & 0xf];
              str[k++] = hexDigits[byte0 & 0xf];
           }

           return new String(str);

       } catch (Exception e) {

           e.printStackTrace();

       }

       return null;

    }

 

}

