package com.yanolja.scbj.domain.alarm.service;

import com.yanolja.scbj.domain.alarm.dto.AlarmHasNonReadResponse;
import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.alarm.exception.AlarmNotFoundException;
import com.yanolja.scbj.domain.alarm.repository.AlarmRepository;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.alarm.dto.CheckInAlarmResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
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

    private final String CHECK_IN_ALARM_TITLE = "체크인 전 알림";
    private final String CHECK_IN_ALARM_CONTNET = "1일 후 '%s'에 체크인 할 수 있어요!";

    @Transactional
    public List<AlarmResponse> getAlarms() {
        List<Alarm> alarmsToRead = alarmRepository.getAllByMemberIdOrderByCreatedAtDesc(
                securityUtil.getCurrentMemberId())
            .orElseThrow(() -> new AlarmNotFoundException(ErrorCode.ALARM_NOT_FOUND));
        List<AlarmResponse> alarmResponses = alarmsToRead.stream().map(AlarmMapper::toAlarmResponse)
            .toList();
        alarmsToRead.forEach(Alarm::read);

        return alarmResponses;
    }

    /**
     * 알람 생성 시, 알람 DB에 알람 데이터 생성 및 푸쉬 알림을 전송합니다.
     *
     * @param memberId 푸쉬 알림을 보낼 이메일을 매개변수로 주세요.
     * @param data     생성자로만 생기며! 제목, 내용, 생성날짜를 매개변수로 생성해주세요.
     */

    @Transactional
    @Async
    public void createAlarm(long memberId, long paymentHistoryId, Data data) {
        Member memberToAlarm = memberService.getMember(memberId);

        alarmRepository.save(AlarmMapper.toAlarm(memberToAlarm,
            getPaymentHistory(paymentHistoryId), data));
        fcmService.sendMessageTo(memberToAlarm.getEmail(), data);
    }

    private PaymentHistory getPaymentHistory(long paymentHistoryId) {
        return paymentHistoryRepository.findById(paymentHistoryId)
            .orElseThrow(() -> new PaymentHistoryNotFoundException(ErrorCode.PURCHASE_LOAD_FAIL));
    }

    @Scheduled(fixedRate = 60000)
    public void AlarmBeforeCheckIn() {
        List<CheckInAlarmResponse> PaymentHistorysNeedForCheckInAlarm = paymentHistoryRepository.findPurchasedHistoriesNeedForCheckInAlarm();
        PaymentHistorysNeedForCheckInAlarm.stream().forEach(
            ph -> createAlarm(ph.memberId(), ph.productHistoryId(),
                new Data(CHECK_IN_ALARM_TITLE,
                    String.format(CHECK_IN_ALARM_CONTNET, ph.productName()),
                    LocalDateTime.now())));
    }

    public AlarmHasNonReadResponse hasNonReadAlarm() {
        return AlarmMapper.toAlarmHasNonReadResponse(
            alarmRepository.existsAlarmByMemberIdAndCheckedIsFalse(
                securityUtil.getCurrentMemberId()));
    }


}
