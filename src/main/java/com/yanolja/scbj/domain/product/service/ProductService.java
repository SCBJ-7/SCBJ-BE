package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.util.RoomThemeMapper;
import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.repository.FavoriteRepository;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final SecurityUtil securityUtil;
    private final FavoriteRepository favoriteRepository;

    private static final int MIN_SECOND_GRANT_PERIOD = 3;
    private final int OUT_OF_STOCK = 0;

    private static final long PRODUCT_QUANTITY = 2;
    private static final int FIRST_HOTEL_IMAGE = 0;

    @Transactional
    public ProductPostResponse postProduct(Long memberId,
                                           Long reservationId,
                                           ProductPostRequest productPostRequest) {

        Member currentMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        ProductAgreement productAgreement = ProductMapper.toProductAgreement(productPostRequest);

        Reservation targetReservation = reservationRepository.findByIdAndYanoljaMemberId(
            reservationId, currentMember.getYanoljaMember().getId()).orElseThrow(
            () -> new ReservationNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        checkFirstPrice(productPostRequest, targetReservation);
        checkSecondPriceAndGrandPeriod(productPostRequest);
        checkAccountRegistered(productPostRequest);

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

    private void checkAccountRegistered(ProductPostRequest productPostRequest) {
        if (productPostRequest.isRegistered()) {
            memberService.updateMemberAccount(
                MemberMapper.toUpdateAccountRequest(productPostRequest));
        }
    }

    public Product getProduct(long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ProductFindResponse findProduct(Long productId) {
        Product foundProduct = getProduct(productId);

        Reservation foundReservation = foundProduct.getReservation();
        Hotel foundHotel = foundReservation.getHotel();
        Room foundRoom = foundHotel.getRoom();

        RoomThemeFindResponse roomThemeResponse = RoomThemeMapper.toFindResponse(
            foundRoom.getRoomTheme());

        return ProductMapper.toProductFindResponse(
            foundHotel,
            getHotelRoomImageUrlList(foundHotel.getHotelRoomImageList()),
            foundRoom,
            foundReservation.getStartDate(),
            foundReservation.getEndDate(),
            getOriginalPrice(foundHotel),
            getSalePrice(foundProduct, foundReservation.getStartDate()),
            roomThemeResponse,
            getSaleStatus(foundProduct, foundReservation.getStartDate()),
            checkSeller(foundProduct),
            getRemovedDuplicateInformation(foundHotel),
            checkLikeState(productId));
    }

    private boolean checkLikeState(Long productId) {
        if(securityUtil.isUserNotAuthenticated()) {
            return false;
        }
        Long currentMemberId = securityUtil.getCurrentMemberId();

        Favorite favorite = favoriteRepository.findByMemberIdAndProductId(
            currentMemberId, productId);

        return favorite != null;
    }

    private boolean getSaleStatus(Product product, LocalDateTime checkIn) {
        if (product.getStock() == OUT_OF_STOCK) {
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

    private int getOriginalPrice(Hotel hotel) {
        int originalPrice = hotel.getHotelRoomPrice().getOffPeakPrice();

        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = hotel.getHotelRoomPrice().getPeakPrice();
        }

        return originalPrice;
    }

    private int getSalePrice(Product product, LocalDateTime checkInDateTime) {
        int price = product.getFirstPrice();

        if (TimeValidator.isOverSecondGrantPeriod(product, checkInDateTime)) {
            price = product.getSecondPrice();
        }

        return price;
    }

    private List<String> getHotelRoomImageUrlList(List<HotelRoomImage> hotelRoomImageList) {
        return hotelRoomImageList.stream()
            .map(HotelRoomImage::getUrl)
            .collect(Collectors.toList());
    }

    private String getRemovedDuplicateInformation(Hotel hotel) {
        Room room = hotel.getRoom();
        RoomTheme roomTheme = room.getRoomTheme();
        String[] roomThemeNameList = roomTheme.getRoomThemeNameList();
        List<String> removeWordList = new java.util.ArrayList<>(
            Arrays.stream(roomThemeNameList).toList());
        String[] targetWord = {"베드", "기준", "최대"};
        removeWordList.addAll(List.of(targetWord));

        List<String> facilityInformationList = Arrays.stream(
            room.getFacilityInformation().split("\n")).toList();

        int listSize = facilityInformationList.size();
        for (int i = 0; i < listSize; i++) {
            String facility = facilityInformationList.get(i);
            for (String word : removeWordList) {
                if (facility.contains(word)) {
                    facilityInformationList.remove(i);
                    break;
                }
            }
        }

        return String.join("\n", facilityInformationList);
    }


    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public Page<ProductSearchResponse> searchByRequest(ProductSearchRequest productSearchRequest,
                                                       Pageable pageable) {

        Page<ProductSearchResponse> responses =
            productRepository.search(pageable, productSearchRequest);

        return responses.isEmpty() ? Page.empty(pageable) : responses;
    }


    public ProductMainResponse getAllProductForMainPage(
        List<String> cityNames,
        Pageable pageable
    ) {
        Map<String, List<CityResponse>> eachCity = getEachCity(cityNames);
        Page<WeekendProductResponse> weekendProductResponse = getWeekendProducts(pageable);

        return ProductMainResponse.builder()
            .seoul(eachCity.get("서울"))
            .gangwon(eachCity.get("강원"))
            .busan(eachCity.get("부산"))
            .jeju(eachCity.get("제주"))
            .jeolla(eachCity.get("전라"))
            .gyeongsang(eachCity.get("경상"))
            .weekend(weekendProductResponse.isEmpty() ? Page.empty(pageable) : weekendProductResponse)
            .build();
    }

    private Map<String, List<CityResponse>> getEachCity(List<String> cities) {
        Map<String, List<CityResponse>> savedProduct = new HashMap<>();
        cities.forEach(city -> {
            List<Product> productsByCity = productRepository.findProductByCity(city);
            List<CityResponse> getCityResponse = productsByCity.stream()
                .map(product -> {
                    Reservation reservation = product.getReservation();
                    int currentPrice = PricingHelper.getCurrentPrice(product);
                    int originalPrice = PricingHelper.getOriginalPrice(reservation.getHotel());
                    double discountRate = PricingHelper.calculateDiscountRate(product, currentPrice);
                    String hotelUrl = getHotelUrl(product.getReservation().getHotel());
                    String hotelRate = product.getReservation().getHotel().getHotelLevel();
                    String roomRate = product.getReservation().getHotel().getRoom().getRoomAllRating();

                    return CityMapper.toCityResponse(product, hotelUrl, reservation,
                        currentPrice, discountRate, originalPrice,hotelRate,roomRate);
                }).sorted(Comparator.comparingDouble(CityResponse::salePercentage).reversed())
                .limit(PRODUCT_QUANTITY)
                .collect(Collectors.toList());

            savedProduct.put(city, getCityResponse);
        });
        return savedProduct;
    }

    private Page<WeekendProductResponse> getWeekendProducts(Pageable pageable) {
        List<Product> productByWeekend = productRepository.findProductByWeekend();
        List<WeekendProductResponse> responses = productByWeekend.stream()
                .flatMap(this::getProductResponses)
                .sorted(ascendCheckin()
                .thenComparing(descendRoomThemeCount())
                .thenComparing(descendSalePercentage()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());

        return new PageImpl<>(responses.subList(start, end), pageable, responses.size());
    }

    private Stream<WeekendProductResponse> getProductResponses(Product product) {
        Reservation reservation = product.getReservation();
        RoomTheme roomTheme = reservation.getHotel().getRoom().getRoomTheme();
        String hotelUrl = getHotelUrl(product.getReservation().getHotel());
        int currentPrice = PricingHelper.getCurrentPrice(product);
        double discountRate = PricingHelper.calculateDiscountRate(product, currentPrice);
        String hotelRate = product.getReservation().getHotel().getHotelLevel();
        String roomRate = product.getReservation().getHotel().getRoom().getRoomAllRating();

        return Stream.of(WeekendMapper.toWeekendProductResponse(product, reservation, hotelUrl,
            currentPrice, discountRate, getThemeCount(roomTheme), roomTheme,hotelRate,roomRate));
    }

    private Comparator<WeekendProductResponse> ascendCheckin() {
        return Comparator
            .comparing(WeekendProductResponse::checkInDate);
    }

    private Comparator<WeekendProductResponse> descendSalePercentage() {
        return Comparator.comparing(WeekendProductResponse::salePercentage,
            Comparator.reverseOrder());
    }

    private Comparator<WeekendProductResponse> descendRoomThemeCount() {
        return Comparator.comparing(WeekendProductResponse::roomThemeCount,
            Comparator.reverseOrder());
    }

    private int getThemeCount(RoomTheme roomTheme) {
        return (roomTheme.isBreakfast() ? 1 : 0) +
            (roomTheme.isPool() ? 1 : 0) +
            (roomTheme.isOceanView() ? 1 : 0);
    }


    public ProductStockResponse isProductStockLeft(long productId) {
        Product product = getProduct(productId);
        if (product.getStock() > OUT_OF_STOCK) {
            return ProductStockResponse.builder().hasStock(true).build();
        }
        return ProductStockResponse.builder().hasStock(false).build();
    }

    public String getHotelUrl(Hotel hotel) {
        return hotel.getHotelRoomImageList().isEmpty() ? null :
            hotel.getHotelRoomImageList().get(FIRST_HOTEL_IMAGE).getUrl();
    }
}
