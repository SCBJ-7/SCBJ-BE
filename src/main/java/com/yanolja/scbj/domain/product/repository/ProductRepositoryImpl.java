package com.yanolja.scbj.domain.product.repository;

import static com.yanolja.scbj.domain.hotelRoom.entity.QHotel.hotel;
import static com.yanolja.scbj.domain.hotelRoom.entity.QHotelRoomImage.hotelRoomImage;
import static com.yanolja.scbj.domain.hotelRoom.entity.QHotelRoomPrice.hotelRoomPrice;
import static com.yanolja.scbj.domain.hotelRoom.entity.QRoomTheme.roomTheme;
import static com.yanolja.scbj.domain.paymentHistory.entity.QPaymentHistory.paymentHistory;
import static com.yanolja.scbj.domain.product.entity.QProduct.product;
import static com.yanolja.scbj.domain.reservation.entity.QReservation.reservation;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductSearchResponse> search(Pageable pageable,
                                              ProductSearchRequest productSearchRequest) {

        List<ProductSearchResponse> response = queryFactory
            .select(
                product.id,
                hotel.hotelName,
                hotel.room.roomName,
                hotelRoomImage.url,
                product.firstPrice,
                product.secondPrice,
                reservation.hotel.room.maxPeople,
                hotelRoomPrice.peakPrice,
                hotelRoomPrice.offPeakPrice,
                product.secondGrantPeriod,
                reservation.startDate,
                reservation.endDate,
                product.createdAt,
                paymentHistory.id
            )
            .from(product)
            .innerJoin(product.reservation, reservation)
            .innerJoin(reservation.hotel, hotel)
            .innerJoin(roomTheme).on(hotel.room.roomTheme.id.eq(roomTheme.id))
            .join(roomTheme)
            .leftJoin(product.paymentHistory, paymentHistory)
            .innerJoin(hotelRoomImage).on(hotelRoomImage.hotel.id.eq(hotel.id))
            .where(allFilter(productSearchRequest).and(paymentHistory.id.isNull()))
            .groupBy(product.id)
            .fetch()
            .stream().map(tuple -> {
                tuple.get(hotelRoomPrice.peakPrice);
                Integer originalPrice = checkPeak(tuple.get(hotelRoomPrice.peakPrice), tuple.get(hotelRoomPrice.offPeakPrice));
                Integer salePrice = getSalePrice(tuple.get(reservation.startDate),
                    tuple.get(product.secondGrantPeriod), tuple.get(product.firstPrice),
                    tuple.get(product.secondPrice));
                return new ProductSearchResponse(
                    tuple.get(product.id),
                    tuple.get(hotel.hotelName),
                    tuple.get(hotel.room.roomName),
                    tuple.get(hotelRoomImage.url),
                    originalPrice,
                    isFirstPrice(salePrice, tuple.get(product.firstPrice)),
                    salePrice,
                    getSaleRate(originalPrice, salePrice),
                    tuple.get(reservation.startDate).toLocalDate(),
                    tuple.get(reservation.endDate).toLocalDate(),
                    tuple.get(product.createdAt)
                );
            })
            .collect(Collectors.toList());

        response.sort(sort(productSearchRequest.getSorted()));

        int total = response.size();
        List<ProductSearchResponse> paginatedResponse = doPaginated(response, pageable, total);

        return new PageImpl<>(paginatedResponse, pageable, total);
    }

    private int checkPeak( int peak , int offPeak) {
        if (TimeValidator.isPeakTime(LocalDate.now())) {
            return peak;
        }
        return offPeak;
    }


    private List<ProductSearchResponse> doPaginated(List<ProductSearchResponse> response,
                                                    Pageable pageable, int total) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        return response.subList(start, end);
    }

    private BooleanBuilder allFilter(ProductSearchRequest productSearchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqParking(productSearchRequest.getParking()))
            .and(eqBrunch(productSearchRequest.getBrunch()))
            .and(eqPool(productSearchRequest.getPool()))
            .and(eqOcean(productSearchRequest.getOceanView()))
            .and(containsLocation(productSearchRequest.getLocation()))
            .and(goeMaximumPeople(productSearchRequest.getQuantityPeople()))
            .and(
                betweenDate(productSearchRequest.getCheckIn(), productSearchRequest.getCheckOut()));

        return builder;
    }

    private BooleanExpression betweenDate(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn != null && checkOut != null) {
            return reservation.startDate.between(checkIn.atStartOfDay(),
                    checkOut.atStartOfDay())
                .and(reservation.endDate
                    .between(checkIn.atStartOfDay().plusDays(1), checkOut.atStartOfDay().plusDays(1)));
        }
        return reservation.startDate.goe(LocalDateTime.now());
    }

    private BooleanExpression goeMaximumPeople(Integer maximumPeople) {
        if (maximumPeople == null || maximumPeople == 0) {
            return null;
        }
        return hotel.room.maxPeople.goe(maximumPeople);
    }

    private BooleanExpression containsLocation(String hotelMainAddress) {
        if (hotelMainAddress == null || hotelMainAddress.isEmpty()) {
            return null;
        }
        return hotel.hotelMainAddress.eq(hotelMainAddress);
    }

    private BooleanExpression eqParking(Boolean hasParking) {
        if (hasParking == null) {
            return null;
        }
        return roomTheme.parkingZone.eq(hasParking);
    }

    private BooleanExpression eqBrunch(Boolean hasBrunch) {
        if (hasBrunch == null) {
            return null;
        }
        return roomTheme.breakfast.eq(hasBrunch);
    }

    private BooleanExpression eqPool(Boolean hasPool) {
        if (hasPool == null) {
            return null;
        }
        return roomTheme.pool.eq(hasPool);
    }

    private BooleanExpression eqOcean(Boolean hasOcean) {
        if (hasOcean == null) {
            return null;
        }
        return roomTheme.oceanView.eq(hasOcean);
    }

    private Boolean isFirstPrice(Integer price, Integer firstPrice) {
        return price == firstPrice;
    }

    private Integer getSalePrice(LocalDateTime startDate, Integer secondPeriod, Integer firstPrice,
                                 Integer secondPrice) {
        return LocalDateTime.now().isBefore(startDate.minus(secondPeriod, ChronoUnit.HOURS)) ?
            firstPrice : secondPrice;
    }

    private Double getSaleRate(Integer purchasePrice, Integer salePrice) {
        return ((purchasePrice.doubleValue() - salePrice.doubleValue()) /
            purchasePrice.doubleValue());
    }

    private Comparator<ProductSearchResponse> sort(String sortCondition) {
        Comparator<ProductSearchResponse> comparator;
        if (sortCondition == null || sortCondition.isEmpty()) {
            return Comparator.comparing(ProductSearchResponse::getCheckIn)
                .thenComparing(ProductSearchResponse::getSalePercentage, Comparator.reverseOrder());
        }
        switch (sortCondition) {
            case "최신 등록 순" ->
                comparator = Comparator.comparing(ProductSearchResponse::getCreatedAt).reversed();
            case "높은 할인 순" -> comparator =
                Comparator.comparing(ProductSearchResponse::getSalePercentage).reversed();
            case "낮은 가격 순" ->
                comparator = Comparator.comparing(ProductSearchResponse::getSalePrice); //낮은 가격순
            default -> comparator = Comparator.comparing(ProductSearchResponse::getCheckIn)
                .thenComparing(ProductSearchResponse::getSalePercentage, Comparator.reverseOrder());
        }

        return comparator;
    }

}
