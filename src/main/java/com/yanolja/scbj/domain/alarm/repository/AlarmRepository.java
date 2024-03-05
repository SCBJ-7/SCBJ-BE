package com.yanolja.scbj.domain.alarm.repository;

import com.yanolja.scbj.domain.alarm.entity.Alarm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> getAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    boolean existsAlarmByMemberIdAndCheckedIsFalse(Long memeberId);

}
