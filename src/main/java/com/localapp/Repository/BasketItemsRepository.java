package com.localapp.Repository;

import java.util.List;

import com.localapp.Model.BasketItem;
import com.localapp.Model.Product;
import com.localapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemsRepository extends JpaRepository<BasketItem, Integer> {
//    List<BasketItem> findByCustId(int custId);
    BasketItem getById(int basketId);
    BasketItem deleteById(int basketId);
    List<BasketItem> findByUser(User user);
    List<BasketItem> findByProduct(Product product);
}