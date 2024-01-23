package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.global.config.SchedulerConfig;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final AlarmService alarmService;

    @Scheduled(cron = "${schedule.cron}")
    @Transactional
    public void settlementPaymentHistorySchedule() {
        List<PaymentHistory> targetPaymentHistoryList
            = paymentHistoryRepository.findPaymentHistoriesWithNotSettlement();

        for (PaymentHistory paymentHistory : targetPaymentHistoryList) {
            paymentHistory.processSettlement();
            Long memberId = paymentHistory.getProduct().getMember().getId();
            alarmService.createAlarm(memberId, paymentHistory.getId(),
                new Data("정산 완료", paymentHistory.getProductName() + "의 정산이 완료되었습니다.",
                    LocalDateTime.now()));
        }
    }

}
