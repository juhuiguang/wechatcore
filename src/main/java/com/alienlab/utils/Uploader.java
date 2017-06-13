package com.alienlab.utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
/**
 * UEditor文件上传辅助类
 *
 */
public class Uploader {
	private static Logger logger = Logger.getLogger(Uploader.class);
	// 输出文件地址
	private String url = "";
	// 文件虚拟路径(自定义配置)
	private String privateUrl="";
	// 上传文件名
	private String fileName = "";
	// 状态
	private String state = "";
	// 文件类型
	private String type = "";
	// 原始文件名
	private String originalName = "";
	// 文件大小
	private long size = 0;

	private HttpServletRequest request = null;
	private String title = "";
	// 保存路径
	private String savePath=new PropertyConfig("sysConfig.properties").getValue("uploadPath");
	// 文件允许格式
	private String[] allowFiles = { ".avi", ".doc", ".docx", ".zip", ".pdf",".txt", ".swf", ".wmv", ".gif", ".png", ".jpg", ".jpeg", ".bmp" };
	// 文件大小限制，单位KB
	private int maxSize = 10000;
	
	private HashMap<String, String> errorInfo = new HashMap<String, String>();

	public Uploader(HttpServletRequest request) {
		this.request = request;
		HashMap<String, String> tmp = this.errorInfo;
		tmp.put("SUCCESS", "SUCCESS"); //默认成功
		tmp.put("NOFILE", "未包含文件上传域");
		tmp.put("TYPE", "不允许的文件格式");
		tmp.put("SIZE", "文件大小超出限制");
		tmp.put("ENTYPE", "请求类型ENTYPE错误");
		tmp.put("REQUEST", "上传请求异常");
		tmp.put("IO", "IO异常");
		tmp.put("DIR", "目录创建失败");
		tmp.put("UNKNOWN", "未知错误");
		
	}

	public void upload() throws Exception {
		boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
		if (!isMultipart) {
			this.state = this.errorInfo.get("NOFILE");
			return;
		}
		DiskFileItemFactory dff = new DiskFileItemFactory();
		String savePath = this.getFolder(this.savePath);
		dff.setRepository(new File(savePath));
		try {
			ServletFileUpload sfu = new ServletFileUpload(dff);
			sfu.setSizeMax(this.maxSize * 1024);
			sfu.setHeaderEncoding("utf-8");
			FileItemIterator fii = sfu.getItemIterator(this.request);
			while (fii.hasNext()) {
				FileItemStream fis = fii.next();
				if (!fis.isFormField()) {
					this.originalName = fis.getName().substring(fis.getName().lastIndexOf(System.getProperty("file.separator")) + 1);
					if (!this.checkFileType(this.originalName)) {
						this.state = this.errorInfo.get("TYPE");
						continue;
					}
					this.fileName = this.getName(this.originalName);
					this.type = this.getFileExt(this.fileName);
					this.url = savePath + "/" + this.fileName;
					BufferedInputStream in = new BufferedInputStream(fis.openStream());
					//File file = new File(this.getPhysicalPath(this.url));
					this.setPrivateUrl(this.url.replace(this.savePath, ""));//修改保存路径
					File file = new File(url);//创建图片
					FileOutputStream out = new FileOutputStream( file );
					BufferedOutputStream output = new BufferedOutputStream(out);
					Streams.copy(in, output, true);
					this.state=this.errorInfo.get("SUCCESS");
					this.size = file.length();
					
					//如果上传成功则处理图片等比压缩，便于使用
					//暂时不做图片压缩
					/*if("SUCCESS".equals(this.state)){
						CompressPic mypic = new CompressPic();
						String smailPath=savePath+"/";
						String smailPathOut=this.getFolder(this.savePath+"/smail")+"/";
						mypic.compressPic(smailPath, smailPathOut, this.fileName,this.fileName, 120, 120, true);
					}*/
					//UE中只会处理单张上传，完成后即退出
					break;
				} else {
					String fname = fis.getFieldName();
					//只处理title，其余表单请自行处理
					if(!fname.equals("pictitle")){
						continue;
					}
                    BufferedInputStream in = new BufferedInputStream(fis.openStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer result = new StringBuffer();  
                    while (reader.ready()) {  
                        result.append((char)reader.read());  
                    }
                    this.title = new String(result.toString().getBytes(),"utf-8");
                    reader.close();  
                    
				}
			}
		} catch (SizeLimitExceededException e) {
			this.state = this.errorInfo.get("SIZE");
		} catch (InvalidContentTypeException e) {
			this.state = this.errorInfo.get("ENTYPE");
		} catch (FileUploadException e) {
			this.state = this.errorInfo.get("REQUEST");
		} catch (Exception e) {
			this.state = this.errorInfo.get("UNKNOWN");
		}
	}
	
	/**
	 * 接受并保存以base64格式上传的文件
	 * @param fieldName
	 */
	public void uploadBase64(String fieldName){
		String savePath = this.getFolder(this.savePath);
		String base64Data = this.request.getParameter(fieldName);
		this.fileName = this.getName("test.png");
		this.url = savePath + "/" + this.fileName;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			File outFile = new File(this.getPhysicalPath(this.url));
			OutputStream ro = new FileOutputStream(outFile);
			byte[] b = decoder.decodeBuffer(base64Data);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			ro.write(b);
			ro.flush();
			ro.close();
			this.state=this.errorInfo.get("SUCCESS");
		} catch (Exception e) {
			this.state = this.errorInfo.get("IO");
		}
	}

