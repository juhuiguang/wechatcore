package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.log.ILogger;

/**
 * 消息基类
 * @author 橘
 *
 */
public class BaseMessage  implements ILogger{
	public BaseMessage(JSONObject message) {
		this.setToUserName(message.getString("ToUserName"));
		this.setFromUserName(message.getString("FromUserName"));
		this.setCreateTime(message.getString("CreateTime"));
		this.setMsgType(message.getString("MsgType"));
	}
	
	private String toUserName;
	private String fromUserName;
	private String createTime;
	private String msgType;
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	@Override
	/**
	 * 记录当前实体日志
	 * @author Eric
	 */
	public void doLog() {
		// TODO Auto-generated method stub
	}

	
}
