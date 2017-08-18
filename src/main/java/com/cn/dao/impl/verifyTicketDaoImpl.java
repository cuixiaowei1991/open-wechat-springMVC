package com.cn.dao.impl;

import com.cn.common.LogHelper;
import com.cn.dao.util.HibernateDAO;
import com.cn.dao.verifyTicketDao;
import com.cn.entity.openPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
@Transactional
@Repository("verifyTicketDao")
public class verifyTicketDaoImpl implements verifyTicketDao {
    @Autowired
    private HibernateDAO hibernateDAO;
    @Override
    public String inserVerifyTicketAppid(openPlatform opf)
    {
        return hibernateDAO.saveOrUpdate(opf);
    }

    @Override
    public List<openPlatform> getVerifyTicketByAppid(String appid)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("from openPlatform op ");
        sql.append("where op.openPlatform_appid = '").append(appid).append("'");
        LogHelper.info("根据第三方平台appid获取10分钟ticket："+sql.toString());
        List<openPlatform> list = (List<openPlatform>)
                hibernateDAO.listByHQL(sql.toString());
        return list;
    }
}
