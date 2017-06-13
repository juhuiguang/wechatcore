package com.alienlab.wechat.onlive.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.onlive.bean.OnliveRoom;
import com.alienlab.wechat.response.BaseResponse;
import com.alienlab.wechat.response.IResponse;

public class OnliveMultyResponse implements IResponse {

	@Override
	public void preResponse(BaseMessage msg, JSONObject param) {
		// TODO Auto-generated method stub

	}

	@Override
	public String doResponse(BaseMessage msg, JSONObject param) {
		JSONArray responses=param.getJSONArray("params");
		List<OnliveRoom> rooms=new ArrayList<OnliveRoom>();
		for(int i=0;i<responses.size();i++){
			JSONObject res=responses.getJSONObject(i);
			JSONObject p=res.getJSONObject("param");
			if(p.containsKey("key")&&p.getString("key").equals("onlivestream")){
				if(p.containsKey("roomno")){
					String roomno=p.getString("roomno");
					OnliveRoom room=OnliveManager.getRoom(roomno);
					if(room!=null){
						rooms.add(room);
					}
				}
			}
		}
		if(rooms.size()>1){
			String text="您当前有"+rooms.size()+"个直播间正在直播中，请到对应的直播界面中发送内容：";
			for(int i=0;i<rooms.size();i++){
				text+="\n<a href=\""+rooms.get(i).getShareLink()+"\">"+rooms.get(i).getName()+"</a>";
			}
			return BaseResponse.getTextResponse(text , msg);
		}
		return BaseResponse.getTextResponse("系统有点忙，请稍后再试" , msg);
	}

	@Override
	public void afterResponse(BaseMessage msg, JSONObject param) {
		// TODO Auto-generated method stub

	}

}
