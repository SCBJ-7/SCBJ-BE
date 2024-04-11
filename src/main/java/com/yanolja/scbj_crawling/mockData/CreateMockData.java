package com.yanolja.scbj_crawling.mockData;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomImageRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomPriceRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RefundPolicyRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RoomThemeRepository;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj_crawling.dto.response.RoomInfoResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
public class CreateMockData {

    private final String BASE_URL = "https://www.yanolja.com/hotel/r-";
    private final String SUFFIX_URL = "?advert=AREA&topAdvertiseMore=0";
    private final String CLASS_NAME = "class name";
    private final String CSS_SELECTOR = "css selector";
    private final String XPATH = "xpath";
    private final String[] localNumberArray = {
        "900582", "900583", "900584", "900585", "900586", "900587", "900588", "900589", "900590"
    };
    private final String[] localNameArray = {
        "서울", "부산", "제주", "경기", "인천", "강원", "경상", "전라", "충청"
    };
    private final String[] bedTypeArray = {
        "싱글 침대", "더블 침대", "퀸 침대", "킹 침대",
    };
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelRoomPriceRepository hotelRoomPriceRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final HotelRoomImageRepository hotelRoomImageRepository;
    private final YanoljaMemberRepository yanoljaMemberRepository;
    private final ReservationRepository reservationRepository;
    private final RoomThemeRepository roomThemeRepository;

    @GetMapping("/mockData")
    public void createMockData() {
//        crawling();
//        createYanoljaMember();
//        createRefundPolicy();
//        createReservation();
    }

