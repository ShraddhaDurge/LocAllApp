package com.localapp.repository;

import com.localapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {

	Boolean existsByEmail(String email);

	User findByEmail(String email);

	User findById(int id);
}