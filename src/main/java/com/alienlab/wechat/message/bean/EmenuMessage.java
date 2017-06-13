package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 菜单点击事件类
 * @author 橘
 *
 */
public class EmenuMessage extends BaseMessage {

	public EmenuMessage(JSONObject message) {
		super(message);
		this.setEvent(message.getString("Event"));
		this.setEventKey(message.getString("EventKey"));
		// TODO Auto-generated constructor stub
	}
	
	//事件类型
	private String event;

	private String eventKey;
	
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
		@Override
		public void doLog(){
			String sql="INSERT INTO `wx_menu_event_log`(`Openid`,`Unionid`,`eventtime`,`menukey`,`eventtype`) "+
							" VALUES ('"+this.getFromUserName()+"','','"+this.getCreateTime()+"','"+this.getEventKey()+"','"+this.getEvent()+"')";
			JSONResponse jr=new JSONResponse();
			ExecResult er=jr.getExecResult(sql, null);
		}

}
