package com.yanolja.scbj.domain.product.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.testdata.TestData;
import com.yanolja.scbj.global.config.QuerydslConfig;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfig.class)
public class ProductMainRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;


    @BeforeEach
    void init() {
        IntStream.rangeClosed(1, 10) // 결제내역이 있으니 포함 X
            .forEach(i -> {
                String randomAddress = "이천";
                Member member = TestData.createMember("user" + i + "@example.com", "홍길동" + i);
                entityManager.persist(member);
                RoomTheme roomTheme = TestData.createRoomTheme(true, true);
                entityManager.persist(roomTheme);
                Hotel hotel2 = TestData.createHotel(roomTheme, randomAddress, 2,"5.0","3성급");
                entityManager.persist(hotel2);
                HotelRoomImage hotelRoomImage = TestData.createHotelRoomImage(hotel2);
                entityManager.persist(hotelRoomImage);
                YanoljaMember yanoljaMember =
                    TestData.createYanoljaMember("yanolja" + i + "@example.com");
                entityManager.persist(yanoljaMember);
                Reservation reservation =
                    TestData.createReservation(hotel2, yanoljaMember, LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5), 200000);
                entityManager.persist(reservation);
                Product product = TestData.createProduct(member, reservation, 100000, 50000, 0);
                entityManager.persist(product);
                PaymentHistory paymentHistory = TestData.createPaymentHistory(product);
                entityManager.persist(paymentHistory);
                entityManager.flush();
            });

        IntStream.rangeClosed(1, 5)
            .forEach(i -> {
                String randomAddress = "강릉";
                Member member =
                    TestData.createMember("user" + i + 20 + "@example.com", "홍길동" + i + 20);
                entityManager.persist(member);
                RoomTheme roomTheme = TestData.createRoomTheme(true, true);
                entityManager.persist(roomTheme);
                Hotel hotel2 = TestData.createHotel(roomTheme, randomAddress, 4,"3.0","5성급");
                entityManager.persist(hotel2);
                HotelRoomImage hotelRoomImage = TestData.createHotelRoomImage(hotel2);
                entityManager.persist(hotelRoomImage);
                YanoljaMember yanoljaMember =
                    TestData.createYanoljaMember("yanolja" + i + 20 + "@example.com");
                entityManager.persist(yanoljaMember);
                Reservation reservation =
                    TestData.createReservation(hotel2, yanoljaMember, LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2), 300000);
                entityManager.persist(reservation);
                Product product = TestData.createProduct(member, reservation, 200000, 100000, 1);
                entityManager.persist(product);
                PaymentHistory paymentHistory = TestData.createPaymentHistory(null);
                entityManager.persist(paymentHistory);
                entityManager.flush();
            });

        IntStream.rangeClosed(1, 5)
            .forEach(i -> {
                String randomAddress = "서울";
                Member member =
                    TestData.createMember("user" + i + 30 + "@example.com", "홍길동" + i + 30);
                entityManager.persist(member);
                RoomTheme roomTheme = TestData.createRoomTheme(true, true);
                entityManager.persist(roomTheme);
                Hotel hotel2 = TestData.createHotel(roomTheme, randomAddress, 3,"4.5","5성급");
                entityManager.persist(hotel2);
                HotelRoomImage hotelRoomImage = TestData.createHotelRoomImage(hotel2);
                entityManager.persist(hotelRoomImage);
                YanoljaMember yanoljaMember =
                    TestData.createYanoljaMember("yanolja" + i + 30 + "@example.com");
                entityManager.persist(yanoljaMember);
                Reservation reservation =
                    TestData.createReservation(hotel2, yanoljaMember, LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(13), 400000);
                entityManager.persist(reservation);
                Product product = TestData.createProduct(member, reservation, 300000, 250000, 0);
                entityManager.persist(product);
                PaymentHistory paymentHistory = TestData.createPaymentHistory(null);
                entityManager.persist(paymentHistory);
                entityManager.flush();
            });
        entityManager.clear();
    }


    @Nested
    @DisplayName("도시별 찾기")
    class Context_findProductByCityTest {

        @Test
        @DisplayName("5개의 결과가 나온다")
        void findProductByCitySeoul() {
            // given
            String city = "서울";

            // when
            List<Product> products = productRepository.findProductByCity(city);

            // then

            assertFalse(products.isEmpty());
            assertEquals(products.size(), 5);
            products.forEach(product -> {
                assertTrue(
                    product.getReservation().getHotel().getHotelMainAddress().contains(city));
            });
        }
    }

}
