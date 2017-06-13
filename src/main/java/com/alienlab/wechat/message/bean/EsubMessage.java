package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 关注与取消关注事件类
 * @author 橘
 *
 */
public class EsubMessage extends BaseMessage {

	public EsubMessage(JSONObject message) {
		super(message);
		this.setEvent(message.getString("Event"));
		// TODO Auto-generated constructor stub
	}
	//事件类型
	private String event;
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	@Override
	public void doLog(){
		String sql="INSERT INTO `wx_sub_event_log`(`Openid`,`Unionid`,`eventtype`,`eventtime`,`Qrkey`) "+
						" VALUES ('"+this.getFromUserName()+"','','"+this.getMsgType()+"','"+this.getCreateTime()+"','')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
	}

}
