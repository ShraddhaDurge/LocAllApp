package com.localapp.Repository;

import com.localapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {

	Boolean existsByEmail(String email);

	User findByEmail(String email);

	User findById(int id);
}