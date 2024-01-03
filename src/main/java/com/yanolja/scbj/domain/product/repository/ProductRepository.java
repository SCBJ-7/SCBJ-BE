package com.yanolja.scbj.domain.product.repository;

import com.yanolja.scbj.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
