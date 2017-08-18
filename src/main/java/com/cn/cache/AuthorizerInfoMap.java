package com.cn.cache;

import java.util.TreeMap;

/**
 * Created by cuixiaowei on 2016/4/27.
 */
public class AuthorizerInfoMap {
    private static TreeMap<String, AuthorizerInfo> authorizerInfoMap = new TreeMap<String, AuthorizerInfo>();


    public static void put(String appid,AuthorizerInfo cvt){
        authorizerInfoMap.put(appid, cvt);
    }

    public static AuthorizerInfo get(String appid) {

        try {
            return authorizerInfoMap.get(appid);
        } catch (NullPointerException e) {
            // TODO: handle exception
            return null;
        }
    }
}
