package com.yanolja.scbj.domain.paymentHistory.paymentController;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

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
