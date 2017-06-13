/**
 * @author  Eric
 * @Date:2016年4月26日下午5:04:30
 * @version 1.0
 */
package com.alienlab.wechat.fans;

import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.utils.HttpsInvoker;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.fans.bean.WechatUserInfo;

public class UserInfoUtil {
	private static Logger logger = Logger.getLogger(UserInfoUtil.class);

	/**
	 * 获取用户信息
	 * 
	 * @author Eric
	 * @param accessToken 接口访问凭证
	 * @param openId  用户标识
	 * @return WechatUserInfo
	 */
	public static WechatUserInfo getUserInfo(String accessToken, String openId) {
		WechatUserInfo WechatUserInfo = null;
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		// 获取用户信息
		JSONObject jsonObject = HttpsInvoker.httpRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			try {
				WechatUserInfo = new WechatUserInfo();
				// 用户的标识
				WechatUserInfo.setOpenId(jsonObject.getString("openid"));
				// 关注状态
				WechatUserInfo.setSubscribe(jsonObject.getInteger("subscribe"));
				// 用户关注时间
				WechatUserInfo.setSubscribe_time(jsonObject.getString("subscribe_time"));
				// 昵称
				WechatUserInfo.setNickname(jsonObject.getString("nickname"));
				// 性别
				WechatUserInfo.setSex(jsonObject.getInteger("sex"));
				// 国家
				WechatUserInfo.setCountry(jsonObject.getString("country"));
				// 省份
				WechatUserInfo.setProvince(jsonObject.getString("province"));
				// 城市
				WechatUserInfo.setCity(jsonObject.getString("city"));
				// 语言zh_CN
				WechatUserInfo.setLanguage(jsonObject.getString("language"));
				// 头像
				WechatUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
			} catch (Exception e) {
				if (0 == WechatUserInfo.getSubscribe()) {
					logger.error("用户{}已取消关注");
				} else {
					
					logger.error("获取用户信息失败 errcode:{} errmsg:{}");
				}
			}
		}

		return WechatUserInfo;
	}
	
	
	//测试
	public static void main(String args[]) {
		// 获取接口访问凭证
		// AccessToken at=WeixinUtil.getAccessToken("wxd5ceca5e98cb548d",
		// "1b0c71ebb41177193b7dd1d49e534e4f");
		String accessToken = WeixinUtil.getAccessToken("配置appid", "配置appsecret")
				.getToken();
		/**
		 * 获取用户信息
		 */
		WechatUserInfo user = getUserInfo(accessToken, "配置openid");
		System.out.println("OpenID：" + user.getOpenId());
		System.out.println("关注状态：" + user.getSubscribe());
		System.out.println("关注时间：" + user.getSubscribe_time());
		System.out.println("昵称：" + user.getNickname());
		System.out.println("性别：" + user.getSex());
		System.out.println("国家：" + user.getCountry());
		System.out.println("省份：" + user.getProvince());
		System.out.println("城市：" + user.getCity());
		System.out.println("语言：" + user.getLanguage());
		System.out.println("头像：" + user.getHeadImgUrl());
		
		/**
		logger.info("OpenID：" + user.getOpenId());
		logger.info("关注状态：" + user.getSubscribe());
		logger.info("关注时间：" + user.getSubscribeTime());
		logger.info("昵称：" + user.getNickname());
		logger.info("性别：" + user.getSex());
		logger.info("国家：" + user.getCountry());
		logger.info("省份：" + user.getProvince());
		logger.info("城市：" + user.getCity());
		logger.info("语言：" + user.getLanguage());
		logger.info("头像：" + user.getHeadImgUrl());
		*/
	}

}
