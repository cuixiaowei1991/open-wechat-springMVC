package com.cn.service;

import java.util.Map;

/**
 * Created by cuixiaowei on 2017/6/20.
 */
public interface weChatConfigurationService {
    /**
     * 获取微信配置信息
     * @param source
     * @return
     * @throws Exception
     */
    public String getWeChatInfo(Map<String, Object> source) throws Exception;

    /**
     * 获取微信配置信息列表
     * @param source
     * @return
     * @throws Exception
     */
    public String getWeChatList(Map<String, Object> source) throws Exception;
    /**
     * 更新微信配置信息
     * @param source
     * @return
     * @throws Exception
     */
    public String updateWeChatInfo(Map<String, Object> source) throws Exception;
}
