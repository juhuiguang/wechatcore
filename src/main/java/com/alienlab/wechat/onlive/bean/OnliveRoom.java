package com.alienlab.wechat.onlive.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.ExecResult;
import com.alienlab.db.ISyncDb;
import com.alienlab.response.JSONResponse;
import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.onlive.service.NameList;
import com.alienlab.wechat.onlive.service.OnliveStart;
import com.alienlab.wechat.response.PushMessage;
import com.alienlab.wechat.response.ResponseConfig;
import com.alienlab.wechat.response.bean.ArticleInfo;
import com.alienlab.wechat.response.bean.ArticleObject;
import com.alienlab.wechat.response.bean.TextInfo;

/**
 * 直播间
 * @author 橘
 *
 */
public class OnliveRoom implements ISyncDb{
	JSONResponse jr=new JSONResponse();
	PropertyConfig pc=new PropertyConfig("sysConfig.properties");
	/**
	 * 直播间编号
	 */
	private String no="";
	/*
	 * 直播间名称
	 */
	private String name;
	/**
	 * 直播间描述
	 */
	private String descript;
	/**
	 * 直播间主题
	 */
	private String project;
	/**
	 * 嘉宾介绍
	 */
	private String guest;
	/**
	 * 主讲人
	 */
	private SortedMap<String,OnliveMember> speakers =new TreeMap<String,OnliveMember>();
	/**
	 * 直播间成员
	 */
	private SortedMap<String,OnliveMember> members=new TreeMap<String,OnliveMember>();
	/**
	 * 直播间管理员
	 */
	private NamelistItem manager;
	/**
	 * 直播间创建时间
	 */
	private String createTime;
	/**
	 * 直播间状态
	 */
	private String status;
	
	//最近更新文字
	private String latestText;
	//最近更新图片
	private String latestPic;
	/**
	 * 直播间二维码
	 */
	private String qrcode;
	/**
	 * 直播间分享链接
	 */
	private String shareLink;
	/**
	 * 直播开始时间
	 */
	private String startTime;
	/**
	 * 直播结束时间
	 */
	private String endTime;
	/**
	 * 演讲模式
	 */
	private String speakMode;
	/**
	 * 直播间封面
	 */
	private String cover;
	/**
	 * 品牌封面，直播头像
	 */
	private String brandcover;
	
	private String joinmsg="1";
	private String commentmsg="1";
	
	public OnliveRoom(){
		
	}
	
	public OnliveRoom(String no){
		this.setNo(no);
		this.initfdb();
	}
	
	
	
	public String getJoinmsg() {
		if(joinmsg==null)joinmsg="";
		return joinmsg;
	}

	public void setJoinmsg(String joinmsg) {
		this.joinmsg = joinmsg;
	}

	public String getCommentmsg() {
		if(commentmsg==null)commentmsg="";
		return commentmsg;
	}

	public void setCommentmsg(String commentmsg) {
		this.commentmsg = commentmsg;
	}

