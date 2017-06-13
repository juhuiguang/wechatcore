package com.alienlab.wechat.picbox;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

/**
 * Created by 橘 on 2016/7/13.
 */
@Controller
public class PictureController {
    @Autowired
    PicService service;
    @Autowired
    ServletContext context;
    @RequestMapping(value="/picboxservlet/upload",method= RequestMethod.POST)
    @ResponseBody
    public String savePic(HttpServletRequest request){
        System.out.println(">>>>>>>>>>>>>>>>>>picbox/upload>>>>>>>>>>>>>>>>>>>>>>>>");
        try{
            String filename="";

            String refer=request.getHeader("referer");
            System.out.println("refer>>>"+refer);

            if(refer.indexOf("wechatcore/picbox/")<0){
                System.out.println("indexof"+refer.indexOf("wechatcore/picbox/"));
                return "";
            }
            String path=context.getRealPath("/picbox/uploadimages");
            String img=request.getParameter("img");
//            if(paramimg!=null){
//                filename=service.base64ToImage(paramimg,path);
//            }else{
                if(img==null){
                    String param=IOUtils.toString(request.getReader());
                    //System.out.println("param>>>"+param);
                    JSONObject params=JSONObject.parseObject(param);
                    filename=service.base64ToImage(URLDecoder.decode(params.getString("img"),"UTF-8"),path);
                }else{
                    //System.out.println("img>>>"+img);
                    filename=service.base64ToImage(URLDecoder.decode(img,"UTF-8"),path);
                }
//            }

            System.out.println("filename>>>"+filename);
            return filename;
        }catch (Exception ex){
            ex.printStackTrace();

            return "";
        }


    }
}
