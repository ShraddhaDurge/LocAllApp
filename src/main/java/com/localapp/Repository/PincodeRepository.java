package com.localapp.Repository;

import com.localapp.Model.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("pincodeRepository")
public interface PincodeRepository extends JpaRepository<Pincode, Integer> {
}