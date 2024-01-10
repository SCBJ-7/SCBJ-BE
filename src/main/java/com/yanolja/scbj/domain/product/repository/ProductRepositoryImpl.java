package com.yanolja.scbj.domain.product.repository;

import static com.yanolja.scbj.domain.hotelRoom.entity.QHotel.hotel;
import static com.yanolja.scbj.domain.hotelRoom.entity.QHotelRoomImage.hotelRoomImage;
import static com.yanolja.scbj.domain.hotelRoom.entity.QRoom.room;
import static com.yanolja.scbj.domain.hotelRoom.entity.QRoomTheme.roomTheme;
import static com.yanolja.scbj.domain.product.entity.QProduct.product;
import static com.yanolja.scbj.domain.reservation.entity.QReservation.reservation;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.dto.response.QProductSearchResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    NumberExpression<Integer> priceToUse = new CaseBuilder()
        .when(product.secondPrice.isNull().and(product.secondPrice.eq(0))).then(product.firstPrice)
        .otherwise(product.secondPrice);

    NumberExpression<Double> discountRate = Expressions.numberTemplate(Double.class,
        " case when {0} = 0 then 0 else ({0} - {1}) / {0} end ", reservation.purchasePrice.doubleValue(), priceToUse.doubleValue());

    @Override
    public Page<ProductSearchResponse> search(Pageable pageable,
                                              ProductSearchRequest productSearchRequest) {

        List<ProductSearchResponse> response = queryFactory
            .select(new QProductSearchResponse(
                product.id,
                hotel.hotelName,
                hotel.room.bedType,
                hotelRoomImage.url,
                priceToUse,
                new CaseBuilder()
                    .when(product.secondPrice.isNull()).then(product.firstPrice)
                    .otherwise(product.secondPrice),
                discountRate,
                reservation.startDate,
                reservation.endDate
            ))
            .from(product)
            .innerJoin(product.reservation, reservation)
            .innerJoin(reservation.hotel, hotel)
            .innerJoin(roomTheme).on(hotel.room.roomTheme.id.eq(roomTheme.id))
            .join(roomTheme)
            .innerJoin(hotelRoomImage).on(hotelRoomImage.hotel.id.eq(hotel.id))
            .where(allFilter(productSearchRequest))
            .orderBy(orderType(productSearchRequest.getSorted()))
            .fetch();

        Long total = queryFactory
            .select(product.count())
            .from(product)
            .innerJoin(product.reservation, reservation)
            .innerJoin(reservation.hotel, hotel)
            .leftJoin(room.roomTheme, roomTheme).on(hotel.room.roomTheme.id.eq(roomTheme.id))
            .innerJoin(hotelRoomImage).on(hotelRoomImage.hotel.id.eq(hotel.id))
            .where(allFilter(productSearchRequest))
            .fetchOne();




        return new PageImpl<>(response, pageable, total != null ? total : 0);
    }

    private BooleanBuilder allFilter(ProductSearchRequest productSearchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqParking(productSearchRequest.getParking()))
            .and(eqBrunch(productSearchRequest.getBrunch()))
            .and(eqPool(productSearchRequest.getPool()))
            .and(eqOcean(productSearchRequest.getOceanView()))
            .and(containsLocation(productSearchRequest.getLocation()))
            .and(goeMaximumPeople(productSearchRequest.getQuantityPeople()))
            .and(betweenDate(productSearchRequest.getCheckIn(), productSearchRequest.getCheckOut()));

        return builder;
    }

    private BooleanExpression betweenDate(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn != null && checkOut != null) {
            return reservation.startDate.between(checkIn, checkOut.minusDays(1));
        }
        return reservation.startDate.goe(LocalDate.now());
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
        return hotel.hotelMainAddress.contains(hotelMainAddress);
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
        return roomTheme.parkingZone.eq(hasBrunch);
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

    private OrderSpecifier<?>[] orderType(String sorted) {
        if (sorted == null || sorted.isEmpty()) {
            return new OrderSpecifier[]{
                reservation.startDate.asc(),
                discountRate.desc()
            };
        }
        switch (sorted) {
            case "최신 등록 순":
                return new OrderSpecifier[] {product.createdAt.desc()};
            case "높은 할인 순":
                return new OrderSpecifier[] {discountRate.desc()};
            case "낮은 가격 순":
                return new OrderSpecifier[] {priceToUse.asc()};
            default:
                return new OrderSpecifier[] {reservation.startDate.asc(), discountRate.desc()};
        }
    }
}
