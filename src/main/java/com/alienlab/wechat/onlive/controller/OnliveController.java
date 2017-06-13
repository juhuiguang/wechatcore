package com.alienlab.wechat.onlive.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.ExecResult;
import com.alienlab.wechat.core.util.CatchImage;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.onlive.bean.NamelistItem;
import com.alienlab.wechat.onlive.bean.OnliveMember;
import com.alienlab.wechat.onlive.bean.OnliveRoom;
import com.alienlab.wechat.onlive.bean.OnliveStream;
import com.alienlab.wechat.onlive.service.NameList;
import com.alienlab.wechat.onlive.service.OnliveManager;
import com.alienlab.wechat.onlive.service.OnliveStart;

@Controller
public class OnliveController {
	private static Logger logger = Logger.getLogger(OnliveController.class);
	@RequestMapping(value="onlive/newuser.do",method=RequestMethod.POST)
	@ResponseBody
	public String validatePhone(HttpServletRequest request){
		String phone=request.getParameter("phone");
		 boolean isvalidate=NameList.validatePhone(phone);
		 //账户不可用
		 if(!isvalidate){
			 System.out.println("phone number wrong...");
			 return new ExecResult(false,"该手机号码不能创建直播间").toString();
		 }else{
			NamelistItem name=NameList.getName(phone);
			name.setCity(request.getParameter("city"));
			name.setCountry(request.getParameter("country"));
			name.setOpenid(request.getParameter("openid"));
			name.setPhone(phone);
			name.setProvince(request.getParameter("province"));
			name.setSex(request.getParameter("sex"));
			name.setUnionid(request.getParameter("unionid"));
			name.setHeaderimg(request.getParameter("headerimg"));
			name.setNickname(request.getParameter("nickname"));
			NameList.addName(name);
			
			ExecResult er=new ExecResult();
			er.setResult(true);
			er.setData((JSON)JSON.toJSON(name));
			return er.toString();
		 }
	}
	
	@RequestMapping(value="onlive/create.do",method=RequestMethod.POST)
	@ResponseBody
	public String createOnliveRoom(HttpServletRequest request){
		OnliveRoom room=new OnliveRoom();
		room.setDescript(request.getParameter("bc_abstract"));
		room.setEndTime(request.getParameter("bc_endtime").replace("T","").replace("-","").replace(":",""));
		room.setGuest(request.getParameter("bc_guest"));
		room.setName(request.getParameter("bc_name"));
		room.setProject(request.getParameter("bc_project"));
		room.setSpeakMode(request.getParameter("bc_speakmode"));
		room.setStartTime(request.getParameter("bc_starttime").replace("T","").replace("-","").replace(":",""));
		room.setStatus(request.getParameter("bc_status"));
		room.setManager(request.getParameter("bc_manager_phone"));
		String covermedia=request.getParameter("bc_cover");
		String iconmedia=request.getParameter("bc_vip_cover");
		if(covermedia!=null&&!covermedia.equals("")){
			System.out.println("download image mediaid is:"+covermedia);
			String url=WeixinUtil.getMediaUrl(covermedia);
			System.out.println("download image url is:"+url);
			CatchImage ci=new CatchImage();
			String cover=ci.Download(url, OnliveStart.coverPath);
			System.out.println("download image success filename is:"+cover);
			room.setCover(cover);
		}
		if(iconmedia!=null&&!iconmedia.equals("")){
			System.out.println("download image mediaid is:"+iconmedia);
			String url=WeixinUtil.getMediaUrl(iconmedia);
			System.out.println("download image url is:"+url);
			CatchImage ci=new CatchImage();
			String cover=ci.Download(url, OnliveStart.coverPath);
			System.out.println("download image success filename is:"+cover);
			room.setBrandcover(cover);
		}
		boolean flag=OnliveManager.addRoom(room);
		if(flag){
			//将管理员自动添加到直播间中。
			room.joinRoom(new OnliveMember(room.getNo(),room.getManager()));
			ExecResult er=new ExecResult();
			er.setResult(true);
			er.setData((JSON)JSON.toJSON(room));
			return er.toString();
		}else{
			ExecResult er=new ExecResult();
			er.setResult(false);
			er.setMessage("直播创建失败。您选择时间区间已有其他直播。");
			return er.toString();
		}
		
	}
	
	@RequestMapping(value="onlive/loadRoomMemberCount.do",method=RequestMethod.POST)
	@ResponseBody
	public String loadRoomMemberCount(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		ExecResult er=new ExecResult();
		JSONObject data=new JSONObject();
		if(room!=null){
			er.setResult(true);
			data.put("count", room.getMemberCount());
			er.setData(data);
		}else{
			data.put("count", 0);
			er.setResult(false);
			er.setData(data);
		}
		return er.toString();
	}
	
