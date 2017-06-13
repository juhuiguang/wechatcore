package com.alienlab.wechat.response;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.utils.HttpsInvoker;
import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.bean.AccessToken;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.response.bean.BaseInfo;

public class PushMessage {
	private static  Logger logger = Logger.getLogger(PushMessage.class);
	private static String sendUrl="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	private static PropertyConfig pc=new PropertyConfig("sysConfig.properties");
	
	public static boolean sendMessage(BaseInfo info){
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		AccessToken at=WeixinUtil.getAccessToken(wxappid, wxappsecret);
		String url=sendUrl.replace("ACCESS_TOKEN", at.getToken());
		logger.info("发送客服消息:消息内容:"+info.getPushInfo());
		JSONObject response = HttpsInvoker.httpRequest(url, "POST", info.getPushInfo());
		logger.info("得到微信服务端返回:"+response.toJSONString());
		if(response.containsKey("errorcode")){
			logger.error("推送消息失败："+response.toJSONString());
			return false;
		}else{
			return true;
		}
	}
}
