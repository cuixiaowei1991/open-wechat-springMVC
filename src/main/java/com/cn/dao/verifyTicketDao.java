package com.cn.dao;

import com.cn.entity.openPlatform;

import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
public interface verifyTicketDao {
    /**
     *
     * @return
     */
    public String inserVerifyTicketAppid(openPlatform opf);

    /**
     * 根据第三方appid获取10分钟ticket
     * @param appid
     * @return
     */
    public List<openPlatform> getVerifyTicketByAppid(String appid);
}
