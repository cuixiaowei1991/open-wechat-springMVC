package com.cn.cache;

/**
 * Created by cuixiaowei on 2016/4/22.
 */
public class ComponentVerifyTicket {
    private String appId;//第三方平台appid
    private String createTime;//时间戳
    private String infoType;//component_verify_ticket
    private String componentVerifyTicket;//Ticket内容
    private long component_access_tokenExprise;//获取第三方平台component_access_token过期时间
    private String component_access_token;//获取第三方平台component_access_token
    private long preauthcode_Exprise;
    private String pre_auth_code;//获取第三方平台的预授权码


    public ComponentVerifyTicket(String appId, String createTime, String infoType, String componentVerifyTicket) {
        setAppId(appId);
        setCreateTime(createTime);
        setComponentVerifyTicket(componentVerifyTicket);
        setInfoType(infoType);

    }

    public ComponentVerifyTicket(String appId, String component_access_token, long expires_in) {
        setAppId(appId);
        setComponent_access_token(component_access_token);
        setComponent_access_tokenExprise(expires_in);

    }
    public ComponentVerifyTicket(String appId, long expires_in, String pre_auth_code)
    {
        setPre_auth_code(pre_auth_code);
        setAppId(appId);
        setPreauthcode_Exprise(expires_in);
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getComponentVerifyTicket() {
        return componentVerifyTicket;
    }

    public void setComponentVerifyTicket(String componentVerifyTicket) {
        this.componentVerifyTicket = componentVerifyTicket;
    }

    public long getComponent_access_tokenExprise() {
        return component_access_tokenExprise;
    }

    public void setComponent_access_tokenExprise(long expires_in) {
        this.component_access_tokenExprise = System.currentTimeMillis()/1000+expires_in-60;
    }
    public boolean component_access_tokenExprise() {
        if(System.currentTimeMillis()/1000>component_access_tokenExprise){
            return true;
        }else{
            return false;
        }
    }

    public String getComponent_access_token() {
        return component_access_token;
    }

    public void setComponent_access_token(String component_access_token) {
        this.component_access_token = component_access_token;
    }

    public long getPreauthcode_Exprise() {
        return preauthcode_Exprise;
    }

    public void setPreauthcode_Exprise(long expires_in) {
        this.preauthcode_Exprise = System.currentTimeMillis()/1000+expires_in-300;;
    }

    public boolean preauthcode_Exprise() {
        if(System.currentTimeMillis()/1000>preauthcode_Exprise){
            return true;
        }else{
            return false;
        }
    }

    public String getPre_auth_code() {
        return pre_auth_code;
    }

    public void setPre_auth_code(String pre_auth_code) {
        this.pre_auth_code = pre_auth_code;
    }
}
