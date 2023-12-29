package com.yanolja.scbj.domain.payment.repository;

import com.yanolja.scbj.domain.payment.entity.PaymentAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAgreementRepository extends JpaRepository<PaymentAgreement, Long> {

}
