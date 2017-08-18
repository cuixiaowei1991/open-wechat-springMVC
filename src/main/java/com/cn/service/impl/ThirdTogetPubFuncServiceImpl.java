package com.cn.service.impl;

import com.cn.MsgCode.MsgAndCode;
import com.cn.cache.AuthorizerInfo;
import com.cn.cache.AuthorizerInfoMap;
import com.cn.cache.JsapiticketInfo;
import com.cn.cache.JsapiticketInfoMap;
import com.cn.common.CommonHelper;
import com.cn.common.LogHelper;
import com.cn.common.WeChatCommon.WeiXinDecode;
import com.cn.common.WeChatCommon.commenUtil;
import com.cn.common.httpsPostMethod;
import com.cn.dao.verifyTicketDao;
import com.cn.dao.weChatPublicDao;
import com.cn.entity.openPlatform;
import com.cn.entity.weChatPublic;
import com.cn.service.ThirdTogetPubFuncService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.cn.common.CommonHelper.map2json;

/**
 * Created by cuixiaowei on 2017/2/28.
 */
@Service
public class ThirdTogetPubFuncServiceImpl implements ThirdTogetPubFuncService {
    @Autowired
    private weChatThridServiceImpl weChatThridService;
    @Autowired
    private weChatPublicDao wcpDao;
    @Value("${app_id}")
    private String app_id;
    @Value("${api_query_auth_url}")
    private String api_query_auth_url;
    @Value("${api_authorizer_token_url}")
    private String api_authorizer_token_url;
    @Value("${token}")
    private String token;
    @Value("${app_key}")
    private String key;
    @Value("${openid_url}")
    private String openid_url;
    @Value("${getopenid_url_component}")
    private String getopenid_url_component;
    @Value("${getopenid_url_secret}")
    private String getopenid_url_secret;
    @Value("${publicNum_url}")
    private String publicNum_url;
    @Value("${menu_url}")
    private String menu_url;

