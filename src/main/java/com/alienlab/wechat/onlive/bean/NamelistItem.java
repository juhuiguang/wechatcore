package com.alienlab.wechat.onlive.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.ExecResult;
import com.alienlab.db.ISyncDb;
import com.alienlab.response.JSONResponse;

/**
 * 白名单成员
 * @author 橘
 *
 */
public class NamelistItem implements ISyncDb{
	JSONResponse jr=new JSONResponse();
	private String phone="";//成员手机号
	private int count=5;//成员直播间数量上限
	private String endtime="";//成员有效期
	private String status="1";//成员状态
	private String openid="";//开放平台身份编号;
	private String sex="";
	private String province="";
	private String city="";
	private String country="";
	private String privilege="";
	private String unionid="";
	private String headerimg="";
	private String nickname="";
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getHeaderimg() {
		return headerimg;
	}
	public void setHeaderimg(String headerimg) {
		this.headerimg = headerimg;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPrivilege() {
		return privilege;
	}
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isValidate(){
		if(Integer.parseInt(this.getStatus())>0){
			String endtime=this.getEndtime();
			//如果无结束时间表示无限期
			if(endtime.equals("")){
				return true;
			}else{
				//有结束期限，验证结束期限。
				String nowtime=TypeUtils.getTime();
				if(nowtime.compareTo(endtime)>0){
					return false;
				}else{
					return true;
				}
			}
		}else{//状态被置为0，直接返回不可用
			return false;
		}
	}
	
	public NamelistItem(){
		
	}
	public NamelistItem(String phone){
		this.phone=phone;
		this.initfdb();
	}
	
	@Override
	public void initfdb() {
		if(this.getPhone()!=null){
			String sql="select * from wx_olive_namelist where namelist_phone='"+this.getPhone()+"'";
			ExecResult er=jr.getSelectResult(sql, null, "wx_olive_namelist");
			if(er.getResult()>0){
				JSONArray array=(JSONArray)er.getData();
				JSONObject item=array.getJSONObject(0);
				this.setCount(item.getIntValue("namelist_count"));
				this.setEndtime(item.getString("namelist_endtime"));
				this.setPhone(item.getString("namelist_phone"));
				this.setStatus(item.getString("namelist_status"));
				this.setCity(item.getString("city"));
				this.setCountry(item.getString("country"));
				this.setEndtime(item.getString("namelist_endtime"));
				this.setHeaderimg(item.getString("headerimg"));
				this.setNickname(item.getString("nickname"));
				this.setOpenid(item.getString("openid"));
				this.setProvince(item.getString("province"));
				this.setSex(item.getString("sex"));
				this.setUnionid(item.getString("unionid"));
			}
		}
		
	}
	@Override
	public boolean insert2db() {
		String sql="INSERT INTO `wx_olive_namelist`(`namelist_phone`,`namelist_count`,`namelist_endtime`,`namelist_status`,`openid`,`nickname`,`headerimg`,`sex`,`province`,`city`,`country`,`unionid`,`privilege`) "+
						" values ('"+this.getPhone()+"','"+this.getCount()+"','"+this.getEndtime()+"','"
										+this.getStatus()+"','"+this.getOpenid()+"','"+this.getNickname()
										+"','"+this.getHeaderimg()+"','"+this.getSex()+"','"+this.getProvince()+"','"+this.getCity()
										+"','"+this.getCountry()+"','"+this.getUnionid()+"','"+this.getPrivilege()+"')";
		ExecResult er=jr.getExecResult(sql, null);
		if(er.getResult()>0){
			return true;
		}else{
			return false;
		}
		
	}
	@Override
	public boolean update2db() {
		String sql="UPDATE `wx_olive_namelist` "+
							" SET "+
							"  `namelist_count` = '"+this.getCount()+"', "+
							"  `namelist_endtime` = '"+this.getEndtime()+"', "+
							"  `namelist_status` = '"+this.getStatus()+"', "+
							"  `openid` = '"+this.getOpenid()+"', "+
							"  `nickname` = '"+this.getNickname()+"', "+
							"  `headerimg` = '"+this.getHeaderimg()+"', "+
							"  `sex` = '"+this.getSex()+"', "+
							"  `province` = '"+this.getProvince()+"', "+
							"  `city` = '"+this.getCity()+"', "+
							"  `country` = '"+this.getCountry()+"', "+
							"  `unionid` = '"+this.getUnionid()+"', "+
							"  `privilege` = '"+this.getPrivilege()+"' "+
							"WHERE `namelist_phone` = '"+this.getPhone()+"'";
		System.out.println("update namelistitem:"+sql);
		ExecResult er=jr.getExecResult(sql, null);
		if(er.getResult()>0){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean deletefdb() {
		String sql="delete from wx_olive_namelist where namelist_phone='"+this.getPhone()+"'";
		ExecResult er=jr.getExecResult(sql, null);
		if(er.getResult()>0){
			return true;
		}else{
			return false;
		}
	}
}
