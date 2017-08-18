package com.cn.cache;

/**
 * Created by cuixiaowei on 2016/5/4.
 */
public class JsapiticketInfo {
    private long jsapi_ticketExpires=0;
    private String jsapi_ticket;

    public long getJsapi_ticketExpires() {
        return jsapi_ticketExpires;
    }
    public void setJsapi_ticketExpires(long expires_in) {
        this.jsapi_ticketExpires = System.currentTimeMillis()/1000+expires_in-1800;
    }
    /**
     * 调用微信JS接口的临时票据 用于生成JS请求微信的签名
     * @return
     */
    public String getJsapi_ticket() {
        return jsapi_ticket;
    }
    /**
     * 调用微信JS接口的临时票据 用于生成JS请求微信的签名
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
}
