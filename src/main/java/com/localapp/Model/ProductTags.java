package com.localapp.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "product_tags")
@AllArgsConstructor
@NoArgsConstructor
public class ProductTags {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name="tag_id")
    int tagId;

    @Column(name = "tag")
    String tag;

}