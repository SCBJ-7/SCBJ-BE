package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.ProductOutOfStockException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.KaKaoPaymentService;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.repository.RedisRepository;
import com.yanolja.scbj.global.util.TimeValidator;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final String REDIS_CACHE_KEY_PREFIX = "kakaoPay:memberId";
    private final String REDIS_LOCK_KEY_PREFIX = "redis:lock:productId";
    private final String PAYMENT_TYPE = "카카오페이";
    private final String BASE_URL = "http://localhost:8080/v1/products";
    private final String KAKAO_BASE_URL = "https://kapi.kakao.com/v1/payment";


    private final int FIRST_IMAGE = 0;
    private final ProductRepository productRepository;
    private final RedisRepository redisRepository;
    private final KaKaoPaymentService kaKaoPaymentService;
    private final RedisTemplate redisTemplate;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final MemberRepository memberRepository;
    private final TransactionManager transactionManager;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public PaymentPageFindResponse getPaymentPage(Long productId) {
        Product targetProduct = productRepository.findProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));
        Reservation targetReservation = targetProduct.getReservation();
        Hotel targetHotel = targetReservation.getHotel();
        Room targetRoom = targetHotel.getRoom();
        HotelRoomPrice targetHotelRoomPrice = targetHotel.getHotelRoomPrice();
        List<HotelRoomImage> targetHotelRoomImageList = targetHotel.getHotelRoomImageList();
        int originalPrice = targetHotelRoomPrice.getOffPeakPrice();
        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = targetHotelRoomPrice.getPeakPrice();
        }
        LocalDateTime checkInDateTime = targetReservation.getStartDate();
        LocalDateTime checkOutDateTime = targetReservation.getEndDate();
        int price = targetProduct.getFirstPrice();
        if (TimeValidator.isOverSecondGrantPeriod(targetProduct, checkInDateTime)) {
            price = targetProduct.getSecondPrice();
        }
        return PaymentPageFindResponse.builder()
            .hotelImage(targetHotelRoomImageList.get(FIRST_IMAGE).getUrl())
            .hotelName(targetHotel.getHotelName())
            .roomName(targetRoom.getRoomName())
            .standardPeople(targetRoom.getStandardPeople())
            .maxPeople(targetRoom.getMaxPeople())
            .checkInDateTime(checkInDateTime)
            .checkOutDateTime(checkOutDateTime)
            .originalPrice(originalPrice)
            .salePrice(price)
            .build();
    }

    //부모 트랜잭션
    @Transactional
    public void stockLock(String pgToken, Long memberId){
        String redisLockKey = orderProductWithLettuceLock(pgToken, memberId);
        redisRepository.unlock(redisLockKey);
    }

    //자식트랜잭션
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String orderProductWithLettuceLock(String pgToken, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        //Cache
        String key = REDIS_CACHE_KEY_PREFIX + memberId;
        String productId = (String) redisTemplate.opsForHash().get(key, "productId");
        String customerName = (String) redisTemplate.opsForHash().get(key, "customerName");
        String customerEmail = (String) redisTemplate.opsForHash().get(key, "customerEmail");
        String customerPhoneNumber = (String) redisTemplate.opsForHash()
            .get(key, "customerPhoneNumber");
        String price = (String) redisTemplate.opsForHash().get(key, "price");
        String tid = (String) redisTemplate.opsForHash().get(key, "tid");

        //Lock
        String redisLockKey = REDIS_LOCK_KEY_PREFIX + productId;
        while (!redisRepository.lock(redisLockKey)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Product product = productRepository.findById(Long.valueOf(productId))
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStock() == 0) {
            throw new ProductOutOfStockException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

//        kaKaoPaymentService.payInfo(pgToken, memberId, Long.valueOf(productId), tid);

        PaymentAgreement agreement = PaymentAgreement.builder()
            .build();

//        productRepository.updateStock(product.getId());

        product.saleProduct();

        PaymentHistory paymentHistory = PaymentHistory.builder()
            .member(member)
            .product(product)
            .customerName(customerName)
            .customerEmail(customerEmail)
            .customerPhoneNumber(customerPhoneNumber)
            .paymentAgreement(agreement)
            .price(Integer.parseInt(price))
            .paymentType(PAYMENT_TYPE)
            .settlement(false)
            .build();


        paymentHistoryRepository.save(paymentHistory);
        return redisLockKey;
    }
}