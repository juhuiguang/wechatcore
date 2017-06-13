package com.alienlab.wechat.message;

import java.io.Writer;
import java.util.List;

import com.alienlab.common.TypeUtils;
import com.alienlab.wechat.response.bean.BaseInfo;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.Serializers;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.wechat.core.servlet.CoreServlet;
import com.alienlab.wechat.response.bean.ArticleInfo;
import com.alienlab.wechat.response.bean.ArticleObject;
import com.alienlab.wechat.response.bean.TextInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;  

/**
 * 消息处理器
 * @author 	Eric
 *
 */
public class MessageProcessor {
	private static Logger logger = Logger.getLogger(MessageProcessor.class);
	/** 
     * xml转换json
     * @author Eric
     * @param  xml			格式的字符串
     * @return 成功返回json 字符串;失败反回null 
     */  
	@SuppressWarnings("unchecked")
	public static  JSONObject xml2JSON(String xml) {
			logger.info("jsonobject xml to json>>>"+xml);
		// 将解析结果存储在HashMap中
			JSONObject map = new JSONObject();
				// 读取输入流
				SAXReader reader = new SAXReader();
				Document document=null;
				//xml= TypeUtils.dcodeUtf8(xml);
				try {
					document = DocumentHelper.parseText(xml);
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 得到xml根元素
				Element root = document.getRootElement();
				// 得到根元素的所有子节点
				List<Element> elementList = root.elements();

				// 遍历所有子节点
				for (Element e : elementList)
					map.put(e.getName(), e.getText());
				return map;
    }  
	
	/**
	 * 文本消息对象转换成xml
	 * 
	 * @param textMessage 文本消息对象
	 * @return xml
	 */
	public static String textMessageToXml(TextInfo textMessage) {
		xstream.alias("xml", textMessage.getClass());
		xstream.setMode(XStream.NO_REFERENCES);
		return xstream.toXML(textMessage);
	}


	/**
	 * 图文消息对象转换成xml
	 * 
	 * @param newsMessage 图文消息对象
	 * @return xml
	 */
	public static String newsMessageToXml(ArticleInfo newsMessage) {
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new ArticleObject().getClass());
		xstream.setMode(XStream.NO_REFERENCES);
		return xstream.toXML(newsMessage);
	}

	/**
	 * 扩展xstream，使其支持CDATA块
	 * 
	 * @date 2013-05-19
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;

				@SuppressWarnings("unchecked")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
  
//    /** 
//     * 一个迭代方法 
//     * @author Eric
//     * @param element ( : org.jdom.Element )
//     * @return java.util.Map 实例 
//     */  
//    @SuppressWarnings("unchecked")  
//    private static Map  iterateElement(Element element) {  
//        List jiedian = element.getChildren();  
//        Element et = null;  
//        Map obj = new HashMap();  
//        List list = null;  
//        for (int i = 0; i < jiedian.size(); i++) {  
//            list = new LinkedList();  
//            et = (Element) jiedian.get(i);  
//            if (et.getTextTrim().equals("")) {  
//                if (et.getChildren().size() == 0)  
//                    continue;  
//                if (obj.containsKey(et.getName())) {  
//                    list = (List) obj.get(et.getName());  
//                }  
//                list.add(iterateElement(et));  
//                obj.put(et.getName(), list);  
//            } else {  
//                if (obj.containsKey(et.getName())) {  
//                    list = (List) obj.get(et.getName());  
//                }  
//                list.add(et.getTextTrim());  
//                obj.put(et.getName(), list);  
//            }  
//        }  
//        return obj;  
//    }  
  
    // 测试  
    public static void main(String[] args) {  
        System.out.println(  MessageProcessor.xml2JSON("<xml>"
        		+"<ToUserName>"+"<![CDATA[toUser]]>"+"</ToUserName>"
        		 +"<FromUserName>"+"<![CDATA[fromUser]]>"+"</FromUserName> "
        		+ "<CreateTime>"+"1348831860"+"</CreateTime>"
        		+ "<MsgType>"+"<![CDATA[text]]>"+"</MsgType>"
        		+ "<Content>"+"<![CDATA[this is a test]]>"+"</Content>"
        		+ "<MsgId>"+"1234567890123456"+"</MsgId>"
        		+"</xml>"));

		TextInfo t=new TextInfo();
		((TextInfo)t).setContent("测试文本输出");
        t.setToUserName("abc");
		xstream.alias("xml", TextInfo.class);
		String xml=xstream.toXML(t);
		System.out.println(xml);
    }  

}
