package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentAmountResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentApproveResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentService;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@ExtendWith(MockitoExtension.class)
class KaKaoPaymentServiceTest {


    @InjectMocks
    private PaymentService paymentService;


    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private KaKaoPaymentService kaKaoPaymentService;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private MemberRepository memberRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

//    @DisplayName("낙관적 락을 이용해 동시성을 제어한다")
//    @Test
//    void _will_success_with_optimisticLock() throws Exception {
//        // given
//        int threadCount = 3;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        Member member = Member.builder()
//            .id(1L)
//            .email("qweqqwe@navrer.com")
//            .password("agasdagasd")
//            .build();
//        Product product = Product.builder()
//            .id(1L)
//            .stock(1)
//            .member(member)
//            .version(1L)
//            .build();
//
//
//        // opsForHash()를 대신하여 사용할 HashOperations 목(Mock) 생성
//        HashOperations<String, Object, Object> hashOperations = mock(HashOperations.class);
//        given(redisTemplate.opsForHash()).willReturn(hashOperations);
//        given(hashOperations.get(any(), any())).willReturn("1");
//
//        given(restTemplate.postForObject(any(), any(), any()))
//            .willReturn(new PaymentApproveResponse("ASdas", new PaymentAmountResponse(15000)));
//        given(memberRepository.findById(any(Long.TYPE))).willReturn(Optional.of(member));
//        given(productRepository.findByIdWithOptimistic((any(Long.TYPE)))).willReturn(
//            Optional.of(product));
//
//        productRepository.save(product);
//
//        PaymentHistory paymentHistory = PaymentHistory.builder()
//            .id(1L)
//            .customerName("박아무개")
//            .customerEmail("yang980329@naver.com")
//            .customerPhoneNumber("010-0000-0000")
//            .price(15000)
//            .product(product)
//            .paymentType("카카오페이")
//            .build();
//
//        given(paymentHistoryRepository.save(any())).willReturn(paymentHistory);
//
//
//        for (int i = 0; i < threadCount; i++) {
//            try {
//                executorService.execute(
//                    () -> kaKaoPaymentService.payInfo("fff", member.getId())
//                );
//                System.out.println(product.getVersion());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }finally {
//                latch.countDown();
//            }
//        }
//        latch.await();
//        executorService.shutdown();
//
//        Assertions.assertThat(product.getStock()).isEqualTo(0L);
//    }

    @DisplayName("낙관적 락을 이용해 동시성을 제어한다")
    @Test
    void _will_success_with_optimisticLock2() throws Exception {
        // given
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Member member = Member.builder()
            .id(1L)
            .email("qweqqwe@navrer.com")
            .password("agasdagasd")
            .build();

        Product product = Product.builder()
            .id(1L)
            .stock(1)
            .member(member)
            .version(1L)
            .build();


        HashOperations<String, Object, Object> hashOperations = mock(HashOperations.class);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.get(any(), any())).willReturn("1");

        String pgToken = "@22";
        String tid = "@22";

        doNothing().when(kaKaoPaymentService).payInfo(any(), any(), any(), any());
        given(memberRepository.findById(any(Long.TYPE))).willReturn(Optional.of(member));
        given(productRepository.findByIdWithOptimistic((any(Long.TYPE)))).willReturn(
            Optional.of(product));


        PaymentHistory paymentHistory = PaymentHistory.builder()
            .id(1L)
            .member(member)
            .customerName("박아무개")
            .customerEmail("yang980329@naver.com")
            .customerPhoneNumber("010-0000-0000")
            .price(15000)
            .product(product)
            .paymentType("카카오페이")
            .build();

        given(paymentHistoryRepository.save(any())).willReturn(paymentHistory);


        for (int i = 0; i < threadCount; i++) {
            try {
                executorService.execute(
                    () -> paymentService.orderProduct(pgToken, member.getId())
                );
                System.out.println(product.getVersion());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                latch.countDown();
            }
        }
        latch.await();
        executorService.shutdown();

        Assertions.assertThat(product.getStock()).isEqualTo(0L);
    }

}
