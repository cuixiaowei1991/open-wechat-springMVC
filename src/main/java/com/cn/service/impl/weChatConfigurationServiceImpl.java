package com.cn.service.impl;

import com.cn.MsgCode.MsgAndCode;
import com.cn.common.CommonHelper;
import com.cn.common.LogHelper;
import com.cn.dao.weChatPublicDao;
import com.cn.entity.weChatPublic;
import com.cn.service.weChatConfigurationService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/6/20.
 */
@Service
public class weChatConfigurationServiceImpl implements weChatConfigurationService
{
    @Autowired
    private weChatPublicDao wpDao;
    @Override
    public String getWeChatInfo(Map<String, Object> source) throws Exception {
        LogHelper.info("获取微信配置信息=="+source);
        JSONObject wechatJo=new JSONObject();
        if(CommonHelper.isNullOrEmpty(source.get("weChatPublic_id")))
        {
            return returnMissParamMessage("weChatPublic_id");
        }
        List<weChatPublic> weChatPublicList= wpDao.getWeChatInfo(source.get("weChatPublic_id").toString());
        if(weChatPublicList==null || weChatPublicList.size()==0)
        {
            wechatJo.put(MsgAndCode.PARAM_MISSING_CODE,MsgAndCode.CODE_003);
            wechatJo.put(MsgAndCode.PARAM_MISSING_MSG,MsgAndCode.MESSAGE_003);
            return wechatJo.toString();
        }
        weChatPublic wp=weChatPublicList.get(0);
        wechatJo.put("weChatPublic_id",wp.getWeChatPublic_id());
        wechatJo.put("weChatPublic_appid",wp.getWeChatPublic_appid());
        wechatJo.put("weChatPublic_appid_secert",wp.getWeChatPublic_appid_secert());
        wechatJo.put("Authorization_state",wp.getAuthorization_state());//授权状态 1：授权成功 2：取消授权 3：授权更新
        wechatJo.put("wechat_num",wp.getWechat_num()==null?"":wp.getWechat_num());
        wechatJo.put("wechat_payapi",wp.getWechat_payapi()==null?"":wp.getWechat_payapi());
        wechatJo.put("wechat_nickname",wp.getWechat_nickname()==null?"":wp.getWechat_nickname());
        wechatJo.put("wechat_headimage",wp.getWechat_headimage()==null?"":wp.getWechat_headimage());
        wechatJo.put("wechat_pay_vasreurl",wp.getWechat_pay_vasreurl()==null?"":wp.getWechat_pay_vasreurl());
        wechatJo.put("wechat_merchant_id",wp.getWechat_merchantid()==null?"":wp.getWechat_merchantid());
        wechatJo.put("code_url",wp.getWechat_codeurl()==null?"":wp.getWechat_codeurl());
        wechatJo.put(MsgAndCode.RSP_CODE,MsgAndCode.SUCCESS_CODE);
        wechatJo.put(MsgAndCode.RSP_DESC,MsgAndCode.SUCCESS_MESSAGE);

        LogHelper.info("根据主键ID："+source.get("weChatPublic_id")+"，查询的微信配置信息："+wechatJo);
        return wechatJo.toString();
    }

