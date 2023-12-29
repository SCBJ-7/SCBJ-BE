package com.yanolja.scbj.domain.prdouct.repository;

import com.yanolja.scbj.domain.prdouct.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
