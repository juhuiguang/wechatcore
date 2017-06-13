package com.alienlab.wechat.media.bean;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.wechat.media.MediaUtil;
import com.alienlab.wechat.message.bean.BaseMessage;

public class MediaObject{
	/**
	 * @param message
	 */
	public MediaObject(JSONObject message) {
		this.setMediaid(message.getString("mediaid"));
		// TODO Auto-generated constructor stub
	}
	
	public MediaObject(String mediaid,String type){
		this.setMediaid(mediaid);
		this.setWechatLink(MediaUtil.getWechatLink(mediaid));
		this.setType(type);
	}

	/**
	 * 多媒体文件序号
	 */
	private String no;
	/**
	 * 多媒体文件编码--微信
	 */
	private String mediaid;
	/**
	 * 多媒体文件编码--本地
	 */
	private String localId;
	/**
	 * 多媒体文件类型
	 */
	private String type;
	/**
	 * 多媒体文件格式
	 */
	private String format;
	/**
	 * 多媒体文件微信链接
	 */
	private String wechatLink;
	/**
	 * 多媒体文件本地链接
	 */
	private String localLink;
	//media大小
	private int media_size;
	//上传或下载
	private String media_direction;
	//media文件名
	private String media_name;
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getMediaid() {
		return mediaid;
	}
	public void setMediaid(String mediaid) {
		this.mediaid = mediaid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getWechatLink() {
		return wechatLink;
	}
	public void setWechatLink(String wechatLink) {
		this.wechatLink = wechatLink;
	}
	public String getLocalLink() {
		return localLink;
	}
	public void setLocalLink(String localLink) {
		this.localLink = localLink;
	}
	public String getLocalId() {
		return localId;
	}
	public void setLocalId(String localId) {
		this.localId = localId;
	}
	
	public int getMedia_size() {
		return media_size;
	}
	public void setMedia_size(int media_size) {
		this.media_size = media_size;
	}
	public String getMedia_direction() {
		return media_direction;
	}
	public void setMedia_direction(String media_direction) {
		this.media_direction = media_direction;
	}
	public String getMedia_name() {
		return media_name;
	}
	public void setMedia_name(String media_name) {
		this.media_name = media_name;
	}
//	@Override
//	public void doLog(){
//		String sql="UPDATE `alienlab_wechat`.`wx_user_message` SET Media_remote_url="+this.getWechatLink()+","
//				+ "Media_size="+this.getMedia_size()+",Media_name="+this.getMedia_name()+" WHERE Openid="+this.getFromUserName()+" "
//								+ "AND Media_id="+this.getMediaid()+"";
//		
//		JSONResponse jr=new JSONResponse();
//		ExecResult er=jr.getExecResult(sql, null);
//
//	}
}

