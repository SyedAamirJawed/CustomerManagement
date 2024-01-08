package com.customer.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.customer.entities.CustomerInfo;
import com.customer.entities.ShopOwner;
import com.customer.helper.ShowMessage;
import com.customer.repo.CustomerRepo;
import com.customer.repo.OwnerRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/User")
@PreAuthorize("hasRole('USER')")
public class UserController {

	@Autowired
	private OwnerRepo ownerRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void commonData(Model model, Principal principal) {
		String soEmail = principal.getName();
		ShopOwner shopOwner = ownerRepo.getUserByEmail(soEmail);
		model.addAttribute("shopOwner", shopOwner);
	}

	@GetMapping("/Index")
	public String userDashord(Model model, Principal principal) {
		String username = principal.getName();
		ShopOwner owner = ownerRepo.getUserByEmail(username);
		List<CustomerInfo> costomerList = owner.getCostomer();

		int allPendingAmt = 0;
		int allPaidAmt = 0;
		int totalCustomer = 0;
		for (CustomerInfo customerInfo : costomerList) {

			int getcPendingAmt = customerInfo.getcPendingAmt();
			int getcPaidAmt = customerInfo.getcPaidAmt();
			allPaidAmt += getcPaidAmt;
			allPendingAmt += getcPendingAmt;
			totalCustomer++;
		}

		int netAmt = (allPaidAmt - allPendingAmt);

		model.addAttribute("pAmount", allPendingAmt);
		model.addAttribute("rAmount", allPaidAmt);
		model.addAttribute("tAmount", netAmt);
		model.addAttribute("ownerData", owner);
		model.addAttribute("tCustomer", totalCustomer);

		return "user/user-dashbord";
	}

	@GetMapping("/Add")
	public String addCustomer(Model model) {
		System.out.println("Add Customer Page Executed..!");
		model.addAttribute("customer", new CustomerInfo());
		model.addAttribute("pageTitle", "Add Customer");
		return "user/add-costomer";
	}

