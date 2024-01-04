package com.yanolja.scbj.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@Transactional
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("postProduct()는 ")
    class Context_postProduct {

        @Test
        @DisplayName("양도글 작성을 성공할 수 있다.")
        void saveProduct_willSuccess() {
            // given
            long memberId = 1L;
            long yanoljaId = 1L;
            long reservationId = 1L;
            ProductPostRequest productPostRequest = ProductPostRequest.builder().firstPrice(350000)
                .secondPrice(200000).bank("신한은행").accountNumber("1000-4400-3330")
                .secondGrantPeriod(48).build();

            YanoljaMember yanoljaMember = YanoljaMember.builder().id(yanoljaId)
                .email("yang980329@naver.com").build();

            Member member = Member.builder().id(memberId).yanoljaMember(yanoljaMember)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            Reservation reservation = Reservation.builder().id(reservationId)
                .yanoljaMember(yanoljaMember).purchasePrice(5000000).startDate(
                    LocalDate.now()).endDate(LocalDate.now()).build();

            given(memberRepository.findById(any(Long.TYPE))).willReturn(
                java.util.Optional.ofNullable(member));
            given(reservationRepository.findByIdAndYanoljaMemberId(any(Long.TYPE),
                any(Long.TYPE))).willReturn(
                java.util.Optional.ofNullable(reservation));

            Product product = Product.builder()
                .id(1L)
                .reservation(reservation)
                .member(member)
                .firstPrice(productPostRequest.getFirstPrice())
                .secondPrice(productPostRequest.getSecondPrice())
                .bank(productPostRequest.getBank())
                .accountNumber(productPostRequest.getAccountNumber())
                .secondGrantPeriod(productPostRequest.getSecondGrantPeriod()).build();

            given(productRepository.save(any(Product.class))).willReturn(product);

            // when
            ProductPostResponse result = productService.postProduct(1L, 1L, productPostRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getProductId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("상품 상세 조회는 ")
    class Context_findProduct {

        @Test
        @DisplayName("성공시 상품 정보를 반환한다.")
        void _will_success() {
            // given

            RoomTheme roomTheme = RoomTheme.builder()
                .id(1L)
                .build();

            Room room = Room.builder()
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .roomTheme(roomTheme)
                .build();

            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .id(1L)
                .offPeakPrice(100000)
                .peakPrice(200000)
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelRoomPrice(hotelRoomPrice)
                .build();

            Reservation reservation = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

            PaymentHistory paymentHistory = PaymentHistory.builder()
                .id(1L)
                .price(100000)
                .customerName("tester")
                .customerEmail("qwe@nav.com")
                .customerPhoneNumber("010-1122-3344")
                .paymentType("카카오페이")
                .settlement(true)
                .build();

            Product product = Product.builder()
                .id(1L)
                .firstPrice(200000)
                .secondPrice(100000)
                .bank("국민")
                .accountNumber("12512-2131-12512")
                .secondGrantPeriod(24)
                .reservation(reservation)
                .paymentHistory(paymentHistory)
                .build();

            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when
            ProductFindResponse response = productService.findProduct(product.getId());

            // then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.isSaleStatus()).isEqualTo(true);
        }
    }
}


