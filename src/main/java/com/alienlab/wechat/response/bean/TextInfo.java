package com.alienlab.wechat.response.bean;

import com.alibaba.fastjson.JSONObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.NONE)
public class TextInfo extends BaseInfo{
	// 接收方帐号（收到的OpenID）
	private String ToUserName;
	// 开发者微信号
	private String FromUserName;
	// 消息创建时间 （整型）
	private long CreateTime;
	// 消息类型（text/music/news）
	private String MsgType;
	// 位0x0001被标志时，星标刚收到的消息
	private int FuncFlag;

	private String Content;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		super.setToUserName(toUserName);
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		super.setFromUserName(fromUserName);
		FromUserName = fromUserName;
	}

	public long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(long createTime) {
		super.setCreateTime(createTime);
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		super.setMsgType(msgType);
		MsgType = msgType;
	}

	public int getFuncFlag() {
		return FuncFlag;
	}

	public void setFuncFlag(int funcFlag) {
		super.setFuncFlag(funcFlag);
		FuncFlag = funcFlag;
	}

	public TextInfo(){
		this.setMsgType("text");
	}
	public TextInfo(String text){
		this.setMsgType("text");
		this.setContent(text);
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		this.Content = content;
	}



	public String getPushInfo(){
		JSONObject textinfo=new JSONObject();
		textinfo.put("touser", this.getToUserName());
		textinfo.put("msgtype","text");
		JSONObject content=new JSONObject();
		content.put("content",this.getContent());
		textinfo.put("text",content);
		return textinfo.toJSONString();
		
	}
	
	
}
