package com.yanolja.scbj.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductMainResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductStockResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.entity.ProductAgreement;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Transactional
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SecurityUtil securityUtil;


    public Product makeProductMock() {
        RoomTheme roomTheme = RoomTheme.builder()
            .id(1L)
            .build();

        Room room = Room.builder()
            .checkIn(LocalTime.now())
            .checkOut(LocalTime.now())
            .roomTheme(roomTheme)
            .roomAllRating("5.0")
            .build();

        HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
            .id(1L)
            .offPeakPrice(100000)
            .peakPrice(200000)
            .build();

        HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
            .id(1L)
            .url("Asdagasd")
            .build();

        Hotel hotel = Hotel.builder()
            .id(1L)
            .room(room)
            .hotelMainAddress("서울")
            .hotelLevel("5성급")
            .hotelRoomPrice(hotelRoomPrice)
            .hotelRoomImageList(List.of(hotelRoomImage))
            .build();

        Reservation reservation = Reservation.builder()
            .id(1L)
            .hotel(hotel)
            .startDate(LocalDateTime.now().minusDays(1))
            .endDate(LocalDateTime.now())
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
            .comments(new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E")))
            .build();

        return product;
    }

    @Nested
    @DisplayName("postProduct()는 ")
    class Context_postProduct {

        @Test
        @DisplayName("2차 가격이 있는 양도글 작성을 성공했습니다.")
        void saveProductWithSecond_willSuccess() {
            // given
            long memberId = 1L;
            long yanoljaId = 1L;
            long reservationId = 1L;
            ProductPostRequest productPostRequest = ProductPostRequest.builder().firstPrice(350000)
                .secondPrice(200000).bank("신한은행").accountNumber("1000-4400-3330").isRegistered(true)
                .secondGrantPeriod(48).standardTimeSellingPolicy(true).totalAmountPolicy(true)
                .sellingModificationPolicy(true).productAgreement(true).build();

            ProductAgreement productAgreement = ProductAgreement.builder()
                .standardTimeSellingPolicy(productPostRequest.standardTimeSellingPolicy())
                .totalAmountPolicy(productPostRequest.totalAmountPolicy())
                .sellingModificationPolicy(productPostRequest.sellingModificationPolicy())
                .productAgreement(productPostRequest.productAgreement())
                .build();

            MemberUpdateAccountRequest memberUpdateAccountRequest =
                MemberUpdateAccountRequest.builder()
                    .accountNumber("1000-4400-3330")
                    .bank("신한은행")
                    .build();

            YanoljaMember yanoljaMember = YanoljaMember.builder().id(yanoljaId)
                .email("yang980329@naver.com").build();

            Member member = Member.builder().id(memberId).yanoljaMember(yanoljaMember)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            Reservation reservation = Reservation.builder().id(reservationId)
                .yanoljaMember(yanoljaMember).purchasePrice(5000000).startDate(
                    LocalDateTime.now()).endDate(LocalDateTime.now()).build();

            given(memberRepository.findById(any(Long.TYPE))).willReturn(
                java.util.Optional.ofNullable(member));
            given(reservationRepository.findByIdAndYanoljaMemberId(any(Long.TYPE),
                any(Long.TYPE))).willReturn(
                java.util.Optional.ofNullable(reservation));

            Product product = Product.builder()
                .id(1L)
                .reservation(reservation)
                .member(member)
                .productAgreement(productAgreement)
                .firstPrice(productPostRequest.firstPrice())
                .secondPrice(productPostRequest.secondPrice())
                .bank(productPostRequest.bank())
                .accountNumber(productPostRequest.accountNumber())
                .secondGrantPeriod(productPostRequest.secondGrantPeriod()).build();

            given(productRepository.save(any(Product.class))).willReturn(product);

            // when
            ProductPostResponse result = productService.postProduct(1L, 1L, productPostRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("2차 가격이 없는 양도글 작성을 성공했습니다.")
        void saveProductwithoutSecond_willSuccess() {
            // given
            long memberId = 1L;
            long yanoljaId = 1L;
            long reservationId = 1L;
            ProductPostRequest productPostRequest = ProductPostRequest.builder().firstPrice(350000)
                .bank("신한은행").accountNumber("1000-4400-3330").isRegistered(false)
                .secondGrantPeriod(48).standardTimeSellingPolicy(true).totalAmountPolicy(true)
                .sellingModificationPolicy(true).productAgreement(true).build();

            ProductAgreement productAgreement = ProductAgreement.builder()
                .standardTimeSellingPolicy(productPostRequest.standardTimeSellingPolicy())
                .totalAmountPolicy(productPostRequest.totalAmountPolicy())
                .sellingModificationPolicy(productPostRequest.sellingModificationPolicy())
                .productAgreement(productPostRequest.productAgreement())
                .build();

            YanoljaMember yanoljaMember = YanoljaMember.builder().id(yanoljaId)
                .email("yang980329@naver.com").build();

            Member member = Member.builder().id(memberId).yanoljaMember(yanoljaMember)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            Reservation reservation = Reservation.builder().id(reservationId)
                .yanoljaMember(yanoljaMember).purchasePrice(5000000).startDate(
                    LocalDateTime.now()).endDate(LocalDateTime.now()).build();

            given(memberRepository.findById(any(Long.TYPE))).willReturn(
                java.util.Optional.ofNullable(member));
            given(reservationRepository.findByIdAndYanoljaMemberId(any(Long.TYPE),
                any(Long.TYPE))).willReturn(
                java.util.Optional.ofNullable(reservation));

            Product product = Product.builder()
                .id(1L)
                .reservation(reservation)
                .member(member)
                .productAgreement(productAgreement)
                .firstPrice(productPostRequest.firstPrice())
                .secondPrice(productPostRequest.secondPrice())
                .bank(productPostRequest.bank())
                .accountNumber(productPostRequest.accountNumber())
                .secondGrantPeriod(productPostRequest.secondGrantPeriod()).build();

            given(productRepository.save(any(Product.class))).willReturn(product);

            // when
            ProductPostResponse result = productService.postProduct(1L, 1L, productPostRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("상품 상세 조회는 ")
    class Context_findProduct {

        @Test
        @DisplayName("성공시 상품 정보를 반환한다.")
        void _will_success() {
            // given
            given(productRepository.findById(any())).willReturn(Optional.of(makeProductMock()));
            given(securityUtil.isUserNotAuthenticated()).willReturn(true);
            // when
            ProductFindResponse response = productService.findProduct(makeProductMock().getId());

            // then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.saleStatus()).isEqualTo(false);
        }
    }


    @Nested
    @DisplayName("상품 삭제는 ")
    class Context_deleteProduct {

        @Test
        @DisplayName("성공 시 deletedAt에 값이 생긴다")
        void _will_success() {
            // given
            Product product = Product.builder()
                .id(1L)
                .firstPrice(200000)
                .secondPrice(100000)
                .bank("국민")
                .accountNumber("12512-2131-12512")
                .secondGrantPeriod(24)
                .build();

            doNothing().when(productRepository).deleteById(any());

            // when
            productService.deleteProduct(1L);

            // then
            verify(productRepository, atLeastOnce()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("상품 검색은")
    class Context_searchProduct {

        @Test
        @DisplayName("성공시 원하는 결과값을 보여준다")
        void will_success() {
            //given
            ProductSearchRequest request =
                ProductSearchRequest.builder()
                    .checkIn(LocalDate.now())
                    .checkOut(LocalDate.now().plusDays(1))
                    .build();

            ProductSearchResponse response = ProductSearchResponse.builder()
                .id(1L)
                .checkIn(LocalDateTime.now().plusDays(1).toLocalDate())
                .checkOut(LocalDateTime.now().plusDays(2).toLocalDate())

                .salePrice(100000)
                .name("시그니엘 레지던스 호텔")
                .build();

            Pageable pageable = PageRequest.of(1, 10);

            PageImpl<ProductSearchResponse> expectedPage =
                new PageImpl<>(List.of(response), pageable, 1);

            given(productRepository.search(pageable, request)).willReturn(expectedPage);

            //when
            Page<ProductSearchResponse> result =
                productService.searchByRequest(request, pageable);

            //then
            assertThat(result).isNotNull();
            System.out.println(response.getCheckIn());
            assertThat(result.getContent()).containsExactly(response);
        }
    }

    @Nested
    @DisplayName("상품 재고 조회는")
    class Context_getProductStock {

        //todo  makeProductMock() 대체하기
        @Test
        @DisplayName("재고가 존재시 true를 반환한다.")
        void will_success() throws Exception {

            // given
            long productId = 1L;

            given(productRepository.findById(any())).willReturn(Optional.of(makeProductMock()));

            // when
            ProductStockResponse result = productService.isProductStockLeft(productId);

            //then
            assertThat(result).isNotNull();
            assertThat(result.hasStock()).isEqualTo(true);
        }

        @Test
        @DisplayName("재고가 존재하지 않을 경우 false를 반환한다.")
        void will_NotSuccess() throws Exception {

            // given
            long productId = 1L;

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
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
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

            product.sell();

            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when
            ProductStockResponse result = productService.isProductStockLeft(productId);

            //then
            assertThat(result).isNotNull();
            assertThat(result.hasStock()).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("메인페이지는 성공시 통과가 된다")
    class Context_Main {

        @Test
        @DisplayName("getAllProductForMainPage 메소드는 각 도시 및 주말 상품을 정확히 반환한다")
        void will_success() {
            // given
            ArrayList<Product> arrayList = new ArrayList<>();
            arrayList.add(makeProductMock());
            List<String> cityNames = Arrays.asList("서울", "강원", "부산", "제주", "전라", "경상");

            cityNames.forEach(city -> {
                List<Product> cityProducts = arrayList;
                when(productRepository.findProductByCity(city)).thenReturn(cityProducts);
            });

            List<Product> weekendProducts = arrayList;
            when(productRepository.findProductByWeekend()).thenReturn(weekendProducts);

            Pageable pageable = PageRequest.of(0, 3);

            //when
            ProductMainResponse result =
                productService.getAllProductForMainPage(cityNames, pageable);

            // then
            assertNotNull(result);
        }


        @Test
        @DisplayName("메인페이지에서 성급과 호텔이 나온다")
        void will_success_show_hotelRate_reviewRate() {
            //given

            ArrayList<Product> arrayList = new ArrayList<>();
            arrayList.add(makeProductMock());
            List<String> cityNames = Arrays.asList("서울", "강원", "부산", "제주", "전라", "경상");

            cityNames.forEach(city -> {
                List<Product> cityProducts = arrayList;
                when(productRepository.findProductByCity(city)).thenReturn(cityProducts);
            });

            Pageable pageable = PageRequest.of(0, 3);

            //when
            ProductMainResponse result =
                productService.getAllProductForMainPage(cityNames, pageable);

            //then
            assertNotNull(result);
            assertEquals(result.seoul().get(0).hotelRate(), "5성급");
            assertEquals(result.seoul().get(0).reviewRate(), "5.0");
        }
    }

}