	@PostMapping("/Process")
	public String processData(@ModelAttribute CustomerInfo customer,
			@RequestParam("profileDp") MultipartFile profileImage, Principal principal, HttpSession session) {
		System.out.println(customer);

		try {

			String soEmail = principal.getName();
			ShopOwner shopOwner = ownerRepo.getUserByEmail(soEmail);
			customer.setOwner(shopOwner);

			// Processing and uploading profile picture
			if (profileImage.isEmpty()) {
				System.out.println("Profile Not Uploaded");
				customer.setcImage("profile-pic.png");
			} 
			else {
				customer.setcImage(profileImage.getOriginalFilename());
				File saveFile = new ClassPathResource("/static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + System.currentTimeMillis()/1000 + profileImage.getOriginalFilename());
				long copy = Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println(copy);
				customer.setcImage(System.currentTimeMillis()/1000 +profileImage.getOriginalFilename());
				System.out.println("Profile Upload Successfuly");
			}

			shopOwner.getCostomer().add(customer);
			ownerRepo.save(shopOwner);
			session.setAttribute("message", new ShowMessage("New Customer Successfully Added..!", "alert-success"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new ShowMessage("Somthing Went Wrong..! Try Again ", "alert-danger"));
		}

		return "user/add-costomer";
	}

	@GetMapping("List/{pageNo}")
	public String customerList(@PathVariable("pageNo") Integer pageNum, Model model, Principal principal) {
		System.out.println("Open Customer List Page ");
        
		String userName = principal.getName();
		ShopOwner owner = ownerRepo.getUserByEmail(userName);

		Pageable pageable = PageRequest.of(pageNum - 1, 8);

		Page<CustomerInfo> customerListByOwnerId = customerRepo.getCustomerListById(owner.getSoId(), pageable);
		model.addAttribute("cutomerList", customerListByOwnerId);
		model.addAttribute("currentPage", pageNum - 1);
		model.addAttribute("totalPage", customerListByOwnerId.getTotalPages());

		List<CustomerInfo> costomerList = owner.getCostomer();

		int allPendingAmt = 0;
		int allPaidAmt = 0;
		for (CustomerInfo customerInfo : costomerList) {
			int getcPendingAmt = customerInfo.getcPendingAmt();
			int getcPaidAmt = customerInfo.getcPaidAmt();
			allPaidAmt += getcPaidAmt;
			allPendingAmt += getcPendingAmt;
		}

		int netAmt = (allPaidAmt - allPendingAmt);

		model.addAttribute("allPaidAmt", allPaidAmt);
		model.addAttribute("allPendingAmt", allPendingAmt);
		model.addAttribute("netAmt", netAmt);
		model.addAttribute("pageTitle", "Customer List");
		return "/user/customer-list";

	}

// Individual Customer Info
	@SuppressWarnings("deprecation")
	@GetMapping("/{userId}")
	public String individualCutomer(@PathVariable("userId") Integer id, Model model, Principal principal) {
		System.out.println(id);
        System.out.println(principal);
		CustomerInfo oneCustomer = customerRepo.getById(id);

		String username = principal.getName();

		ShopOwner userByEmail = ownerRepo.getUserByEmail(username);

		// Authorization
		if (oneCustomer.getOwner().getSoId() == userByEmail.getSoId()) {
			model.addAttribute("oneCustomerInfo", oneCustomer);
			model.addAttribute("rAmount", oneCustomer.getcPaidAmt());
			model.addAttribute("pAmount", oneCustomer.getcPendingAmt());
			model.addAttribute("tAmount", oneCustomer.getcPendingAmt() - oneCustomer.getcPaidAmt());
			model.addAttribute("pageTitle", oneCustomer.getcName());
		}
		return "/user/ndividual-customer";
	}

	@GetMapping("Delete/{userId}")
	public String deleteCustomer(@PathVariable("userId") Integer id, Principal principal, HttpSession session) {

		String username = principal.getName();
		ShopOwner userByEmail = ownerRepo.getUserByEmail(username);

		Optional<CustomerInfo> findById = customerRepo.findById(id);
		CustomerInfo customerInfo = findById.get();

		if (userByEmail.getSoId() == customerInfo.getOwner().getSoId()) {
			customerRepo.deleteById(id);
			System.out.println("Delete Successfully..!");
			session.setAttribute("message", new ShowMessage("Customer Data Successfully Deleted..! ", "alert-success"));
		}
		return "redirect:/User/List/1";
	}

//Updating Pending Only
	@GetMapping("Pending/{userId}")
	public String pendingAmount(@PathVariable("userId") Integer id, Model model, HttpSession session) {
		System.out.println("Edit Pending Amount");

		CustomerInfo oneCustomer = customerRepo.findById(id).get();
		model.addAttribute("oneCustomerInfo", oneCustomer);
		model.addAttribute("rAmount", oneCustomer.getcPaidAmt());
		model.addAttribute("pAmount", oneCustomer.getcPendingAmt());
		model.addAttribute("tAmount", oneCustomer.getcPendingAmt() - oneCustomer.getcPaidAmt());
		model.addAttribute("pageTitle", oneCustomer.getcName());

		session.setAttribute("message", new ShowMessage("Pending Amount Successfully Update..! ", "alert-success"));

		model.addAttribute("pageTitle", "Update | " + oneCustomer.getcName());
		return "/user/update-pendingAmt";
	}

//Updating Received Amount
	@GetMapping("Recevied/{userId}")
	public String reveivedAmount(@PathVariable("userId") Integer id, Model model, HttpSession session) {
		System.out.println("Edit Recevied Amount");

		CustomerInfo oneCustomer = customerRepo.findById(id).get();
		model.addAttribute("oneCustomerInfo", oneCustomer);
		model.addAttribute("rAmount", oneCustomer.getcPaidAmt());
		model.addAttribute("pAmount", oneCustomer.getcPendingAmt());
		model.addAttribute("tAmount", oneCustomer.getcPendingAmt() - oneCustomer.getcPaidAmt());
		model.addAttribute("pageTitle", oneCustomer.getcName());

		session.setAttribute("message", new ShowMessage("Recevied Amount Successfully Update..! ", "alert-success"));
		model.addAttribute("pageTitle", "Update | " + oneCustomer.getcName());
		return "/user/update-receivedAmt";
	}

//Processing Amount Update
	@PostMapping("PendingAmount/{userId}")
	public String updatingPeinding(Model model, @PathVariable("userId") Integer id,
			@RequestParam("addPending") Integer pendingAdd) {

		CustomerInfo oneCustomer = customerRepo.findById(id).get();
		model.addAttribute("oneCustomerInfo", oneCustomer);
		model.addAttribute("rAmount", oneCustomer.getcPaidAmt());
		model.addAttribute("pAmount", oneCustomer.getcPendingAmt());
		model.addAttribute("tAmount", oneCustomer.getcPendingAmt() - oneCustomer.getcPaidAmt());
		model.addAttribute("pageTitle", oneCustomer.getcName());

		oneCustomer.setcPendingAmt(oneCustomer.getcPendingAmt() + pendingAdd);
		customerRepo.save(oneCustomer);

		return "redirect:/User/" + id;
	}

	@PostMapping("ReceivedAmount/{userId}")
	public String updatingReceived(Model model, @PathVariable("userId") Integer id,
			@RequestParam("addReceive") Integer receiveAdd) {

		CustomerInfo oneCustomer = customerRepo.findById(id).get();
		model.addAttribute("oneCustomerInfo", oneCustomer);
		model.addAttribute("rAmount", oneCustomer.getcPaidAmt());
		model.addAttribute("pAmount", oneCustomer.getcPendingAmt());
		model.addAttribute("tAmount", oneCustomer.getcPendingAmt() - oneCustomer.getcPaidAmt());
		model.addAttribute("pageTitle", oneCustomer.getcName());

		oneCustomer.setcPaidAmt(oneCustomer.getcPaidAmt() + receiveAdd);
		customerRepo.save(oneCustomer);
		return "redirect:/User/" + id;
	}

	
	
	// Updating All data
	@GetMapping("Update/{userId}")
	public String updateCustomer(@PathVariable("userId") Integer id, Model model) {
		CustomerInfo customerInfo = customerRepo.findById(id).get();
		model.addAttribute("oldData", customerInfo);
		model.addAttribute("pageTitle", "Update | " + customerInfo.getcName());
		return "/user/update-profile";
	}
	@PostMapping("Updating/{userId}")
	public String updatingData(@PathVariable("userId") Integer id, Model model,
			@ModelAttribute("cutomer") CustomerInfo customerNew, @RequestParam("profileDp") MultipartFile profilePic,
			HttpSession session) throws IOException {

		CustomerInfo customerOld = customerRepo.findById(id).get();

		customerNew.setcId(customerOld.getcId());
		customerNew.setOwner(customerOld.getOwner());

		String newProfile = profilePic.getOriginalFilename();
		if (newProfile.isEmpty()) {
			customerNew.setcImage(customerOld.getcImage());
		} else {
			File saveFile = new ClassPathResource("/static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator +System.currentTimeMillis()/1000 + profilePic.getOriginalFilename());
			Files.copy(profilePic.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			customerNew.setcImage(System.currentTimeMillis()/1000 +newProfile);

			// Delete Old Profile Photo
			File oldPicturePath = new ClassPathResource("/static/img").getFile();
			File deleteOldFile = new File(oldPicturePath, customerOld.getcImage());

			if (!deleteOldFile.getName().equals("profile-pic.png")) {
				deleteOldFile.delete();
				System.out.println("Old Profile Picture Deleted");
			}

			System.out.println("Profile Upload Successfuly");
		}

		customerRepo.save(customerNew);
		session.setAttribute("message", new ShowMessage("Update Successfully..! ", "alert-success"));

		return "redirect:/User/" + id;
	}

	
	
	
	
	@GetMapping("/Profile")
	public String profile(Principal principal, Model model) {

		String username = principal.getName();
		ShopOwner owner = ownerRepo.getUserByEmail(username);

		List<CustomerInfo> costomerList = owner.getCostomer();

		int allPendingAmt = 0;
		int allPaidAmt = 0;
		for (CustomerInfo customerInfo : costomerList) {
			int getcPendingAmt = customerInfo.getcPendingAmt();
			int getcPaidAmt = customerInfo.getcPaidAmt();
			allPaidAmt += getcPaidAmt;
			allPendingAmt += getcPendingAmt;
		}

		int netAmt = (allPaidAmt - allPendingAmt);

		model.addAttribute("pAmount", allPendingAmt);
		model.addAttribute("rAmount", allPaidAmt);
		model.addAttribute("tAmount", netAmt);
		model.addAttribute("ownerData", owner);

		return "/user/owner-profile";
	}

	
	
	
	
	@GetMapping("/UpdateProfile")
	public String updateOwnerProfile() {
		return "/user/owner-profileUpdate";
	}
	@PostMapping("UpdatingProfile")
	public String updatingOwnerProfile(@ModelAttribute("owner") ShopOwner ownerNew,
			@RequestParam("profileDp") MultipartFile profilePic, Principal principal, HttpSession session)
			throws Exception {
		System.out.println("update adim");
		String username = principal.getName();

		ShopOwner owner = ownerRepo.getUserByEmail(username);
		ownerNew.setSoId(owner.getSoId());
		ownerNew.setSoEmail(owner.getSoEmail());
		ownerNew.setCostomer(owner.getCostomer());
		ownerNew.setSoRole(owner.getSoRole());
		ownerNew.setSoPassword(owner.getSoPassword());
		ownerNew.setSoAuth(false);
		ownerNew.setSoRole(owner.getSoRole());
		ownerNew.setCostomer(owner.getCostomer());

		String newProfile = profilePic.getOriginalFilename();
		System.out.println(newProfile);

		if (newProfile.isEmpty()) {
			ownerNew.setSoImage(owner.getSoImage());
		} else {
			System.out.println("enter elase Block");
			File saveFile = new ClassPathResource("/static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + System.currentTimeMillis()/1000 + profilePic.getOriginalFilename());
			System.out.println(path);
			Files.copy(profilePic.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			ownerNew.setSoImage(System.currentTimeMillis()/1000 + newProfile);

			File oldPicturePath = new ClassPathResource("/static/img").getFile();
			File deleteOldFile = new File(oldPicturePath, owner.getSoImage());

			if (!deleteOldFile.getName().equals("profile-pic.png")) {
				deleteOldFile.delete();
				System.out.println("Old Profile Picture Deleted");
			}

		}
		session.setAttribute("message", new ShowMessage("Profile Successfully Update..! ", "alert-success"));
		ownerRepo.save(ownerNew);
		System.out.println("Profile Upload Successfuly");

		return "redirect:/User/Profile";
	}

	
	
	@GetMapping("/ChangePassword")
	public String changePassword() {
		return "/user/change-password";
	}
	@PostMapping("/UpdatingPassword")
	public String updatingPassword(Principal principal, @RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, HttpSession session) {

		ShopOwner userByEmail = ownerRepo.getUserByEmail(principal.getName());

		if (bCryptPasswordEncoder.matches(oldPassword, userByEmail.getSoPassword())) {
           
			userByEmail.setSoPassword(bCryptPasswordEncoder.encode(newPassword));
			ownerRepo.save(userByEmail);
			session.setAttribute("message", new ShowMessage("Password Successfully Update..! ", "alert-success"));
			
		}else {
			session.setAttribute("message", new ShowMessage("Enter Correct Old Password..!", "alert-danger"));
			
		}

		return "redirect:/User/Profile";
	}

}
