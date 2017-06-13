package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 位置消息类
 * @author 橘
 *
 */
public class LocationMessage extends BaseMessage {

	public LocationMessage(JSONObject message) {
		super(message);
		this.setLocation_x(message.getString("Location_X"));
		this.setLocation_y(message.getString("Location_Y"));
		this.setScale(message.getString("Scale"));
		this.setLabel(message.getString("Label"));
		this.setMsgId(message.getString("MsgId"));
		// TODO Auto-generated constructor stub
	}
	//坐标x
	private String location_x;
	//坐标y
	private String location_y;
	//地图缩放大小
	private String scale;
	//地理位置信息
	private String label;
	//消息id
	private String msgId;
	public String getLocation_x() {
		return location_x;
	}
	public void setLocation_x(String location_x) {
		this.location_x = location_x;
	}
	public String getLocation_y() {
		return location_y;
	}
	public void setLocation_y(String location_y) {
		this.location_y = location_y;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	@Override
	public void doLog(){
		String sql="INSERT INTO `wx_user_message`(`Openid`,`Unionid`,`Messagetype`,`Messagetime`,`Messagecontent`,`Messagemedia`) "+
						" VALUES ('"+this.getFromUserName()+"','','"+this.getMsgType()+"','"+this.getCreateTime()+"',CONCAT('"+this.getLocation_x()+"',',','"+this.getLocation_y()+"','')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
	}
	
	

}
