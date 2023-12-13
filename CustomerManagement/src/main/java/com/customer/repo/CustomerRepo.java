package com.customer.repo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.customer.entities.CustomerInfo;

public interface CustomerRepo extends JpaRepository<CustomerInfo, Integer> {

	@Query("FROM CustomerInfo as c WHERE c.owner.soId =:ownerId")
	public Page<CustomerInfo> getCustomerListById(@Param("ownerId") int id, Pageable pageable);
	
	
}
