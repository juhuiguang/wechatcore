package com.alienlab.wechat.picbox;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.DAO;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by 橘 on 2016/7/24.
 */
public class PicUploadServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(PicUploadServlet.class);
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = this.getServletContext();
        PicService service=new PicService();
        System.out.println(">>>>>>>>>>>>>>>>>>servlet/upload>>>>>>>>>>>>>>>>>>>>>>>>");
        try{
            String filename="";
            System.out.println(request.getHeader("referer"));
            String refer=request.getHeader("referer");
            if(refer.indexOf("wechatcore/picbox")<0){
                return;
            }

            String path=context.getRealPath("/picbox/uploadimages");
//            String img=request.getParameter("img");
////            if(paramimg!=null){
////                filename=service.base64ToImage(paramimg,path);
////            }else{
//            if(img==null){
//                String param= IOUtils.toString(request.getInputStream(),"UTF-8");
//                System.out.println("param>>>"+param);
//                JSONObject params=JSONObject.parseObject(param);
//                filename=service.base64ToImage(URLDecoder.decode(params.getString("img"),"UTF-8"),path);
//            }else{
//                System.out.println("img>>>"+img);
//                filename=service.base64ToImage(URLDecoder.decode(img,"UTF-8"),path);
//            }
////            }
            String file=IOUtils.toString(request.getReader());
            filename=service.base64ToImage(URLDecoder.decode(file,"UTF-8"),path);
            System.out.println("filename>>>"+filename);
            response.getWriter().write(filename);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
