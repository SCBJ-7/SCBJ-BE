package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.FirstPriceHigherException;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.exception.SecondPriceHigherException;
import com.yanolja.scbj.domain.product.exception.SecondPricePeriodException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.exception.ReservationNotFoundException;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final ProductDtoConverter productDtoConverter;
    private final MemberService memberService;

    private static final int MIN_SECOND_GRANT_PERIOD = 3;

    @Transactional
    public ProductPostResponse postProduct(Long memberId, Long reservationId,
        ProductPostRequest productPostRequest) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        YanoljaMember yanoljaMember = member.getYanoljaMember();

        Reservation reservation = reservationRepository.findByIdAndYanoljaMemberId(reservationId,
            yanoljaMember.getId()).orElseThrow(
            () -> new ReservationNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        if (productPostRequest.firstPrice() > reservation.getPurchasePrice()) {
            throw new FirstPriceHigherException(ErrorCode.FIRST_PRICE_HIGHER);
        }
        if (productPostRequest.secondPrice() != 0
            && productPostRequest.secondGrantPeriod() != 0) {
            if (productPostRequest.secondPrice() > productPostRequest.firstPrice()) {
                throw new SecondPriceHigherException(ErrorCode.SECOND_PRICE_HIGHER);
            }
            if (productPostRequest.secondGrantPeriod() < MIN_SECOND_GRANT_PERIOD) {
                throw new SecondPricePeriodException(ErrorCode.INVALID_SECOND_PRICE_PERIOD);
            }
        }

        if (productPostRequest.isRegistered()) {
            MemberUpdateAccountRequest memberUpdateAccountRequest = MemberUpdateAccountRequest.builder()
                .accountNumber(productPostRequest.accountNumber())
                .bank(productPostRequest.bank())
                .build();
            memberService.updateMemberAccount(memberUpdateAccountRequest);
        }

        Product product = Product.builder()
            .reservation(reservation)
            .member(member)
            .firstPrice(productPostRequest.firstPrice())
            .secondPrice(productPostRequest.secondPrice())
            .bank(productPostRequest.bank())
            .accountNumber(productPostRequest.accountNumber())
            .secondGrantPeriod(productPostRequest.secondGrantPeriod()).build();

        Product savedProduct = productRepository.save(product);

        return ProductPostResponse.builder().productId(savedProduct.getId()).build();

    }

    @Transactional(readOnly = true)
    public ProductFindResponse findProduct(Long productId) {
        Product foundProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        return productDtoConverter.toFindResponse(foundProduct);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product targetProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        targetProduct.delete(LocalDateTime.now());
    }

    public Page<ProductSearchResponse> searchByRequest(ProductSearchRequest productSearchRequest,
                                              Pageable pageable) {
        Page<ProductSearchResponse> responses =
            productRepository.search(pageable, productSearchRequest);

        return responses.isEmpty() ? Page.empty() : responses;
    }
}
