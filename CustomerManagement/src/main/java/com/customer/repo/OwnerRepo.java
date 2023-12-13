package com.customer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.customer.entities.ShopOwner;

public interface OwnerRepo extends JpaRepository<ShopOwner, Integer> {

	@Query("SELECT u FROM ShopOwner u WHERE u.soEmail = :email")
	public ShopOwner getUserByEmail(@Param("email") String email);
}
