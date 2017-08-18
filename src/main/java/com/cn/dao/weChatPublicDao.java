package com.cn.dao;

import com.cn.entity.weChatPublic;

import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/24.
 */
public interface weChatPublicDao {
    /**
     * 根据第三方ID和公众号appid获取信息
     * @param third_appid
     * @param public_appid
     * @return
     */
    public List<weChatPublic> getWeChatPublicByParamters(String third_appid,String public_appid);

    public String saveOrUpdateWeChatPublic(weChatPublic wp);

    /**
     * 根据主键获取
     * @param id
     * @return
     * @throws Exception
     */
    public List<weChatPublic> getWeChatInfo(String id) throws Exception;

    /**
     *条件查询列表
     * @param source
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getWeChatList(Map<String, Object> source) throws Exception;

    /**
     * 总数
     * @param source
     * @return
     * @throws Exception
     */
    public int count(Map<String, Object> source) throws Exception;
}
