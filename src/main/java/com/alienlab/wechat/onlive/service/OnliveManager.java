package com.alienlab.wechat.onlive.service;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alienlab.db.DAO;
import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.onlive.bean.OnliveMember;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.wechat.onlive.bean.OnliveRoom;
import com.alienlab.wechat.response.PushMessage;
import com.alienlab.wechat.response.ResponseConfig;
import com.alienlab.wechat.response.bean.TextInfo;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 直播间管理器
 * 
 * @author 橘
 *
 */
public class OnliveManager {
	private static Logger logger = Logger.getLogger(OnliveManager.class);
	public static SortedMap<String, OnliveRoom> rooms = new TreeMap<String, OnliveRoom>();

	public static void getRooms() {
		logger.info("get rooms from database");
		String sql = "select * from wx_onlive_broadcasting";
		JSONResponse jr = new JSONResponse();
		ExecResult er = jr.getSelectResult(sql, null, "wx_onlive_broadcasting");
		if (er.getResult() > 0) {
			JSONArray array = (JSONArray) er.getData();
			for (int i = 0; i < array.size(); i++) {
				OnliveRoom or = new OnliveRoom(array.getJSONObject(i));
				rooms.put(or.getNo(), or);

				// 添加消息响应配置1---直播响应
				ResponseConfig rc1 = new ResponseConfig();
				rc1.setUseropenid(or.getManager().getOpenid());
				rc1.setMsgType("ALL");
				rc1.setTimeStart(or.getRawStartTime());
				rc1.setTimeEnd(or.getRawEndTime());
				rc1.setResponse(new OnliveResponse());
				rc1.setConfigId(or.getNo());
				JSONObject param = rc1.getParam();
				param.put("key", "onlivestream");
				param.put("roomno", or.getNo());
				ResponseConfig.addConfig(rc1);
			}
		}
	}

	/**
	 * 创建直播间
	 *
	 * @param room
	 *            直播间JSONObject
	 * @return 返回创建的直播间
	 */
	public static boolean addRoom(OnliveRoom room) {
		//boolean duplicate = isdulplicate(room);
		boolean duplicate = false;//取消多直播间验证
		if (duplicate) {
			return false;
		} else {
			if (room.getNo()==null||room.getNo().equals("")) {
				room.insert2db();
				// 加载用户时，创建响应器
				ResponseConfig rc1 = new ResponseConfig();
				rc1.setUseropenid(room.getManager().getOpenid());
				rc1.setMsgType("text image video shortvideo voice");
				rc1.setTimeStart(room.getRawStartTime());
				rc1.setTimeEnd(room.getRawEndTime());
				rc1.setResponse(new OnliveResponse());
				rc1.setConfigId(room.getNo());
				JSONObject param = rc1.getParam();
				param.put("key", "onlivestream");
				param.put("roomno", room.getNo());
				ResponseConfig.addConfig(rc1);
				
				rooms.put(room.getNo(), room);
				String text="您的直播间《"+room.getName()+"》已成功创建。请在"+room.getStartTime()+"至"+room.getEndTime()+" 之间在此服务号上发送直播内容。";
				TextInfo ti=new TextInfo(text);
				ti.setToUserName(room.getManager().getOpenid());
				PushMessage.sendMessage(ti);
			} else {
				if (rooms.containsKey(room.getNo())) {
					room.update2db();
					rooms.remove(room.getNo());
					rooms.put(room.getNo(), room);
				}
			}
			return true;
		}
	}
	
	public static boolean updateRoom(OnliveRoom room) {
		if(rooms.containsKey(room.getNo())){
			room.update2db();
			rooms.remove(room.getNo());
			rooms.put(room.getNo(), room);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 验证新添加的直播间时间是否与已有的直播间时间冲突
	 * 
	 * @param newroom
	 * @return
	 */
	private static boolean isdulplicate(OnliveRoom newroom) {
		List<OnliveRoom> rooms = getRoomsByOpenid(newroom.getManager().getOpenid());
		for (int i = 0; i < rooms.size(); i++) {
			OnliveRoom room = rooms.get(i);
			String starttime = newroom.getRawStartTime();
			String endtime = newroom.getRawEndTime();
			if ((room.getRawStartTime().compareTo(starttime) >= 0 && room.getRawStartTime().compareTo(endtime) <= 0)
					|| (room.getRawEndTime().compareTo(starttime) >= 0
							&& room.getRawEndTime().compareTo(endtime) <= 0)) {
				return true;
			} else {
				if ((starttime.compareTo(room.getRawStartTime()) >= 0 && starttime.compareTo(room.getRawEndTime()) <= 0)
						|| (endtime.compareTo(room.getRawStartTime()) >= 0
								&& endtime.compareTo(room.getRawEndTime()) <= 0)) {
					return true;
				}
			}
		}
		return false;
	}

	public static OnliveRoom getRoom(String roomno) {
		if (rooms.containsKey(roomno)) {
			return rooms.get(roomno);
		} else {
			return null;
		}
	}

	private static PropertyConfig pc=new PropertyConfig("sysConfig.properties");
	/**
	 * 公开课程关联的直播，需要验证当前人员是否具有访问权限，如果没有则跳转至公开课引导
	 * @return
	 */
	public static String validateRoomForCourse(OnliveRoom room,String openid){
		//获取直播间是否被课程引用
		String sql="select * from course_onlive_relation where onlive_id="+room.getNo();
		DAO dao=new DAO();
		List onlives=dao.getDataSet(sql);
		if(onlives!=null&&onlives.size()>0){//如果已被课程引用
			if(room.getManager().equals(openid)){
				return "0"; //如果当前openid是此直播间的管理员，直接可以进入
			}
			Map<String,OnliveMember> members=room.getMembers();
			if(members.containsKey(openid)){
				return "0"; //如果是已经取得进入资格的用户，可以直接进入。
			}
			sql="SELECT * FROM course_onlive_relation a,course_order b, wechat_user c " +
					"WHERE a.onlive_id="+room.getNo()+" AND a.`course_id`=b.`course_id` AND b.`user_id`=c.`id` AND c.`open_id`='"+openid+"'";
			List result=dao.getDataSet(sql);
			if(result.size()>0){
				return "0";//可以正常进入
			}else{
				//跳转到课程宣传界面
				String url=pc.getValue("wechatcourseurl");
				url.replace("$(STATE)",room.getNo());
				return url;
			}
		}else{
			return "0";
		}



	}

	public static List<OnliveRoom> getRoomsByOpenid(String openid) {
		List<OnliveRoom> userrooms = new ArrayList<OnliveRoom>();
		for (String roomno : rooms.keySet()) {
			OnliveRoom or = rooms.get(roomno);
			if (or.getManager() != null) {
				//logger.error("getRoomsByOpenid>>>>openid:"+openid+",onliveroom is:"+ JSON.toJSONString(or));
				if (openid.equals(or.getManager().getOpenid())) {
					userrooms.add(or);
				}
			}
		}
		return userrooms;
	}

	public static boolean delRoom(String roomno) {
		if (rooms.containsKey(roomno)) {
			OnliveRoom room = rooms.get(roomno);
			if (room.deletefdb()) {
				rooms.remove(roomno);
			}
			ResponseConfig.removeConfig(room.getNo());
		}
		return true;
	}

}
