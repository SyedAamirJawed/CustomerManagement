package com.customer.controller;


import java.security.SecureRandom;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.customer.entities.ShopOwner;
import com.customer.helper.ShowMessage;
import com.customer.repo.OwnerRepo;
import com.customer.service.SendOtp;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Password")
public class ForgotPassword {
	
	@Autowired
	private SendOtp sendOtp;
	
	@Autowired
	private OwnerRepo ownerRepo;
	
	
	@GetMapping("/Forgot")
	public String passwordForgote() {
		return "forgot-password";
	}

	
	private SecureRandom secureRandom = new SecureRandom();
	String userEmail;
	@PostMapping("/Otp")
	public String verifyOtp(@RequestParam("username")String email, HttpSession session) {
		System.out.println(email);
		userEmail=email;
		
		ShopOwner userByEmail = ownerRepo.getUserByEmail(email);
		
		
		
		if (userByEmail==null) {
			System.out.println("Account Not Found");
			session.setAttribute("message", new ShowMessage("You don't have Udhar Khaata account please Signup first ", "alert-danger"));
			return"forgot-password";
		}
		else {
			
			String soName = userByEmail.getSoName();
			// Mail Operation Here
			
			int otp = secureRandom.nextInt(900000) + 100000;
	        System.out.println(otp);
	        
	        session.setAttribute("myOTP", otp);
			
	        String from="jamiahub@gmail.com";
	        String to=email;
	        String subject="Your OTP for reset Udhar Khaata password";
	        String msg = "Hello <span style=\"font-weight: bold;\">" + soName + ",</span>"
	                + "<br>Please enter below OTP to reset your Udhar Khaata password.<br><br>"
	                + "Your OTP is : <span style=\"font-weight: bold;\">" + otp + "</span>"
	                + "<br><br>Note: This OTP is valid for the next 10 minutes only."
	                + "<br><br><div style=\"color:red; text-align:center;\">copyright &copy; BugsOP</div>";

	        
	        boolean otpSend = sendOtp.otpSend(from, to, subject, msg);
	             if (otpSend){
					System.out.println("Otp Successfuly Send");
					session.setAttribute("message", new ShowMessage("OTP successfully send..!", "alert-success"));
					return "otp-verification";
				}else {
					System.out.println("Someting Went Wrong");
					session.setAttribute("message", new ShowMessage("Somting went wrong try again..! ", "alert-danger"));
					return "forgot-password";
				}	
		}	
	}

	@PostMapping("/Updating")
	public String updatingPassword(@RequestParam("getOtp") int getOtp,HttpSession session) {
		System.out.println(getOtp);
		int oldOTP = (int)session.getAttribute("myOTP");
		System.out.println(oldOTP);
		
		if (getOtp==oldOTP) {
			System.out.println("testing");
			return "update-password";
		}else {
			session.setAttribute("message", new ShowMessage("Please enter valid OTP ", "alert-danger"));
			return "otp-verification";
		}
		
		
	}

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@PostMapping("/Update")
	public String updatePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		System.out.println(newPassword);
		
		ShopOwner userByEmail = ownerRepo.getUserByEmail(userEmail);
		userByEmail.getSoName();
		userByEmail.setSoPassword(bCryptPasswordEncoder.encode(newPassword));
		ownerRepo.save(userByEmail);

		session.setAttribute("message", new ShowMessage("Password Update Successfully..! ", "alert-success"));
		return "redirect:/Login";
	}

}