	@RequestMapping(value="onlive/deleteRoom.do",method=RequestMethod.POST)
	@ResponseBody
	public String deleteRoom(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		boolean result=OnliveManager.delRoom(roomno);
		if(result){
			return new ExecResult(true,"删除成功").toString();
		}else{
			return new ExecResult(false,"删除失败，您的直播间正在直播。").toString();
		}
	}
	@RequestMapping(value="onlive/getUserRoom.do",method=RequestMethod.POST)
	@ResponseBody
	public String getUserRoom(HttpServletRequest request){
		String openid=request.getParameter("openid");
		logger.error("getuserroom.do>>>>openid:"+openid);
		try {
			String input=IOUtils.toString(request.getInputStream());
			logger.error("getuserroom.do>>>>input:"+input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<OnliveRoom> rooms=OnliveManager.getRoomsByOpenid(openid);
		JSONArray array=new JSONArray();
		for(int i=0;i<rooms.size();i++){
			JSONObject room=new JSONObject();
			room.put("roomno",rooms.get(i).getNo());
			room.put("roomname",rooms.get(i).getName());
			room.put("roomdesc",rooms.get(i).getDescript());
			room.put("cover",rooms.get(i).getCover());
			array.add(room);
		}
		ExecResult er=new ExecResult();
		er.setResult(true);
		er.setData(array);
		return er.toString();
	}
	
	@RequestMapping(value="onlive/loadstream.do",method=RequestMethod.POST)
	@ResponseBody
	public String loadStream(HttpServletRequest request){
		String time=request.getParameter("time");
		String direction=request.getParameter("direction");
		String sorttype=request.getParameter("sorttype");
		//如果正序排列
		if(sorttype!=null&&sorttype.equals("asc")){
			if(direction.equals("head")){
				direction="<";
			}else{
				direction=">";
			}
		}else{//倒序排列
			if(direction.equals("head")){
				direction=">";
			}else{
				direction="<";
			}
		}
		String roomno=request.getParameter("roomno");
		ExecResult er=new ExecResult();
		if(roomno!=null&&!roomno.equals("")){
			OnliveRoom room=OnliveManager.getRoom(roomno);
			List<OnliveStream> streams=room.getStream(time, direction,sorttype);
			er.setResult(true);
			er.setData((JSON)JSON.toJSON(streams));
		}else{
			er.setResult(false);
			er.setMessage("参数传递错误");
		}
		return er.toString();
	}
	
	@RequestMapping(value="onlive/reloaditem.do",method=RequestMethod.POST)
	@ResponseBody
	public String reloaditem(HttpServletRequest request){
		String streamno=request.getParameter("streamno");
		OnliveStream stream=OnliveStream.loadStream(streamno);
		return JSON.toJSONString(stream);
	}
	@RequestMapping(value="onlive/joinRoom.do",method=RequestMethod.POST)
	@ResponseBody
	public String joinRoom(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		String openid=request.getParameter("openid");
		String nick=request.getParameter("nick");
		String headerimg=request.getParameter("headerimg");
		String joinType=request.getParameter("jointype");
		String isvip=request.getParameter("isvip");
		JSONObject member=new JSONObject();
		member.put("member_join_type", joinType);
		member.put("member_pic", headerimg);
		member.put("member_nick", nick);
		member.put("member_openid", openid);
		member.put("member_phone", "");
		member.put("bc_no", roomno);
		member.put("member_unionid", "");
		member.put("member_vip", isvip);
		OnliveMember om=new OnliveMember(member);
		OnliveRoom room=OnliveManager.getRoom(roomno);
		ExecResult er=new ExecResult();
		if(room!=null){
			room.joinRoom(om);
			er.setResult(true);
			er.setMessage("");
		}else{
			er.setResult(false);
			er.setMessage("join failure");
		}
		return er.toString();
		
	}
	
	@RequestMapping(value="onlive/publishStream.do",method=RequestMethod.POST)
	@ResponseBody
	public String publishStream(HttpServletRequest request){ 
		String roomno=request.getParameter("roomno");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		ExecResult er=new ExecResult();
		if(room!=null){
			JSONObject s=new JSONObject();
			s.put("openid", request.getParameter("openid"));
			s.put("content_type", request.getParameter("type"));
			s.put("member_nick", request.getParameter("nick"));
			s.put("content_time", TypeUtils.getTime(new Date(), "yyyyMMddHHmmss"));
			s.put("bc_no", roomno);
			s.put("content_body", request.getParameter("content"));
			s.put("content_welink", request.getParameter("mediaid"));
			if(room.publishStream(s)){
				er.setResult(true);
				er.setMessage("发布成功");
			}else{
				er.setResult(false);
				er.setErrormsg("内容发布失败");
			}
		}else{
			er.setResult(false);
			er.setErrormsg("未找到直播间。");
		}
		return er.toString();	
	}
	
	@RequestMapping(value="onlive/getRoomLatestInfo.do",method=RequestMethod.POST)
	@ResponseBody
	public String getRoomLatestInfo(HttpServletRequest request){ 
		String roomno=request.getParameter("roomno");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			JSONObject info=new JSONObject();
			info.put("latesttext", room.getLatestText());
			info.put("latestpic", room.getLatestPic());
			ExecResult er=new ExecResult(true,"获取成功");
			er.setData(info);
			return er.toString();
		}
	}
	
	@RequestMapping(value="onlive/renameRoom.do",method=RequestMethod.POST)
	@ResponseBody
	public String renameRoom(HttpServletRequest request){ 
		String roomno=request.getParameter("roomno");
		String newname=request.getParameter("name");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			room.setName(newname);
			OnliveManager.updateRoom(room);
			return (new ExecResult(true,"更新成功")).toString();
		}
	}
	
