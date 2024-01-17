package com.yanolja.scbj.domain.product.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomImageRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RoomThemeRepository;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentService;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class ProductSearchRepositoryTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomThemeRepository roomThemeRepository;

    @Autowired
    private YanoljaMemberRepository yanoljaMemberRepository;

    @Autowired
    private HotelRoomImageRepository hotelRoomImageRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    private List<Member> memberList = new ArrayList<>();

    private int threadSize = 11;

    private Member createMember(String email, String phoneNumber) {

        Member member = Member.builder()
            .email(email)
            .password("password")
            .name("홍길동")
            .phone(phoneNumber)
            .authority(Authority.ROLE_USER)
            .build();

        Member savedMember = memberRepository.save(member);
        memberList.add(savedMember);
        return savedMember;
    }

    private RoomTheme createRoomTheme(Boolean parking, Boolean pool) {
        RoomTheme roomTheme = RoomTheme.builder()
            .parkingZone(parking)
            .breakfast(false)
            .pool(pool)
            .oceanView(false)
            .build();
        roomThemeRepository.save(roomTheme);
        return roomTheme;
    }

    private Hotel createHotel(RoomTheme roomTheme, String hotelAddress, Integer maxPeople) {
        Room room = Room.builder()
            .roomName("Deluxe Room")
            .checkIn(LocalTime.of(14, 0))
            .checkOut(LocalTime.of(11, 0))
            .bedType("Double Bed")
            .standardPeople(100)
            .maxPeople(maxPeople)
            .roomTheme(roomTheme)
            .build();

        Hotel hotel = Hotel.builder()
            .hotelName("롯데 시그니엘 호텔")
            .hotelMainAddress(hotelAddress)
            .hotelDetailAddress("123 Yanolja St, Gangnam-gu")
            .hotelInfoUrl("http://yanoljahotel.com")
            .room(room)
            .build();

        hotelRoomRepository.save(hotel);
        return hotel;
    }

    private HotelRoomImage createHotelRoomImage(Hotel hotel) {
        HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
            .hotel(hotel)
            .url("http://example.com/hotel-room-image.jpg")
            .build();
        hotelRoomImageRepository.save(hotelRoomImage);
        return hotelRoomImage;
    }


    private YanoljaMember createYanoljaMember(String email) {
        YanoljaMember yanoljaMember = YanoljaMember.builder()
            .email(email)
            .build();
        yanoljaMemberRepository.save(yanoljaMember);
        return yanoljaMember;
    }

    private Reservation createReservation(Hotel hotel, YanoljaMember yanoljaMember,
        LocalDate checkIn, LocalDate checkOut, int purchasePrice) {
        Reservation reservation = Reservation.builder()
            .hotel(hotel)
            .yanoljaMember(yanoljaMember)
            .startDate(checkIn.atStartOfDay())
            .endDate(checkOut.atStartOfDay())
            .purchasePrice(purchasePrice)
            .build();
        reservationRepository.save(reservation);
        return reservation;
    }

    private Product createProduct(Member member, Reservation reservation, int firstPrice,
        int secondPrice, int time) {
        Product product = Product.builder()
            .reservation(reservation)
            .member(member)
            .bank("하나 은행")
            .accountNumber("123123")
            .firstPrice(firstPrice)
            .secondPrice(secondPrice)
            .secondGrantPeriod(time)
            .build();
        productRepository.save(product);
        return product;
    }


    @BeforeEach
    void init() {
        IntStream.rangeClosed(1, threadSize - 1)
            .forEach(i -> {
                String randomAddress = "서울";
                Member member = createMember("user" + i + 30 + "@example.com", "홍길동" + i + 30);
                RoomTheme roomTheme = createRoomTheme(true, true);
                Hotel hotel2 = createHotel(roomTheme, randomAddress, 3);
                createHotelRoomImage(hotel2);
                YanoljaMember yanoljaMember =
                    createYanoljaMember("yanolja" + i + 30 + "@example.com");
                Reservation reservation =
                    createReservation(hotel2, yanoljaMember, LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(13), 400000);
                Product product = createProduct(member, reservation, 300000, 250000, 0);
            });
    }


    @Nested
    @DisplayName("상품 검색 조회는")
    class Context_searchProduct {

        @Test
        @DisplayName("판매중인것만 조회가 된다")
        public void will_success_get_on_sale() {
            //given
            long startTime = System.currentTimeMillis();

            ProductSearchRequest productSearchRequest = ProductSearchRequest.builder().build();

            //when
            Page<ProductSearchResponse> responses =
                productRepository.search(PageRequest.of(0, 10), productSearchRequest);

            //then

            long endTime = System.currentTimeMillis();
            System.out.println("시간:" + endTime);
        }
    }
}
