package com.yanolja.scbj.domain.alarm.repository;

import com.yanolja.scbj.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
