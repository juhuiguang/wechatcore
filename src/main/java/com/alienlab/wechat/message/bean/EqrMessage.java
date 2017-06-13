package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 二维码扫描事件类
 * 
 * @author 橘
 *
 */
public class EqrMessage extends BaseMessage {

	public EqrMessage(JSONObject message) {
		super(message);
		this.setEvent(message.getString("Event"));
		this.setEventKey(message.getString("EventKey"));
		this.setTicket(message.getString("Ticket"));
		// TODO Auto-generated constructor stub
	}

	// 事件类型
	private String event;
	// 事件key值
	private String eventKey;
	// 二维码的ticket
	private String ticket;

	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	@Override
	public void doLog(){
		String sql="INSERT INTO `wx_qr_event_log`(`Openid`,`Unionid`,`eventtime`,`qrkey`) "+
						" VALUES ('"+this.getFromUserName()+"','','"+this.getCreateTime()+"','"+this.getTicket()+"')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
	}

}
