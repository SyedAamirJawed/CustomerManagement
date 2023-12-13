package com.customer.controller;

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

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

	@Autowired
	private OwnerRepo ownerRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	

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
		model.addAttribute("pageTitle","Signup");
		return "signup";
	}

	// Get Data Form Registration Form
	@PostMapping("/Submit")
	public String submitForm(@ModelAttribute("shopOwner") ShopOwner shopOwner, Model model, @RequestParam(value = "agreement", defaultValue = "false") boolean checkBox, HttpSession session) {
		System.out.println("User Agreement : " + checkBox);
		System.out.println(shopOwner);
		
		
		
		try {
            
			if (checkBox==false) {
				throw new Exception(" Please Accepet Term And Condition");
			}
			
			shopOwner.setSoAbout("By Defulat Massage");
			shopOwner.setSoRole("ROLE_USER");
			shopOwner.setSoImage("profiledp.png");
			shopOwner.setSoAuth(false);
			shopOwner.setSoPassword(passwordEncoder.encode(shopOwner.getSoPassword()));

			ShopOwner save = ownerRepo.save(shopOwner);
			System.out.println("Successfuly Register : " + save);
			
			
			model.addAttribute("shopOwner",new ShopOwner());
			session.setAttribute("message",  new ShowMessage("Registration Successful..! ", "alert-success"));
			
			
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("shopOwner", shopOwner);
			session.setAttribute("message",  new ShowMessage("Somthing Went Wrong : " +e.getMessage(),"alert-danger"));
			return "signup";
		}

		
	}

}
