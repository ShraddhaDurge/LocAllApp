package com.localapp.Repository;

import java.util.List;

import com.localapp.Model.ProductTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("productCategoryTagsRepository")
public interface ProductTagsRepository extends JpaRepository<ProductTags, Integer>{
    List<ProductTags> findAll();
}