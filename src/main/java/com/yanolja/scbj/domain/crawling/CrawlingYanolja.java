package com.yanolja.scbj.domain.crawling;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomImageRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomPriceRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
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
public class CrawlingYanolja {

    private final String BASE_URL = "https://www.yanolja.com/hotel/r-";
    private final String SUFFIX_URL = "?advert=AREA&topAdvertiseMore=0";
    private final String CLASS_NAME = "class name";
    private final String CSS_SELECTOR = "css selector";
    private final String XPATH = "xpath";
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelRoomPriceRepository hotelRoomPriceRepository;
    private final HotelRoomImageRepository hotelRoomImageRepository;
    private final String[] localNumberArray = {
        "900582", "900583", "900584", "900585", "900586", "900587", "900588", "900589", "900590"
    };
    private final String[] localNameArray = {
        "서울", "부산", "제주", "경기", "인천", "강원", "경상", "전라", "충청"
    };
    private final String[] bedTypeArray = {
        "싱글 침대", "더블 침대", "퀸 침대", "킹 침대",
    };

    @GetMapping("/crawling")
    public void crawling() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable--gpu");
        chromeOptions.addArguments("--start-minimized");
        chromeOptions.addArguments("--disable-popup-blocking");
        System.setProperty("webdriver.chrome.driver",
            "/Users/qwert/Downloads/chromedriver-mac-arm64/chromedriver");
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);

        for (int i = 0; i < localNumberArray.length; i++) {
            String localNumber = localNumberArray[i];
            String url = BASE_URL + localNumber + SUFFIX_URL;

            chromeDriver.get(url);
            chromeDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

            String hotelDetailUrlPath = "div.PlaceListBody_listGroup__LddQf > div > div > a";
            List<WebElement> hotelDetailElements = chromeDriver.findElements(
                By.cssSelector(hotelDetailUrlPath));

            System.err.println(hotelDetailElements.size());

            for (WebElement hotelDetailElement : hotelDetailElements) {
                try {
                    String hotelUrl = hotelDetailElement.getAttribute("href");
                    System.out.println(hotelUrl);

                    ChromeDriver secondChromeDriver = new ChromeDriver();
                    secondChromeDriver.get(hotelUrl);

                    secondChromeDriver.manage().timeouts()
                        .implicitlyWait(Duration.ofMillis(3000));

                    // 호텔 이름
                    String hotelNameClassName = "css-1g3ik0v";
                    WebElement hotelNameElement = getElement(CLASS_NAME, hotelNameClassName,
                        secondChromeDriver);

                    // 방 이름
                    String roomNameCssSelector = "h3.css-deizzc > div.css-1rr4h0w";
                    WebElement roomNameElement = getElement(CSS_SELECTOR, roomNameCssSelector,
                        secondChromeDriver);

                    // 체크인, 체크아웃
                    String timeCssSelector = "a.css-1w7jlh2 > div.css-1qxtkjb";
                    WebElement timeElement = getElement(CSS_SELECTOR, timeCssSelector,
                        secondChromeDriver);

                    String[] timeArray = timeElement.getText().split("\n");
                    int checkInHour = Integer.parseInt(timeArray[1].split(":")[0]);
                    int checkInMin = Integer.parseInt(timeArray[1].split(":")[1]);

                    int checkOutHour = Integer.parseInt(timeArray[3].split(":")[0]);
                    int checkOutMin = Integer.parseInt(timeArray[3].split(":")[1]);

                    // 기준인원, 최대인원
                    String peopleNumClassName = "css-18j6obq";
                    WebElement peopleNumElement = getElement(CLASS_NAME, peopleNumClassName,
                        secondChromeDriver);

                    char[] peopleNumArray = peopleNumElement.getText().toCharArray();

                    // 호텔 이미지, 방 이미지
                    String imgPath = "//*[@id=\"__next\"]/div/div/main/article/div[1]/section/div[1]/div/div/div[3]/div/span/img";
                    WebElement hotelImgElement = getElement(XPATH, imgPath, secondChromeDriver);

                    String hotelImgUrl = hotelImgElement.getAttribute("src");

                    String nextImgBtnClassName = "css-ln49wb";
                    WebElement nextBtn = getElement(CLASS_NAME, nextImgBtnClassName,
                        secondChromeDriver);
                    nextBtn.click();

                    WebElement roomImgElement = getElement(XPATH, imgPath, secondChromeDriver);
                    String roomImgUrl = roomImgElement.getAttribute("src");

                    // 호텔 주소
                    String hotelAddressSelector = "div.css-11ynnk0 > div.css-cxbger > div> span";
                    WebElement addressElement = getElement(CSS_SELECTOR, hotelAddressSelector,
                        secondChromeDriver);

                    // 가격
                    Random random = new Random();
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
                        .checkIn(LocalTime.of(checkInHour, checkInMin))
                        .checkOut(LocalTime.of(checkOutHour, checkOutMin))
                        .standardPeople(Integer.parseInt(String.valueOf(peopleNumArray[3])))
                        .maxPeople(Integer.parseInt(String.valueOf(peopleNumArray[11])))
                        .bedType(randomBedType)
                        .roomTheme(roomTheme)
                        .build();

                    Hotel hotel = Hotel.builder()
                        .hotelName(hotelNameElement.getText())
                        .hotelMainAddress(localNameArray[i])
                        .hotelDetailAddress(addressElement.getText())
                        .room(room)
                        .hotelInfoUrl(hotelUrl)
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

                    hotelRoomRepository.save(hotel);
                    hotelRoomPriceRepository.save(hotelRoomPrice);
                    hotelRoomImageRepository.save(hotelImage);
                    hotelRoomImageRepository.save(roomImage);

                    secondChromeDriver.quit();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("error");
                }
            }
        }
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

}

