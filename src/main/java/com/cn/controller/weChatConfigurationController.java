package com.cn.controller;

import com.cn.common.LogHelper;
import com.cn.service.weChatConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by cuixiaowei on 2017/6/20.
 */
@Controller
@RequestMapping("/weChatConfiguration")
public class weChatConfigurationController {

    @Autowired
    private weChatConfigurationService wc;
    @ResponseBody
    @RequestMapping(value ="/getInfo",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public String getWeiXinAuthorization(@RequestBody Map<String, Object> source)
    {
        String result="";
        try {
            result= wc.getWeChatInfo(source);
        } catch (Exception e) {
            LogHelper.error(e, "根据主键ID获取微信信息异常！！！！", false);
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value ="/getList",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public String getWeChatInfoList(@RequestBody Map<String, Object> source)
    {
        String result="";
        try {
            result= wc.getWeChatList(source);
        } catch (Exception e) {
            LogHelper.error(e, "获取微信列表异常！！！！", false);
            e.printStackTrace();
        }
        return result;
    }
    @ResponseBody
    @RequestMapping(value ="/update",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public String updateWeChatInfo(@RequestBody Map<String, Object> source)
    {
        String result="";
        try {
            result= wc.updateWeChatInfo(source);
        } catch (Exception e) {
            LogHelper.error(e, "更新微信配置信息异常！！！！", false);
            e.printStackTrace();
        }
        return result;
    }

}
