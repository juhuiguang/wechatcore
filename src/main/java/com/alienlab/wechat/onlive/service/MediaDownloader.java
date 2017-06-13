package com.alienlab.wechat.onlive.service;

import com.alienlab.wechat.onlive.service.downloader.FileDownloader;
import com.alienlab.wechat.onlive.service.downloader.StreamFileCallback;
import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.DefaultErrorAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.FileAsynServiceHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

public class MediaDownloader {
	private static AsynService asynService = null;

	public static void initAnyscDownloader() {
		asynService = AsynServiceImpl.getService(300, 3000L, 100, 100, 1000);
		// 异步工作缓冲处理器
		asynService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
		// 服务启动和关闭处理器
		asynService.setServiceHandler(new FileAsynServiceHandler());
		// 异步工作执行异常处理器
		asynService.setErrorAsynWorkHandler(new DefaultErrorAsynWorkHandler());
		// 启动服务
		asynService.init();
	}

	public static boolean addDownload(String url,String target,String ext,String streamno,AsynCallBack callback) {
		 asynService.addWork(FileDownloader.class ,  "downloadFile" ,  new  Object[] { url,target,ext,streamno }, callback);
		return true;
	}

	public static void main(String[] args) {
		// 异步回调对象
		// AsynCallBack back = new TargetBack();
		for (int i = 0; i < 1000; i++) {
			// 添加加异步工作- TargetService 的 test 方法 ，方法参数 asynej+ i
			// asynService.addWork(new Object[] { "asyn4j" + i },
			// TargetService.class, "test", new TargetBack());
			// 实例化目标对象再调用
			// TargetService targetService = new TargetService ();
			// asynService.addWork(new Object[] { "asyn4j" + i },
			// targetService , "test", new TargetBack());

		}
	}
}
