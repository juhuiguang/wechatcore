package com.alienlab.wechat.onlive.service;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.bean.AccessToken;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.response.ResponseConfig;

/**
 * Servlet implementation class OnliveStart
 */
public class OnliveStart extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public static String coverPath="";
    public static String streamPath="";
    public static String onlivebasepath="";
    public static String ffmpegpath="";
    public static String streamUrl="";
    public static String coverUrl="";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnliveStart() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 * 直播系统启动
	 */
	public void init(ServletConfig config) throws ServletException {
		try{
			// TODO Auto-generated method stub
			//启动加载白名单
			NameList.getNameList();
			//启动加载已有直播间
			OnliveManager.getRooms();
			
			PropertyConfig pc=new PropertyConfig("sysConfig.properties");
			onlivebasepath=pc.getValue("base_path");
			ffmpegpath=config.getServletContext().getRealPath("/onlive");
			coverPath=config.getServletContext().getRealPath("/"+pc.getValue("cover_path"));
			streamPath=config.getServletContext().getRealPath("/"+pc.getValue("stream_path"));
			streamUrl=onlivebasepath+"/"+pc.getValue("stream_path");
			coverUrl=onlivebasepath+"/"+pc.getValue("cover_path");
			System.out.println("coverPath:"+coverPath);
			System.out.println("streamPath:"+streamPath);
			ResponseConfig allrc=new ResponseConfig();
			allrc.setConfiglevel(0);
			allrc.setUseropenid("ALL");
			allrc.setMsgType("text image video shortvideo voice");
			allrc.setResponse(new OnliveResponse());
			JSONObject p=allrc.getParam();
			p.put("key", "all");
			allrc.setParam(p);
			ResponseConfig.addConfig(allrc);
			
			ResponseConfig normalrc=new ResponseConfig();
			normalrc.setConfiglevel(2);
			normalrc.setUseropenid("ALL");
			normalrc.setMsgType("text");
			normalrc.setKeywords("我的直播间 查看直播 我要直播");
			normalrc.setResponse(new OnliveResponse());
			JSONObject param=normalrc.getParam();
			param.put("key", "onliveroom");
			normalrc.setParam(param);
			//系统启动创建默认回复类
			ResponseConfig.addConfig(normalrc);
			
			//qr scan response config
			ResponseConfig qrrc=new ResponseConfig();
			qrrc.setConfiglevel(3);
			qrrc.setUseropenid("ALL");
			qrrc.setMsgType("event");
			qrrc.setEventkey("subscribe SCAN");
			JSONObject param1=qrrc.getParam();
			param1.put("key", "qrscan");
			qrrc.setParam(param1);
			qrrc.setResponse(new OnliveResponse());
			ResponseConfig.addConfig(qrrc);
			
			ResponseConfig menuclick=new ResponseConfig();
			menuclick.setConfiglevel(9);
			menuclick.setUseropenid("ALL");
			menuclick.setMsgType("event");
			menuclick.setEventkey("CLICK");
			JSONObject mclickparam=new JSONObject ();
			mclickparam.put("key", "menu");
			menuclick.setParam(mclickparam);
			menuclick.setResponse(new OnliveMenuClick());
			ResponseConfig.addConfig(menuclick);
			//初始化媒体文件异步下载器
			MediaDownloader.initAnyscDownloader();
			
//			 //预先获取一次微信token
//		    String wxappid=pc.getValue("wxappid");
//			String wxappsecret=pc.getValue("wxappsecret");
//			AccessToken at=WeixinUtil.getAccessToken(wxappid, wxappsecret);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static boolean createMenu(){
		JSONArray buttons=new JSONArray();
		JSONObject button0=new JSONObject();
		button0.put("name", "快速创建");
		button0.put("type", "click");
		button0.put("key", "qc");
		
		JSONObject button1=new JSONObject();
		button1.put("name", "创建向导");
		button1.put("type", "view");
		String createurl=WeixinUtil.getAuthUrl("http://www.bigercat.com/wechatcore/onlive/mobile/CreateRoom.jsp", null, "info");
		button1.put("url",createurl);
		
		JSONObject buttona=new JSONObject();
		buttona.put("name","创建直播");
		JSONArray btnasub=new JSONArray();
		btnasub.add(button0);
		btnasub.add(button1);
		buttona.put("sub_button", btnasub);
		
		
		JSONObject button2=new JSONObject();
		button2.put("name", "已有直播");
		button2.put("type", "click");
		button2.put("key", "mine");
		
		JSONObject button3=new JSONObject();
		button3.put("name", "管理直播");
		button3.put("type", "view");
		String mineurl=WeixinUtil.getAuthUrl("http://www.bigercat.com/wechatcore/onlive/mobile/myroom.jsp", null, "info");
		button3.put("url", mineurl);
		
		JSONObject buttonb=new JSONObject();
		buttonb.put("name", "我的直播");
		JSONArray btnbsub=new JSONArray();
		btnbsub.add(button2);
		btnbsub.add(button3);
		buttonb.put("sub_button", btnbsub);
		
		JSONObject buttonc=new JSONObject();
		buttonc.put("name","AlienZoo");
//		buttonc.put("type","view");
//		buttonc.put("url","http://mp.weixin.qq.com/mp/homepage?__biz=MzAwOTUzOTc0MQ==&hid=1&sn=5f95766f02d15e703d72b7574ea46065#wechat_redirect");

		JSONArray btncsub=new JSONArray();
		JSONObject button4=new JSONObject();
		button4.put("name", "图片生成器");
		button4.put("type", "view");
		button4.put("url", "http://www.bigercat.com/wechatcore/picbox/index2.jsp");
		btncsub.add(button4);
		JSONObject button5=new JSONObject();
		button5.put("name", "历史文章");
		button5.put("type", "view");
		button5.put("url", "http://mp.weixin.qq.com/mp/homepage?__biz=MzAwOTUzOTc0MQ==&hid=1&sn=5f95766f02d15e703d72b7574ea46065#wechat_redirect");
		btncsub.add(button5);
		buttonc.put("sub_button", btncsub);

		buttons.add(buttona);
		buttons.add(buttonb);
		//buttons.add(buttonc);
		
		JSONObject menu=new JSONObject();
		menu.put("button", buttons);
		PropertyConfig pc=new PropertyConfig("sysConfig.properties");
		// 第三方用户唯一凭证
		String appId = pc.getValue("wxappid");
		// 第三方用户唯一凭证密钥
		String appSecret = pc.getValue("wxappsecret");
		// 调用接口获取access_token
		AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);
		if (null != at) {
			// 调用接口创建菜单
			int result = WeixinUtil.createMenu(menu, at.getToken());
			// 判断菜单创建结果
			if (0 == result){
				System.out.println("菜单创建成功！");
				return true;
			}	
			else{
				System.out.println("菜单创建失败，错误码：" + result);
				return false;
			}
				
		}else{
			System.out.println("获取token失败");
			return false;
		}
	}
	
	public static void main(String [] args){
		//OnliveStart.createMenu();
	}

}