    @Autowired
    private verifyTicketDao ticketDao;
    @Override
    public String getSignPackage(String appid, String url) throws Exception
    {
        LogHelper.info("vvvvvvvvvvvvvvvvvvvvvvvvvvH5获取JS访问条件接口vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = commenUtil.createSuiJi();

        String rawstring = "jsapi_ticket=" + getJsapi_ticket(appid)
                + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url="
                + url + "";
        LogHelper.info("H5获取JS访问条件接口--生成的rawstring结果为：---" + rawstring);
        String signature = commenUtil.getSHA1(rawstring);
        JSONObject jj2 = new JSONObject();
        try {
            jj2.put("appId", appid);

            jj2.put("timestamp", timestamp);
            jj2.put("nonceStr", nonceStr);
            jj2.put("signature", signature);
        } catch (JSONException e) {
            LogHelper.error(e, "H5获取JS访问条件接口异常！！！！", false);
        }
        return jj2.toString();
    }

    /**
     * 调用微信JS接口的临时票据 用于生成JS请求微信的签名
     *
     * @return
     */
    public String getJsapi_ticket(String authaccess_appid) {
        JsapiticketInfo js = JsapiticketInfoMap.get(authaccess_appid);
        LogHelper.info("获取的授权token----------------》" + getAuthAccesstoken(authaccess_appid));
        LogHelper.info("AuthorizerInfo----------------》" + js);
        if (js==null || js.jsapi_ticketExpires()) {
            String rJsapi = httpsPostMethod.sendHttpsPost(
                    "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
                            + getAuthAccesstoken(authaccess_appid), "&type=jsapi",
                    "获取jsapi_ticket");
            if (!"error".equals(rJsapi)) {
                try {
                    JSONObject jsapiJSON = new JSONObject(rJsapi);
                    int errcode = -1;
                    errcode = jsapiJSON.getInt("errcode");
                    if (errcode == 0) {
                        String jsapi_ticket = jsapiJSON.getString("ticket");
                        long expires_in = jsapiJSON.getInt("expires_in");
                        JsapiticketInfo af1=new JsapiticketInfo();
                        af1.setJsapi_ticket(jsapi_ticket);
                        af1.setJsapi_ticketExpires(expires_in);
                        JsapiticketInfoMap.put(authaccess_appid, af1);

                    } else {
                        return null;
                    }
                } catch (JSONException e) {
                    LogHelper.info("获取jsapi_ticket,authaccess_appid=" + authaccess_appid + "抛异常:"
                            + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        return JsapiticketInfoMap.get(authaccess_appid).getJsapi_ticket();
    }
    /**
     * 根据授权码和第三方平台appid换取公众号的接口调用凭据和授权信息
     * @return
     */
    public String getAuthAccesstoken(String authaccess_appid)
    {
        LogHelper.info("授权方的appid(getAuthAccesstoken)--------------》" + authaccess_appid);
        JSONObject accessToken_json = new JSONObject();
        AuthorizerInfo authorizerInfo= AuthorizerInfoMap.get(authaccess_appid);
        List<weChatPublic> weChatPublicList= wcpDao.getWeChatPublicByParamters(app_id, authaccess_appid);
        if(null==authorizerInfo)
        {
            authorizerInfo=new AuthorizerInfo();
        }
        if(weChatPublicList==null || weChatPublicList.size()<=0)
        {
            accessToken_json.put(MsgAndCode.CODE_001, MsgAndCode.MESSAGE_001);
            return accessToken_json.toString();
        }
        weChatPublic war=weChatPublicList.get(0);
        if( (null==war.getWeChatPublic_appid_refreshtoken()|| "".equals(war.getWeChatPublic_appid_refreshtoken())))
        {
            String auth_code="";
            try
                {
                    if(war.getAuthorization_state()==null || (war.getAuthorization_state().equals("2")))
                    {//已取消授权，重新获取授权码
                        accessToken_json.put(MsgAndCode.CODE_002, MsgAndCode.MESSAGE_002);
                        return accessToken_json.toString();
                    }
                    auth_code=war.getWechatpublic_Authorizer_code();
                    accessToken_json.put("component_appid",app_id);
                    accessToken_json.put("authorization_code", auth_code);
                    String responseStr= httpsPostMethod.sendHttpsPost(api_query_auth_url+"?component_access_token="+weChatThridService.getComponentAccessToken(),
                            accessToken_json.toString() ,"根据授权码和第三方平台appid换取公众号的接口调用凭据和授权信息");
                    LogHelper.info("根据授权码和第三方平台appid换取公众号的接口调用凭据和授权信息-----------》"+responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    String authorization_info=rjson.get("authorization_info")+"";
                    JSONObject js_authorization = new JSONObject(authorization_info);
                    AuthorizerInfo ai=new AuthorizerInfo(app_id,authaccess_appid,js_authorization.getString("authorizer_access_token"),
                            js_authorization.getLong("expires_in"),js_authorization.getString("authorizer_refresh_token"));
                    AuthorizerInfoMap.put(js_authorization.getString("authorizer_appid"), ai);
                    war.setWeChatPublic_appid_refreshtoken(js_authorization.getString("authorizer_refresh_token"));
                    wcpDao.saveOrUpdateWeChatPublic(war);
                 } catch (JSONException e) {
                    LogHelper.error(e,"换取公众号的接口调用凭据和授权信息(本地authorizer_refresh_token)为空异常！！！！",false);
                    e.printStackTrace();
                }

        }
        else
        {
            if( authorizerInfo.authorizer_access_tokenExprise())
            {
                try
                {
                    //从数据库中查询出刷新令牌用的refreshtoken
                    LogHelper.debug("刷新令牌用的refreshtoken------------------>" + war.getWeChatPublic_appid_refreshtoken());
                    accessToken_json.put("component_appid",app_id);
                    accessToken_json.put("authorizer_appid",authaccess_appid);
                    accessToken_json.put("authorizer_refresh_token", war.getWeChatPublic_appid_refreshtoken());
                    String responseStr= httpsPostMethod.sendHttpsPost(api_authorizer_token_url+"?component_access_token=" + weChatThridService.getComponentAccessToken(),
                            accessToken_json.toString(), "根据刷新token重新获取授权token");
                    LogHelper.debug("(本地过期根据authorizer_refresh_token重新获取token)-----------》"+responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    AuthorizerInfo ai=new AuthorizerInfo(app_id,authaccess_appid,rjson.getString("authorizer_access_token"),
                            rjson.getLong("expires_in"),rjson.getString("authorizer_refresh_token"));
                    AuthorizerInfoMap.put(authaccess_appid,ai);
                    war.setWeChatPublic_appid_refreshtoken(rjson.getString("authorizer_refresh_token"));
                    wcpDao.saveOrUpdateWeChatPublic(war);
                   } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        LogHelper.info("token========================"+AuthorizerInfoMap.get(authaccess_appid).getAuthorizer_access_token());
        return AuthorizerInfoMap.get(authaccess_appid).getAuthorizer_access_token();
    }

    public void handleAuthorize(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String msg_signature=request.getParameter("msg_signature");
        String timestamp=request.getParameter("timestamp");
        String nonce=request.getParameter("nonce");
        LogHelper.info("签名串------------:" + msg_signature);
        LogHelper.info("时间戳------------:" + timestamp);
        LogHelper.info("随机数------------:" + nonce);
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
            LogHelper.info("接收post发送数据:\n" + postStr);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String result_json= WeiXinDecode.decode(token, key, app_id, msg_signature, timestamp, nonce, postStr);
        JSONObject json=new JSONObject(result_json);
        String appid=json.getString("AppId");//第三方平台appid
        String createTime=json.getString("CreateTime");//时间戳
        String infoType=json.getString("InfoType");//component_verify_ticket
        List<openPlatform> mapList= ticketDao.getVerifyTicketByAppid(appid);
        if(infoType!=null && "component_verify_ticket".equalsIgnoreCase(infoType))
        {//每十分钟推一次验证ticket
            String componentVerifyTicket=json.getString("ComponentVerifyTicket");//Ticket内容

            openPlatform op=null;
            if(mapList.size()>0)
            {
                op= mapList.get(0);
            }
            else
            {
                op=new openPlatform();
            }
            op.setOpenPlatform_ticket(componentVerifyTicket);
            op.setOpenPlatform_ticket_time(createTime);
            op.setOpenPlatform_appid(appid);
            ticketDao.inserVerifyTicketAppid(op);
            LogHelper.info("每隔十分钟获取一次ticket！！！");
        }
        if(infoType!=null && ("unauthorized".equalsIgnoreCase(infoType)
                || "authorized".equalsIgnoreCase(infoType)))
        {//授权状态修改
            String result= AssemblingWeChatPublic(appid,infoType,json);
            LogHelper.info("授权状态修改："+result);
        }

    }

    @Override
    public String getMenu(Map<String, Object> source) throws Exception {
        LogHelper.info("接收查询自定义菜单配置接口参数：" + source);
        if(CommonHelper.isNullOrEmpty(source.get("appid")))
        {
            return returnMissParamMessage("appid");
        }
        String result= httpsPostMethod.sendHttpsPost(
                menu_url+"?access_token=" + getAuthAccesstoken(source.get("appid").toString()), "", "接收查询自定义菜单配置接口参数");
        return result;
    }

    @Override
    public String createMenu(Map<String, Object> source) throws Exception {
        String deleteResponse= deleteMenu(source.get("appid").toString());
        String json= map2json(source);
        JSONObject jsonObject=new JSONObject(json);
        LogHelper.info("创建菜单传入的请求字符串参数-----》" + json);
        LogHelper.info("删除自定义菜单返回-----》" + deleteResponse);
        LogHelper.info("创建菜单传入的菜单格式-----》" + jsonObject.get("menuinfo"));
        JSONObject deleteinfo = new JSONObject(deleteResponse);
        String result="";
        if(deleteinfo.getString("errmsg")!=null && "ok".equalsIgnoreCase(deleteinfo.getString("errmsg")))
        {
            result= httpsPostMethod.sendHttpsPost(
                    "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
                            + getAuthAccesstoken(source.get("appid").toString()), jsonObject.get("menuinfo").toString(), "创建自定义菜单");

        }
        else
        {
            result="{\"error\":\"创建之前删除失败！\"}";
        }
        return  result ;
    }

    @Override
    public String deleteMenu(String appid) throws Exception {
         LogHelper.info("接收查询自定义菜单配置接口参数：" + appid);
        String rp = httpsPostMethod.sendHttpsPost(
             "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token="+ getAuthAccesstoken(appid), "", "删除自定义菜单");
        LogHelper.info("删除自定义菜单微信回应：" + rp);
        return rp;
    }

    public String AssemblingWeChatPublic(String appid,String infoType ,JSONObject json)
    {

        String authorizerAppid=json.getString("AuthorizerAppid");//需授权的公众号appid
        String CreateTime=json.getString("CreateTime");//微信服务端授权码创建日期（毫秒）存入数据库后转为正常日期
        String shopnumInfo= null;//获取商户公众号信息
        try {
            shopnumInfo = getShopPublicNumberInfo(authorizerAppid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject body_js = new JSONObject(shopnumInfo);
        String authorizer_info=body_js.get("authorizer_info")+"";
        JSONObject bb = new JSONObject(authorizer_info);
        String nick_name="";
        String head_img="";
        try
        {
            nick_name= bb.getString("nick_name");
            head_img=bb.getString("head_img");
        }catch (Exception e)
        {

        }
        String authorizationCodeExpiredTime="";//过期时间
        String authorizationCode="";
        String result="";
        if(infoType!=null && "authorized".equalsIgnoreCase(infoType))
        {
            authorizationCode=json.getString("AuthorizationCode");//需授权公众号授权码
            authorizationCodeExpiredTime=json.getString("AuthorizationCodeExpiredTime");//过期时间
        }
        List<weChatPublic> weChatPublicList= wcpDao.getWeChatPublicByParamters(appid, authorizerAppid);
        weChatPublic wechat=null;
        if(weChatPublicList!=null&&weChatPublicList.size()>0)
        {
            wechat=weChatPublicList.get(0);
        }
        else
        {
            wechat=new weChatPublic();
        }
        if(infoType!=null && "unauthorized".equalsIgnoreCase(infoType))
        {
            wechat.setAuthorization_state("2");//取消授权
            wechat.setWeChatPublic_appid_refreshtoken("");
            wechat.setWechatpublic_CodeExpiredTime("");
            result=authorizerAppid+"--取消授权！";
        }
        else if(infoType!=null && "authorized".equalsIgnoreCase(infoType))
        {
            wechat.setAuthorization_state("1");//授权成功
            result=authorizerAppid+"--授权成功！";
        }
        wechat.setWechatpublic_CodeExpiredTime(authorizationCodeExpiredTime);
        wechat.setWechatpublic_Authorizer_code(authorizationCode);
        wechat.setWechatpublic_Code_CreateTime(CreateTime);
        wechat.setWechat_headimage(head_img);
        wechat.setWechat_nickname(nick_name);
        wechat.setWeChatPublic_appid(authorizerAppid);
        wechat.setWeChatPublic_openPlat_appid(appid);
        String aa=wcpDao.saveOrUpdateWeChatPublic(wechat);
        try {
            getAuthAccesstoken(authorizerAppid);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return result+aa;
    }
    /**
     * 获取微信用户信息
     * @return
     */
    public String getUserInfo(Map<String, Object> source) throws Exception
    {
        JSONObject requestJSON = new JSONObject();
        String shop_appid="";//商户appid
        String openid="";

        try {
            shop_appid=source.get("app_id").toString();
            openid=source.get("openid").toString();
            LogHelper.info("前台页面回传的openid-------------》" + openid);
        } catch (Exception e)
        {
            openid="";
            LogHelper.info("未传入openid");
        }
        String getuserinfo="";
        if(openid==null || "".equals(openid))
        {
            getuserinfo = httpsPostMethod.sendHttpsPost(
                    openid_url+"?access_token="+getAuthAccesstoken(shop_appid)+"&openid="+getOpenId(source)
                            +""
                    , "&lang=zh_CN",
                    "获取获取openid");
        }
        else
        {
            getuserinfo = httpsPostMethod.sendHttpsPost(
                    openid_url+"?access_token="+getAuthAccesstoken(shop_appid)+"&openid="+openid
                            +""
                    , "&lang=zh_CN",
                    "获取获取openid");
        }


        LogHelper.info("通过openid获取用户信息 微信返回的数据userinfo---------------------》" + getuserinfo);

        try {
            JSONObject userinfo_json=new JSONObject(getuserinfo);
            requestJSON.put("userinfo",userinfo_json);
            requestJSON.put("rspCode","000");
            requestJSON.put("rspDesc","成功");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestJSON.toString();
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
            List<weChatPublic> weChatPublicList= wcpDao.getWeChatPublicByParamters(app_id,shop_appid);
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
                    "&grant_type=authorization_code&component_appid=" + app_id + "&component_access_token=" + weChatThridService.getComponentAccessToken(), "", "");
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
     * 获取公众号基本信息
     * @return
     */
    public String getShopPublicNumberInfo(String authorizer_appid) throws Exception
    {
        JSONObject vas_callback = new JSONObject();
        vas_callback.put("component_appid", app_id);
        vas_callback.put("authorizer_appid", authorizer_appid);
        String ShopPublicNumberInfo = httpsPostMethod.sendHttpsPost(
                publicNum_url + "?component_access_token=" + weChatThridService.getComponentAccessToken(), vas_callback.toString(),
                "获取公众号基本信息");
        return ShopPublicNumberInfo;
    }

    private String returnMissParamMessage(String errorMessage){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(MsgAndCode.RSP_CODE, MsgAndCode.PARAM_MISSING_CODE);
        node.put(MsgAndCode.RSP_DESC, errorMessage + MsgAndCode.PARAM_MISSING_MSG);
        return node.toString();
    }
}
