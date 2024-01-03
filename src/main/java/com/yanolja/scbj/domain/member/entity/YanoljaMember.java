package com.yanolja.scbj.domain.member.entity;

import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class YanoljaMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "yanoljaMember", fetch = FetchType.LAZY)
    private List<Reservation> reservationList = new ArrayList<>();

    @Builder
    private YanoljaMember(Long id, String email, List<Reservation> reservationList) {
        this.id = id;
        this.email = email;
        this.reservationList = reservationList;
    }
}