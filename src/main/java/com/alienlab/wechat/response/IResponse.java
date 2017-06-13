package com.alienlab.wechat.response;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.message.bean.BaseMessage;

public interface IResponse {
	/**
	 * 响应前的处理
	 * @param msg
	 */
	public void preResponse(BaseMessage msg, JSONObject param);
	/**
	 * 获得响应
	 * @param msg
	 * @return
	 */
	public String doResponse(BaseMessage msg, JSONObject param);
	
	/**
	 * 响应后处理
	 * @param msg
	 */
	public void afterResponse(BaseMessage msg, JSONObject param);
}
