package com.yanolja.scbj.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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

            Hotel hotel = Hotel.builder().hotelName("a호텔").hotelMainAddress("경기도")
                .hotelDetailAddress("고양시").hotelInfoUrl("www.naver.com").build();

            Reservation reservation = Reservation.builder().id(reservationId)
                .yanoljaMember(yanoljaMember).purchasePrice(5000000).hotel(hotel).startDate(
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
//            assertThat(result).extracting("productId").isEqualTo(1L);
        }
    }


}