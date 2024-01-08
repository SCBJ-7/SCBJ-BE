package com.yanolja.scbj.domain.payment.paymentController;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.hisotryService.PurchasedHistoryService;
import com.yanolja.scbj.domain.payment.historycontroller.PurchasedHistoryController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

//@WebMvcTest(PurchasedHistoryController.class)
//@AutoConfigureRestDocs(outputDir = "target/snippets")
//public class PurchasedHistoryRestDocControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PurchasedHistoryService purchasedHistoryService;
//
//    @Test
//    void getPurchasedHistoriesTest() throws Exception {
//        PurchasedHistoryResponse response = new PurchasedHistoryResponse(
//            1L, LocalDateTime.now(), "시그니엘", "싱글스텐다드", 1000000, LocalDate.now(),
//            LocalDate.now().plusDays(1)
//        );
//
//        PageImpl<List<PurchasedHistoryResponse>> page = new PageImpl<>(
//            Collections.singletonList(Collections.singletonList(response)),
//            PageRequest.of(0, 10),
//            1
//        );
//        given(purchasedHistoryService.getPurchasedBeforeCheckIn(1, 10)).willReturn(page);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.get("/v1/members/purchased-history?page=1&pageSize=10")
//                    .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andDo(document("purchased-history",
//                responseFields(
//                    fieldWithPath("code").description("응답 코드"),
//                    fieldWithPath("message").description("응답 메시지"),
//                    subsectionWithPath("data").description("응답 데이터"),
//                    fieldWithPath("data[].id").description("구매 ID"),
//                    fieldWithPath("data[].createdAt").description("생성 시간"),
//                    fieldWithPath("data[].name").description("호텔 이름"),
//                    fieldWithPath("data[].roomType").description("객실 타입"),
//                    fieldWithPath("data[].price").description("가격"),
//                    fieldWithPath("data[].checkInDate").description("체크인 날짜"),
//                    fieldWithPath("data[].checkOutDate").description("체크아웃 날짜")
//                )));
//    }
//}
