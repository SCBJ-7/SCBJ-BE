package com.yanolja.scbj.domain.paymentHistory.repository;

import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAgreementRepository extends JpaRepository<PaymentAgreement, Long> {

}
