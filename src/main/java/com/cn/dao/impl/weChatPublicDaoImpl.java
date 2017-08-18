package com.cn.dao.impl;

import com.cn.common.CommonHelper;
import com.cn.common.LogHelper;
import com.cn.dao.util.HibernateDAO;
import com.cn.dao.weChatPublicDao;
import com.cn.entity.weChatPublic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/24.
 */
@Transactional
@Repository("weChatPublicDao")
public class weChatPublicDaoImpl implements weChatPublicDao {
    @Autowired
    private HibernateDAO hibernateDAO;
    @Override
    public List<weChatPublic> getWeChatPublicByParamters(String third_appid, String public_appid) {
        StringBuilder sql=new StringBuilder();
        sql.append("from weChatPublic wp where wp.weChatPublic_openPlat_appid='"+third_appid+"'");
        sql.append(" and wp.weChatPublic_appid='"+public_appid+"'");
        LogHelper.info("根据第三方ID和公众号appid查询信息："+sql);
        //hibernateDAO.listByHQL(sql.toString());
        return (List<weChatPublic>) hibernateDAO.listByHQL(sql.toString());
    }

    @Override
    public String saveOrUpdateWeChatPublic(weChatPublic wp) {
        return hibernateDAO.saveOrUpdate(wp);
    }

    @Override
    public List<weChatPublic> getWeChatInfo(String id) throws Exception {

        StringBuilder sql=new StringBuilder();
        sql.append("from weChatPublic wp where wp.weChatPublic_id='"+id+"'");
        LogHelper.info("根据主键ID查询信息："+sql);

        return (List<weChatPublic>) hibernateDAO.listByHQL(sql.toString());
    }

    @Override
    public List<Map<String, Object>> getWeChatList(Map<String, Object> source) throws Exception {
        StringBuilder sql=new StringBuilder();
        sql.append("select wp.WECHATPUBLIC_ID,wp.WECHATPUBLIC_APPID,wp.WECHATPUBLIC_APPID_SECERT,wp.AUTHORIZATION_STATE," +
                "wp.WECHAT_NUM,wp.WECHAT_PAYAPI,wp.WECHAT_NICKNAME,wp.WECHAT_HEADIMAGE,wp.WECHAT_PAY_VASREURL,wp.WECHAT_MERCHANTID,wp.WECHAT_CODEURL from weChatPublic wp where 1=1 ");

        if(!CommonHelper.isNullOrEmpty(source.get("name")))
        {
            sql.append(" AND wp.WECHAT_NICKNAME like'%"+source.get("name")+"%'");
        }
        if(!CommonHelper.isNullOrEmpty(source.get("weChatPublic_appid")))
        {
            sql.append(" AND wp.WECHATPUBLIC_APPID='"+source.get("weChatPublic_appid")+"'");
        }
        LogHelper.info("查询列表sql:"+sql);
        List<Map<String, Object>> wcpList= (List<Map<String, Object>>) hibernateDAO.listByPageBySQL(sql.toString(),Integer.parseInt(String.valueOf(source.get("pageSize"))),
                Integer.parseInt(String.valueOf(source.get("curragePage")))-1,false);
        return wcpList;
    }

    @Override
    public int count(Map<String, Object> source) throws Exception {
        StringBuilder sql=new StringBuilder();
        sql.append("select count(*) numberr from weChatPublic wp where 1=1");
        if(!CommonHelper.isNullOrEmpty(source.get("name")))
        {
            sql.append(" AND wp.WECHAT_NICKNAME like'%"+source.get("name")+"%'");
        }
        if(!CommonHelper.isNullOrEmpty(source.get("weChatPublic_appid")))
        {
            sql.append(" AND wp.WECHATPUBLIC_APPID='"+source.get("weChatPublic_appid")+"'");
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>)
                hibernateDAO.listBySQL(sql.toString(), false);
        if(!list.isEmpty()){
            Map<String, Object> map = list.get(0);
            return Integer.parseInt(map.get("NUMBERR").toString());
        }
        return 0;
    }
}
