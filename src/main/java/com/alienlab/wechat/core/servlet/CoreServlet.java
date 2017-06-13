package com.alienlab.wechat.core.servlet;  
  
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.core.util.SignUtil;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.fans.UserInfoUtil;
import com.alienlab.wechat.fans.bean.WechatUserInfo;
import com.alienlab.wechat.message.MessageFactory;
import com.alienlab.wechat.message.MessageProcessor;
import com.alienlab.wechat.message.bean.BaseMessage;
import com.alienlab.wechat.response.ResponseConfig;
import com.alienlab.wechat.response.ResponseFactory;

  
public class CoreServlet extends HttpServlet {  
    private static final long serialVersionUID = 4440739483644821986L;  
    private static Logger logger = Logger.getLogger(CoreServlet.class);
  
    /** 
     * 确认请求来自微信服务器 
     */  
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        // 微信加密签名  
        String signature = request.getParameter("signature");  
        //System.out.println("request signature:"+signature);
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        //System.out.println("request timestamp:"+timestamp);
        // 随机数  
        String nonce = request.getParameter("nonce");  
       // System.out.println("request nonce:"+nonce);
        // 随机字符串  
        String echostr = request.getParameter("echostr");  
        //System.out.println("request echostr:"+echostr);
        
        PrintWriter out = response.getWriter();  
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
        	logger.info("微信服务端验证通过，确认请求来自微信端。");
            out.print(echostr);  
        }  
        out.close();  
        out = null;  
    }  
  
    /** 
     * 处理微信服务器发来的消息 
     */  
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        // TODO 消息的接收、处理、响应  
    	// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		// 微信加密签名  
        String signature = request.getParameter("signature");  

        // 时间戳  
        String timestamp = request.getParameter("timestamp");  

        // 随机数  
        String nonce = request.getParameter("nonce");  

        PrintWriter out = response.getWriter();
        //验证合法性
        if (true) {
        	logger.info("Message from wechat server>>>>>");
        	logger.info("wechat>request:"+request.getInputStream().toString());
     		//1、处理收到的消息
        	InputStream inputStream = request.getInputStream();
        	String requestxml = IOUtils.toString(inputStream,"utf-8");
        	JSONObject jomsg=MessageProcessor.xml2JSON(requestxml);
        	logger.info("wechat step1>jsonobject:"+jomsg.toString());
        	//2、将消息转为消息对象
        	BaseMessage msg=MessageFactory.getMessage(jomsg);
        	logger.info("wechat step2>BaseMessage:"+msg.getClass().toString());
        	//3、通过消息对象获得响应类
        	ResponseConfig res=ResponseFactory.getResponse(msg);
        	//4、执行响应前方法
        	if(res!=null&&res.getResponse()!=null){
        		logger.info("wechat step3>IResponse:"+res.getClass().toString());
        		res.getResponse().preResponse(msg,res.getParam());
        		logger.info("wechat step4>preResponse:");
        		/** 
        		 * 修改
        		 * 记录活跃
        		 * @author Eric
        		 * @Date:2016年5月10日下午5:34:30
        		 */
        		/**************************/
				/*String accessToken = WeixinUtil.getAccessToken("wxd5ceca5e98cb548d", "1b0c71ebb41177193b7dd1d49e534e4f")
						.getToken();
				WechatUserInfo user = UserInfoUtil.getUserInfo(accessToken, jomsg.get("FromUserName").toString());
				logger.info("wechat userinfo:");
				out.println("OpenID：" + user.getOpenId());
				out.println("关注状态：" + user.getSubscribe());
				out.println("关注时间：" + user.getSubscribe_time());
				out.println("昵称：" + user.getNickname());
				out.println("性别：" + user.getSex());
				out.println("国家：" + user.getCountry());
				out.println("省份：" + user.getProvince());
				out.println("城市：" + user.getCity());
				out.println("语言：" + user.getLanguage());
				out.println("头像：" + user.getHeadImgUrl());*/
        		/**************************/
        		
        		//5、执行响应
            	String result=res.getResponse().doResponse(msg,res.getParam());
            	logger.info("wechat step5>result:"+result);
            	out.print(result);	
            	//6、执行响应后方法
            	res.getResponse().afterResponse(msg,res.getParam());
            	logger.info("wechat step6>afterResponse:");
        	}else{
        		out.print("");	
        		logger.info("wechat step5>no response!!!!");
        	}
        }else{
        	logger.error("message validate from wechat is not correct.");
     		out.print("请求消息不合法！");
        }
        out.close();
    }
  
}  