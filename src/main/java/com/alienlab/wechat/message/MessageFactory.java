package com.alienlab.wechat.message;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.core.servlet.CoreServlet;
import com.alienlab.wechat.message.bean.AudioMessage;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.message.bean.ElocationMessage;
import com.alienlab.wechat.message.bean.EmenuMessage;
import com.alienlab.wechat.message.bean.EqrMessage;
import com.alienlab.wechat.message.bean.EsubMessage;
import com.alienlab.wechat.message.bean.LinkMessage;
import com.alienlab.wechat.message.bean.LocationMessage;
import com.alienlab.wechat.message.bean.PicMessage;
import com.alienlab.wechat.message.bean.ShortVideoMessage;
import com.alienlab.wechat.message.bean.TextMessage;
import com.alienlab.wechat.message.bean.VideoMessage;

/**
 * 消息类型工厂
 * @author 橘
 *
 */
public class MessageFactory {
	 private static Logger logger = Logger.getLogger(MessageFactory.class);
	public static BaseMessage getMessage(JSONObject message){
		String msgType=message.getString("MsgType");
		if(msgType==null){
			logger.error("message has no msgType,can't be anlysed.");
		}
		switch(msgType){
			case	"text":{return new TextMessage(message);}
			case	"image":{return new PicMessage(message);}
			case	"voice":{return new AudioMessage(message);}
			case	"video":{return new VideoMessage(message);}
			case	"shortvideo":{return new ShortVideoMessage(message);}
			case	"location":{return new LocationMessage(message);}
			case	"link":{return new LinkMessage(message);}
			case "event":{
				String eventType=message.getString("Event");
				switch(eventType){
					case	"subscribe":
					case "unsubscribe":{
						if(message.containsKey("EventKey")){
							return new EqrMessage(message);
						}else{
							return new EsubMessage(message);
						}
					}
					case "SCAN":{return new EqrMessage(message);}
					case "LOCATION":{return new ElocationMessage(message);}
					case "VIEW":
					case "CLICK":{return new EmenuMessage(message);}
				}
			}
			default:return null;
		}

	}
}
