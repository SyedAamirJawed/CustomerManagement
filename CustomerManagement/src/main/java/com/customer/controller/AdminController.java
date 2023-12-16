package com.customer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@RequestMapping("/Index")
	public String userDashord() {
		System.out.println("Admin Dashbord Page Executed..!");
		return "admin/admin-dashbord";
	}
	
}
