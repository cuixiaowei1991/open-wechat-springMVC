package com.cn.test.Aes;

import com.cn.common.LogHelper;
import com.cn.common.WeChatCommon.WeiXinDecode;
import org.junit.Test;
import org.json.JSONObject;
/**
 * Created by cuixiaowei on 2017/3/6.
 */
public class DecodeTest {
    @Test
    public void decode()
    {
        String str="<xml>\n" +
                "    <AppId><![CDATA[wxfa3422f1f3dd9689]]></AppId>\n" +
                "    <Encrypt><![CDATA[PErlFV1OTPmfLvIZ5ZLVgVhgyDlilyoZnDhU7+dKn1IlbduWNoDWfc/sn+LPlNDjLzQ/od3fpQHesVrrX1KvDeZEW3/afOC5BVpKY1v6evrnLGfUufU+Uen2UbABRzfjr6X6zOyxm8gRpYUeQMsq7s39IIgO3GOcegmEvUXsUy4H5YRlSM4LWWrkE5gDZwz2tM1/POxzAdmQJgWB9zcJcVHlwO8MZdLmoUrtUA0ZdVHUHfaON2i2Ghyf8kgWdrWrRlAZrRWUFM7W1fn+mZK2uJuJadBt/MPdncy3cedMeWtzsXQeEQBdhwc4C/WZXzPD6vlFqah5Nsog4xYWDb9SAEeClnAoXeOiWREBZpMgDM7UGmoOvxhO7fh/TzxYiJs40aTldiWJmO8eB3aLGOsw5h5a8y2ps+pD3hiUGBGrASao+a0ZanZAkHWngScFfKuwYAL3T1i51gCZTYyLTnHBCg==]]></Encrypt>\n" +
                "</xml>\n";
        String token="uwU5ANAtbeNfVbu";
        String key="avAnztwetUbepplienNf4ureppixiappwANVbliuwma";
        String app_id="wxfa3422f1f3dd9689";
        String msg_signature="16c32c179c05d9877d9f6625ccc0101da41506f6";
        String timestamp="1488766485";
        String nonce="1990126894";
        String result_json="";
        try {
            result_json= WeiXinDecode.decode(token, key, app_id, msg_signature, timestamp, nonce, str);
        } catch (Exception e) {
            LogHelper.error(e,"解码异常！！！！",false);
            e.printStackTrace();
        }
        System.out.println("解码后："+result_json);
        JSONObject obj=new JSONObject(result_json);
        System.out.println("ComponentVerifyTicket:"+obj.getString("ComponentVerifyTicket"));

    }
}
