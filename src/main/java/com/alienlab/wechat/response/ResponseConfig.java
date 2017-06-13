package com.alienlab.wechat.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.message.bean.EmenuMessage;
import com.alienlab.wechat.message.bean.EqrMessage;
import com.alienlab.wechat.message.bean.TextMessage;

/**
 * 消息响应配置
 * @author 橘
 *
 */
public class ResponseConfig {
	private static Logger logger = Logger.getLogger(ResponseConfig.class);
	private String configId="config";
	private int configlevel=1;
	private JSONObject param=new JSONObject();
	
	public JSONObject getParam() {
		return param;
	}
	public void setParam(JSONObject param) {
		this.param = param;
	}
	public int getConfiglevel() {
		return configlevel;
	}
	public void setConfiglevel(int configlevel) {
		this.configlevel = configlevel;
	}
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}

	/**
	 * 消息类型
	 */
	private String msgType;
	private String eventType;
	/**
	 * 发送用户
	 */
	private String useropenid;
	/**
	 * 消息开始时间
	 */
	private String timeStart;
	/**
	 * 消息结束时间
	 */
	private String timeEnd;
	/**
	 * 消息关键字
	 * 文本消息为关键字
	 * 扫码事件为二维码key
	 * 菜单点击为菜单key
	 */
	private String keywords;
	private String eventkey;
	/**
	 * 对应的响应实现类
	 */
	private IResponse response;
	
	
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventkey() {
		return eventkey;
	}
	public void setEventkey(String eventkey) {
		this.eventkey = eventkey;
	}
	public static List<ResponseConfig> get_config() {
		return _config;
	}
	public static void set_config(List<ResponseConfig> _config) {
		ResponseConfig._config = _config;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getUseropenid() {
		return useropenid;
	}
	public void setUseropenid(String useropenid) {
		this.useropenid = useropenid;
	}
	public String getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}
	public String getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public IResponse getResponse() {
		return response;
	}
	public void setResponse(IResponse response) {
		this.response = response;
	}
	
	public boolean isMatched(BaseMessage msg){
		//当前时间
		//logger.info("ResponseConfig:get message:"+JSONObject.toJSONString(msg));
		
		String openid=msg.getFromUserName();
		//判断用户匹配
		if((!(this.getUseropenid().equals("ALL")))&&(this.getUseropenid().indexOf(openid)<0)){
			//logger.info("ResponseConfig:not correct user..");
			return false;
		}
		if(!(this.getTimeStart()==null||this.getTimeStart().equals(""))){
			String time=TypeUtils.getTime();
			if(time.compareTo(this.getTimeStart())<0){
				//logger.info("ResponseConfig:early than starttime.."+time+"<"+this.getTimeStart());
				return false;
			}
		}
		if(!(this.getTimeEnd()==null||this.getTimeEnd().equals(""))){
			String time=TypeUtils.getTime();
			if(time.compareTo(this.getTimeEnd())>0){
				//logger.error("ResponseConfig:late than endtime.."+time+">"+this.getTimeEnd());
				//过期的响应从列表中删除
				ResponseConfig.removeConfig(this.getConfigId());
				return false;
			}
		}
		//判断消息类型匹配
		if(this.getMsgType().equalsIgnoreCase("ALL")||this.getMsgType().indexOf(msg.getMsgType())>=0){
			//logger.info("ResponseConfig:msg type is matched "+this.getMsgType());
			//如果是文本消息，增加判断关键字是否匹配
			if(msg.getMsgType().equals("text")){
				String content=((TextMessage)msg).getContent();
				if((this.getKeywords()!=null&&!this.getKeywords().equals("")&&!this.getKeywords().equalsIgnoreCase("ALL"))){
					if(this.getKeywords().indexOf(content)<0){
						return false;
					}
				}
			}else if(msg.getMsgType().equals("event")){
				String type=msg.getClass().toString();
				if(type.indexOf("EqrMessage")>=0){
					EqrMessage qrmessage=(EqrMessage)msg;
					String eventkey=this.getEventkey();
					//logger.info("compare event type>>>config is "+eventkey+",the message is "+qrmessage.getEvent());
					if(eventkey==null || eventkey.equals("")){
						return false;
					}
					if(eventkey.indexOf(qrmessage.getEvent())>=0){
						logger.error("event qr matched");
						return true;
					}else{
						//logger.info("event qr not matched");
						return false;
					}
				}else if(type.indexOf("EmenuMessage")>=0){
					EmenuMessage em=(EmenuMessage)msg;
					if(this.getEventkey()!=null&&!this.getEventkey().equals("")){
						if(em.getEvent().indexOf(this.getEventkey())>=0){
							logger.error("event menu matched");
							return true;
						}
					}
					return true;
				}else{
					return false;
				}
			}
			return true;
		}else{
			logger.error("ResponseConfig:msg type is not matched "+this.getMsgType());
			System.out.println();
			return false;
		}
	}
	/**
	 * 响应配置列表
	 * 可动态维护
	 */
	private static List<ResponseConfig> _config=new ArrayList<ResponseConfig>();
	public static void initConfig(){
		
	}
	
	/**
	 * 增加一项config
	 * @param config
	 */
	public static void addConfig(ResponseConfig config){
		logger.info("add ResponseConfig>>>"+JSON.toJSONString(config));
		if(config.getConfigId().equals("config")){
			config.setConfigId("tmp_config_"+(_config.size()+1));
		}
		_config.add(config);
	}
	
	/**
	 * 删除一项config
	 * @param id
	 */
	public static void removeConfig(String id){
		logger.info("prepare for delete config "+ id);
		for(int i=0;i<_config.size();i++){
			ResponseConfig c=_config.get(i);
			if(c.getConfigId().equals(id)){
				logger.info("find config at pos "+ i);
				_config.remove(i);
				logger.error(JSON.toJSONString(_config));
				break;
			}
		}
	}
	
	/**
	 * 获得当前消息响应配置列表
	 * @return
	 */
	public static List<ResponseConfig> getConfig(){
		return _config;
	}
	static{
		initConfig();
	}
	
	public static ResponseConfig getConfigById(String configid){
		for(int i=0;i<_config.size();i++){
			if(configid.equals(_config.get(i).getConfigId())){
				return _config.get(i);
			}
		}
		return null;
	}
	
}
