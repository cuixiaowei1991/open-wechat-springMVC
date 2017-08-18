package com.cn.MsgCode;

/**
 * Created by cuixiaowei on 2016/11/8.
 */
public class MsgAndCode {
    public final static String RSP_CODE = "rspCode";
    public final static String RSP_DESC = "rspDesc";

    public final static String SUCCESS_CODE = "000";
    public final static String SUCCESS_MESSAGE = "成功";

    public final static String CODE_001 = "001";
    public final static String MESSAGE_001 = "公众号授权未成功，请重新扫描二维码授权或者联系相关人员！";

    public final static String CODE_002 = "002";
    public final static String MESSAGE_002 = "公众号已取消授权，请重新扫描二维码授权或者联系相关人员！";

    /*****/
    public final static String PARAM_MISSING_CODE = "001";
    /****/
    public final static String PARAM_MISSING_MSG = ",不能为空!";

    public final static String CODE_003 = "003";
    public final static String MESSAGE_003 = "根据主键ID查询不到相关微信配置信息！";

}
