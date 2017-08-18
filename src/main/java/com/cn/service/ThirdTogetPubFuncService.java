package com.cn.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/28.
 */
public interface ThirdTogetPubFuncService {
    /**
     * h5签名注册
     * @param appid 公众号appid
     * @param url referer 中的url
     * @return
     * @throws IOException
     */
    public String getSignPackage(String appid,String url) throws Exception;

    public String getOpenId(Map<String, Object> source) throws Exception;

    public String getShopPublicNumberInfo(String authorizer_appid) throws Exception;

    public String getUserInfo(Map<String, Object> source) throws Exception;

    public void handleAuthorize(HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    public String getMenu(Map<String, Object> source) throws Exception;

    public String createMenu(Map<String, Object> source) throws Exception;

    public String deleteMenu(String appid) throws Exception;
}
