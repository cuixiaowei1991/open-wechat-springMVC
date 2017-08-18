package com.cn.cache;

import java.util.TreeMap;

/**
 * Created by cuixiaowei on 2016/4/25.
 */
public class ComponentAccessTokenMap {
    private static TreeMap<String, ComponentVerifyTicket> componentAccessTokenMap = new TreeMap<String, ComponentVerifyTicket>();


    public static void put(String appid,ComponentVerifyTicket cvt){
        componentAccessTokenMap.put(appid, cvt);
    }

    public static ComponentVerifyTicket get(String appid) {

        try {
            return componentAccessTokenMap.get(appid);
        } catch (NullPointerException e) {
            // TODO: handle exception
            return null;
        }
    }
}
