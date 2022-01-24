package com.localapp.repository;

import com.localapp.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("businessRepository")
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    Boolean existsById(int bid);
    Boolean existsByGstin(String gstin);
    Business findById(int bid);
    Business findByGstin(String gstin);
}