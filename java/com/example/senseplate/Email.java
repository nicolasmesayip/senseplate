package com.example.senseplate;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    //final String emailHost = "smtp.gmail.com";
    final String emailHost = "smtp.office365.com";


    String fromEmail;
    String fromPassword;
    //List<String> toEmailList;
    String toEmailList;
    String emailSubject;
    String emailBody;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public Email() {

    }
    public Email(String fromEmail, String fromPassword,
                 String toEmailList, String emailSubject, String emailBody) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
//        for (String toEmail : toEmailList) {
//            Log.i("GMail", "toEmail: " + toEmail);
//            emailMessage.addRecipient(Message.RecipientType.TO,
//                    new InternetAddress(toEmail));
//        }
        emailMessage.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmailList));
        emailMessage.setSubject(emailSubject);
        String emailcontent = "<table border=\"1\" width=\"100%\" style=\"background-color: #e1e3e6;\">\n" +
                "\t\t<td>\n" +
                "\t\t<table width=\"90%\" align=\"center\" style=\"border-collapse: collapse; background-color: #ffffff;\">\n" +
                "\t\t\t<tr> <td align=\"center\" bgcolor=\"#70bbd9\" style=\"padding: 40px 0 30px 0;\"> <img src=\"http://nam33.student.eda.kent.ac.uk/Logo.png\" alt=\"Creating Email Magic\" width=\"500\" height=\"230\" style=\"display: block;\" /></td></tr>\n" +
                "\t\t\t<tr> <td align=\"center\" style=\"padding-top: 20px;font-size: 25px;\"> Thank you for registering on the SensePlate app. <br> Your verification code is the following:</td></tr>\n" +
                "\t\t\t<tr> <td align=\"center\" style=\"font-size: 40px; font-weight: bold; padding-top: 20px; padding-bottom: 20px;\" > 803491</td></tr>\n" +
                "\t\t\t<tr> <td align=\"center\" style=\"font-size: 25px; padding-bottom: 20px;\"> This is an automatic email, if you received this email by error, just ignore it.</td></tr>\n" +
                "\t\t</table>\n" +
                "\t</td>\n" +
                "\t</table>";
        emailMessage.setContent(emailBody, "text/html");// for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }
    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

}