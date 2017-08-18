package com.cn.cache;

/**
 * Created by cuixiaowei on 2016/4/27.
 */
public class AuthorizerInfo {
    private String appid;//������ƽ̨appid
    private String authorizer_appid;//�̻�appid
    private String authorizer_access_token;//�̻���Ȩtoken
    private long authorizer_access_tokenExprise=0;//��Чʱ��
    private String authorizer_refresh_token;//�ӿڵ���ƾ��ˢ������
    private String func_info;//���ں���Ȩ�������ߵ�Ȩ�޼��б�
    private long jsapi_ticketExpires=0;
    private String jsapi_ticket;
    private String openid;


    public AuthorizerInfo(String appid,String authorizer_appid,String authorizer_access_token,
                          long expires_in,String authorizer_refresh_token,String func_info)
    {
        setAppid(appid);
        setAuthorizer_appid(authorizer_appid);
        setAuthorizer_access_token(authorizer_access_token);
        setAuthorizer_refresh_token(authorizer_refresh_token);
        setFunc_info(func_info);
        setAuthorizer_access_tokenExprise(expires_in);
    }
    public AuthorizerInfo(String appid,String authorizer_appid,String authorizer_access_token,
                          long expires_in,String authorizer_refresh_token) {
        setAppid(appid);
        setAuthorizer_appid(authorizer_appid);
        setAuthorizer_access_token(authorizer_access_token);
        setAuthorizer_refresh_token(authorizer_refresh_token);
        setAuthorizer_access_tokenExprise(expires_in);
    }
    public AuthorizerInfo()
    {}

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAuthorizer_appid() {
        return authorizer_appid;
    }

    public void setAuthorizer_appid(String authorizer_appid) {
        this.authorizer_appid = authorizer_appid;
    }

    public String getAuthorizer_access_token() {
        return authorizer_access_token;
    }

    public void setAuthorizer_access_token(String authorizer_access_token) {
        this.authorizer_access_token = authorizer_access_token;
    }

    public long getAuthorizer_access_tokenExprise() {
        return authorizer_access_tokenExprise;
    }

    public void setAuthorizer_access_tokenExprise(long expires_in) {
        this.authorizer_access_tokenExprise = System.currentTimeMillis()/1000+expires_in-600;
    }
    public boolean authorizer_access_tokenExprise() {
        if(System.currentTimeMillis()/1000>authorizer_access_tokenExprise){
            return true;
        }else{
            return false;
        }
    }

    public String getAuthorizer_refresh_token() {
        return authorizer_refresh_token;
    }

    public void setAuthorizer_refresh_token(String authorizer_refresh_token) {
        this.authorizer_refresh_token = authorizer_refresh_token;
    }

    public String getFunc_info() {
        return func_info;
    }

    public void setFunc_info(String func_info) {
        this.func_info = func_info;
    }
    public long getJsapi_ticketExpires() {
        return jsapi_ticketExpires;
    }
    public void setJsapi_ticketExpires(long expires_in) {
        this.jsapi_ticketExpires = System.currentTimeMillis()/1000+expires_in-1800;
    }
    /**
     * ����΢��JS�ӿڵ���ʱƱ�� ��������JS����΢�ŵ�ǩ��
     * @return
     */
    public String getJsapi_ticket() {
        return jsapi_ticket;
    }
    /**
     * ����΢��JS�ӿڵ���ʱƱ�� ��������JS����΢�ŵ�ǩ��
     * @param jsapi_ticket
     */
    public void setJsapi_ticket(String jsapi_ticket) {
        this.jsapi_ticket = jsapi_ticket;
    }
    public boolean jsapi_ticketExpires() {
        if(System.currentTimeMillis()/1000>jsapi_ticketExpires){
            return true;
        }else{
            return false;
        }
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