	public void setNo(String no) {
		this.no = no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getGuest() {
		return guest;
	}
	public void setGuest(String guest) {
		this.guest = guest;
	}
	
	public String getLatestText() {
		if(this.latestText==null||this.latestText.equals(""))return "";
		return latestText;
	}

	public void setLatestText(String latestText) {
		this.latestText = latestText;
	}

	public String getLatestPic() {
		if(this.latestPic==null||this.latestPic.equals(""))return "";
		String base_path=pc.getValue("base_path");
		String stream_path=pc.getValue("stream_path");
		if(this.latestPic.indexOf("http")>=0){
			return latestPic;
		}else{
			return base_path+stream_path+latestPic;
		}
	}

	public void setLatestPic(String latestPic) {
		this.latestPic = latestPic;
	}
	
	public String getLayoutTime(){
		String s=this.getRawStartTime();
		String e=this.getRawEndTime();
		
		String date="";
		String time="";
		if(s.substring(0,8).equals(e.substring(0,8))){
			date=s.substring(0,8);
			date=date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
			time=s.substring(8,12)+e.substring(8,12);
			time=time.substring(0,2)+":"+time.substring(2,4)+"~"+time.substring(4,6)+":"+time.substring(6,8);
			
			return date+" "+time;
		}else{
			return this.getStartTime()+"~"+this.getEndTime();
		}
		
		
		
		
	}

	public NamelistItem getManager() {
		return manager;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartTime() {
		Date stime=null;
		if(this.startTime.length()>12){
			stime=TypeUtils.str2date(this.startTime, "yyyyMMddHHmmss");
		}else{
			stime=TypeUtils.str2date(this.startTime, "yyyyMMddHHmm");
		}
		return TypeUtils.getTime(stime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getRawStartTime(){
		return this.startTime;
	}
	public String getRawEndTime(){
		return this.endTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		Date stime=null;
		if(this.endTime.length()>12){
			stime=TypeUtils.str2date(this.endTime, "yyyyMMddHHmmss");
		}else{
			stime=TypeUtils.str2date(this.endTime, "yyyyMMddHHmm");
		}
		return TypeUtils.getTime(stime, "yyyy-MM-dd HH:mm:ss");
	}
	public void setEndTime(String endTime) {
		this.endTime=endTime;
	}
	public String getSpeakMode() {
		return speakMode;
	}
	public void setSpeakMode(String speakMode) {
		this.speakMode = speakMode;
	}
	public String getCover() {
		String base_path=pc.getValue("base_path");
		String cover_path=pc.getValue("cover_path");
		if(this.cover.indexOf("http")>=0){
			return cover;
		}else{
			return base_path+cover_path+cover;
		}
		
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getBrandcover() {
		String base_path=pc.getValue("base_path");
		String cover_path=pc.getValue("cover_path");
		
		if(this.brandcover.indexOf("http")>=0){
			return this.brandcover;
		}else{
			return base_path+cover_path+brandcover;
		}
		
	}
	public void setBrandcover(String brandcover) {
		this.brandcover = brandcover;
	}
	public String getNo() {
		return no;
	}
	
	public String getQrcode() {
		String qrticket=this.qrcode;
		try {
			String url="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+URLEncoder.encode(qrticket,"UTF-8");
			return url;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	public String getShareLink() {
		String url=pc.getValue("base_path");
		url+="onlive/mobile/onliveroom.jsp";
		String link=WeixinUtil.getAuthUrl(url, this.getNo(), "info");
		return link;
	}

	
	public void setManager(NamelistItem name){
		this.manager=name;
	}
	
	public void setManager(String manager_phone){
		NamelistItem name=NameList.getName(manager_phone);
		this.setManager(name);
	}
	
	public int getMemberCount(){
		return this.members.keySet().size();
	}
	
	public Map<String, OnliveMember> getMembers() {
		return members;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	/**
	 * 成员加入直播间
	 * @param member
	 * @return
	 */
	public boolean joinRoom(OnliveMember member){
		//如果在列表中不存在，表示新用户
		if(!this.members.containsKey(member.getOpenid())){
			if(member.insert2db()){
				if(this.getJoinmsg().equals("1")){
					String text=member.getNick()+" 进入您的直播间《"+this.getName()+"》。";
					TextInfo ti=new TextInfo(text);
					ti.setToUserName(this.getManager().getOpenid());
					PushMessage.sendMessage(ti);
				}
			}
		}else{
			//如果成员已存在，获得成员是否vip属性，更新member
			OnliveMember existsmem=this.members.get(member.getOpenid());
			member.setVip(existsmem.isVip());
			member.update2db();
		}
		this.members.put(member.getOpenid(),member);
		//if(member.isVip()){
		//	addSpeaker(member);
		//}
		return true;
	}
	
	public String getSpeakerOpenid(){
		List<OnliveMember> s=getSpeakers();
		String openids="";
		for(int i=0;i<s.size();i++){
			if(i==0){
				openids+=s.get(i).getOpenid();
			}else{
				openids+=","+s.get(i).getOpenid();
			}
		}
		return openids;
	}
	
	public List<OnliveMember> getSpeakers(){
		List<OnliveMember> s=new ArrayList<OnliveMember>();
		for(Entry<String,OnliveMember> e:speakers.entrySet()){
			s.add(e.getValue());
		}
		return s;
	}
	
	public OnliveMember getMember(String nickname){
		for(String openid : members.keySet()){
			OnliveMember member=members.get(openid);
			if(member.getNick().equals(nickname)){
				return member;
			}
		}
		return null;
	}
	
	public boolean addSpeaker(String nickname){
		OnliveMember member=getMember(nickname);
		member.setVip(true);
		return addSpeaker(member);
		 
	}
	
	public boolean addSpeaker(OnliveMember member){
		if(!member.isVip()){
			member.setVip(true);
			member.update2db();
			TextInfo t=new TextInfo();
			t.setToUserName(member.getOpenid());
		
			t.setContent(this.getManager().getNickname()+"邀请您做他的直播嘉宾，快进来看看吧：<a href=\""+this.getShareLink()+"\">"+this.getName()+"</a>");
			PushMessage.sendMessage(t);
		}
		speakers.put(member.getOpenid(),member);
		
		//此处需要增加响应监听
		ResponseConfig rc=ResponseConfig.getConfigById(this.getNo());
		String rcopenid=rc.getUseropenid();
		if("".equals(rcopenid)){
			rcopenid=member.getOpenid();
		}else{
			if(rcopenid.indexOf(member.getOpenid())>=0){
				//已存在该用户监听
			}else{
				rcopenid+=","+member.getOpenid();
			}
		}
		rc.setUseropenid(rcopenid);
		return true;
	}
	
	public boolean removeSpeaker(String openid){
		if(speakers.containsKey(openid)){
			OnliveMember member=this.getMembers().get(openid);
			if(member.isVip()){
				return removeSpeaker(member);
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	public boolean removeSpeaker(OnliveMember member){
		if(member.isVip()){
			member.setVip(false);
			member.update2db();
		}
		speakers.remove(member.getOpenid());
	
		//此处删除响应监听
		ResponseConfig rc=ResponseConfig.getConfigById(this.getNo());
		String rcopenid=rc.getUseropenid();
		if(rcopenid.indexOf(member.getOpenid())>=0){
			rcopenid=rcopenid.replace(member.getOpenid(), "");
		}
		rc.setUseropenid(rcopenid);
		return true;
	}
	
	public OnliveRoom(JSONObject item){
		this.setNo(item.getString("bc_no"));
		this.setBrandcover(item.getString("bc_vip_cover"));
		this.setCover(item.getString("bc_cover"));
		this.setCreateTime(item.getString("bc_cttime"));
		this.setDescript(item.getString("bc_abstract"));
		this.setEndTime(item.getString("bc_endtime"));
		this.setGuest(item.getString("bc_guest"));
		this.setName(item.getString("bc_name"));
		this.setProject(item.getString("bc_project"));
		this.setSpeakMode(item.getString("bc_speakmod"));
		this.setStartTime(item.getString("bc_starttime"));
		this.setStatus(item.getString("bc_status"));
		this.setQrcode(item.getString("bc_qrcode"));
		this.setLatestPic(item.getString("bc_latest_pic"));
		this.setLatestText(item.getString("bc_latest_text"));
		this.setQrcode(item.getString("bc_qrcode"));
		this.setJoinmsg(item.getString("bc_join_message"));
		this.setCommentmsg(item.getString("bc_comment_message"));
		String manager_phone=item.getString("bc_manager_phone");
		this.setManager(NameList.getName(manager_phone));
		this.initMembers();
	}
	
	public void initMembers(){
		String sql="select * from wx_onlive_members where bc_no="+this.getNo();
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getSelectResult(sql, null, "wx_onlive_members");
		if(er.getResult()>0){
			JSONArray array=(JSONArray)er.getData();
			for(int i=0;i<array.size();i++){
				OnliveMember member=new OnliveMember(array.getJSONObject(i));
				if(member.isVip()){
					speakers.put(member.getOpenid(),member);
				}
				members.put(member.getOpenid(),member);
			}
		}
	}
	
	
	
	/**
	 * 获取直播间信息流，每次返回20条
	 * @param date 时间条件
	 * @param compare比较符><
	 * @return 20条信息流列表
	 */
	public List<OnliveStream> getStream(String date,String compare,String sorttype){
		String sort="DESC";
		if(sorttype!=null&&sorttype.equals("asc")){
			sort="ASC";
		}
		String sql="SELECT a.content_no,a.bc_no,a.content_type,a.content_time,a.`openid`,a.content_body,a.content_link,a.`content_piclink`,b.`member_nick`,b.`member_pic`,c.`bc_cover`,c.`bc_vip_cover` FROM `wx_onlive_content` a,`wx_onlive_members` b,`wx_onlive_broadcasting` c "+
							"WHERE a.`bc_no`="+this.getNo()+" AND a.`openid`=b.`member_openid` AND a.`bc_no`=c.`bc_no` AND a.`bc_no`=b.`bc_no` "+
							"AND content_time"+compare+"'"+date+"' ORDER BY content_time "+sort+" "+
							"LIMIT 0,10 ";
		ExecResult er=jr.getSelectResult(sql, null, "wx_onlive_content");
		List<OnliveStream> streams=new ArrayList<OnliveStream>();
		if(er.getResult()>0){
			JSONArray array=(JSONArray)er.getData();
			
			String streampath=OnliveStart.streamUrl;
			for(int i=0;i<array.size();i++){
				JSONObject item=array.getJSONObject(i);
				OnliveStream stream=new OnliveStream(item);
				stream.setUsericon(item.getString("member_pic"));
				String link=stream.getLink();
				if(link!=null&&!link.equals("")){
					stream.setLink(streampath+"/"+link);
					stream.setContent_piclink(streampath+"/"+item.getString("content_piclink"));
				}
//				String time=stream.getTime();
//				Date dt=TypeUtils.str2date(time, "yyyyMMddHHmmss");
//				stream.setTime(TypeUtils.getTime(dt, "yyyy年MM月dd日 HH:mm:ss"));
				streams.add(stream);
			}
		}
		return streams;
	}
	
	/**
	 * 发布一条信息流
	 * @param s 信息流对象
	 * this.setContent(stream.getString("content_body"));
		this.setOpenid(stream.getString("openid"));
		this.setContenttype(stream.getString("type"));
		this.setNick(stream.getString("member_nick"));
		this.setTime(stream.getString("content_time"));
		this.setUnionid(stream.getString("unionid"));
		this.setNo(stream.getString("content_no"));
		this.setRoom_no(stream.getString("bc_no"));
	 * @return 是否发布成功
	 */
	public boolean publishStream(JSONObject s){
		String now=TypeUtils.getTime();
		if(now.compareTo(this.getRawEndTime())>0){
			
			return false;
		}
		OnliveStream stream=new OnliveStream(s);
		//记录入库
		stream.setRoom_no(this.getNo());
		stream.insert2db();
		stream.downLoadMedia();
		
		if(stream.getContenttype().equals("text")){
			this.setLatestText(stream.getContent());
		}
		return true;
	}
	
	
	
	@Override
	public void initfdb() {
		if(this.getNo()==null||this.getNo().equals(""))return;
		String sql="SELECT`bc_no`,`bc_name`,`bc_abstract`,`bc_project`,`bc_guest`,`bc_cttime`,`bc_manager`,"
						+ " `bc_manager_union`,`bc_status`,`bc_qucode`,`bc_link`,`bc_starttime`,`bc_endtime`,`bc_speakmod`,"
						+ "`bc_cover`,`bc_vip_cover`,`bc_manager_phone`,bc_join_message,bc_comment_message "+
							"FROM `wx_onlive_broadcasting` where bc_no="+this.getNo();
		ExecResult er=jr.getSelectResult(sql, null, "wx_olive_namelist");
		if(er.getResult()>0){
			JSONArray array=(JSONArray)er.getData();
			JSONObject item=array.getJSONObject(0);
			this.setNo(item.getString("bc_no"));
			this.setBrandcover(item.getString("bc_vip_cover"));
			this.setCover(item.getString("bc_cover"));
			this.setCreateTime(item.getString("bc_cttime"));
			this.setDescript(item.getString("bc_abstract"));
			this.setEndTime(item.getString("bc_endtime"));
			this.setGuest(item.getString("bc_guest"));
			this.setName(item.getString("bc_name"));
			this.setProject(item.getString("bc_project"));
			this.setSpeakMode(item.getString("bc_speakmod"));
			this.setStartTime(item.getString("bc_starttime"));
			this.setStatus(item.getString("bc_status"));
			this.setLatestPic(item.getString("bc_latest_pic"));
			this.setLatestText(item.getString("bc_latest_text"));
			this.setQrcode(item.getString("bc_qrcode"));
			this.setJoinmsg(item.getString("bc_join_message"));
			this.setCommentmsg(item.getString("bc_comment_message"));
			String manager_phone=item.getString("bc_manager_phone");
			this.setManager(NameList.getName(manager_phone));
			//初始化成员
			this.initMembers();
		}
		
	}
	@Override
	public boolean insert2db() {
		String sql="insert into `wx_onlive_broadcasting`(`bc_name`,`bc_abstract`,`bc_project`,`bc_guest`,"
							+ "`bc_manager`,`bc_manager_union`,`bc_status`,`bc_starttime`,"
							+ "`bc_endtime`,`bc_speakmod`,`bc_cover`,`bc_vip_cover`,`bc_manager_phone`,bc_join_message,bc_comment_message) "+ 
						"values ('"+this.getName()+"','"+this.getDescript()+"','"+this.getProject()+"','"+this.getGuest()+"',"
						+ "'"+((this.getManager()!=null)?this.getManager().getOpenid():"")+"','"+((this.getManager()!=null)?this.getManager().getUnionid():"")
						+"','"+this.getStatus()+"',"
						+ "'"+this.startTime+"','"+this.endTime+"','"+this.getSpeakMode()+"','"+this.cover+"',"
						+ "'"+this.brandcover+"','"+((this.getManager()!=null)?this.getManager().getPhone():"")+"','"+this.getJoinmsg()+"','"+this.getCommentmsg()+"')";
		//写入数据库获得数据库编号
		ExecResult er=jr.getExecInsertId(sql, null, null, null);
		if(er.getResult()>0){
			this.setNo(er.getMessage());
			this.setQrcode(WeixinUtil.getEcodeTicket(this.getNo()));
			this.update2db();
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean update2db() {
		String sql="UPDATE `wx_onlive_broadcasting` "+
				" SET "+
						" `bc_name` = '"+this.getName()+"', "+
								  " `bc_abstract` = '"+this.getDescript()+"', "+
								  " `bc_project` = '"+this.getProject()+"', "+
								  " `bc_guest` = '"+this.getGuest()+"', "+
								  "  `bc_status` = '"+this.getStatus()+"', "+
								  " `bc_starttime` = '"+this.startTime+"', "+
								  " `bc_endtime` = '"+this.endTime+"', "+
								  " `bc_speakmod` = '"+this.getSpeakMode()+"', "+
								  " `bc_cover` = '"+this.cover+"', "+
								  " `bc_qrcode` = '"+this.qrcode+"', "+
								  " `bc_latest_pic` = '"+this.getLatestPic()+"', "+
								  " `bc_latest_text` = '"+this.getLatestText()+"', "+
								  " `bc_qrcode` = '"+this.qrcode+"', "+
								  " `bc_join_message` = '"+this.joinmsg+"', "+
								  " `bc_comment_message` = '"+this.commentmsg+"', "+
								  "   `bc_vip_cover` = '"+this.brandcover+"' "+
						" WHERE `bc_no` = '"+this.getNo()+"'";
		ExecResult er=jr.getExecResult(sql, null);
		if(er.getResult()>0){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean deletefdb() {
		String sql="delete from wx_onlive_broadcasting where bc_no="+this.getNo();
		ExecResult er=jr.getExecResult(sql, null);
		if(er.getResult()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public ExecResult getPraises(){
		String sql="select * from wx_onlive_stream_praise where room_no="+this.getNo()+" order by stream_no,praise_time";
		JSONResponse jr=new JSONResponse();
		return jr.getSelectResult(sql, null, "wx_onlive_stream_praise");
	}
	
	public ExecResult getComments(){
		
		String sql="select * from wx_onlive_stream_comment a,wx_onlive_members b where a.room_no=b.bc_no and a.openid=b.member_openid and a.room_no="+this.getNo()+" order by stream_no,comment_time";
		JSONResponse jr=new JSONResponse();
		return jr.getSelectResult(sql, null, "wx_onlive_stream_praise");
	}
	
	public ExecResult publishPraise(String openid,String streamno){
		String sql="insert into wx_onlive_stream_praise(stream_no,room_no,openid) values("+streamno+","+this.getNo()+",'"+openid+"')";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getExecResult(sql,null);
		if(er.getResult()>0){
			if(this.getCommentmsg().equals("1")){
				OnliveMember member=this.getMembers().get(openid);
				if(member!=null){
					String text=member.getNick()+" 赞了您的直播内容。";
					TextInfo ti=new TextInfo(text);
					ti.setToUserName(this.getManager().getOpenid());
					PushMessage.sendMessage(ti);
				}
			}
		}
		return er;
		
	}
	
	public ExecResult publishComment(String openid,String streamno,String comment){
		if(this.getSpeakMode().equals("speaker")){
			return new ExecResult(false,"直播间未开放评论");
		}
		String sql="insert into wx_onlive_stream_comment(stream_no,room_no,openid,content) values("+streamno+","+this.getNo()+",'"+openid+"','"+comment+"')";
		JSONResponse jr=new JSONResponse();
		ExecResult er= jr.getExecResult(sql,null);
		if(er.getResult()>0){
			if(this.getCommentmsg().equals("1")){
				OnliveMember member=this.getMembers().get(openid);
				if(member!=null){
					String text=member.getNick()+" 评论了您："+comment;
					TextInfo ti=new TextInfo(text);
					ti.setToUserName(this.getManager().getOpenid());
					PushMessage.sendMessage(ti);
				}
			}
		}
		return er;
	}
	
	public JSON getPraise(String streamno){
		String sql="select * from wx_onlive_stream_praise where stream_no="+streamno+" order by praise_time";
		JSONResponse jr=new JSONResponse();
		return jr.getSelectResult(sql, null, "wx_onlive_stream_praise").getData();
	}
	
	
	public JSON getComment(String streamno){
		String sql="select * from wx_onlive_stream_comment a,wx_onlive_members b where a.room_no=b.bc_no and a.openid=b.member_openid and a.stream_no="+streamno+" order by comment_time";
		JSONResponse jr=new JSONResponse();
		return jr.getSelectResult(sql, null, "wx_onlive_stream_comment").getData();
	}
	
	public ExecResult delComment(String comment_no){
		String sql="delete from wx_onlive_stream_comment where comment_no="+comment_no;
		JSONResponse jr=new JSONResponse();
		return jr.getExecResult(sql, null);
	}
	
	
	public ArticleObject getArticle(){
		String latestpic=this.getLatestPic();
		String latesttext=this.getLatestText();
		String coverpic=this.getBrandcover();
		String destext=this.getDescript();
		if(latestpic!=null&&!latestpic.equals("")){
			coverpic=latestpic;
		}
		if(latesttext!=null&&!latesttext.equals("")){
			destext=latesttext;
		}
		ArticleObject onliveroom=new ArticleObject();
		onliveroom.setDescription(destext);
		onliveroom.setPicUrl(coverpic);
		onliveroom.setTitle(this.getName());
		onliveroom.setUrl(this.getShareLink());
		return onliveroom;
	}
	
}
