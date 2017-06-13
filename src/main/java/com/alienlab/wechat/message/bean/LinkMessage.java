package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 链接消息类
 * @author 橘
 *
 */
public class LinkMessage extends BaseMessage {

	public LinkMessage(JSONObject message) {
		super(message);
		this.setTitle(message.getString("Title"));
		this.setDescription(message.getString("Description"));
		this.setUrl(message.getString("Url"));
		this.setMsgid(message.getString("MsgId"));
		// TODO Auto-generated constructor stub
	}
	//消息标题
	private String title;
	//消息描述
	private String description;
	//消息连接
	private String url;
	//消息id，64
	private String msgid;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	@Override
	public void doLog(){
		String sql="INSERT INTO `wx_user_message`(`Openid`,`Unionid`,`Messagetype`,`Messagetime`,`Messagecontent`,`Messagemedia`) "+
						" VALUES ('"+this.getFromUserName()+"','','"+this.getMsgType()+"','"+this.getCreateTime()+"','"+this.getUrl()+"','')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
	
	}
}
