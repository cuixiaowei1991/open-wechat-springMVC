package com.cn.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
public interface weChatThridService
{


    public String entranceWinXin() throws Exception;

    public String commonReturn(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public String weixinForPay(Map<String, Object> source) throws Exception;


}
