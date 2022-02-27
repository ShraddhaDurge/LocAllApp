package com.localapp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localapp.Model.CategoryTags;

@Repository("productCategoryTagsRepository")
public interface CategoryTagsRepository extends JpaRepository<CategoryTags, Integer>{
    List<CategoryTags> findAll();
}