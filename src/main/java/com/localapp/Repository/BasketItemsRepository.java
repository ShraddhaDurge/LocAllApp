package com.localapp.Repository;

import java.util.List;

import com.localapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.localapp.Model.BasketItems;

public interface BasketItemsRepository extends JpaRepository<BasketItems, Integer> {
//    List<BasketItems> findByCustId(int custId);
    BasketItems getById(int basketId);
    BasketItems deleteById(int basketId);
    List<BasketItems> findByUser(User user);
}