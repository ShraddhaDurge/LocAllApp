package com.localapp.Repository;

import com.localapp.Model.Business;
import com.localapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("businessRepository")
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    Boolean existsById(int bid);
    Boolean existsByGstin(String gstin);
    Business findById(int bid);
    List<Business> findByStatus(String status);
    Business findByUser(User user);
}