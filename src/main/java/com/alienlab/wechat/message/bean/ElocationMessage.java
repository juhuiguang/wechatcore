package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 上报地理位置事件类
 * @author 橘
 *
 */
public class ElocationMessage extends BaseMessage {

	public ElocationMessage(JSONObject message) {
		super(message);
		this.setEvent(message.getString("Event"));
		this.setLatitude(message.getString("Latitude"));
		this.setLongitude(message.getString("Longitude"));
		this.setPrecision(message.getString("Precision"));
		// TODO Auto-generated constructor stub
	}
	
	//事件类型
	private String event;
	//地理位置纬度
	private String latitude;
	//地理位置经度
	private String longitude;
	//地理位置精度
	private String precision;
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	@Override
	public void doLog(){
		String sql="INSERT INTO `wx_pos_event_log`(`Openid`,`Unionid`,`eventtime`,`Latitude`,`Longitude`,`Precision`,) "+
						" VALUES ('"+this.getFromUserName()+"','','"+this.getCreateTime()+"','"+this.getLatitude()+"','"+this.getLongitude()+"','"+this.getPrecision()+"')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
	}

}
