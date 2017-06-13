package com.alienlab.wechat.core.util;

import java.awt.Menu;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.utils.HttpsInvoker;
import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.bean.AccessToken;
import com.alienlab.wechat.bean.JSApiTicket;
import org.springframework.web.client.RestTemplate;


public class WeixinUtil {
	private static Logger logger = Logger.getLogger(WeixinUtil.class);
	private static PropertyConfig pc=new PropertyConfig("sysConfig.properties");
	
	// 获取access_token的接口地址（GET） 限200（次/天）  
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";  
	// 菜单创建（POST） 限100（次/天）  
	public final static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN"; 
	
	public final static String menu_get_url="https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	
	public final static String page_access_token="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=APPSECRET&code=CODE&grant_type=authorization_code";
	
	public final static String get_fan_info="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	//获取网页版本用户信息
	public final static String get_fan_info_web="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	
	public final static String get_fan_all="https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
	
	//参数二维码请求URL
	public final static String ticketUrl = " https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
	
	//JSAPI请求URL
	public final static String jsapi_ticket_url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	//从服务器下载上传的图片
	public final static String img_download_url="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	
	
	public static Map<String, String> getJsapiSignature(String url){
		logger.info("获得微信js-SDK加密signature:"+url);
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		JSApiTicket jt=getJSApiTicket(wxappid,wxappsecret);
		if(jt==null){
			return null;
		}
		String jsapi_ticket=jt.getTicket();
		logger.info("获得jsapi_ticket："+jsapi_ticket);
		Map map=Sign.sign(jsapi_ticket, url);
		map.put("appid", wxappid);
		return map;
	}
	
	private static JSApiTicket jsticket=null;
	public static JSApiTicket getJSApiTicket(String appid,String secret){
		logger.info("获取微信jsticket");
		if(jsticket== null){
			logger.info("系统中jsticket不存在！");
			jsticket=getJsApiTicket(appid,secret);
		}else{
			Calendar c=Calendar.getInstance();
			long now=c.getTimeInMillis();
			if(now-jsticket.getTicketTime()>=7000*1000){
				logger.info("系统中jsticket已超时！gettoken时间："+jsticket.getTicketTime()+",当前时间:"+now);
				jsticket=getJsApiTicket(appid,secret);
			}else{
				logger.info("系统中jsticket未过期可使用");
			}
		}
		return jsticket;
	}
	
