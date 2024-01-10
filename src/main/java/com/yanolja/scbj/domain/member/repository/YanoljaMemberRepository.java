package com.yanolja.scbj.domain.member.repository;

import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YanoljaMemberRepository extends JpaRepository<YanoljaMember, Long> {

    Optional<YanoljaMember> findByEmail(final String email);

}
