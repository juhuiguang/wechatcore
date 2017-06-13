package com.alienlab.wechat.onlive.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.wechat.onlive.bean.NamelistItem;
import com.alienlab.wechat.response.ResponseConfig;

/**
 * 白名单
 * @author 橘
 *
 */
public class NameList {
	private static Map<String,NamelistItem> nameList=new HashMap<String,NamelistItem>();
	
	/**
	 * 获取当前白名单
	 * @return
	 */
	public static Map<String,NamelistItem> getNameList(){
		String sql="select * from wx_olive_namelist";
		JSONResponse jr=new JSONResponse();
		ExecResult er=jr.getSelectResult(sql, null, "wx_olive_namelist");
		if(er.getResult()>0){
			JSONArray array=(JSONArray)er.getData();
			for(int i=0;i<array.size();i++){
				JSONObject item=array.getJSONObject(i);
				System.out.println("namelistitem:"+item.toJSONString());
				NamelistItem nameitem=new NamelistItem();
				nameitem.setCount(item.getIntValue("namelist_count"));
				nameitem.setEndtime(item.getString("namelist_endtime"));
				nameitem.setPhone(item.getString("namelist_phone"));
				nameitem.setStatus(item.getString("namelist_status"));
				nameitem.setCity(item.getString("city"));
				nameitem.setCountry(item.getString("country"));
				nameitem.setEndtime(item.getString("namelist_endtime"));
				nameitem.setHeaderimg(item.getString("headerimg"));
				nameitem.setNickname(item.getString("nickname"));
				nameitem.setOpenid(item.getString("openid"));
				nameitem.setProvince(item.getString("province"));
				nameitem.setSex(item.getString("sex"));
				nameitem.setUnionid(item.getString("unionid"));
//				if(!nameList.containsKey(nameitem.getPhone())){
//					
//				}
				nameList.put(nameitem.getPhone(),nameitem);
				
			}
		}
		return nameList;
	}
	
	/**
	 * 增加白名单
	 * @param name 名单
	 * @return
	 */
	public static boolean addName(NamelistItem name){
		if(nameList.containsKey(name.getPhone())){
			nameList.remove(name.getPhone());
			name.update2db();
		}else{
			name.insert2db();
		}
		nameList.put(name.getPhone(),name);
		return true;
	}
	
	/**
	 * 获取指定名单
	 * @param phone 电话号码
	 * @return
	 */
	public static NamelistItem getName(String phone){
		getNameList();
		if(nameList.containsKey(phone)){
			return nameList.get(phone);
		}
		return null;
	}
	
	public static NamelistItem getNameByOpenid(String openid){
		getNameList();
		NamelistItem name=null;
		for(String phone :nameList.keySet()){
			NamelistItem n=nameList.get(phone);
			if(n.getOpenid()!=null&&n.getOpenid().equals(openid)){
				name=n;
				break;
			}
		}
		return name;
	}
	
	/**
	 * 从白名单中移除名单
	 * @param phone 电话号码
	 * @return
	 */
	public static boolean removeName(String phone){
		NamelistItem name=getName(phone);
		if(name!=null){
			nameList.remove(phone);
		}
		return true;
	}
	
	
	/**
	 * 通过手机号码，验证该号码是否可以创建直播间
	 * @param phone
	 * @return
	 */
	public static boolean validatePhone(String phone){
		NamelistItem name=getName(phone);
		if(name!=null){
			System.out.println("validatePhone get:"+phone);
			return name.isValidate();
		}else{
			System.out.println("validatePhone wrong:"+phone);
		}
		return false;
	}
	
}
