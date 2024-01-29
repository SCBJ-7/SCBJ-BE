package com.yanolja.scbj.domain.reservation.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.domain.reservation.util.ReservationMapper;
import com.yanolja.scbj.global.exception.ErrorCode;
import io.netty.util.internal.StringUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final int HOURS_OF_DAY = 24;
    private final int RESERVATION_IMAGE = 0;
    private final int NOT_REMAIN = 0;

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ReservationFindResponse> getReservation(Long memberId) {

        Member currentMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<Reservation> targetReservationList = reservationRepository.findByYanoljaMemberId(
            currentMember.getYanoljaMember().getId());

        List<ReservationFindResponse> reservationResList = new ArrayList<>();

        for (Reservation reservation : createNotProductListOf(targetReservationList)) {
            int remainingDay = (int) Duration.between(LocalDateTime.now(),
                reservation.getStartDate()).toDays();

            if (remainingDay >= NOT_REMAIN) {
                reservationResList.add(addReservationList(reservation, remainingDay));
            }
        }

        return reservationResList;
    }

    private List<Reservation> createNotProductListOf(List<Reservation> reservationList) {

        List<Reservation> reservationListOfNotProduct = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if(checkProduct(reservation)){
                reservationListOfNotProduct.add(reservation);
            }
        }
        return reservationListOfNotProduct;
    }

    private boolean checkProduct(Reservation reservation){
        Optional<Product> getProduct = productRepository.findByReservationId(reservation.getId());
        if(getProduct.isPresent()){
            return false;
        }
        return true;
    }

    private String getImageUrl(List<HotelRoomImage> hotelRoomImageList) {

        if (hotelRoomImageList.isEmpty()) {
            return StringUtil.EMPTY_STRING;
        }
        return hotelRoomImageList.get(RESERVATION_IMAGE).getUrl();
    }

    private double getRefundPrice(Reservation reservation, List<RefundPolicy> refundPolicyList,
        int remainingDay) {

        double refundPrice = reservation.getPurchasePrice();
        for (RefundPolicy refundPolicy : refundPolicyList) {
            if (refundPolicy.getBaseDate() == remainingDay) {
                refundPrice = refundPolicy.getPercent() * 0.01 * reservation.getPurchasePrice();
            }
        }
        return refundPrice;
    }

    private ReservationFindResponse addReservationList(Reservation reservation, int remainingDay) {

        Hotel foundHotel = reservation.getHotel();

        Room foundRoom = reservation.getHotel().getRoom();

        List<RefundPolicy> refundPolicyList = foundHotel.getHotelRefundPolicyList();

        String imageUrl = getImageUrl(foundHotel.getHotelRoomImageList());

        int remainingTimes =
            remainingDay * HOURS_OF_DAY + Duration.between(LocalDateTime.now(),
                reservation.getStartDate()).toHoursPart();

        int refundPrice = (int) getRefundPrice(reservation, refundPolicyList, remainingDay);

        return ReservationMapper.toReservationFindResponse(reservation, foundHotel, foundRoom,
            imageUrl, remainingDay, remainingTimes, refundPrice);
    }
}
