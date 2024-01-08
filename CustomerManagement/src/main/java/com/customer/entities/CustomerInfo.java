package com.customer.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CustomerInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cId;
	private String cName;
	private String cPhone;
	private String cEmail;
	private String cImage;
	@Column(length = 1000)
	private String cDetails;
	private int cPendingAmt;
	private int cPaidAmt;
	
	
	@ManyToOne
	private ShopOwner owner;
	
	public CustomerInfo() {
		super();
	}


	public CustomerInfo(int cId, String cName, String cPhone, String cEmail, String cImage, String cDetails,
			int cPendingAmt, int cPaidAmt) {
		super();
		this.cId = cId;
		this.cName = cName;
		this.cPhone = cPhone;
		this.cEmail = cEmail;
		this.cImage = cImage;
		this.cDetails = cDetails;
		this.cPendingAmt = cPendingAmt;
		this.cPaidAmt = cPaidAmt;
	}

	
	public ShopOwner getOwner() {
		return owner;
	}
	public void setOwner(ShopOwner owner) {
		this.owner = owner;
	}


	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}


	public String getcName() {
		return cName;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}


	public String getcPhone() {
		return cPhone;
	}
	public void setcPhone(String cPhone) {
		this.cPhone = cPhone;
	}


	public String getcEmail() {
		return cEmail;
	}
	public void setcEmail(String cEmail) {
		this.cEmail = cEmail;
	}


	public String getcImage() {
		return cImage;
	}
	public void setcImage(String cImage) {
		this.cImage = cImage;
	}


	public String getcDetails() {
		return cDetails;
	}
	public void setcDetails(String cDetails) {
		this.cDetails = cDetails;
	}


	public int getcPendingAmt() {
		return cPendingAmt;
	}
	public void setcPendingAmt(int cPendingAmt) {
		this.cPendingAmt = cPendingAmt;
	}


	public int getcPaidAmt() {
		return cPaidAmt;
	}
	public void setcPaidAmt(int cPaidAmt) {
		this.cPaidAmt = cPaidAmt;
	}


	@Override
	public String toString() {
		return "CustomerInfo [cId=" + cId + ", cName=" + cName + ", cPhone=" + cPhone + ", cEmail=" + cEmail
				+ ", cImage=" + cImage + ", cDetails=" + cDetails + ", cPendingAmt=" + cPendingAmt + ", cPaidAmt="
				+ cPaidAmt + "]";
	}
	
	
	
}
