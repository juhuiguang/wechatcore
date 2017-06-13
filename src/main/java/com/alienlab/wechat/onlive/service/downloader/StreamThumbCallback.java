package com.alienlab.wechat.onlive.service.downloader;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.wechat.onlive.bean.OnliveRoom;
import com.alienlab.wechat.onlive.service.OnliveManager;
import com.googlecode.asyn4j.core.callback.AsynCallBack;

/**
 * 信息流缩略图下载回调函数
 * @author 橘
 *
 */

@SuppressWarnings("serial")
public class StreamThumbCallback extends AsynCallBack {
	private static Logger logger = Logger.getLogger(StreamThumbCallback.class);
	@Override
	public void doNotify() {
		ExecResult er=(ExecResult)this.methodResult;
		logger.info("StreamThumbCallback>>>>"+er.toString());
		JSONObject data=(JSONObject)er.getData();
		String sql="update wx_onlive_content set content_piclink='"+data.getString("filename")+"',download_pic_time='"+TypeUtils.getTime()+"' where content_no="+data.getString("streamno");
		JSONResponse jr=new JSONResponse();
		jr.getExecResult(sql, null);
		sql="SELECT * FROM `wx_onlive_content` a WHERE content_no="+data.getString("streamno");
		ExecResult result=jr.getSelectResult(sql, null, "wx_onlive_content");
		if(result.getResult()>0){
			JSONArray ja=(JSONArray)result.getData();
			if(ja.size()>0){
				JSONObject stream=ja.getJSONObject(0);
				String type=stream.getString("content_type");
				String roomno=stream.getString("bc_no");
				OnliveRoom room=OnliveManager.getRoom(roomno);
				if(type.equals("image")){
					room.setLatestPic(stream.getString("content_link"));
				}else if(type.equals("shortvideo")||type.equals("video")){
					room.setLatestPic(stream.getString("content_piclink"));
				}
				OnliveManager.updateRoom(room);
			}
			
		}
	}

}
