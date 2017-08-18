package com.cn.cache;

import java.util.TreeMap;

/**
 * Created by cuixiaowei on 2016/5/4.
 */
public class JsapiticketInfoMap {
    private static TreeMap<String, JsapiticketInfo> jsapitickeMap = new TreeMap<String, JsapiticketInfo>();


    public static void put(String appid,JsapiticketInfo cvt){
        jsapitickeMap.put(appid, cvt);
    }

    public static JsapiticketInfo get(String appid) {

        try {
            return jsapitickeMap.get(appid);
        } catch (NullPointerException e) {
            // TODO: handle exception
            return null;
        }
    }
}
