package com.alienlab.wechat.message.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

/**
 * 语音消息类
 * 	@author 橘
 *
 */
public class AudioMessage extends BaseMessage {

	public AudioMessage(JSONObject message) {
		super(message);
		this.setMediaId(message.getString("MediaId"));
		this.setFormat(message.getString("Format"));
		this.setMsgId(message.getString("MsgID"));
		// TODO Auto-generated constructor stub
	}
	//语音消息媒体id
	private String mediaId;
	//语音格式
	private String format;
	//消息id，64
	private String msgId;
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
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
						" VALUES ('"+this.getFromUserName()+"','','"+this.getMsgType()+"','"+this.getCreateTime()+"','"+this.getMediaId()+"','')";
		String logsql="INSERT INTO `wx_media_log`(`media_log_id`,`Openid`,`Unionid`,`Media_type`,`Media_direction`,`Media_remote_url`,`Media_size`,`Media_name`,`Media_local_url`,`Media_id`) "+
				" VALUES ('"+this.getMsgId()+"','"+this.getFromUserName()+"','','"+this.getMsgType()+"','','','0','','','"+this.getMediaId()+"')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql, null);
		ExecResult er1=jr.getExecResult(logsql, null);
	}
	

}
