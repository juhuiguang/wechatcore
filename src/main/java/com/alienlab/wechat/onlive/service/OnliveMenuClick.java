package com.alienlab.wechat.onlive.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.message.bean.EmenuMessage;
import com.alienlab.wechat.onlive.bean.OnliveRoom;
import com.alienlab.wechat.response.BaseResponse;
import com.alienlab.wechat.response.IResponse;
import com.alienlab.wechat.response.bean.ArticleObject;

public class OnliveMenuClick implements IResponse {
	private static Logger logger = Logger.getLogger(OnliveMenuClick.class);
	@Override
	public void preResponse(BaseMessage msg, JSONObject param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String doResponse(BaseMessage msg, JSONObject param) {
		logger.info("begin OnliveResponse>>>>>>>>>>>" + param.toJSONString());
		String res = "";
		if (param.containsKey("key")) {
			if (param.getString("key").equals("menu")) {
				EmenuMessage em=(EmenuMessage)msg;
				String key=em.getEventKey();
				if(key.equals("qc")){
					OnliveResponse or=new OnliveResponse();
					res=or.quickOnlive(msg);
				}else if(key.equals("mine")){
					List<OnliveRoom> rooms=OnliveManager.getRoomsByOpenid(msg.getFromUserName());
					List<ArticleObject> articles=new ArrayList<ArticleObject>();
					int count=rooms.size();
					if(count>9)count=9;
					for(int i=0;i<count;i++){
						OnliveRoom room=rooms.get(i);
						ArticleObject onliveroom=room.getArticle();
						articles.add(onliveroom);
					}
					res=BaseResponse.getNewsResponse(articles, msg);
				}
			}
		}
		return res;
	}

	@Override
	public void afterResponse(BaseMessage msg, JSONObject param) {
		// TODO Auto-generated method stub

	}

}
