package com.cn.common;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by cuixiaowei on 2016/11/14.
 */
public class LogHelper {

    public static void info(Object msg) {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();

        Logger logger = Logger.getLogger(stack[1].getClassName());
        logger.log(LogHelper.class.getName(), Level.INFO, msg, null);
    }

    /**
     *
     * @param e 功能说明（功能模块）
     * @param msg
     * @param isToMail 是否发送邮件 true是  false否
     */
    public static void error(Exception e, String msg,boolean isToMail) {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();

        Logger logger = Logger.getLogger(stack[1].getClassName());
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.log(LogHelper.class.getName(), Level.ERROR,msg+"\n"+sw.toString(), null);
        if(isToMail){
            MailSenderHelper.sendMail(msg, msg + "\n" + sw.toString());  //发送邮件
        }
    }

    public static void debug(Object msg) {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();

        Logger logger = Logger.getLogger(stack[1].getClassName());
        logger.log(LogHelper.class.getName(), Level.DEBUG, msg, null);
    }

    public static void warn(Object msg) {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();

        Logger logger = Logger.getLogger(stack[1].getClassName());
        logger.log(LogHelper.class.getName(), Level.WARN, msg, null);
    }



}
