package com.customer.controller;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.customer.entities.ShopOwner;
import com.customer.helper.ShowMessage;
import com.customer.repo.OwnerRepo;
import com.customer.service.SendOtp;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

	@Autowired
	private OwnerRepo ownerRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private SendOtp sendOtp;

	@GetMapping("/")
	public String homePage(Model model) {
		System.out.println("Home Page Executed");
		model.addAttribute("pageTitle", "Home");
		return "home";
	}

	@GetMapping("/Login")
	public String logIn(Model model) {
		System.out.println("Login Page Executed");
		model.addAttribute("pageTitle", "Login");
		return "login";
	}

	@GetMapping("/About")
	public String aboutPage(Model model) {
		System.out.println("About Page Executed");
		model.addAttribute("pageTitle", "About");
		return "about";
	}

	@GetMapping("/Signup")
	public String signUp(Model model) {
		System.out.println("Signup Page Executed");
		model.addAttribute("shopOwner", new ShopOwner());
		model.addAttribute("pageTitle", "Signup");
		return "signup";
	}

	
	
// Get Data Form Registration Form
	private SecureRandom secureRandom = new SecureRandom();
	int tempOtp;
	@PostMapping("/Submit")
	public String submitForm(@ModelAttribute("shopOwner") ShopOwner shopOwner, Model model,
			@RequestParam(value = "agreement", defaultValue = "false") boolean checkBox, HttpSession session) {

		ShopOwner newuser = ownerRepo.getUserByEmail(shopOwner.getSoEmail());

		if (newuser == null) {
				String userByEmail = shopOwner.getSoEmail();
	
				String soName = shopOwner.getSoName();
				
				// Mail Operation Here
				int otp = secureRandom.nextInt(900000) + 100000;
				System.out.println(otp);
				tempOtp = otp;
	
				String from = "udhaarkhaata@gmail.com";
				String to = userByEmail;
				String subject = "signup OTP for Udhaar Khaata";
				String msg = "Hello <span style=\"font-weight: bold;\">" + soName + ",</span>"
							+ "<br>Please enter below OTP to create your Udhaar Khaata account.<br><br>"
							+ "Your OTP is : <span style=\"font-weight: bold;\">" + otp + "</span>"
							+ "<br><br>Note: This OTP is valid for the next 2 minutes only."
							+ "<br><br><div style=\"color:red; text-align:center;\">copyright &copy; BugsOP</div>";
	
				boolean otpSend = sendOtp.otpSend(from, to, subject, msg);
				      if (otpSend) {
				    	  session.setAttribute("message", new ShowMessage("OTP successfully send..! ", "alert-success"));
							return "signup-otp-verification";

					    }else {
					    	 session.setAttribute("message", new ShowMessage("Somting is wrong please try again ", "alert-danger"));
								return "signup";
						}
							
		} else {
			session.setAttribute("message",
					new ShowMessage("User already exists please login here ", "alert-danger"));
			return "login";
		}

	}

	
	// OTP verification and save in database
	@PostMapping("/Submiting")
	public String finalSubmit(@ModelAttribute("shopOwner") ShopOwner shopOwner, Model model,
			@RequestParam(value = "agreement", defaultValue = "false") boolean checkBox,
			@RequestParam("getOtp") int getOtp, HttpSession session) {

		try {

			if (checkBox == false) {
				throw new Exception(" Please Accepet Term And Condition");
			}
			System.out.println(tempOtp);
			if (getOtp == tempOtp) {
				System.out.println("inside verifaction ");

				shopOwner.setSoAbout("By Defulat Massage");
				shopOwner.setSoRole("ROLE_USER");
				shopOwner.setSoImage("profile-pic.png");
				shopOwner.setSoAuth(false);
				shopOwner.setSoPassword(passwordEncoder.encode(shopOwner.getSoPassword()));

				ShopOwner save = ownerRepo.save(shopOwner);
				System.out.println(shopOwner);
				System.out.println("Successfuly Register : " + save);

				model.addAttribute("shopOwner", new ShopOwner());
				session.setAttribute("message", new ShowMessage("Registration Successful..! ", "alert-success"));

				return "login";
			} else {
				session.setAttribute("message", new ShowMessage("Please enter valid OTP ", "alert-danger"));
				return "signup-otp-verification";
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("shopOwner", shopOwner);
			session.setAttribute("message", new ShowMessage("Somthing Went Wrong : " + e.getMessage(), "alert-danger"));
			return "signup-otp-verification";
		}

	}

}
