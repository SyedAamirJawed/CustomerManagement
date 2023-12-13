package com.customer.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.customer.entities.ShopOwner;

public class UserDetailsImpl implements UserDetails {

	private ShopOwner shopOwner;

	
	
	public UserDetailsImpl(ShopOwner shopOwner) {
		super();
		this.shopOwner = shopOwner;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(shopOwner.getSoRole());

		return List.of(simpleGrantedAuthority);
	}

	@Override
	public String getPassword() {
		String soPassword = shopOwner.getSoPassword();
		return soPassword;
	}

	@Override
	public String getUsername() {
		String soEmail = shopOwner.getSoEmail();
		return soEmail;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
