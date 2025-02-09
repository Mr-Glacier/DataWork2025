package org.mrglacier.until;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote 邮件推送工具类
 * @since 2025/2/8 16:23
 */
public class SendEmailUtils {

    /**
      JavaMail 版本: 1.6.0
      JDK 版本: JDK 1.7 以上（必须）
     */
    public static String myEmailSMTPHost = "smtp.163.com";


    /**
     * 发送邮件工具类
     *
     * @param message 文本信息
     * @return boolean 邮件发送成功返回true，失败返回false
     */
    public static boolean methodSendEmail(String title, String message, String myEmailAccount, String myEmailPassword, String receiveMailAccount) {
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.host", myEmailSMTPHost);
            // 需要请求认证
            props.setProperty("mail.smtp.auth", "true");

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(props);
            session.setDebug(false);

            // 3. 创建一封邮件
            MimeMessage mimeMessage = createMimeMessage(session, myEmailAccount, receiveMailAccount,title, message);

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();

            transport.connect(myEmailAccount, myEmailPassword);

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

            // 7. 关闭连接
            transport.close();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session     和服务器交互的会话
     * @param sendMail    发件人邮箱
     * @param receiveMail 收件人邮箱
     * @param title       邮件主题
     * @param mailContent  邮件正文
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,String title, String mailContent) throws Exception {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time = formatter.format(date);

        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
        message.setFrom(new InternetAddress(sendMail, "ryk_windows", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "ryk_phone", "UTF-8"));

        // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
        message.setSubject(title +"-"+time, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
        message.setContent(mailContent, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }


}
