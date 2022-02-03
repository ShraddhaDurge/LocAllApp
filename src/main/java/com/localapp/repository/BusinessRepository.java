package com.localapp.repository;

import com.localapp.model.Business;
import com.localapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("businessRepository")
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    Boolean existsById(int bid);
    Boolean existsByGstin(String gstin);
    Business findById(int bid);
    Business findByGstin(String gstin);
    List<Business> findByStatus(String status);
    Business findByUser(User user);
}