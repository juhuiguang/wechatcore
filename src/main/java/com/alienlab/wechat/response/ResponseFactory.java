package com.alienlab.wechat.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.onlive.service.OnliveMultyResponse;

public class ResponseFactory {
	private static Logger logger = Logger.getLogger(ResponseFactory.class);
	public static ResponseConfig getResponse(BaseMessage msg){
		logger.error("getResponse from factory:"+JSONObject.toJSONString(msg));
		List<ResponseConfig> configs=new ArrayList<ResponseConfig>();
		for(int i=0;i<ResponseConfig.getConfig().size();i++){
			ResponseConfig rc=ResponseConfig.getConfig().get(i);
			if(rc.isMatched(msg)){
				configs.add(rc);
			}
		}
		logger.error("get "+configs.size()+" Response from factory");
		if(configs.size()==1){
			return configs.get(0);
		}else{
			int maxlevel=0;
			List<ResponseConfig> irs=new ArrayList<ResponseConfig>();
			//循环找到最大优先级
			for(int i=0;i<configs.size();i++){
				if(configs.get(i).getConfiglevel()>maxlevel){
					maxlevel=configs.get(i).getConfiglevel();
				}
			}
			logger.error("the max level response is "+maxlevel);
			//获得最大优先级响应数组
			for(int i=0;i<configs.size();i++){
				if(configs.get(i).getConfiglevel()==maxlevel){
					irs.add(configs.get(i));
				}
			}
			if(irs.size()==1){
				logger.error("there is just 1 responseconfig on level "+maxlevel+",it is>>"+JSONObject.toJSONString(irs.get(0)));
				return irs.get(0);
			}else{
				logger.error("there are more than 1 responseconfig on level "+maxlevel+",they are>>"+JSONArray.toJSONString(irs));
				return processMutilyResponse(irs);
			}
		}
		
	}
	
	/**
	 * 处理多个相同优先级的响应
	 * @param rcs
	 * @return 合并成一个新的ResponseConfig，指向复杂响应类。
	 */
	private static ResponseConfig processMutilyResponse(List<ResponseConfig> rcs){
		ResponseConfig rc=new ResponseConfig();
		JSONObject params=new JSONObject();
		params.put("params",JSONArray.toJSON(rcs));
		rc.setParam(params);
		//此处固定为直播多响应类
		rc.setResponse(new OnliveMultyResponse());
		logger.error("process mutily responseconfig >>"+JSONObject.toJSONString(rc));
		return rc;
	}
}