	/**
	 * 文件类型判断
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileType(String fileName) {
		Iterator<String> type = Arrays.asList(this.allowFiles).iterator();
		while (type.hasNext()) {
			String ext = type.next();
			if (fileName.toLowerCase().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return string
	 */
	private String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 依据原始文件名生成新文件名
	 * @return
	 */
	private String getName(String fileName) {
		Random random = new Random();
		return this.fileName = "" + random.nextInt(10000)
				+ System.currentTimeMillis() + this.getFileExt(fileName);
	}

	/**
	 * 根据字符串创建本地目录 并按照日期建立子目录返回
	 * @param path 
	 * @return 
	 */
	private String getFolder(String path) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		path += "/" + formater.format(new Date());
		File dir = new File(path);///此处创建文件夹
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				this.state = this.errorInfo.get("DIR");
				return "";
			}
		}
		return path;
	}

	/**
	 * 根据传入的虚拟路径获取物理路径
	 * 
	 * @param path
	 * @return
	 */
	private String getPhysicalPath(String path) {
		String servletPath = this.request.getServletPath();
		String realPath = this.request.getSession().getServletContext()
				.getRealPath(servletPath);
		return new File(realPath).getParent() +"/" +path;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public void setAllowFiles(String[] allowFiles) {
		this.allowFiles = allowFiles;
	}

	public void setMaxSize(int size) {
		this.maxSize = size;
	}

	public long getSize() {
		return this.size;
	}

	public String getUrl() {
		return this.url;
	}
	


	public String getFileName() {
		return this.fileName;
	}

	public String getState() {
		return this.state;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	public String getOriginalName() {
		return this.originalName;
	}

	public String getPrivateUrl() {
		return privateUrl;
	}

	public void setPrivateUrl(String privateUrl) {
		this.privateUrl = privateUrl;
	}
	/**
	 * 获取上传视频长度
	 * @param viedoPath
	 * @return
	 */
/*	public static String getViedoSeconds(String viedoPath){
		String seconds="0";
	    File source = new File(viedoPath);
	    //如果视频文件存在
	    if(source.exists()){
	    	 Encoder encoder = new Encoder();
	         try {
	              MultimediaInfo m = encoder.getInfo(source);
	              long ls = m.getDuration();
	              seconds=(ls/1000)+"";
	              logger.info("视频路径："+viedoPath+"》》此视频时长为:"+ls/1000+"秒");
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	    }else{
	    	logger.info("视频路径："+viedoPath+"》》不存在");
	    }
		return seconds;
	} */
	
	
    public static void main(String args[]){
    	//System.out.println(Uploader.getViedoSeconds("E:\\教学视频\\01.avi"));
    }
}
