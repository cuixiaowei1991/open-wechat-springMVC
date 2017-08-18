package com.cn.service.impl;

import com.cn.cache.*;
import com.cn.common.LogHelper;
import com.cn.common.WeChatCommon.WXBizMsgCrypt;
import com.cn.common.WeChatCommon.WeiXinDecode;
import com.cn.common.WeChatCommon.commenUtil;
import com.cn.common.httpsPostMethod;
import com.cn.dao.verifyTicketDao;
import com.cn.dao.weChatPublicDao;
import com.cn.entity.openPlatform;
import com.cn.entity.weChatPublic;
import com.cn.service.ThirdTogetPubFuncService;
import com.cn.service.weChatThridService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
@Service
public class weChatThridServiceImpl implements weChatThridService {

    @Value("${token}")
    private String token;
    @Value("${app_key}")
    private String key;
    @Value("${app_id}")
    private String app_id;
    @Value("${app_secret}")
    private String app_secret;
    @Value("${component_access_token_url}")
    private String component_access_token_url;
    @Value("${pre_auth_code_url}")
    private String pre_auth_code_url;
    @Value("${qR_code_url}")
    private String qR_code_url;
    @Value("${redirect_uri}")
    private String redirect_uri;
    @Value("${getopenid_url_secret}")
    private String getopenid_url_secret;
    @Value("${getopenid_url_component}")
    private String getopenid_url_component;
    @Value("${unified_order_url}")
    private String unified_order_url;
    @Value("${publicNum_url}")
    private String publicNum_url;
    @Value("${openid_url}")
    private String openid_url;


    @Autowired
    private verifyTicketDao ticketDao;
    @Autowired
    private weChatPublicDao wpDao;


