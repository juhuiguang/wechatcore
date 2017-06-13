package com.alienlab.wechat.onlive.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.db.ISyncDb;
import com.alienlab.response.JSONResponse;
import com.alienlab.wechat.media.MediaUtil;
import com.alienlab.wechat.onlive.service.MediaDownloader;
import com.alienlab.wechat.onlive.service.OnliveStart;
import com.alienlab.wechat.onlive.service.downloader.StreamFileCallback;
import com.alienlab.wechat.onlive.service.downloader.StreamThumbCallback;

/**
 * 直播间内容流
 * @author 橘
 *
 */
public class OnliveStream implements ISyncDb{
	/**
	 * 内容流序号
	 */
	private String no;
	
	private String room_no;
	public String getRoom_no() {
		return room_no;
	}

	public void setRoom_no(String room_no) {
		this.room_no = room_no;
	}
	/**
	 * 内容流类型
	 */
	private String contenttype;
	/**
	 * 内容流时间
	 */
	private String time;
	/**
	 * 内容流发送者身份码
	 */
	private String openid;
	/**
	 * 内容流发送者统一码
	 */
	private String unionid;
	/**
	 * 内容流发送者昵称
	 */
	private String nick;
	/**
	 * 内容流内容
	 */
	private String content;
	
	private String link;
	
	private String content_pic;
	public String getContent_pic() {
		return content_pic;
	}

	public void setContent_pic(String content_pic) {
		this.content_pic = content_pic;
	}
	
	private String content_piclink;
	
	private String usericon;

	public String getUsericon() {
		return usericon;
	}

	public void setUsericon(String usericon) {
		this.usericon = usericon;
	}

	public String getContent_piclink() {
		return content_piclink;
	}

	public void setContent_piclink(String content_piclink) {
		this.content_piclink = content_piclink;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * 多媒体文件
	 */
	private String media;
	
	public OnliveStream(JSONObject stream){
		this.setContent(stream.getString("content_body"));
		this.setOpenid(stream.getString("openid"));
		this.setContenttype(stream.getString("content_type"));
		this.setNick(stream.getString("member_nick"));
		this.setTime(stream.getString("content_time"));
		this.setUnionid(stream.getString("unionid"));
		this.setNo(stream.getString("content_no"));
		this.setRoom_no(stream.getString("bc_no"));
		this.setLink(stream.getString("content_link"));
		this.setContent_pic(stream.getString("content_pic"));
		this.setMedia(stream.getString("content_welink"));
		//本地链接未生成
		if((!this.getContenttype().equals("text"))&&(this.getLink()==null||this.getLink().equals(""))){
			//调用多媒体接口进行下载后，更新本地连接字段
			switch(this.getContenttype()){
				case "image":
				case "voice":{
					this.setMedia(stream.getString("content_welink"));
					break;
				}
				case "video":
				case "shortvideo":{
					this.setMedia(stream.getString("content_welink"));
					this.setContent_pic(stream.getString("content_pic"));
					break;
				}
			}
		}
		
	}
	
	public OnliveStream(){
		
	}
	
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	@Override
	public void initfdb() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean insert2db() {
		JSONResponse jr=new JSONResponse();
		// TODO Auto-generated method stub
		String sql="INSERT INTO `wx_onlive_content`( `bc_no`, `content_type`, `content_time`, `openid`, `unionid`, `member_nick`, `content_body`, `content_welink`, `content_pic`) "+
						"VALUES ('"+this.getRoom_no()+"','"+this.getContenttype()+"','"+this.getTime()+"','"+this.getOpenid()
						+"','"+this.getUnionid()+"','"+this.getNick()+"','"+this.getContent()+"','"+this.getMedia()+"','"+this.getContent_pic()+"') ";
		//写入数据库获得数据库编号
		ExecResult er=jr.getExecInsertId(sql, null, null, null);
		if(er.getResult()>0){
			this.setNo(er.getMessage());
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean update2db() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean deletefdb() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void downLoadMedia(){
		String path=OnliveStart.streamPath;
		//如果不存在媒体文件，直接返回
		if(this.getMedia()==null||this.getMedia().equals(""))return;
		String mediaurl=MediaUtil.getWechatLink(this.getMedia());
		String ext="";
		switch(this.getContenttype()){
			case "image":{
				ext="jpg";
				break;
			}
			case "voice":{
				ext="amr";
				break;
			}
			case "video":
			case "shortvideo":{
				ext="mp4";
				break;
			}
		}
		//下载文件自动回调
		MediaDownloader.addDownload(mediaurl, path, ext, this.getNo(),new StreamFileCallback());
		//如果缩略图不为空，下载缩略图
		if(this.getContent_pic()!=null&&(!this.getContent_pic().equals(""))){
			MediaDownloader.addDownload(MediaUtil.getWechatLink(this.getContent_pic()), path, "jpg", this.getNo(),new StreamThumbCallback());
		}
	}
	
	public static OnliveStream loadStream(String streamno){
		String sql="SELECT a.content_no,a.bc_no,a.content_type,a.content_time,a.`openid`,a.content_body,a.content_link,a.`content_piclink`,b.`member_nick`,b.`member_pic`,c.`bc_cover`,c.`bc_vip_cover` FROM `wx_onlive_content` a,`wx_onlive_members` b,`wx_onlive_broadcasting` c "+
				"WHERE a.`content_no`="+streamno+" AND a.`openid`=b.`member_openid` AND a.`bc_no`=c.`bc_no` AND a.`bc_no`=b.`bc_no` ";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getSelectResult(sql, null, "wx_onlive_content");
		if(er.getResult()>0){
			JSONArray array=(JSONArray)er.getData();
			if(array.size()>0){
				JSONObject item=array.getJSONObject(0);
				OnliveStream s=new OnliveStream(item);
				if(s.getLink()!=null&&!s.getLink().equals("")&&!s.getLink().equals("null")){
					s.setLink(OnliveStart.streamUrl+"/"+item.getString("content_link"));
					s.setContent_piclink(OnliveStart.streamUrl+"/"+item.getString("content_piclink"));
				}
				return s;
			}
		}
		return new OnliveStream();
	}
	
	
	
	
}
