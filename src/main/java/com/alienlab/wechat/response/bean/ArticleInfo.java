package com.alienlab.wechat.response.bean;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.NONE)
public class ArticleInfo extends BaseInfo {
	// 接收方帐号（收到的OpenID）
	private String ToUserName;
	// 开发者微信号
	private String FromUserName;
	// 消息创建时间 （整型）
	private long CreateTime;
	// 消息类型（text/music/news）
	private String MsgType;
	// 位0x0001被标志时，星标刚收到的消息
	private int FuncFlag;
	// 图文消息个数，限制为10条以内
			private int ArticleCount;
			// 多条图文消息信息，默认第一个item为大图
			private List<ArticleObject> Articles;

	@Override
	public String getToUserName() {
		return ToUserName;
	}

	@Override
	public void setToUserName(String toUserName) {
		super.setToUserName(toUserName);
		ToUserName = toUserName;
	}

	@Override
	public String getFromUserName() {
		return FromUserName;
	}

	@Override
	public void setFromUserName(String fromUserName) {
		super.setFromUserName(fromUserName);
		FromUserName = fromUserName;
	}

	@Override
	public long getCreateTime() {
		return CreateTime;
	}

	@Override
	public void setCreateTime(long createTime) {
		super.setCreateTime(createTime);
		CreateTime = createTime;
	}

	@Override
	public String getMsgType() {
		return MsgType;
	}

	@Override
	public void setMsgType(String msgType) {
		super.setMsgType(msgType);
		MsgType = msgType;
	}

	@Override
	public int getFuncFlag() {

		return FuncFlag;
	}

	@Override
	public void setFuncFlag(int funcFlag) {
		super.setFuncFlag(funcFlag);
		FuncFlag = funcFlag;
	}

	public int getArticleCount() {
		return ArticleCount;
	}

	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}

	public List<ArticleObject> getArticles() {
		return Articles;
	}

	public void setArticles(List<ArticleObject> articles) {
		Articles = articles;
	}

	@Override
			public String getPushInfo(){
//				JSONObject newsinfo=new JSONObject();
//				newsinfo.put("touser", this.getToUserName());
//				newsinfo.put("msgtype","news");
//				newsinfo.put("news",JSONObject.toJSON(this));
//				return newsinfo.toJSONString();
				return "";
			}
}
