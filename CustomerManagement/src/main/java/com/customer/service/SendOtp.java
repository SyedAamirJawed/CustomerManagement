package com.customer.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class SendOtp {
	
	public boolean otpSend(String from, String to, String subject, String msg) {
		boolean result = false;
		String username="jamiahub";
		String password="dodasc****mspvxq";
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		

		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			//message.setText(msg);
			message.setContent(msg,"text/html");

			Transport.send(message);
			
			System.out.println("Otp Send To : "+to);
			result=true;
			return result;

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
	}

}
