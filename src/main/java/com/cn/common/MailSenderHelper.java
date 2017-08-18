package com.cn.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 简单邮件发送器，可单发，群发。
 * Created by WangWenFang on 2016/11/11.
 */
@Component
public class MailSenderHelper {
    private static String file = "config.properties";  //邮件配置文件
    private static String from;  //发件人邮箱
    private static String passwrod;  //密码
    private static String to;  //收件人

    private transient MailAuthenticatorHelper authenticator;  //邮件服务器登录验证
    private transient Session session;  //邮箱session

    /**
     * 初始化邮件发送器
     *
     * @param username
     *                发送邮件的用户名(地址)，并以此解析SMTP服务器地址
     * @param password
     *                发送邮件的密码
     */
    public MailSenderHelper(final String username, final String password) {
        //通过邮箱地址解析出smtp服务器，对大多数邮箱都管用
        final String smtpHostName = "smtp." + username.split("@")[1];
        init(username, password, smtpHostName);
    }

    public MailSenderHelper(){
    }

    /**
     * 初始化
     *
     * @param username
     *                发送邮件的用户名(地址)
     * @param password
     *                密码
     * @param smtpHostName
     *                SMTP主机地址
     */
    private void init(String username, String password, String smtpHostName) {
        // 初始化props
        Properties props = getProperties(file);
        props.put("mail.smtp.host", smtpHostName);  //邮件服务器主机名
        // 验证
        authenticator = new MailAuthenticatorHelper(username, password);
        // 创建session
        session = Session.getInstance(props, authenticator);
    }

    /**
     * 单发邮件
     *
     * @param recipient
     *                收件人邮箱地址
     * @param subject
     *                邮件主题
     * @param content
     *                邮件内容
     * @throws AddressException
     * @throws MessagingException
     */
    public void send(String recipient, String subject, Object content)
            throws AddressException, MessagingException {
        // 创建mime类型邮件
        final MimeMessage message = new MimeMessage(session);
        // 设置发信人
        message.setFrom(new InternetAddress(authenticator.getUsername()));
        // 设置收件人
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));
        // 设置主题
        message.setSubject(subject);
        // 设置邮件内容
        message.setContent(content.toString(), "text/html;charset=utf-8");
        // 发送
        Transport.send(message);
    }

    /**
     * 群发邮件
     *
     * @param recipients
     *                收件人们
     * @param subject
     *                主题
     * @param content
     *                内容
     * @throws AddressException
     * @throws MessagingException
     */
    public void send(List<String> recipients, String subject, Object content)
            throws AddressException, MessagingException {
        // 创建mime类型邮件
        final MimeMessage message = new MimeMessage(session);
        // 设置发信人
        message.setFrom(new InternetAddress(authenticator.getUsername()));
        // 设置收件人们
        final int num = recipients.size();
        InternetAddress[] addresses = new InternetAddress[num];
        for (int i = 0; i < num; i++) {
            addresses[i] = new InternetAddress(recipients.get(i));
        }
        message.setRecipients(MimeMessage.RecipientType.TO, addresses);
        // 设置主题
        message.setSubject(subject);
        // 设置邮件内容
        message.setContent(content.toString(), "text/html;charset=utf-8");
        // 发送
        Transport.send(message);
    }

    /**
     * 读取邮件配置文件
     * @return
     */
    public static Properties getProperties(String file){
        String filePath = MailSenderHelper.class.getResource("/"+file).toString();
        System.out.println("filePath================" + filePath);
        InputStream in = null;
        Properties props = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath.substring(6)));
            props = new Properties();  //发送邮件的props文件
            props.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     * 发送邮件
     * @param subject 主题
     * @param content 内容
     * @return
     */
    public static boolean sendMail(String subject, String content){
        try {
            MailSenderHelper sms = new MailSenderHelper(from, passwrod);
            if(to.contains(",")){
                List<String> recipients = new ArrayList<String>();  //收件人
                String[] tos = to.split(",");
                for(int i=0, count = tos.length; i<count; i++){
                    recipients.add(tos[i]);
                }
                sms.send(recipients, subject, content);  //群发
            }else{
                sms.send(to, subject, content);  //单发
            }
        } catch (AddressException ex) {
            ex.printStackTrace();
            return false;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        System.out.println("邮件发送成功！");
        return true;
    }

    @SuppressWarnings("static-access")
    @Value("${from}")
    public void setFrom(String from) {
        this.from = from;
    }

    @SuppressWarnings("static-access")
    @Value("${passwrod}")
    public void setPasswrod(String passwrod) {
        this.passwrod = passwrod;
    }

    @SuppressWarnings("static-access")
    @Value("${to}")
    public void setTo1(String to) {
        this.to = to;
    }
}

