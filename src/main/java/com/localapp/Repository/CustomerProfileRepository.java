package com.localapp.Repository;

import com.localapp.Model.CustomerProfile;
import com.localapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("CustomerProfileRepository")
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Integer> {

    CustomerProfile findByUser(User user);
}
