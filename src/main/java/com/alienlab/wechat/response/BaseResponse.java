package com.alienlab.wechat.response;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.message.MessageProcessor;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.response.bean.ArticleInfo;
import com.alienlab.wechat.response.bean.ArticleObject;
import com.alienlab.wechat.response.bean.TextInfo;

/**
 * 默认回复类
 * @author 橘
 *
 */
public class BaseResponse implements  IResponse {

	@Override
	public void preResponse(BaseMessage msg,JSONObject param) {
		// TODO Auto-generated method stub
		/**
		 * 调用消息自身记录日志功能。
		 */
		msg.doLog();
	}

	@Override
	public String doResponse(BaseMessage msg,JSONObject param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void afterResponse(BaseMessage msg,JSONObject param) {
		// TODO Auto-generated method stub
		
	}
	
	public static String getTextResponse(String text, BaseMessage msg) {
		TextInfo t = new TextInfo();
		t.setContent(text);
		t.setCreateTime(new Date().getTime());
		t.setFromUserName(msg.getToUserName());
		t.setToUserName(msg.getFromUserName());
		return MessageProcessor.textMessageToXml(t);
	}
	
	public static String getNewsResponse(List<ArticleObject> articles, BaseMessage msg) {
		ArticleInfo a = new ArticleInfo();
		a.setArticleCount(articles.size());
		a.setArticles(articles);
		a.setCreateTime(new Date().getTime());
		a.setFromUserName(msg.getToUserName());
		a.setToUserName(msg.getFromUserName());
		a.setMsgType("news");
		return MessageProcessor.newsMessageToXml(a);
	}

	
}
