package com.yanolja.scbj.domain.alarm.service;

import com.yanolja.scbj.domain.alarm.dto.AlarmHasNonReadResponse;
import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.dto.CheckInAlarmResponse;
import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.alarm.repository.AlarmRepository;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final SecurityUtil securityUtil;
    private final FCMService fcmService;
    private final MemberService memberService;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final String CHECK_IN_ALARM_TITLE = "체크인 전 알림";
    private final String CHECK_IN_ALARM_CONTNET = "1일 후 '%s'에 체크인 할 수 있어요!";
    private final String CHECK_IN_REDIS_PREFIX = "CHECKED: ";
    private final long CHECK_IN_FIXED_RATE = 3_0000;

    
    
    @Value("${check-in.expiration}")
    private long CHECK_IN_EXPIRATION;


    @Transactional
    public List<AlarmResponse> getAlarms() {
        List<Alarm> alarmsToRead = alarmRepository.getAllByMemberIdOrderByCreatedAtDesc(
                securityUtil.getCurrentMemberId());
        List<AlarmResponse> alarmResponses = alarmsToRead.stream().map(AlarmMapper::toAlarmResponse)
            .toList();

        alarmsToRead.forEach(Alarm::read);

        return alarmResponses;
    }

    @Transactional
    public void createAlarm(long memberId, long paymentHistoryId, Data data) {
        Member memberToGetAlarm = memberService.getMember(memberId);

        alarmRepository.save(AlarmMapper.toAlarm(memberToGetAlarm,
            getPaymentHistory(paymentHistoryId), data));
        fcmService.sendMessageTo(memberToGetAlarm.getEmail(), data);
    }

    private PaymentHistory getPaymentHistory(long paymentHistoryId) {
        return paymentHistoryRepository.findById(paymentHistoryId)
            .orElseThrow(() -> new PaymentHistoryNotFoundException(ErrorCode.PURCHASE_LOAD_FAIL));
    }

    @Scheduled(fixedRate = CHECK_IN_FIXED_RATE)
    public void AlarmBeforeCheckIn() {
        List<CheckInAlarmResponse> PaymentHistorysNeedForCheckInAlarm = paymentHistoryRepository.findPurchasedHistoriesNeedForCheckInAlarm();
        PaymentHistorysNeedForCheckInAlarm.stream().forEach(
            ph -> {
                if(isUnCheckedBefore(ph.productHistoryId())){
                    saveCheckLog(ph.productHistoryId());
                    createAlarm(ph.memberId(), ph.productHistoryId(),
                        new Data(CHECK_IN_ALARM_TITLE,
                            String.format(CHECK_IN_ALARM_CONTNET, ph.productName()),
                            LocalDateTime.now()));
                }
            });
    }

    public AlarmHasNonReadResponse hasNonReadAlarm() {
        return AlarmMapper.toAlarmHasNonReadResponse(
            alarmRepository.existsAlarmByMemberIdAndCheckedIsFalse(
                securityUtil.getCurrentMemberId()));
    }

    private boolean isUnCheckedBefore(Long productHistoryId) {
        return redisTemplate.opsForValue().get(CHECK_IN_REDIS_PREFIX+String.valueOf(productHistoryId)) == null;
    }

    private void saveCheckLog(Long productHistoryId) {
        redisTemplate.opsForValue().set(CHECK_IN_REDIS_PREFIX+String.valueOf(productHistoryId), true, CHECK_IN_EXPIRATION, TimeUnit.MILLISECONDS);
    }


}
