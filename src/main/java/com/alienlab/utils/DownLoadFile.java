package com.alienlab.utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;
/**
 * 下载文件
 * @author Wenyin
 *
 */
public class DownLoadFile {
	private static  Logger logger = Logger.getLogger(DownLoadFile.class);
    /** 
     * 从网络Url中下载文�? 
     * @param urlStr   访问URL
     * @param fileName 文件名称
     * @param savePath 保存文件路径
     * @throws IOException 
     */  
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                //设置超时间为3�?  
        conn.setConnectTimeout(10*1000);  
        //防止屏蔽程序抓取而返�?403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //得到输入�?  
        InputStream inputStream = conn.getInputStream();    
        //获取自己数组  
        byte[] getData = readInputStream(inputStream);      
  
        //文件保存位置  
        File saveDir = new File(savePath);  
        if(!saveDir.exists()){  
            saveDir.mkdir();  
        }  
        File file = new File(saveDir+File.separator+fileName);      
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null){  
            fos.close();    
        }  
        if(inputStream!=null){  
            inputStream.close();  
        }  
        logger.info("info:"+url+" download success");   
    }  
  
  
  
    /** 
     * 从输入流中获取字节数�? 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }    
  
    public static void main(String[] args) {  
        try{  
            downLoadFromUrl("http://localhost:8080/doc/image//20150310/88241425997803773.mp4","百度.mp4","D:/");  
        }catch (Exception e) {  
            // TODO: handle exception  
        }  
    }  
}
