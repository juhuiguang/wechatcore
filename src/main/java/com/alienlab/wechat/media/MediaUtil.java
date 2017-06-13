package com.alienlab.wechat.media;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alienlab.utils.PropertyConfig;
import com.alienlab.wechat.bean.AccessToken;
import com.alienlab.wechat.core.util.WeixinUtil;
import com.alienlab.wechat.media.bean.MediaObject;

public class MediaUtil {
	private static Logger logger = Logger.getLogger(MediaUtil.class);
	private static Map<String,MediaObject> medias=new HashMap<String,MediaObject>();
	
	public static boolean addMedia(String mediaId,String type){
		if(medias.containsKey(mediaId)){
			return false;
		}else{
			MediaObject m=new MediaObject(mediaId,type);
			medias.put(m.getMediaid(), m);
			return true;
		}
	}
	
	
	public static MediaObject getMedia(String mediaId,String type){
		if(medias.containsKey(mediaId)){
			return medias.get(mediaId);
		}else{
			MediaObject m=new MediaObject(mediaId,type);
			medias.put(m.getMediaid(), m);
			return m;
		}
	}
	
	public static String getWechatLink(String mediaid){
		PropertyConfig pc=new PropertyConfig("sysConfig.properties");
		 //获取访问授权
	    String wxappid=pc.getValue("wxappid");
		String wxappsecret=pc.getValue("wxappsecret");
		AccessToken at=WeixinUtil.getAccessToken(wxappid, wxappsecret);

        String accessToken = at.getToken();

        InputStream is = null;
        String img_download_url="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        String url = img_download_url.replace("ACCESS_TOKEN",accessToken).replace("MEDIA_ID",mediaid);
        return url;
	}
}
