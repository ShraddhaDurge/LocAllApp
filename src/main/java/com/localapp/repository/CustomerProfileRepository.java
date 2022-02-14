package com.localapp.repository;

import com.localapp.model.CustomerProfile;
import com.localapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("CustomerProfileRepository")
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Integer> {

    CustomerProfile findByUser(User user);
}
