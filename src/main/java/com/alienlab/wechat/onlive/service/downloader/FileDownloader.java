package com.alienlab.wechat.onlive.service.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

public class FileDownloader {
	private static Logger logger = Logger.getLogger(FileDownloader.class);
	public ExecResult downloadFile(String url,String target,String ext,String streamno){
		ExecResult er=new ExecResult();
		JSONObject result=new JSONObject();
		result.put("streamno", streamno);
		 try {
	        	Date now=new Date();
	        	String imageName=String.valueOf(now.getTime())+"."+ext;
	        	System.out.println("download image:"+imageName);
	            URL uri = new URL(url);  
	            InputStream in = uri.openStream();  
	            System.out.println("download begin:"+uri.getPath());
	            FileOutputStream fo = new FileOutputStream(new File(target+File.separator+imageName));  
	            System.out.println("download target:"+target+File.separator+imageName);
	            byte[] buf = new byte[1024];  
	            int length = 0;  
	            logger.info("开始下载:" + url);  
	            while ((length = in.read(buf, 0, buf.length)) != -1) {  
	                fo.write(buf, 0, length);  
	            }  
	            in.close();  
	            fo.close();  
	            logger.info(imageName + "下载完成");  
	            //amr文件进行转换成MP3
	            if(ext.equals("amr")){
	            	if(AudioCorrect.Amr2Mp3(imageName)){
	            		imageName+=".mp3";
	            	}
	            }
	            result.put("filename", imageName);
	            er.setData(result);
	            er.setResult(true);
	            return er;
	        } catch (Exception e) {  
	            logger.error("下载失败："+e.getMessage());  
	            result.put("filename","");
	            er.setData(result);
	            er.setResult(false);
	            return er;
	        }  
	}
	
}
