package com.cn.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
@Entity
@Table(name="openPlatform")
@GenericGenerator(name = "system-uuid", strategy = "uuid")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class openPlatform {
    /**
     * 三方平台表主键ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "OPENPLATFORM_ID", length = 32)
    private String openPlatform_id;

    /**
     * 三方平台ID
     */
    @Column(name = "OPENPLATFORM_APPID", length = 200)
    private String openPlatform_appid;

    /**
     * 三方平台十分钟ticket
     */
    @Column(name = "OPENPLATFORM_TICKET", length = 500)
    private String openPlatform_ticket;

    /**
     * 三方平台十分钟ticket时间戳
     */
    @Column(name = "OPENPLATFORM_TICKET_TIME", length = 500)
    private String openPlatform_ticket_time;
    /**
     * 获取第三方平台component_access_token
     */
    @Column(name = "OPENPLATFORM_COM_ACCESS_TOKEN",length = 500 )
    private String openPlatform_com_access_token;

    public String getOpenPlatform_ticket_time() {
        return openPlatform_ticket_time;
    }

    public void setOpenPlatform_ticket_time(String openPlatform_ticket_time) {
        this.openPlatform_ticket_time = openPlatform_ticket_time;
    }

    public String getOpenPlatform_id() {
        return openPlatform_id;
    }

    public void setOpenPlatform_id(String openPlatform_id) {
        this.openPlatform_id = openPlatform_id;
    }

    public String getOpenPlatform_appid() {
        return openPlatform_appid;
    }

    public void setOpenPlatform_appid(String openPlatform_appid) {
        this.openPlatform_appid = openPlatform_appid;
    }

    public String getOpenPlatform_ticket() {
        return openPlatform_ticket;
    }

    public void setOpenPlatform_ticket(String openPlatform_ticket) {
        this.openPlatform_ticket = openPlatform_ticket;
    }
}
