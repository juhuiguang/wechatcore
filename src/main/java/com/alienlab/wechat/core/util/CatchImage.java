package com.alienlab.wechat.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import com.alienlab.utils.PropertyConfig;

public class CatchImage {
	private static Logger logger = Logger.getLogger(CatchImage.class);
	private static PropertyConfig pc=new PropertyConfig("sysConfig.properties");
	public static String path=null;
	public static String onlivepath=null;
	public CatchImage(){
//		path=pc.getValue("weixinImagePath");
//		onlivepath=pc.getValue("onliveImage");
	}
//	 // 地址  
//    private static final String URL = "http://www.csdn.net";  
//    // 编码  
//    private static final String ECODING = "UTF-8";  
//    // 获取img标签正则  
//    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";  
//    // 获取src路径的正则  
//    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";  
  
      
    public static void main(String[] args) throws Exception {  
        CatchImage cm = new CatchImage();  
        cm.Download("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=Sg2kSKorifr4Ywh94yW2MJd3SpJ2tL-CfuOpDBTRCwavBStpgUw5ZJ0Ec3s9rcYXHGuxcjkxEfK4TOe23fGW1dR2XVN0ptq_l1PmqcH5w0oKUOhAIAXHB&media_id=JT7u2cR4dkNWsH4u9pXy4iljL_5NkwPV8u3MzaBYEwmuEh6_cxz2fBs3F4M8fVHR"
        		,"d://");  
    }  
    
  
    /*** 
     * 下载图片 
     *  
     * @param listImgSrc 
     */  
    public String  Download(String url,String target) {  
        try {
        	Date now=new Date();
        	String imageName=String.valueOf(now.getTime())+".jpg";
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
            return imageName;
        } catch (Exception e) {  
            logger.error("下载失败："+e.getMessage());  
            return "error";
        }  
    }  
}