	@RequestMapping(value="onlive/setSwitch.do",method=RequestMethod.POST)
	@ResponseBody
	public String setSwitch(HttpServletRequest request){ 
		String roomno=request.getParameter("roomno");
		String value=request.getParameter("value");
		String config=request.getParameter("config");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			switch(config){
				case "speakmod":{
					room.setSpeakMode(value);
					break;
				}
				case "joinmsg":{
					room.setJoinmsg(value);
					break;
				}
				case "commentmsg":{
					room.setCommentmsg(value);
					break;
				}	
			}
			OnliveManager.updateRoom(room);
			return (new ExecResult(true,"更新成功")).toString();
		}
		
	}
	
	
	@RequestMapping(value="onlive/publishPraise.do",method=RequestMethod.POST)
	@ResponseBody
	public String publishPraise(HttpServletRequest request){ 
		String openid=request.getParameter("openid");
		String roomno=request.getParameter("roomno");
		String streamno=request.getParameter("streamno");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			ExecResult er=room.publishPraise(openid, streamno);
			return er.toString();
		}
	}
	
	@RequestMapping(value="onlive/publishComment.do",method=RequestMethod.POST)
	@ResponseBody
	public String publishComment(HttpServletRequest request){ 
		String openid=request.getParameter("openid");
		String roomno=request.getParameter("roomno");
		String streamno=request.getParameter("streamno");
		String comment=request.getParameter("comment");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			ExecResult er=room.publishComment(openid, streamno, comment);
			return er.toString();
		}
	}
	
	@RequestMapping(value="onlive/deleteComment.do",method=RequestMethod.POST)
	@ResponseBody
	public String deleteComment(HttpServletRequest request){ 
		String commentno=request.getParameter("commentno");
		String roomno=request.getParameter("roomno");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			ExecResult er=room.delComment(commentno);
			return er.toString();
		}
	}
	
	@RequestMapping(value="onlive/getPraiseComment.do",method=RequestMethod.POST)
	@ResponseBody
	public String getPraiseComment(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		String streamno=request.getParameter("streamno");
		ExecResult er=new ExecResult();
		JSONObject data=new JSONObject();
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			if(streamno==null){
				ExecResult per=room.getPraises();
				if(per.getResult()>0){
					data.put("praise", per.getData());
				}
				ExecResult cper=room.getComments();
				if(cper.getResult()>0){
					data.put("comment", cper.getData());
				}
			}else{
				data.put("praise",room.getPraise(streamno));
				data.put("comment",room.getComment(streamno));
			}
			er.setResult(true);
			er.setData(data);
			return er.toString();
		}
	
	}
	
	@RequestMapping(value="onlive/getMembers.do",method=RequestMethod.POST)
	@ResponseBody
	public String getMembers(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		String openid=request.getParameter("openid");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}else{
			if(!room.getManager().getOpenid().equals(openid)){
				return (new ExecResult(false,"对不起，您不是直播间管理员。")).toString();
			}
			List<OnliveMember> members=new ArrayList<OnliveMember>();
			for(Entry e:room.getMembers().entrySet()){
				members.add((OnliveMember)e.getValue());
			}
			ExecResult er=new ExecResult();
			er.setResult(true);
			er.setData((JSON)JSON.toJSON(members));
			return er.toString();
		}
	}
	
	@RequestMapping(value="onlive/setVip.do",method=RequestMethod.POST)
	@ResponseBody
	public String setVip(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		String openid=request.getParameter("openid");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}
		OnliveMember member=room.getMembers().get(openid);
		if(member!=null){
			if(room.addSpeaker(member)){
				return (new ExecResult(true,"设置成功")).toString();
			}else{
				return (new ExecResult(false,"保存失败")).toString();
			}
		}else{
			return (new ExecResult(false,"直播间不存在该成员")).toString();
		}
	}
	
	@RequestMapping(value="onlive/removeVip.do",method=RequestMethod.POST)
	@ResponseBody
	public String removeVip(HttpServletRequest request){
		String roomno=request.getParameter("roomno");
		String openid=request.getParameter("openid");
		OnliveRoom room=OnliveManager.getRoom(roomno);
		if(room==null){
			return (new ExecResult(false,"直播间不存在")).toString();
		}
		if(room.removeSpeaker(openid)){
			return (new ExecResult(true,"设置成功")).toString();
		}else{
			return (new ExecResult(false,"保存失败")).toString();
		}
	}
	
}