	private static JSApiTicket getJsApiTicket(String appid,String secret){
		logger.info("微信服务号获取新JSApiTicket");
		HttpsInvoker invoker=new HttpsInvoker();
		JSApiTicket jt=null;
		AccessToken at=getAccessToken(appid,secret);
		if(at==null){
			return jt;
		}
		String requestUrl = jsapi_ticket_url.replace("ACCESS_TOKEN", at.getToken());
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				jt = new JSApiTicket();
				jt.setErrcode(jsonObject.getString("errcode"));
				jt.setErrmsg(jsonObject.getString("errmsg"));
				jt.setExpires_in(jsonObject.getString("expires_in"));
				jt.setTicket(jsonObject.getString("ticket"));
				Calendar c=Calendar.getInstance();
				jt.setTicketTime(c.getTimeInMillis());
			} catch (JSONException e) {
				jt = null;
				// 获取token失败
				logger.error("获取JSApiTicket失败 errcode:"+jsonObject.getString("errcode")+"errmsg:"+jsonObject.getString("errmsg"));
			}
		}
		return jt;
	}
	
	/**
	 * 获取access_token
	 * 
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	private static AccessToken accessToken = null;
	public static AccessToken getAccessToken(String appid, String appsecret) {
		logger.info("获取微信AccessToken");
		String tokenurl=pc.getValue("wechattokenurl");
		if(tokenurl!=null&&tokenurl.length()>0){
			logger.info("get remote wechat accessToken");
			RestTemplate restTemplate=new RestTemplate();
			AccessToken at=restTemplate.getForObject(tokenurl,AccessToken.class);
			logger.info("get remote wechat accessToken>>"+at.getToken());
			return at;
		}
		logger.info("no remote token config,get token with appid and secret");
		WeixinUtil wu=new WeixinUtil();
		if(accessToken== null){
			logger.info("系统中token不存在！");
			accessToken=wu.getToken(appid,appsecret);
		}else{
			Calendar c=Calendar.getInstance();
			long now=c.getTimeInMillis();
			if(now-accessToken.getTokenTime()>=7000*1000){
				logger.info("系统中token已超时！gettoken时间："+accessToken.getTokenTime()+",当前时间:"+now);
				accessToken=wu.getToken(appid,appsecret);
			}else{
				logger.info("系统中token未过期可使用");
			}
		}
		return accessToken;

	}
	
	public AccessToken getToken(String appid,String appsecret){
		logger.info("微信服务号获取新token");
		AccessToken at=null;
		String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		HttpsInvoker invoker=new HttpsInvoker();
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				at = new AccessToken();
				at.setToken(jsonObject.getString("access_token"));
				at.setExpiresIn(jsonObject.getIntValue("expires_in"));
				Calendar c=Calendar.getInstance();
				at.setTokenTime(c.getTimeInMillis());
			} catch (JSONException e) {
				at = null;
				// 获取token失败
				logger.error("获取token失败 errcode:"+jsonObject.getString("errcode")+"errmsg:"+jsonObject.getString("errmsg"));
			}
		}
		return at;
	}
	
	/**
	 * 创建菜单
	 * 
	 * @param menu 菜单实例
	 * @param accessToken 有效的access_token
	 * @return 0表示成功，其他值表示失败
	 */
	public static int createMenu(JSONObject menu, String accessToken) {
		int result = 0;
		// 拼装创建菜单的url
		String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);
		// 将菜单对象转换成json字符串
		String jsonMenu = menu.toJSONString();
		HttpsInvoker invoker=new HttpsInvoker();
		// 调用接口创建菜单
		JSONObject jsonObject = HttpsInvoker.httpRequest(url, "POST", jsonMenu);

		if (null != jsonObject) {
			if (0 != jsonObject.getIntValue("errcode")) {
				result = jsonObject.getIntValue("errcode");
				logger.error("创建菜单失败 errcode:"+jsonObject.getIntValue("errcode")+",errmsg:"+jsonObject.getString("errmsg"));
			}
		}

		return result;
	}
	
	public static String getMenu(){
		// 拼装url
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		AccessToken at=WeixinUtil.getAccessToken(wxappid, wxappsecret);
		String url = menu_get_url.replace("ACCESS_TOKEN", at.getToken());
		// 调用接口创建菜单
		JSONObject jsonObject = HttpsInvoker.httpRequest(url, "POST","");
		
		logger.info("Get Menu:"+jsonObject.toString());
		return jsonObject.toString();
	}
	
	public static JSONObject getWxUserBase(String code){
		if(code.equals(""))return null;
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		String requestUrl = page_access_token.replace("APPID", wxappid).replace("APPSECRET", wxappsecret).replace("CODE", code);
		logger.info("发出获取Openid请求，code为:"+code);
		HttpsInvoker invoker=new HttpsInvoker();
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "GET","");
		return jsonObject;
	}
	
	public static String getPageOpenId(String code){
		if(code.equals(""))return "";
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		String requestUrl = page_access_token.replace("APPID", wxappid).replace("APPSECRET", wxappsecret).replace("CODE", code);
		logger.info("发出获取Openid请求，code为:"+code);
		HttpsInvoker invoker=new HttpsInvoker();
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "GET","");
		String openid=jsonObject.getString("openid");
		if(openid!=null){
			logger.info("获得到openid为："+openid);
			return openid;
		}else{
			logger.info("请求openid失败："+jsonObject.toString());
			return "";
		}
		
	}
	
	/**
	 * 生成字符串永久二维码
	 * @param ecode_id
	 * @return
	 */
	public static String getEcodeTicket(String ecode_id){
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		AccessToken at=getAccessToken(wxappid,wxappsecret);
		String requestUrl = ticketUrl.replace("TOKEN", at.getToken());
		String post="{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": "+ecode_id+"}}}";
		HttpsInvoker invoker=new HttpsInvoker();
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "POST", post);
		logger.info("查询带参数的二维码ticket：二维码编号="+ecode_id+",取得信息："+jsonObject.toString());
		try{
			if(jsonObject.containsKey("errcode")){
				return "";
			}else{
				return jsonObject.get("ticket").toString();
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			return "";
		}
		
	}
	
	
	
	
	private static JSONObject getWebAccessToken(String code){
		if(code.equals(""))return null;
		String headimgurl="";
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		String requestUrl = page_access_token.replace("APPID", wxappid).replace("APPSECRET", wxappsecret).replace("CODE", code);
		logger.info("发出获取页面授权的code，code为:"+code);
		HttpsInvoker invoker=new HttpsInvoker();
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "GET","");
		try{
			if(!jsonObject.containsKey("errcode")){
				return jsonObject;
			}else{
				logger.error("通过网页授权获取token:"+jsonObject.toString());
				return null;
			}
		}catch(Exception e){
			logger.error("网页授权获取token失败："+e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获得微信用户基本信息
	 * @param openid
	 * @param accesstoken
	 * @return
	 */
	public static Map<String,String> getWxinfo(String openid,String accesstoken){
		String url=get_fan_info_web.replace("ACCESS_TOKEN", accesstoken).replace("OPENID", openid);
		JSONObject jsonInfo = HttpsInvoker.httpRequest(url, "GET", null);
		logger.info("通过网页授权查询关注者信息：openid="+openid+",取得信息："+jsonInfo.toString());
		Map maps=new HashMap();
		if(jsonInfo.containsKey("errcode")){
			logger.error("未获取网页授权token:"+jsonInfo.toString());
			maps=new HashMap<String, String>();
			maps.put("openid", openid);
			return maps;
		}else{
			maps=new HashMap<String, String>();
			String headimgurl=TypeUtils.getString(jsonInfo.get("headimgurl"));
			String webopenid=TypeUtils.getString(jsonInfo.get("openid"));
			String nickname=TypeUtils.getString(jsonInfo.get("nickname"));
			maps.put("headimgurl", headimgurl);
			maps.put("openid", webopenid);
			maps.put("nickname", nickname);
			maps.put("sex", TypeUtils.getString(jsonInfo.get("sex")));
			maps.put("province", TypeUtils.getString(jsonInfo.get("province")));
			maps.put("city", TypeUtils.getString(jsonInfo.get("city")));
			maps.put("country", TypeUtils.getString(jsonInfo.get("country")));
			maps.put("privilege", TypeUtils.getString(jsonInfo.get("privilege")));
			maps.put("unionid", TypeUtils.getString(jsonInfo.get("unionid")));
			
			return maps;		
		}
	}
	
	/**
	 * 通过code换取网页授权access_token  获取用户基本信息  无调用限制
	 * @param code  页面授权的code
	 * @return 
	 * @return
	 */
	public  static Map<String,String> getHeadimgurl(String code){
		Map<String,String> maps=null;
		if(code.equals(""))return maps;
		JSONObject jsonObject =getWebAccessToken(code);
		try{
			if(jsonObject==null){
				logger.error("未获取网页授权token");
				return maps;
			}else{
				if(!jsonObject.containsKey("errcode")){
					String access_token=TypeUtils.getString(jsonObject.get("access_token"));
					String openid=TypeUtils.getString(jsonObject.get("openid"));
					String url=get_fan_info_web.replace("ACCESS_TOKEN", access_token).replace("OPENID", openid);
					JSONObject jsonInfo = HttpsInvoker.httpRequest(url, "GET", null);
					logger.info("通过网页授权查询关注者信息：openid="+openid+",取得信息："+jsonInfo.toString());
					if(jsonInfo.containsKey("errcode")){
						logger.error("未获取网页授权token:"+jsonInfo.toString());
						maps=new HashMap<String, String>();
						maps.put("openid", openid);
						return maps;
					}else{
						maps=new HashMap<String, String>();
						String headimgurl=TypeUtils.getString(jsonInfo.get("headimgurl"));
						String webopenid=TypeUtils.getString(jsonInfo.get("openid"));
						String nickname=TypeUtils.getString(jsonInfo.get("nickname"));
						maps.put("headimgurl", headimgurl);
						maps.put("openid", webopenid);
						maps.put("nickname", nickname);
						return maps;		
					}
				}else{
					logger.error("未获取网页授权token:"+jsonObject.toString());
					return maps;
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("通过网页授权获取用户信息失败"+e);
			return maps;
		}
       
	}
	
	public static String getMediaUrl(String mediaId){
		 //获取访问授权
	    String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		AccessToken at=WeixinUtil.getAccessToken(wxappid, wxappsecret);
        String accessToken = at.getToken();
        String url = img_download_url.replace("ACCESS_TOKEN",accessToken).replace("MEDIA_ID",mediaId);
        return url;
	}
	
	/**

	    * 根据文件id下载文件

	    * 

	    * @param mediaId

	    *            媒体id

	    * @throws Exception

	    */

	   public  static InputStream getInputStream(String mediaId) {
	        InputStream is = null;
	        String url = getMediaUrl(mediaId);
	       try {
	           URL urlGet = new URL(url);
	           HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
	           http.setRequestMethod("GET"); // 必须是get方式请求
	           http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	           http.setDoOutput(true);
	           http.setDoInput(true);
	           System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
	           System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
	           http.connect();
	           // 获取文件转化为byte流
	           is = http.getInputStream();
	       } catch (Exception e) {
	    	   logger.info("从微信服务器下载文件失败");
	           e.printStackTrace();
	       }
	       return is;

	   }
	public static String getAuthUrl(String url,String state,String type){
		String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		String key="wxposturl";
		if(!type.equals("base")){
			key="wxauthposturl";
		}
		String baseurl=pc.getValue(key);
		baseurl=baseurl.replace("$(APPID)", wxappid);
		if(state==null)state="null";
		baseurl=baseurl.replace("$(STATE)", state);
		try {
			url=URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		baseurl=baseurl.replace("$(TARGETURL)",url);
		return baseurl;
	}
	
	public static void main(String [] arg){
		try{
			AccessToken at=WeixinUtil.getAccessToken("","");
		}catch (Exception ex){
			ex.printStackTrace();
		}

	}
}
