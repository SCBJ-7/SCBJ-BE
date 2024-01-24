package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.util.RoomThemeMapper;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.member.util.MemberMapper;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.CityResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductMainResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductStockResponse;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.entity.ProductAgreement;
import com.yanolja.scbj.domain.product.exception.FirstPriceHigherException;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.exception.SecondPriceHigherException;
import com.yanolja.scbj.domain.product.exception.SecondPricePeriodException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.product.util.ProductMapper;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.exception.ReservationNotFoundException;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SecurityUtil;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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
    private final MemberService memberService;
    private final CityDtoConverter cityDtoConverter;
    private final WeekendDtoConverter weekendDtoConverter;
    private final SecurityUtil securityUtil;

    private static final int MIN_SECOND_GRANT_PERIOD = 3;
    private final int OUT_OF_STOCK = 0;

    @Transactional
    public ProductPostResponse postProduct(Long memberId, Long reservationId,
        ProductPostRequest productPostRequest) {

        Member currentMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        ProductAgreement productAgreement = ProductMapper.toProductAgreement(productPostRequest);

        Reservation targetReservation = reservationRepository.findByIdAndYanoljaMemberId(
            reservationId, currentMember.getYanoljaMember().getId()).orElseThrow(
            () -> new ReservationNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        checkFirstPrice(productPostRequest, targetReservation);
        checkSecondPriceAndGrandPeriod(productPostRequest);
        checkAccountRegisterd(productPostRequest);

        Product savedProduct = productRepository.save(
            ProductMapper.toProduct(targetReservation, currentMember, productAgreement,
                productPostRequest));

        return ProductMapper.toProductPostResponse(savedProduct);
    }

    private void checkFirstPrice(ProductPostRequest productPostRequest, Reservation reservation) {
        if (productPostRequest.firstPrice() > reservation.getPurchasePrice()) {
            throw new FirstPriceHigherException(ErrorCode.FIRST_PRICE_HIGHER);
        }
    }

    private void checkSecondPriceAndGrandPeriod(ProductPostRequest productPostRequest) {
        if (productPostRequest.secondPrice() != 0
            && productPostRequest.secondGrantPeriod() != 0) {
            checkSecondPrice(productPostRequest);
            checkSecondGrantPeriod(productPostRequest);
        }
    }

    private void checkSecondPrice(ProductPostRequest productPostRequest) {
        if (productPostRequest.secondPrice() > productPostRequest.firstPrice()) {
            throw new SecondPriceHigherException(ErrorCode.SECOND_PRICE_HIGHER);
        }
    }

    private void checkSecondGrantPeriod(ProductPostRequest productPostRequest) {
        if (productPostRequest.secondGrantPeriod() < MIN_SECOND_GRANT_PERIOD) {
            throw new SecondPricePeriodException(ErrorCode.INVALID_SECOND_PRICE_PERIOD);
        }
    }

    private void checkAccountRegisterd(ProductPostRequest productPostRequest) {
        if (productPostRequest.isRegistered()) {
            memberService.updateMemberAccount(
                MemberMapper.toUpdateAccountRequest(productPostRequest));
        }
    }


    @Transactional(readOnly = true)
    public ProductFindResponse findProduct(Long productId) {
        Product foundProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        Reservation foundReservation = foundProduct.getReservation();
        Hotel foundHotel = foundReservation.getHotel();
        Room foundRoom = foundHotel.getRoom();

        RoomThemeFindResponse roomThemeResponse = RoomThemeMapper.toFindResponse(
            foundRoom.getRoomTheme());

        return ProductMapper.toProductFindResponse(foundHotel,
            getHotelRoomImageUrlList(foundHotel.getHotelRoomImageList()), foundRoom,
            foundReservation.getStartDate(), foundReservation.getEndDate(),
            getOriginalPrice(foundHotel),
            getSalePrice(foundProduct, foundReservation.getStartDate()),
            roomThemeResponse, getSaleStatus(foundProduct, foundReservation.getStartDate()),
            checkSeller(foundProduct));
    }

    private boolean getSaleStatus(Product product, LocalDateTime checkIn) {
        if (product.getStock() == OUT_OF_STOCK){
            return false;
        }
        return LocalDateTime.now().isBefore(checkIn);
    }

    private boolean checkSeller(Product product) {
        if (securityUtil.isUserNotAuthenticated()) {
            return false;
        }
        return product.getMember().getId() == securityUtil.getCurrentMemberId();
    }

    private int getOriginalPrice(Hotel hotel){
        int originalPrice = hotel.getHotelRoomPrice().getOffPeakPrice();

        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = hotel.getHotelRoomPrice().getPeakPrice();
        }

        return originalPrice;
    }

    private int getSalePrice(Product product, LocalDateTime checkInDateTime){
        int price = product.getFirstPrice();

        if (TimeValidator.isOverSecondGrantPeriod(product, checkInDateTime)) {
            price = product.getSecondPrice();
        }

        return price;
    }

    private List<String> getHotelRoomImageUrlList(List<HotelRoomImage> hotelRoomImageList){
        return hotelRoomImageList.stream()
            .map(HotelRoomImage::getUrl)
            .collect(Collectors.toList());
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

        return responses.isEmpty() ? Page.empty(pageable) : responses;
    }


    public ProductMainResponse getAllProductForMainPage(List<String> cityNames,
        Pageable pageable
    ) {
        HashMap<String, List<CityResponse>> savedProduct = new HashMap<>();

        getEachCity(cityNames, savedProduct);
        Page<WeekendProductResponse> weekendProductResponse = getWeekendProducts(pageable);

        return ProductMainResponse.builder()
            .seoul(savedProduct.get("서울"))
            .gangwon(savedProduct.get("강원"))
            .busan(savedProduct.get("부산"))
            .jeju(savedProduct.get("제주"))
            .jeolla(savedProduct.get("전라"))
            .gyeongsang(savedProduct.get("경상"))
            .weekend(
                weekendProductResponse.isEmpty() ? Page.empty(pageable) : weekendProductResponse)
            .build();
    }

    private Page<WeekendProductResponse> getWeekendProducts(Pageable pageable) {
        List<Product> productByWeekend = productRepository.findProductByWeekend();
        Page<WeekendProductResponse> weekendProductResponse =
            weekendDtoConverter.toWeekendProductResponse(productByWeekend, pageable);
        return weekendProductResponse;
    }

    private void getEachCity(List<String> cities,
        HashMap<String, List<CityResponse>> savedProduct) {
        cities.forEach(city -> {
            List<Product> productsByCity = productRepository.findProductByCity(city);
            List<CityResponse> cityResponses = cityDtoConverter.toCityResponse(productsByCity);
            savedProduct.put(city, cityResponses);
        });
    }

    public ProductStockResponse isProductStockLeft(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getStock() > OUT_OF_STOCK) {
            return ProductStockResponse.builder().hasStock(true).build();
        }
        return ProductStockResponse.builder().hasStock(false).build();
    }
}
