package com.cn.cache;

import java.util.TreeMap;

/**
 * Created by cuixiaowei on 2016/4/25.
 */
public class PreAuthCodeMap {

    private static TreeMap<String, ComponentVerifyTicket> preAuthCodeMap = new TreeMap<String, ComponentVerifyTicket>();


    public static void put(String appid,ComponentVerifyTicket cvt){
        preAuthCodeMap.put(appid, cvt);
    }

    public static ComponentVerifyTicket get(String appid) {

        try {
            return preAuthCodeMap.get(appid);
        } catch (NullPointerException e) {
            // TODO: handle exception
            return null;
        }
    }
}