    /**
     * 获取当前Request对象.
     *
     * @return 当前Request对象 可能为null
     * @throws IllegalStateException 当前线程不是web请求抛出此异常.
     */
    protected static HttpServletRequest currentRequest() throws IllegalStateException {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("当前线程中不存在 Request 上下文");
        }
        return attrs.getRequest();
    }

    private static String bb = null;

    /**
     * 获取第三方平台component_access_token
     * @return
     */
    public String getComponentAccessToken()
    {
        try {
            ComponentVerifyTicket cvt = ComponentAccessTokenMap.get(app_id);
            List<openPlatform> mapList= ticketDao.getVerifyTicketByAppid(app_id);
            String ticket=mapList.get(0).getOpenPlatform_ticket();
            JSONObject componentAccessToken_json = new JSONObject();
            componentAccessToken_json.put("component_appid",app_id);
            componentAccessToken_json.put("component_appsecret",app_secret);
            componentAccessToken_json.put("component_verify_ticket",ticket);
            if(null==cvt)
            {
                String responseStr= httpsPostMethod.sendHttpsPost(component_access_token_url,
                        componentAccessToken_json.toString(), "获取第三方平台component_access_token");
                LogHelper.info("获取第三方平台token微信返回-----------》" + responseStr);
                JSONObject rjson = new JSONObject(responseStr);
                if(rjson.isNull("component_access_token"))
                {
                    return null;
                }
                String component_access_token=rjson.getString("component_access_token");
                long expires_in=rjson.getLong("expires_in");
                ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,component_access_token,expires_in);
                ComponentAccessTokenMap.put(app_id,componentVerifyTicket);
                LogHelper.info("第三方平台component_access_token第一次请求存入-----------》" + ComponentAccessTokenMap.get(app_id).getComponent_access_token());

            }
            else
            {
                if(cvt.component_access_tokenExprise())
                {
                   String responseStr= httpsPostMethod.sendHttpsPost(component_access_token_url,
                            componentAccessToken_json.toString(), "获取第三方平台component_access_token");
                    LogHelper.info("获取第三方平台token微信返回（本地过期）-----------》" + responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    String component_access_token=rjson.getString("component_access_token");
                    long expires_in=rjson.getLong("expires_in");
                    ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,component_access_token,expires_in);
                    ComponentAccessTokenMap.put(app_id, componentVerifyTicket);
                    LogHelper.info("第三方平台component_access_token过期存入-----------》" + ComponentAccessTokenMap.get(app_id).getComponent_access_token());
                }
            }
            return ComponentAccessTokenMap.get(app_id).getComponent_access_token();


        }catch (Exception e)
        {
            LogHelper.error(e,"获取第三方平台component_access_token异常！！！！",false);
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取预授权码pre_auth_code 预授权码用于公众号授权时的第三方平台方安全验证
     * @return
     */
    public String getPreAuthCode()
    {
        ComponentVerifyTicket cvt = PreAuthCodeMap.get(app_id);
        JSONObject PreAuthCode_json = new JSONObject();
        PreAuthCode_json.put("component_appid", app_id);
        try {
            if(null==cvt)
            {
               String responseStr= httpsPostMethod.sendHttpsPost(pre_auth_code_url+"?component_access_token="+getComponentAccessToken(),
                        PreAuthCode_json.toString(),"获取预授权码pre_auth_code（第一次访问）");
                LogHelper.info("获取预授权码pre_auth_code微信返回----------------》" + responseStr);
                JSONObject rjson = new JSONObject(responseStr);
                if(rjson.isNull("pre_auth_code"))
                {
                    return null;
                }
                else
                {
                    long expires_in=rjson.getLong("expires_in");//有效期20分钟
                    ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,expires_in,rjson.getString("pre_auth_code"));
                    PreAuthCodeMap.put(app_id, componentVerifyTicket);
                    LogHelper.info("预授权码pre_auth_code第一次存入缓存-----------》" + PreAuthCodeMap.get(app_id).getPre_auth_code());
                }
            }
            else {
                if (cvt.preauthcode_Exprise()) {
                    String responseStr= httpsPostMethod.sendHttpsPost(pre_auth_code_url+"?component_access_token="+getComponentAccessToken(),
                            PreAuthCode_json.toString(),"获取预授权码pre_auth_code（过期）");
                    LogHelper.info("获取预授权码pre_auth_code微信返回（本地过期）----------------》" + responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    long expires_in=rjson.getLong("expires_in");//有效期20分钟
                    ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,expires_in,rjson.getString("pre_auth_code"));
                    PreAuthCodeMap.put(app_id,componentVerifyTicket);
                    LogHelper.info("预授权码pre_auth_code过期重新存入缓存-----------》" + PreAuthCodeMap.get(app_id).getPre_auth_code());
                }
            }
            return PreAuthCodeMap.get(app_id).getPre_auth_code();
        }
        catch (Exception e)
        {
            LogHelper.error(e,"预授权码pre_auth_code异常！！！！",false);
            e.printStackTrace();;
            return null;
        }
    }

    /**
     * 微信第三方平台授权的入口
     * @return
     */
    public String entranceWinXin() throws Exception
    {
        JSONObject json=new JSONObject();
        String url=qR_code_url+"?component_appid="+app_id+"&pre_auth_code="
                +getPreAuthCode()+"&redirect_uri="+redirect_uri;
        LogHelper.info("返回到前台的地址为：---------------》" + url);
        json.put("qrcode_url",url);
        return json.toString();
    }

    /**
     *微信统一回复接收接口
     * @return
     * @throws Exception
     */
    @Override
    public String commonReturn(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String msg_signature=request.getParameter("msg_signature");
        String timestamp=request.getParameter("timestamp");
        String nonce=request.getParameter("nonce");
        LogHelper.info("签名串------------:" + msg_signature);
        LogHelper.info("时间戳------------:" + timestamp);
        LogHelper.info("随机数------------:" + nonce);
        String appid_reply="";
        try {
            appid_reply = request.getParameter("appid");
            LogHelper.info("传过来的appid为------------------》" + appid_reply);
        } catch (Exception e) {
            LogHelper.error(e,"截取appid时出现异常！！！",false);
        }
        BufferedReader br;
        String postStr = null;
        try {
            br = request.getReader();
            String buffer = null;
            StringBuffer buff = new StringBuffer();
            while ((buffer = br.readLine()) != null) {
                buff.append(buffer + "\n");
            }
            br.close();
            postStr = buff.toString();
            LogHelper.debug("接收-------------（授权后）公众号消息与事件接收---------post发送数据:\n" + postStr);
            //收到消息后，5秒内回复success
            output(response, "success");
            String result_json= WeiXinDecode.decode(token, key, app_id, msg_signature, timestamp, nonce, postStr);
            JSONObject json=new JSONObject(result_json);
            LogHelper.info("微信统一回复接收接口接收微信数据json---------------》" + json);
            String MsgType=json.getString("MsgType");
            String ToUserName=json.getString("ToUserName");//微信公众号
            String FromUserName=json.getString("FromUserName");//用户openid
            String event="";
            if("event".equalsIgnoreCase(MsgType))
            {
                event=json.getString("Event");
            }
            if("text".equalsIgnoreCase(MsgType))
            {
                event="text";
            }
            if("image".equalsIgnoreCase(MsgType))
            {
                event="image";
            }
            WXBizMsgCrypt pc = new WXBizMsgCrypt(token, key, app_id);
            if(ToUserName.equals("gh_3c884a361561"))
            {
                String Content="";
                if(json.toString().contains("Content"))
                {
                    Content=json.getString("Content");
                }
                   if ("event".equals(MsgType)) {
                    event = json.getString("Event");

                    String quanwang_event = event + "from_callback";

                    Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
                    StringBuffer sb = new StringBuffer();
                    sb.append("<xml>");
                    sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
                    sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
                    sb.append("<CreateTime>"+createTime+"</CreateTime>");
                    sb.append("<MsgType><![CDATA[text]]></MsgType>");
                    sb.append("<Content><![CDATA[" + quanwang_event + "]]></Content>");
                    sb.append("</xml>");
                    String replyMsg = sb.toString();
                    String quanwang_event_xml_miwen = pc.encryptMsg(replyMsg, createTime.toString(), request.getParameter("nonce"));
                    LogHelper.info("----bb---------" + bb);
                    String responseStr= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+bb,
                            "{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_event+"\"}}","");
                    String aa_event="{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_event+"\"}}";
                    response.getWriter().write("");
                    response.getWriter().write(quanwang_event_xml_miwen);
                    response.getWriter().flush();
                    response.getWriter().close();
                    //output(response,quanwang_event_xml_miwen);

                }
                else if ("text".equals(MsgType) && !Content.contains("QUERY_AUTH_CODE:")) {

                    String quanwang_content = json.getString("Content");

                    if ("TESTCOMPONENT_MSG_TYPE_TEXT".equals(quanwang_content)) {
                        quanwang_content = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";
                        Long createTime = System.currentTimeMillis() / 1000;
                        StringBuffer sb = new StringBuffer(512);
                        sb.append("<xml>");
                        sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
                        sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
                        sb.append("<CreateTime>"+createTime+"</CreateTime>");
                        sb.append("<MsgType><![CDATA[text]]></MsgType>");
                        sb.append("<Content><![CDATA["+quanwang_content+"]]></Content>");
                        sb.append("</xml>");
                        String replyMsg = sb.toString();
                        LogHelper.info("确定发送的XML为：" + replyMsg);//千万别加密
                        //String quanwang_content_xml = commenUtil.map2xml(hashMap);
                        LogHelper.info("测试全网发布=========text（加密之前）=======" + replyMsg);
                        String quanwang_content_xml_miwen = pc.encryptMsg(replyMsg, createTime.toString(), request.getParameter("nonce"));
                        LogHelper.info("\"测试全网发布=========text（加密之后）=======\"" + quanwang_content_xml_miwen);
                        response.setContentType("text/html;charset=UTF-8");
                        response.getWriter().write("");
                        response.getWriter().flush();
                        response.getWriter().close();
                        response.getWriter().write(quanwang_content_xml_miwen);
                        response.getWriter().flush();
                        response.getWriter().close();
                        LogHelper.info("----bb---------" + bb);
                        String responseStr= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+bb,
                                "{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_content+"\"}}","");
                        String aa_event="{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_content+"\"}}";
                        LogHelper.info("全网发布测试test-----------》" + responseStr + "--------------实例字符串-----------------》" + aa_event);

                        response.getWriter().write(quanwang_content_xml_miwen);
                        response.getWriter().flush();
                        response.getWriter().close();
                        //output(response,quanwang_content_xml_miwen);
                    }


                }
                //3.全网发布第三步
                else if(Content.contains("QUERY_AUTH_CODE:"))

                {
                    output(response, "");
                    String queryAuthCode=Content.substring(16);
                    LogHelper.debug("------截取的QUERY_AUTH_CODE为--------"+queryAuthCode);
                    String responseStr= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+getComponentAccessToken(),
                            "{\"component_appid\":\""+app_id+"\",\"authorization_code\":\""+queryAuthCode+"\"}","全网发布根据微信传的QUERY_AUTH_CODE获取token");
                    JSONObject rjson = new JSONObject(responseStr);
                    String authorization_info=rjson.get("authorization_info")+"";
                    JSONObject js_authorization = new JSONObject(authorization_info);
                    String authorizer_access_token=js_authorization.getString("authorizer_access_token");//授权方token
                    String kefu_response_1= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + authorizer_access_token,
                            "{\"touser\":\"" + FromUserName + "\",\"msgtype\":\"" + "text" + "\",\"text\":{\"content\":\"" + "" + "\"}}", "");
                    bb=authorizer_access_token;
                    LogHelper.info("----bb---------" + bb);
                    response.getWriter().write("");
                    response.getWriter().flush();
                    response.getWriter().close();
                    String kefu_response_2= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + authorizer_access_token,
                            "{\"touser\":\"" + FromUserName + "\",\"msgtype\":\"" + "text" + "\",\"text\":{\"content\":\"" + queryAuthCode+"_from_api" + "\"}}", "");
                   }
            }


        }catch (Exception e)
        {
            LogHelper.error(e,"统一回复接收接口异常！！！",false);
        }
        return null;
    }







    /**
     * 获取用户openid
     * @param source 包含商户app_id 商户appsecret 商户code 券id
     * @return
     */
    public String getOpenId(Map<String, Object> source) throws Exception
    {
        String shop_appid="";
        String code="";
        String getopenid="";
        String openid="";
        try {
            LogHelper.info("获取openid前台传给的参数(根据appid和APPsecret获取openid)-------------》" + source);
            shop_appid=source.get("app_id").toString();
            List<weChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(app_id,shop_appid);
            String appsecret=weChatPublicList.get(0).getWeChatPublic_appid_secert();
            code=source.get("code").toString();
            getopenid = httpsPostMethod.sendHttpsPost(getopenid_url_secret+"?appid="+ shop_appid + "&secret=" + appsecret + "&code=" + code, "&grant_type=authorization_code",
                    "获取获取openid");
            JSONObject openid_js=new JSONObject(getopenid);
            openid=openid_js.getString("openid");

        } catch (Exception e)
        {
            LogHelper.info("获取openid前台传给的参数(根据三方平台ID和三方平台授权token获取openid)-------------》" + source);
            getopenid=httpsPostMethod.sendHttpsPost(getopenid_url_component+"?appid=" + shop_appid + "&code=" + code +
                    "&grant_type=authorization_code&component_appid=" + app_id + "&component_access_token=" + getComponentAccessToken(), "", "");
            JSONObject openid_js= null;
            try {
                openid_js = new JSONObject(getopenid);
                openid=openid_js.getString("openid");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return openid;
    }

    /**
     * 微信支付入口
     * @return
     */
    public String weixinForPay(Map<String, Object> source) throws Exception
    {
        LogHelper.info("---------------进入微信支付接口------------"+source);
        List<weChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(app_id,String.valueOf(source.get("app_id")));
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("appid",String.valueOf(source.get("app_id")));//商户微信公众号appid
        hashMap.put("mch_id",weChatPublicList.get(0).getWechat_num());//商户号
        hashMap.put("device_info","WEB");
        hashMap.put("nonce_str", commenUtil.createSuiJi());
        hashMap.put("body",String.valueOf(source.get("body")));//商户内容
        hashMap.put("out_trade_no",String.valueOf(source.get("out_trade_no")));//本地的商户订单ID
        hashMap.put("fee_type","CNY");
        hashMap.put("total_fee", String.valueOf(source.get("total_fee")));//支付总金额
        if ( currentRequest().getHeader("x-forwarded-for") == null)
        {
            hashMap.put("spbill_create_ip", currentRequest().getRemoteAddr());
        }
        else
        {
            hashMap.put("spbill_create_ip", currentRequest().getHeader("x-forwarded-for"));
        }
        hashMap.put("notify_url","");
        hashMap.put("trade_type","JSAPI");
        hashMap.put("openid",String.valueOf(source.get("openid")));
        String sign=commenUtil.getSign(weChatPublicList.get(0).getWechat_payapi(), hashMap, "utf-8");
        hashMap.put("sign", sign);
        String xml=commenUtil.map2xml(hashMap);
        LogHelper.info("统一下单转换成的map转换成的xml为----------------------》" + xml);
        String responseStr= httpsPostMethod.sendHttpsPost(unified_order_url,xml,"微信支付统一下单");
        JSONObject rjson = new JSONObject(commenUtil.xml2JSON(responseStr));
        String return_code=rjson.getString("return_code");
        String return_msg=rjson.getString("return_msg");
        JSONObject return_json=new JSONObject();
        if("SUCCESS".equals(return_code))
        {
            String result_code=rjson.getString("result_code");
            if("SUCCESS".equalsIgnoreCase(result_code))
            {

                String timeStamp=String.valueOf(System.currentTimeMillis() / 1000);
                String nonceStr=commenUtil.createSuiJi();
                Map<String, String> return_map = new HashMap<>();
                return_map.put("appId",rjson.getString("appid"));
                return_map.put("timeStamp",nonceStr);
                return_map.put("signType","MD5");
                return_map.put("nonceStr",commenUtil.createSuiJi());
                return_map.put("package", "prepay_id=" + rjson.getString("prepay_id"));
                String sign_order=commenUtil.getSign(weChatPublicList.get(0).getWechat_payapi(), return_map, "utf-8");


                return_json.put("appId",rjson.getString("appid"));
                return_json.put("nonceStr",nonceStr);
                return_json.put("signType","MD5");
                return_json.put("timeStamp",timeStamp);
                return_json.put("package","prepay_id="+rjson.getString("prepay_id"));
                return_json.put("paySign",sign);
                return_json.put("rspCode","000");
                return_json.put("rspDesc","下单成功");
            }
            else
            {
                return_json.put("rspCode",rjson.getString("err_code"));
                return_json.put("rspDesc",rjson.getString("err_code_des"));
            }

        }
        else
        {
            return_json.put("rspCode", "321009");
            return_json.put("rspDesc", return_msg);
        }
        return return_json.toString();
    }

    /**
     * 微信支付返回信息
     */
    public void payReturn(HttpServletResponse response) throws Exception
    {
        JSONObject requestJSON = new JSONObject();
        BufferedReader br;
        br = currentRequest().getReader();
        String buffer = null;
        StringBuffer buff = new StringBuffer();
        while ((buffer = br.readLine()) != null) {
             buff.append(buffer + "\n");
        }
            br.close();
        String postStr = buff.toString();
        LogHelper.info("接收-----------------------微信支付-------------post发送数据:\n" + postStr);
        String result_json=commenUtil.xml2JSON(postStr);
        JSONObject rjson = new JSONObject(result_json);
        String return_code=rjson.getString("return_code");
        if("success".equalsIgnoreCase(return_code))
        {
            String result_code=rjson.getString("result_code");
            if("SUCCESS".equalsIgnoreCase(result_code))
            {
                String sign=rjson.getString("sign");
                List<weChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(app_id, rjson.getString("appid"));
                Document doc = DocumentHelper.parseText(postStr);
                Element root = doc.getRootElement();
                Map<String, String> return_map = (Map<String, String>) commenUtil.xml2map(root);
                return_map.remove("sign");
                String sign_local = commenUtil.getSign(weChatPublicList.get(0).getWechat_payapi(), return_map, "utf-8");
                if(sign_local.equals(sign))
                {
                    String result="<xml>\n" +
                            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                            "</xml>";
                    output(response,result);
                    if(weChatPublicList.get(0).getWechat_pay_vasreurl() !=null && !weChatPublicList.get(0).getWechat_pay_vasreurl().equals(""))
                    {
                        JSONObject vas_callback = new JSONObject();
                        vas_callback.put("orderNo",rjson.getString("out_trade_no"));
                        vas_callback.put("weichatOpenid",rjson.getString("openid"));
                        vas_callback.put("remoteOrderNo",rjson.getString("transaction_id"));
                        vas_callback.put("appid",rjson.getString("appid"));
                        vas_callback.put("amount",Long.parseLong(rjson.getString("cash_fee")));
                        httpsPostMethod.postHttp("balance_weichat_callback", vas_callback.toString(), weChatPublicList.get(0).getWechat_pay_vasreurl());
                    }
                }
                else
                {
                    LogHelper.info("支付验签失败！");
                }

            }
        }
    }






    /**
     * 工具类：回复微信服务器"文本消息"
     * @param response
     * @param returnvaleue
     */
    public void output(HttpServletResponse response,String returnvaleue){
        try {
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
//          System.out.println("****************returnvaleue***************="+returnvaleue);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
