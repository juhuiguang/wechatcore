package com.alienlab.utils;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
* 使用DES加密和解密的方法
* 
* @修改�?  小�?�虎2
* @author:azhong 
* @change:exmorning
* */
public class CryptoTools {

   private final byte[] DESkey = new PropertyConfig("sysConfig.properties").getValue("syskey").getBytes();// 设置密钥，略�?
   private final byte[] DESIV = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};// 设置向量，略�?

   private AlgorithmParameterSpec iv = null;// 加密算法的参数接口，IvParameterSpec是它的一个实�?
   private Key key = null;

   public CryptoTools() throws Exception {
       DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
       iv = new IvParameterSpec(DESIV);// 设置向量
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
       key = keyFactory.generateSecret(keySpec);// 得到密钥对象

   }

   public String encode(String data) throws Exception {
       Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
       enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向�?
       byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
       BASE64Encoder base64Encoder = new BASE64Encoder();
       return base64Encoder.encode(pasByte);
   }

   public String decode(String data) throws Exception {
       Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
       deCipher.init(Cipher.DECRYPT_MODE, key, iv);
       BASE64Decoder base64Decoder = new BASE64Decoder();

       byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));

       return new String(pasByte, "UTF-8");
   }
   public static void main(String[] args) {
       try {
           String test = "aaaaaa";
           CryptoTools des = new CryptoTools();//自定义密�?
          // System.out.println("加密前的字符�?"+test);
           System.out.println("加密后的字符�?"+des.encode("123456"));
           System.out.println("解密后的字符�?"+des.decode("C552DB617970A4DC72689F5A70C8256B"));
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}