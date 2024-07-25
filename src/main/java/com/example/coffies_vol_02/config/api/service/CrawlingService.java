package com.example.coffies_vol_02.config.api.service;

import com.example.coffies_vol_02.config.api.dto.PlaceCrawlingDto;
import com.example.coffies_vol_02.config.api.dto.PlaceDocumentDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.service.PlaceService;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final PlaceService placeService;

    //가게 크롤링
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";

    public static String WEB_DRIVER_PATH = "C:\\Users\\well4\\OneDrive\\바탕 화면\\chromedriver-win32 (1)\\chromedriver-win32\\chromedriver.exe";

    private static final String DEFAULT_IMAGE_PATH = "C:\\spring_work\\workspace\\CoffiesVol.02\\default_image.png"; // 대체 이미지 URL

    private static final int MAX_ATTEMPTS = 3;

    private static final int WAIT_TIME = 20; // 20초 대기

    private static int storeNumber = 1; // 가게 번호 초기화


    public void runCrawlingAndSaveToCSV() {
        WebDriver driver = null;
        try {
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("headless");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--remote-allow-origins=*");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); //암시적 대기 10초
            driver.get("https://map.kakao.com//");

            String searchKeyword = "강북구 카페";
            WebElement searchBox = driver.findElement(By.id("search.keyword.query"));
            searchBox.sendKeys(searchKeyword);
            searchBox.sendKeys(Keys.RETURN);

            TimeUnit.SECONDS.sleep(2);

            collectStoreInfo(driver);

        } catch (Exception e) {
            log.error("Error occurred during crawling and CSV generation: {}", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void collectStoreInfo(WebDriver driver) throws InterruptedException, IOException {
        List<List<String>> dataLines = new ArrayList<>();
        dataLines.add(List.of("번호", "가게이름", "가게주소", "가게시작시간", "가게종료시간", "전화번호", "메인이미지URL", "서브이미지1URL", "서브이미지2URL", "서브이미지3URL"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('dimmedLayer').style.display='none';");
        TimeUnit.SECONDS.sleep(2);

        WebElement option2 = driver.findElement(By.xpath("//*[@id=\"info.main.options\"]/li[2]/a"));
        option2.click();
        TimeUnit.SECONDS.sleep(2);

        WebElement btn = driver.findElement(By.cssSelector(".more"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        boolean hasNextPage = true;

        initializeCSV();

        while (hasNextPage) {
            for (int pageNo = 1; pageNo <= 5; pageNo++) {

                int attempt = 0;
                boolean success = false;

                while (!success & attempt < MAX_ATTEMPTS) {
                    try {
                        String xPath = "//*[@id=\"info.search.page.no" + pageNo + "\"]";
                        WebElement pageElement = new WebDriverWait(driver, Duration.ofSeconds(30L)).until(
                                ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
                        pageElement.sendKeys(Keys.ENTER);
                        Thread.sleep(2000);

                        List<WebElement> storeList = driver.findElements(By.cssSelector(".PlaceItem"));
                        String originalWindow = driver.getWindowHandle();

                        for (WebElement store : storeList) {
                            try {
                                WebElement detailButton = store.findElement(By.cssSelector(".moreview"));
                                String detailUrl = detailButton.getAttribute("href");
                                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", detailUrl);
                                Thread.sleep(2000);

                                Set<String> allWindows = driver.getWindowHandles();
                                for (String window : allWindows) {
                                    if (!window.equals(originalWindow)) {
                                        driver.switchTo().window(window);
                                        break;
                                    }
                                }

                                List<String> storeInfo = new ArrayList<>();
                                storeInfo.add(String.valueOf(storeNumber++));
                                String storeName = driver.getTitle().trim().replace(" | 카카오맵", "");
                                storeInfo.add(storeName);

                                try {
                                    String storeAddress = driver.findElement(By.cssSelector(".txt_address")).getText();
                                    storeInfo.add(storeAddress);
                                } catch (Exception e) {
                                    storeInfo.add("");
                                }

                                try {
                                    StringBuilder storeHours = new StringBuilder();
                                    List<WebElement> hoursElements = driver.findElements(By.cssSelector(".list_operation > li"));
                                    for (WebElement element : hoursElements) {
                                        storeHours.append(element.getText()).append(" ");
                                    }
                                    String[] hours = storeHours.toString().trim().split("~");
                                    String storeStartTime = hours.length > 0 ? hours[0].trim() : "";
                                    String storeEndTime = hours.length > 1 ? hours[1].trim() : "";
                                    storeInfo.add(extractTimeFromHours(storeStartTime));
                                    storeInfo.add(extractTimeFromHours(storeEndTime));
                                } catch (Exception e) {
                                    storeInfo.add("");
                                    storeInfo.add("");
                                }

                                try {
                                    String storePhone = driver.findElement(By.cssSelector(".txt_contact")).getText();
                                    storeInfo.add(storePhone);
                                } catch (Exception e) {
                                    storeInfo.add("");
                                }

                                try {
                                    WebElement mainImageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                                            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='0']")));
                                    String mainImageUrl = extractUrlFromStyle(mainImageElement.getAttribute("style"));
                                    storeInfo.add(mainImageUrl);
                                } catch (TimeoutException e) {
                                    for (int i = 0; i < 4; i++) {
                                        storeInfo.add(DEFAULT_IMAGE_PATH);
                                    }
                                }

                                for (int i = 1; i <= 3; i++) {
                                    try {
                                        WebElement imageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                                                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='" + i + "']")));
                                        String imageUrl = extractUrlFromStyle(imageElement.getAttribute("style"));
                                        storeInfo.add(imageUrl);
                                    } catch (TimeoutException e) {
                                        storeInfo.add(DEFAULT_IMAGE_PATH);
                                    }
                                }

                                writeToCSV(storeInfo);
                                driver.close();
                                driver.switchTo().window(originalWindow);
                                Thread.sleep(4000);
                                success = true;
                            } catch (Exception e) {
                                driver.navigate().back();
                                Thread.sleep(4000);
                            }
                        }

                    } catch (Exception e) {
                        attempt++;
                    }
                }

                if (attempt >= MAX_ATTEMPTS) {
                    hasNextPage = false;
                    break;
                }
            }

            try {
                WebElement nextPageBtn = driver.findElement(By.id("info.search.page.next"));
                if (nextPageBtn.isDisplayed() && nextPageBtn.isEnabled()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextPageBtn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageBtn);
                    Thread.sleep(4000);
                } else {
                    hasNextPage = false;
                }
            } catch (Exception e) {
                hasNextPage = false;
            }
        }
    }

    private String extractUrlFromStyle(String style) {
        int start = style.indexOf("url(") + 4;
        int end = style.indexOf(")", start);
        if (start > 3 && end > start) {
            return style.substring(start, end - 1);
        } else {
            return DEFAULT_IMAGE_PATH;
        }
    }

    private String extractTimeFromHours(String hours) {
        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2}");
        Matcher matcher = pattern.matcher(hours);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    private void writeToCSV(List<String> storeInfo) throws IOException {
        String filePath = "store_info.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            String[] record = storeInfo.toArray(new String[0]);
            writer.writeNext(record);
            log.info("CSV 파일에 저장: {}", String.join(",", record));
        } catch (IOException e) {
            log.error("CSV 파일 저장 실패: {}", e.getMessage());
            throw e;
        }
    }

    private void initializeCSV() {
        String filePath = "store_info.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"번호", "가게명", "가게주소", "가게시작시간", "가게종료시간", "가게전화번호", "메인이미지URL", "서브이미지1URL", "서브이미지2URL", "서브이미지3URL"};
            writer.writeNext(header);
        } catch (IOException e) {
            log.error("CSV 파일 초기화 실패: {}", e.getMessage());
        }
    }


    //DB 갱신
    private void updateDB(PlaceDocumentDto placeDocumentDto, PlaceCrawlingDto placeCrawlingDto) throws Exception {
        PlaceRequestDto placeRequestDto = PlaceRequestDto.builder()
                .placeName(placeDocumentDto.getPlaceName())
                .placePhone(placeDocumentDto.getPhone())
                .placeAddr1(placeDocumentDto.getRoadAddressName())
                .placeAddr2(placeDocumentDto.getAddressName())
                .placeLng(placeDocumentDto.getLongitude())
                .placeLat(placeDocumentDto.getLatitude())
                .placeStart(placeCrawlingDto.getPlaceStart())
                .placeClose(placeCrawlingDto.getPlaceClose())
                .build();
        log.info(placeRequestDto);
        PlaceImageRequestDto placeImageRequestDto = PlaceImageRequestDto
                .builder()
                .images(placeCrawlingDto.getPlaceImage())
                .build();
        log.info(placeImageRequestDto);
        placeService.createCafePlace(placeRequestDto, placeImageRequestDto);
    }

}
