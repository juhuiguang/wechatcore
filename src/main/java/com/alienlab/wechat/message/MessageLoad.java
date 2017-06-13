package com.alienlab.wechat.message;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.bean.AccessToken;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.media.bean.MediaObject;
/**
 * 获取媒体文件
 	* @author  Eric
 	* @Date:2016年3月24日下午1:40:05
 	* @version 1.0
     * */  

public class MessageLoad {
	private static PropertyConfig pc = new PropertyConfig("sysConfig.properties");
	private static Logger logger = Logger.getLogger(MessageLoad.class);
	/**
	 * 文件后缀扩展名 
	 * @param contentType
	 * @return
	 */
	public static String getFilePostfix(String contentType) {
		String filePostfix = "";
		if ("image/jpeg".equals(contentType))
			filePostfix = ".jpg";
		else if ("audio/amr".equals(contentType))
			filePostfix = ".amr";
		else if ("audio/mpeg".equals(contentType))
			filePostfix = ".amr";
		else if ("video/mp4".equals(contentType))
			filePostfix = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			filePostfix = ".mp4";
		return filePostfix;
	}
/**
 	* 下载至本地
 	* @param accessToken 接口访问凭证 
 	* @param media_id 媒体文件id 
 	* @param savePath 文件在服务器上的存储路径 
 	* @return  savePath
 */
	public static String downloadMedia(String accessToken,String mediaId,String savePath){
		String filePath=null;
		//请求地址
		String requestUrl="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
		//mediaid链接
		requestUrl=requestUrl.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
		System.out.println(requestUrl);
		try{
			
			URL url=new URL(requestUrl);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			if(!savePath.endsWith("/")){
				savePath+="/";
				
			}
			//根据content-type获取文件扩展名
			String fileExt=getFilePostfix(conn.getHeaderField("Content-Type"));
			//media作为文件名
			filePath =savePath+mediaId+fileExt;
			
			BufferedInputStream bis=new BufferedInputStream(conn.getInputStream());
			FileOutputStream fos=new FileOutputStream(new File(filePath));
			byte[] buf=new byte[1024*8];
			int size=0;
			while ((size=bis.read(buf))!=-1){
				fos.write(buf, 0, size);
			}
			fos.close();
			bis.close();
			conn.disconnect();
			logger.info("下载媒体文件成功,filePath="+filePath);
		}catch(Exception e){
			filePath=null;
			logger.error("下载媒体文件失败：%s",e);
		}
		return savePath;
	}
	
	 //测试
	public static void main(String[] args) {
		MediaObject mediaobject=null;
		String wxappid = pc.getValue("wxappid");
		String wxappsecret = pc.getValue("wxappsecret");
		String access_token = WeixinUtil.getAccessToken(wxappid, wxappsecret).getToken();
		String savePath = downloadMedia(access_token,mediaobject.getMediaid(), "C:/wxdownload");
	}

}