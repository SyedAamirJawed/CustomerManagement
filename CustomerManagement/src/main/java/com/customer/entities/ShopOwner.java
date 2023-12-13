package com.customer.entities;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import javax.validation.constraints.NotBlank;

@Entity
public class ShopOwner {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int soId;
	private String soName;
	private String soRole;
	private String soEmail;
	private String soPassword;
	@Column(length = 1000)
	private String soAbout;
	private boolean soAuth;
	private String soImage;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
	private List<CustomerInfo> costomer = new ArrayList<>();

	
	public ShopOwner(int soId, String soName, String soRole, String soEmail, String soPassword, String soAbout,
			boolean soAuth, String soImage, List<CustomerInfo> costomer) {
		super();
		this.soId = soId;
		this.soName = soName;
		this.soRole = soRole;
		this.soEmail = soEmail;
		this.soPassword = soPassword;
		this.soAbout = soAbout;
		this.soAuth = soAuth;
		this.soImage = soImage;
		this.costomer = costomer;
	}

	public ShopOwner() {
		super();
	}

	public int getSoId() {
		return soId;
	}
	public void setSoId(int soId) {
		this.soId = soId;
	}

	public String getSoName() {
		return soName;
	}
	public void setSoName(String soName) {
		this.soName = soName;
	}

	public String getSoRole() {
		return soRole;
	}
	public void setSoRole(String soRole) {
		this.soRole = soRole;
	}

	public String getSoEmail() {
		return soEmail;
	}
	public void setSoEmail(String soEmail) {
		this.soEmail = soEmail;
	}

	public String getSoPassword() {
		return soPassword;
	}
	public void setSoPassword(String soPassword) {
		this.soPassword = soPassword;
	}

	
	public String getSoAbout() {
		return soAbout;
	}

	public void setSoAbout(String soAbout) {
		this.soAbout = soAbout;
	}

	public boolean isSoAuth() {
		return soAuth;
	}

	public void setSoAuth(boolean soAuth) {
		this.soAuth = soAuth;
	}

	public String getSoImage() {
		return soImage;
	}

	public void setSoImage(String soImage) {
		this.soImage = soImage;
	}

	public List<CustomerInfo> getCostomer() {
		return costomer;
	}

	public void setCostomer(List<CustomerInfo> costomer) {
		this.costomer = costomer;
	}

	@Override
	public String toString() {
		return "ShopOwner [soId=" + soId + ", soName=" + soName + ", soRole=" + soRole + ", soEmail=" + soEmail
				+ ", soPassword=" + soPassword + ", soAbout=" + soAbout + ", soAuth=" + soAuth + ", soImage=" + soImage
				+ ", costomer=" + costomer + "]";
	}

}
