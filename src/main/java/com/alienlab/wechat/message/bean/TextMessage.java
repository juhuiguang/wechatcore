package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 文本消息类
 * @author 橘
 *
 */
public class TextMessage extends BaseMessage {

	public TextMessage(JSONObject message) {
		super(message);
		this.setMsgId(message.getString("MsgId"));
		this.setContent(message.getString("Content"));
		// TODO Auto-generated constructor stub
	}
	//文本消息id，64	
	private String msgId;
	//文本消息内容
	private String content;
	
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public void doLog(){
		String sql="INSERT INTO `wx_user_message`(`Openid`,`Unionid`,`Messagetype`,`Messagetime`,`Messagecontent`,`Messagemedia`) "+
						" VALUES ('"+this.getFromUserName()+"','','"+this.getMsgType()+"','"+this.getCreateTime()+"','"+this.getContent()+"','')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
	}

}