    @Override
    public String getWeChatList(Map<String, Object> source) throws Exception {
        LogHelper.info("获取微信列表信息=="+source);
        if(CommonHelper.isNullOrEmpty(source.get("curragePage")))
        {
            return returnMissParamMessage("curragePage");
        }
        if(CommonHelper.isNullOrEmpty(source.get("pageSize")))
        {
            return returnMissParamMessage("pageSize");
        }
        JSONObject wechatJos=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        List<Map<String, Object>> wcpList=wpDao.getWeChatList(source);
        int count=wpDao.count(source);
        if(wcpList != null && wcpList.size()>0)
        {
            for(Map<String, Object> wcpMap : wcpList)
            {
                JSONObject wechatJo=new JSONObject();
                wechatJo.put("weChatPublic_id",wcpMap.get("WECHATPUBLIC_ID"));
                wechatJo.put("weChatPublic_appid",wcpMap.get("WECHATPUBLIC_APPID")==null?"":wcpMap.get("WECHATPUBLIC_APPID"));
                wechatJo.put("weChatPublic_appid_secert",wcpMap.get("WECHATPUBLIC_APPID_SECERT")==null?"":wcpMap.get("WECHATPUBLIC_APPID_SECERT"));
                wechatJo.put("Authorization_state",wcpMap.get("AUTHORIZATION_STATE")==null?"":wcpMap.get("AUTHORIZATION_STATE"));//授权状态 1：授权成功 2：取消授权 3：授权更新
                wechatJo.put("wechat_num",wcpMap.get("WECHAT_NUM")==null?"":wcpMap.get("WECHAT_NUM"));
                wechatJo.put("wechat_payapi",wcpMap.get("WECHAT_PAYAPI")==null?"":wcpMap.get("WECHAT_PAYAPI"));
                wechatJo.put("wechat_nickname",wcpMap.get("WECHAT_NICKNAME")==null?"":wcpMap.get("WECHAT_NICKNAME"));
                wechatJo.put("wechat_headimage",wcpMap.get("WECHAT_HEADIMAGE")==null?"":wcpMap.get("WECHAT_HEADIMAGE"));
                wechatJo.put("wechat_pay_vasreurl",wcpMap.get("WECHAT_PAY_VASREURL")==null?"":wcpMap.get("WECHAT_PAY_VASREURL"));
                wechatJo.put("wechat_merchant_id",wcpMap.get("WECHAT_MERCHANTID")==null?"":wcpMap.get("WECHAT_MERCHANTID"));
                wechatJo.put("code_url",wcpMap.get("WECHAT_CODEURL")==null?"":wcpMap.get("WECHAT_CODEURL"));
                jsonArray.put(wechatJo);
            }

        }
        wechatJos.put(MsgAndCode.RSP_CODE,MsgAndCode.SUCCESS_CODE);
        wechatJos.put(MsgAndCode.RSP_DESC,MsgAndCode.SUCCESS_MESSAGE);
        wechatJos.put("list",jsonArray);
        wechatJos.put("curragePage",source.get("curragePage"));
        wechatJos.put("pageSize",source.get("pageSize"));
        wechatJos.put("total",count);

        LogHelper.info("查询微信列表："+wechatJos.toString());
        return wechatJos.toString();
    }

    @Override
    public String updateWeChatInfo(Map<String, Object> source) throws Exception {
        LogHelper.info("修改微信配置信息=="+source);
        if(CommonHelper.isNullOrEmpty(source.get("weChatPublic_id")))
        {
            return returnMissParamMessage("weChatPublic_id");
        }
        JSONObject wechatJo=new JSONObject();
        List<weChatPublic> weChatPublicList= wpDao.getWeChatInfo(source.get("weChatPublic_id").toString());
        if(weChatPublicList==null || weChatPublicList.size()==0)
        {
            wechatJo.put(MsgAndCode.PARAM_MISSING_CODE,MsgAndCode.CODE_003);
            wechatJo.put(MsgAndCode.PARAM_MISSING_MSG,MsgAndCode.MESSAGE_003);
            return wechatJo.toString();
        }
        weChatPublic wp=weChatPublicList.get(0);
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_num")))
        {
            wp.setWechat_num(source.get("wechat_num").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_payapi")))
        {
            wp.setWechat_payapi(source.get("wechat_payapi").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("weChatPublic_appid_secert")))
        {
            wp.setWeChatPublic_appid_secert(source.get("weChatPublic_appid_secert").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("vas_url")))
        {
            wp.setWechat_pay_vasreurl(source.get("vas_url").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("code_url")))
        {
            wp.setWechat_codeurl(source.get("code_url").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_merchant_id")))
        {
            wp.setWechat_merchantid(source.get("wechat_merchant_id").toString());
        }
        wpDao.saveOrUpdateWeChatPublic(wp);
        wechatJo.put(MsgAndCode.RSP_CODE,MsgAndCode.SUCCESS_CODE);
        wechatJo.put(MsgAndCode.RSP_DESC, MsgAndCode.SUCCESS_MESSAGE);
        return wechatJo.toString();
    }

    /**
     * 校验　非空
     * @param obj
     * @return
     */
    private String checkIsOrNotNull(Object obj){

        if(!CommonHelper.isNullOrEmpty(obj)){
            return obj.toString();
        }
        return "";
    }
    private String returnMissParamMessage(String errorMessage){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(MsgAndCode.RSP_CODE, MsgAndCode.PARAM_MISSING_CODE);
        node.put(MsgAndCode.RSP_DESC, errorMessage + MsgAndCode.PARAM_MISSING_MSG);
        return node.toString();
    }
}