    private void crawling() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable--gpu");
        chromeOptions.addArguments("--start-minimized");
        chromeOptions.addArguments("--disable-popup-blocking");
        System.setProperty("webdriver.chrome.driver",
            "./driver/chromedriver");
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        for (int i = 0; i < localNameArray.length; i++) {
            String localNumber = localNumberArray[i];
            String url = BASE_URL + localNumber + SUFFIX_URL;

            System.out.println(url);
            chromeDriver.get(url);
            chromeDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

            String hotelDetailUrlPath = "div.PlaceListItemBanner_container__ARsIm > a";
            List<WebElement> hotelDetailElements = chromeDriver.findElements(
                By.cssSelector(hotelDetailUrlPath));

            System.err.println("호텔 개수: " + hotelDetailElements.size());
            int cnt = 0;
            for (WebElement hotelDetailElement : hotelDetailElements) {
                try {
                    String hotelUrl = hotelDetailElement.getAttribute("href");
                    System.out.println(hotelUrl);

                    ChromeDriver secondChromeDriver = new ChromeDriver();

                    secondChromeDriver.get(hotelUrl);
                    secondChromeDriver.manage().timeouts()
                        .implicitlyWait(Duration.ofMillis(3000));

                    String roomUrlClassName = "css-1w7jlh2";
                    WebElement element = getElement(CLASS_NAME, roomUrlClassName,
                        secondChromeDriver);
                    String[] roomHref = element.getAttribute("href").split("/");

                    String[] hotelUrlArr = hotelUrl.split("/");
                    String roomUrl = "https://place-site.yanolja.com/places/" + hotelUrlArr[hotelUrlArr.length - 1] + "/" + roomHref[roomHref.length - 2];

                    // 호텔 평점
                    RoomInfoResponse roomInfoResponse = getRoomInfoAndRating(roomUrl);

                    // 호텔 성급
//                    String hotelLevel = "css-1xsh8xn";
                    String hotelLevel = "css-be09kp";
                    WebElement hotelLevelElement = getElement(CLASS_NAME, hotelLevel,
                        secondChromeDriver);

                    // 호텔 이름
                    String hotelNameClassName = "css-1uitq6g";
                    WebElement hotelNameElement = getElement(CLASS_NAME, hotelNameClassName,
                        secondChromeDriver);

                    // 방 이름
//                    String roomNameCssSelector = "h3.css-deizzc > div.css-1rr4h0w";
                    String roomNameClassName = "css-94cw4j";
                    WebElement roomNameElement = getElement(CLASS_NAME, roomNameClassName,
                        secondChromeDriver);

                    // 기준인원, 최대인원
                    Random random = new Random();
                    int maxPeopleStart = 2;
                    int maxPeopleEnd = 4;
                    int maxPeopleNum = random.nextInt(maxPeopleEnd - maxPeopleStart + 1) + maxPeopleStart;

                    // 체크인 아웃
                    int checkIn = random.nextInt(18 - 15 + 1) + 15;
                    int checkOut = random.nextInt(13 - 10 + 1) + 11;


                    // 호텔 이미지, 방 이미지
                    String imgPath = "//*[@id=\"__next\"]/div/div/main/article/div[1]/div/section/div[1]/div/div/div[3]/div/span/img";
                    WebElement hotelImgElement = getElement(XPATH, imgPath, secondChromeDriver);

                    String hotelImgUrl = hotelImgElement.getAttribute("src");

                    String roomImgClassName = "css-sr2c7j";
                    WebElement roomImgElement = getElement(CLASS_NAME, roomImgClassName, secondChromeDriver);
                    String roomImgUrl = roomImgElement.getAttribute("src");

                    // 호텔 주소
                    String hotelAddressSelector = "div.css-11ynnk0 > div.css-cxbger > div> span";
                    WebElement addressElement = getElement(CSS_SELECTOR, hotelAddressSelector,
                        secondChromeDriver);

                    // 가격
                    random = new Random();
                    int min = 20;
                    int max = 50;
                    int randomNum = random.nextInt(max - min + 1) + min;
                    int price = randomNum * 10000;

                    // 룸 테마
                    boolean hasParkingZone = random.nextBoolean();
                    boolean hasPool = random.nextBoolean();
                    boolean hasOceanView = random.nextBoolean();
                    boolean hasBreakfast = random.nextBoolean();

                    // 베드 타입
                    int randomBedTypeNum = random.nextInt(4);
                    String randomBedType = bedTypeArray[randomBedTypeNum];

                    RoomTheme roomTheme = RoomTheme.builder()
                        .parkingZone(hasParkingZone)
                        .pool(hasPool)
                        .oceanView(hasOceanView)
                        .breakfast(hasBreakfast)
                        .build();

                    Room room = Room.builder()
                        .roomName(roomNameElement.getText())
                        .checkIn(LocalTime.of(checkIn, 0))
                        .checkOut(LocalTime.of(checkOut, 0))
                        .standardPeople(2)
                        .maxPeople(maxPeopleNum)
                        .bedType(randomBedType)
                        .roomTheme(roomTheme)
                        .roomAllRating(roomInfoResponse.getRoomAllRating())
                        .roomKindnessRating(roomInfoResponse.getRoomKindnessRating())
                        .roomCleanlinessRating(roomInfoResponse.getRoomCleanlinessRating())
                        .roomConvenienceRating(roomInfoResponse.getRoomConvenienceRating())
                        .roomLocationRating(roomInfoResponse.getRoomLocationRating())
                        .facilityInformation(roomInfoResponse.getRoomFacilityInfo())
                        .build();

                    Hotel hotel = Hotel.builder()
                        .hotelLevel(hotelLevelElement.getText())
                        .hotelName(hotelNameElement.getText())
                        .hotelMainAddress(localNameArray[i])
                        .hotelDetailAddress(addressElement.getText())
                        .room(room)
                        .hotelInfoUrl(hotelUrl)
                        .hotelLevel(hotelLevelElement.getText())
                        .build();

                    HotelRoomImage hotelImage = HotelRoomImage.builder()
                        .hotel(hotel)
                        .url(hotelImgUrl)
                        .build();

                    HotelRoomImage roomImage = HotelRoomImage.builder()
                        .hotel(hotel)
                        .url(roomImgUrl)
                        .build();

                    HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                        .hotel(hotel)
                        .peakPrice(price)
                        .offPeakPrice((int) (price * 0.8))
                        .build();

                    roomThemeRepository.save(roomTheme);
                    hotelRoomRepository.save(hotel);
                    hotelRoomPriceRepository.save(hotelRoomPrice);
                    hotelRoomImageRepository.save(hotelImage);
                    hotelRoomImageRepository.save(roomImage);

                    secondChromeDriver.quit();
                    System.err.println("quit");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private RoomInfoResponse getRoomInfoAndRating(String roomUrl) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable--gpu");
        chromeOptions.addArguments("--start-minimized");
        chromeOptions.addArguments("--disable-popup-blocking");
        System.setProperty("webdriver.chrome.driver",
            "./driver/chromedriver");
        ChromeDriver thirdChromeDriver = new ChromeDriver(chromeOptions);
        thirdChromeDriver.manage().timeouts()
            .implicitlyWait(Duration.ofMillis(3000));

        thirdChromeDriver.get(roomUrl);

        // 객실 기본 정보
        String roomFacilityClassName = "css-t2dyoj";
        List<WebElement> facilityElements = thirdChromeDriver.findElements(
            By.className(roomFacilityClassName));

        String facilityInformation = facilityElements.stream()
            .map(webElement -> webElement.getText() + "\n")
            .collect(Collectors.joining());

        String[] roomUrlArray = roomUrl.split("/");
        String hotelNumber = roomUrlArray[roomUrlArray.length - 2];
        String roomNumber = roomUrlArray[roomUrlArray.length - 1];
        String roomRatingUrl = "https://www.yanolja.com/reviews/domestic/" + hotelNumber + "?roomTypeIds=" + roomNumber;

        thirdChromeDriver.get(roomRatingUrl);

        // 호텔 평점
        String hotelRatingClassName = "css-1pd8ix2";
        String roomRatingClassName = "css-ftdln6";
//        String roomRatingClassName = "css-iz9ise";

        WebElement hotelRatingElement = getElement(CLASS_NAME, hotelRatingClassName, thirdChromeDriver);
        List<WebElement> roomRatingElementList = thirdChromeDriver.findElements(
            By.className(roomRatingClassName));

        RoomInfoResponse roomInfo = RoomInfoResponse.builder()
            .roomFacilityInfo(facilityInformation)
            .roomAllRating(hotelRatingElement.getText())
            .roomKindnessRating(roomRatingElementList.get(0).getText())
            .roomCleanlinessRating(roomRatingElementList.get(1).getText())
            .roomConvenienceRating(roomRatingElementList.get(2).getText())
            .roomLocationRating(roomRatingElementList.get(3).getText())
            .build();

        thirdChromeDriver.quit();

        return roomInfo;
    }

    private WebElement getElement(String type, String selector, ChromeDriver driver) {
        switch (type) {
            case "class name":
                return driver.findElement(By.className(selector));
            case "css selector":
                return driver.findElement(By.cssSelector(selector));
            case "xpath":
                return driver.findElement(By.xpath(selector));
            default:
                return null;
        }
    }

    private void createYanoljaMember() {
        String email = "";
        for (int i = 1; i <= 100; i++) {
            email = "test" + i + "@example.com";
            yanoljaMemberRepository.save(YanoljaMember.builder()
                .email(email)
                .build());
        }
    }

    private void createRefundPolicy() {
        List<Hotel> hotelList = hotelRoomRepository.findAll();

        for (Hotel hotel : hotelList) {
            Random random = new Random();
            int randomBaseDate = random.nextInt(1, 7);
            int randomPercent = 0;

            if (randomBaseDate == 1) {
                randomPercent = random.nextInt(0, 10);
            } else if (randomBaseDate == 2) {
                randomPercent = random.nextInt(10, 20);
            } else if (randomBaseDate == 3) {
                randomPercent = random.nextInt(20, 30);
            } else if (randomBaseDate == 4) {
                randomPercent = random.nextInt(30, 40);
            } else if (randomBaseDate == 5) {
                randomPercent = random.nextInt(40, 50);
            } else if (randomBaseDate == 6) {
                randomPercent = random.nextInt(60, 70);
            } else if (randomBaseDate == 7) {
                randomPercent = random.nextInt(80, 90);
            }

            RefundPolicy refundPolicy = RefundPolicy.builder()
                .hotel(hotel)
                .baseDate(randomBaseDate)
                .percent(randomPercent)
                .build();

            refundPolicyRepository.save(refundPolicy);
        }
    }


    private void createReservation() {
        List<Hotel> hotelList = hotelRoomRepository.findAll();
        List<YanoljaMember> yanoljaMemberList = yanoljaMemberRepository.findAll();

        Random random = new Random();

        for (Hotel hotel : hotelList) {
            YanoljaMember yanoljaMember = yanoljaMemberList.get(
                random.nextInt(yanoljaMemberList.size() - 1));

            LocalDate startDate = LocalDate.of(2024, random.nextInt(2, 3), random.nextInt(1, 29));
            LocalDateTime startDateTime = LocalDateTime.of(startDate, hotel.getRoom().getCheckIn());
            LocalDateTime endDateTime = LocalDateTime.of(startDate.plusDays(random.nextInt(1, 5)),
                hotel.getRoom().getCheckOut());

            int price = hotel.getHotelRoomPrice().getPeakPrice();
            String splitPrice = String.valueOf(price).substring(0, 2);
            int left = String.valueOf(price).substring(2).length();
            int randomInt = random.nextInt(5, 10);
            int finalPrice = (int) ((Integer.parseInt(splitPrice) * (randomInt * 0.1)) * Math.pow(
                10, left));

            Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .yanoljaMember(yanoljaMember)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .purchasePrice(finalPrice)
                .build();

            reservationRepository.save(reservation);
        }

    }
}
