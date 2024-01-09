package com.yanolja.scbj.domain.crawling;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomPriceRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RoomThemeRepository;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
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
    private final String baseUrl = "https://www.yanolja.com/hotel/r-";
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelRoomPriceRepository hotelRoomPriceRepository;

    // 강남/역삼/삼성, 신사/청담/압구정, 해운대/마린시티, 벡스코/센텀시티, 제주시/제주국제공항, 서귀포시/모슬포, 가평/청평/양평, 수원/화성,
    // 송도/소래포구, 인천국제공항/강화/을왕리, 강릉, 속초/고성, 대구/구미/안동/문경, 경주, 전주/완주, 광주/나주/함평, 대전/세종, 천안/아산/도고
    // , "910162" 서귀포시/모슬포 빠짐
    private String[] urlArray = {
        "910062", "910063", "910078", "910079", "910088", "910091", "910167", "910101",
        "910102", "910108", "910279", "910112", "910113", "910258", "910120", "910125", "910126"
    };

    private String[] localNameArray = {
        "서울", "서울", "부산", "부산", "제주", "경기", "경기", "인천", "인천", "강원", "강원", "경상",
        "경상", "전라", "전라", "충청", "충청"
    };

    private String[] checkInArray = {
        "15:00", "14:00", "13:00"
    };

    private String[] checkOutArray = {
        "10:00", "11:00", "12:00"
    };

    private String[] bedTypeArray = {
        "싱글 침대", "더블 침대", "퀸 침대", "킹 침대",
    };

    private final String suffix = "?advert=AREA&topAdvertiseMore=0";

    @GetMapping("/crawling")
    public void crawling(){
        String hotelNameClassName = "css-1g3ik0v";
        String roomNameClassName = "css-1rr4h0w";
        String peopleNumClassName = "css-18j6obq";
        String priceClassName = "css-g269i3";
        String placeClassName = "css-11ynnk0";

        String hotelImg = "css-sr2c7j";

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--start-minimized");
        chromeOptions.addArguments("--disable--gpu");
        chromeOptions.addArguments("--disable-popup-blocking");
        System.setProperty("webdriver.chrome.driver", "/Users/qwert/Downloads/chromedriver-mac-arm64/chromedriver");
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);


        for (int i = 0; i < urlArray.length; i++) {
            String local = urlArray[i];
            String url = baseUrl + local + suffix;
            chromeDriver.get(url);
            chromeDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(4000));
            try {
                List<WebElement> webElementsList = chromeDriver.findElements(
                    By.cssSelector("div.PlaceListBody_listGroup__LddQf > div > div > a"));
                for (WebElement a : webElementsList) {

                    String hotelUrl = a.getAttribute("href");
                    System.out.println(hotelUrl);
                    ChromeDriver secondChromeDriver = new ChromeDriver(chromeOptions);
                    secondChromeDriver.get(hotelUrl);
                    secondChromeDriver.manage().timeouts()
                        .implicitlyWait(Duration.ofMillis(4000));

                    WebElement hotelNameElement = secondChromeDriver.findElement(
                        By.className(hotelNameClassName));

                    WebElement roomNameElement = secondChromeDriver.findElement(
                        By.cssSelector("h3.css-deizzc > div." + roomNameClassName));

                    WebElement timeElement = secondChromeDriver.findElement(
                        By.cssSelector("a.css-1w7jlh2 > div.css-1qxtkjb"));

                                        String[] timeArray = timeElement.getText().split("\n");
                    int checkInHour = Integer.parseInt(timeArray[1].split(":")[0]);
                    int checkInMin = Integer.parseInt(timeArray[1].split(":")[1]);

                    int checkOutHour = Integer.parseInt(timeArray[3].split(":")[0]);
                    int checkOutMin = Integer.parseInt(timeArray[3].split(":")[1]);

                    WebElement peopleNumElement = secondChromeDriver.findElement(
                        By.className(peopleNumClassName));

                    WebElement hotelImgElement = secondChromeDriver.findElement(
                        By.cssSelector("div.swiper-wrapper > div.swiper-slide swiper-slide-visible swiper-slide-active > div > span > img"));

                    System.out.println(hotelImgElement.getAttribute("src"));

//                    WebElement priceElement = null;
//                    boolean hasPriceElement = secondChromeDriver.findElement(By.xpath(
//                            "section.css-1qwzivr > div > a > div.css-g269i3 > div.rack_price"))
//                        .isDisplayed();
//
//                    if(hasPriceElement){
//                        priceElement = secondChromeDriver.findElement(
//                            By.cssSelector("section.css-1qwzivr > div > a > div.css-g269i3 > div.rack_price"));
//                    }else if{
//                        priceElement = secondChromeDriver.findElement(
//                            By.cssSelector("section.css-1qwzivr > div > a > div.css-g269i3 > div.price "));
//                    }

//                    price  랜덤이 나을듯? ?

//                    System.out.println(priceElement.getText());
                    WebElement priceElement = null;
                    WebElement addressElement = secondChromeDriver.findElement(
                        By.cssSelector("div." + placeClassName + " > div.css-cxbger > div > span"));

                    RoomTheme roomTheme = RoomTheme.builder()
                        .parkingZone(true)
                        .build();

                    Room room = Room.builder()
                        .roomName(roomNameElement.getText())
                        .checkIn(LocalTime.of(checkInHour, checkInMin))
                        .checkOut(LocalTime.of(checkOutHour, checkOutMin))
                        .standardPeople(peopleNumElement.getText().indexOf(3))
                        .maxPeople(peopleNumElement.getText().indexOf(11))
                        .bedType("싱글 침대")
                        .roomTheme(roomTheme)
                        .build();

                    Hotel hotel = Hotel.builder()
                        .hotelName(hotelNameElement.getText())
                        .hotelMainAddress(localNameArray[i])
                        .hotelDetailAddress(addressElement.getText())
                        .room(room)
                        .hotelInfoUrl(hotelUrl)
                        .build();

                    HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                        .hotel(hotel)
//                        .peakPrice(Integer.parseInt(priceElement.getText().replace(",", "")))
//                        .offPeakPrice((int) (Integer.parseInt(priceElement.getText().replace(",", "")) * 0.8))
                        .build();

//                    hotelRoomRepository.save(hotel);
//                    hotelRoomPriceRepository.save(hotelRoomPrice);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("error");
            }

        }
    }
}

