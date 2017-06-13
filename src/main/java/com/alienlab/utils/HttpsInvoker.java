package com.alienlab.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * https请求类，使用自定义信任管理器MyX509TrustManager信任�?有证�?
 * @author JuHuiguang
 *
 */
public class HttpsInvoker {
	private static  Logger logger = Logger.getLogger(HttpsInvoker.class);
	/**
	 * 发起https请求并获取结�?
	 * 
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST�?
	 * @param outputStr 提交的数�?
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性�??)
	 */
	
	public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			System.setProperty("https.protocols", "TLSv1");
			// 创建SSLContext对象，并使用我们指定的信任管理器初始�?
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			System.out.println(sslContext.getProtocol());
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			logger.info("发起https请求，请求地�?�?"+requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST�?
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod)){
				httpUrlConn.connect();
				logger.info("请求方式为GET");
			}

			// 当有数据�?要提交时
			if (null != outputStr) {
				logger.info("请求post数据�?"+outputStr);
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱�?
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符�?
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			String rst=buffer.toString();
			jsonObject = JSONObject.parseObject(rst);
			logger.info("请求返回内容�?"+rst);
		} catch (ConnectException ce) {
			logger.error("请求地址�?"+requestUrl+">>连接失败�?");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求地址�?"+requestUrl+">>连接失败，详细信息："+e.getMessage());
		}
		return jsonObject;
	}
}
