package com.customer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.customer.entities.ShopOwner;
import com.customer.repo.OwnerRepo;

public class UserDetailsSeviceImple implements UserDetailsService {

	@Autowired
	private OwnerRepo ownerRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		ShopOwner shopOwner = ownerRepo.getUserByEmail(username);

		if (shopOwner == null) {

			throw new UsernameNotFoundException("User Can't Found..!");
		}

		UserDetailsImpl userDetailsImpl = new UserDetailsImpl(shopOwner);

		return userDetailsImpl;
	}

}
