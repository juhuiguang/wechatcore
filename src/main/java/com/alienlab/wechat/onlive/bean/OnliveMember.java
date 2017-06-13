package com.alienlab.wechat.onlive.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.ExecResult;
import com.alienlab.db.ISyncDb;
import com.alienlab.response.JSONResponse;

/**
 * 直播间成员
 * @author 橘
 *
 */
public class OnliveMember implements ISyncDb {
	JSONResponse jr=new JSONResponse();
	private String room_no;
	public String getRoom_no() {
		return room_no;
	}
	public void setRoom_no(String room_no) {
		this.room_no = room_no;
	}
	/**
	 * 成员微信openid
	 */
	private String openid;
	/**
	 *成员unionid 
	 */
	private String unionid;
	/**
	 * 成员手机号码
	 */
	private String phone;
	/**
	 * 成员昵称
	 */
	private String nick;
	/**
	 * 成员远程头像地址
	 */
	private String pic;
	/**
	 * 成员本地头像地址
	 */
	private String localPic;
	/**
	 * 成员加入方式
	 */
	private String joinType;
	/**
	 * 成员加入时间
	 */
	private String joinTime;
	/**
	 * 是否嘉宾
	 */
	private boolean isVip;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getLocalPic() {
		return localPic;
	}
	public void setLocalPic(String localPic) {
		this.localPic = localPic;
	}
	public String getJoinType() {
		return joinType;
	}
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	public String getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}
	public boolean isVip() {
		return isVip;
	}
	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}
	
	public OnliveMember(){
		
	}
	
	/**
	 * 通过jsonboject创建member对象
	 * @param item
	 */
	public OnliveMember(JSONObject item){
		this.setJoinTime(TypeUtils.getTime());
		this.setJoinType(item.getString("member_join_type"));
		this.setLocalPic(item.getString("member_pic"));
		this.setNick(item.getString("member_nick"));
		this.setOpenid(item.getString("member_openid"));
		this.setPhone(item.getString("member_phone"));
		this.setPic(item.getString("member_pic"));
		this.setRoom_no(item.getString("bc_no"));
		this.setUnionid(item.getString("member_unionid"));
		this.setVip(item.getBooleanValue("member_vip"));
	}
	
	/**
	 * 通过直播间管理员对象创建成员对象
	 * @param roomno
	 * @param name
	 */
	public OnliveMember(String roomno,NamelistItem name){
		this.setJoinTime(TypeUtils.getTime());
		this.setJoinType("system");
		this.setLocalPic(name.getHeaderimg());
		this.setNick(name.getNickname());
		this.setOpenid(name.getOpenid());
		this.setPhone(name.getPhone());
		this.setPic(name.getHeaderimg());
		this.setRoom_no(roomno);
		this.setUnionid(name.getUnionid());
		this.setVip(true);
	}
	
	@Override
	public void initfdb() {
		// TODO Auto-generated method stub
		String sql="select * from wx_onlive_members where bc_no="+this.getRoom_no()+" and member_openid='"+this.getOpenid()+"'";
		ExecResult er=jr.getSelectResult(sql, null, "wx_onlive_members");
		if(er.getResult()>0){
			JSONArray array=(JSONArray)er.getData();
			JSONObject item=array.getJSONObject(0);
			this.setJoinTime(item.getString("member_join_time"));
			this.setJoinType(item.getString("member_join_type"));
			this.setLocalPic(item.getString("member_pic"));
			this.setNick(item.getString("member_nick"));
			this.setOpenid(item.getString("member_openid"));
			this.setPhone(item.getString("member_phone"));
			this.setPic(item.getString("member_pic"));
			this.setRoom_no(item.getString("bc_no"));
			this.setUnionid(item.getString("member_unionid"));
			this.setVip(item.getBooleanValue("member_vip"));
		}
	}
	@Override
	public boolean insert2db() {
		String sql="INSERT INTO `wx_onlive_members`(`bc_no`, `member_openid`, `member_unionid`, `member_nick`, `member_join_type`, `member_join_time`"
				+ ", `member_vip`, `member_pic`, `member_phone`) "+ 
							"VALUES ('"+this.getRoom_no()+"','"+this.getOpenid()+"','"+this.getUnionid()+"','"+this.getNick()
							+"','"+this.getJoinType()+"','"+this.getJoinTime()+"','"+this.isVip()+"','"+this.getPic()+"','"+this.getPhone()+"')";
		//写入数据库获得数据库编号
		ExecResult er=jr.getExecInsertId(sql, null, null, null);
		if(er.getResult()>0){
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
		String sql="delete from wx_onlive_members where bc_no="+this.getRoom_no()+" and member_openid='"+this.getOpenid()+"'";
		ExecResult er=jr.getExecResult(sql, null);
		if(er.getResult()>0){
			return true;
		}else{
			return false;
		}
	}
	
}
