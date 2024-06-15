package com.example.coffies_vol_02.config.Crawling;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
@SpringBootTest
public class SeleniumTest {

    private WebDriver driver;

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";

    public static String WEB_DRIVER_PATH = "C:\\Users\\well4\\OneDrive\\바탕 화면\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";

    @BeforeEach
    public void init() throws Exception {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        // 2. WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("headless"); // 창 숨기는 옵션 추가
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); //암시적 대기 10초
        // 카카오맵 접속
        driver.get("https://map.kakao.com//");
        // 접속한 페이지의 소스 확인.
        log.debug(driver.getPageSource());
        //검색 키워드
        String searchKeyword = "강북구 카페";
        //검색어를 입력하기.
        WebElement searchBox = driver.findElement(By.id("search.keyword.query"));
        searchBox.sendKeys(searchKeyword);
        searchBox.sendKeys(Keys.RETURN);

        TimeUnit.SECONDS.sleep(2);

    }

    @AfterEach
    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("카카오맵 페이지 검색어 입력후 장소탭 누르기.")
    public void testKakaoMapPageConnection() {
        //클릭시 방지.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('dimmedLayer').style.display='none';");

        //장소 탭 누르기.
        WebElement option2 = driver.findElement(By.xpath("//*[@id=\"info.main.options\"]/li[2]/a"));
        option2.click();

    }

    @Test
    @DisplayName("가게목록에서 상세보기로 들어가서 가게정보 보기.(15개)")
    public void test4() throws Exception {
        //클릭시 방지.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('dimmedLayer').style.display='none';");
        TimeUnit.SECONDS.sleep(2);

        //장소 탭 누르기.
        WebElement option2 = driver.findElement(By.xpath("//*[@id=\"info.main.options\"]/li[2]/a"));
        option2.click();
        TimeUnit.SECONDS.sleep(2);

        //목록에 있는 더보기 클릭하기.
        WebElement btn = driver.findElement(By.cssSelector(".more"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        // 가게 목록 가져오기
        List<WebElement> storeList = driver.findElements(By.cssSelector(".PlaceItem"));
        System.out.println("result" + storeList.toArray().length);
        Thread.sleep(5000); // 상세 조회 후 잠시 대기

        // 현재 창 핸들 저장
        String originalWindow = driver.getWindowHandle();

        for (WebElement store : storeList) {
            try {
                // 상세 조회 버튼 다시 찾기
                WebElement detailButton = store.findElement(By.cssSelector(".moreview"));
                String detailUrl = detailButton.getAttribute("href");

                // 새로운 탭에서 상세 조회 페이지 열기
                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", detailUrl);
                Thread.sleep(5000);

                // 새 탭으로 전환
                Set<String> allWindows = driver.getWindowHandles();

                for (String window : allWindows) {
                    if (!window.equals(originalWindow)) {
                        driver.switchTo().window(window);
                        break;
                    }
                }

                // 가게 이름
                String storeName = driver.getTitle().trim().replace("| 카카오맵","");
                System.out.println("가게 상세 정보: " + storeName);

                // 가게 주소
                String storeAddress = driver.findElement(By.cssSelector(".txt_address")).getText();
                System.out.println("주소: " + storeAddress);


                // 가게 운영시간
                try {
                    StringBuilder storeHours = new StringBuilder();
                    List<WebElement> hoursElements = driver.findElements(By.cssSelector(".list_operation > li"));
                    for (WebElement element : hoursElements) {
                        storeHours.append(element.getText()).append(" ");
                    }
                    System.out.println("운영시간: " + storeHours.toString().trim());
                } catch (Exception e) {
                    System.out.println("운영시간: 정보 없음");
                }

                // 가게 전화번호
                try {
                    String storePhone = driver.findElement(By.cssSelector(".txt_contact")).getText();
                    System.out.println("전화번호: " + storePhone);
                } catch (Exception e) {
                    System.out.println("전화번호: 정보 없음");
                }

                // 가게 이미지 정보 4장 가져오기
                try {
                    // 메인 이미지
                    WebElement mainImageElement = driver.findElement(By.cssSelector(".link_photo[data-pidx='0']"));
                    String mainImageUrl = mainImageElement.getAttribute("style");
                    mainImageUrl = mainImageUrl.substring(mainImageUrl.indexOf("url(") + 4, mainImageUrl.length() - 2);
                    System.out.println("메인 이미지 URL: " + mainImageUrl);

                    // 나머지 3장 이미지
                    for (int i = 1; i <= 3; i++) {
                        try {
                            WebElement imageElement = driver.findElement(By.cssSelector(".link_photo[data-pidx='" + i + "']"));
                            String imageUrl = imageElement.getAttribute("style");
                            imageUrl = imageUrl.substring(imageUrl.indexOf("url(") + 4, imageUrl.length() - 2);
                            System.out.println("이미지 URL: " + imageUrl);
                        } catch (Exception e) {
                            System.out.println("이미지 URL (data-pidx=" + i + "): 정보 없음");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("이미지: 정보 없음");
                }

                // 상세 조회 후 탭 닫기
                driver.close();
                // 원래 창으로 돌아가기
                driver.switchTo().window(originalWindow);
                Thread.sleep(2000);
            } catch (Exception e) {
                log.info(e.getMessage());
                // 상세 조회 버튼 클릭 실패 시 다시 목록 페이지로 돌아가기
                driver.navigate().back();
                Thread.sleep(2000);
            }
        }

    }

    @Test
    @DisplayName("test5+ 페이징(5개)")
    public void test6() throws InterruptedException {

        //장소 탭 클릭 방지.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('dimmedLayer').style.display='none';");
        TimeUnit.SECONDS.sleep(2);

        //장소 탭 누르기.
        WebElement option2 = driver.findElement(By.xpath("//*[@id=\"info.main.options\"]/li[2]/a"));
        option2.click();
        TimeUnit.SECONDS.sleep(2);

        //목록에 있는 더보기 클릭하기.
        WebElement btn = driver.findElement(By.cssSelector(".more"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);


        // 페이지 번호를 1부터 시작
        int pageNo = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            try {
                // 가게 목록 가져오기
                List<WebElement> storeList = driver.findElements(By.cssSelector(".PlaceItem"));

                // 현재 창 핸들 저장
                String originalWindow = driver.getWindowHandle();

                // 각 가게의 상세 조회 버튼 클릭
                for (WebElement store : storeList) {
                    try {
                        // 상세 조회 버튼 다시 찾기
                        WebElement detailButton = store.findElement(By.cssSelector(".moreview"));
                        String detailUrl = detailButton.getAttribute("href");

                        // 새로운 탭에서 상세 조회 페이지 열기
                        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", detailUrl);
                        Thread.sleep(2000);

                        // 새 탭으로 전환
                        Set<String> allWindows = driver.getWindowHandles();
                        for (String window : allWindows) {
                            if (!window.equals(originalWindow)) {
                                driver.switchTo().window(window);
                                break;
                            }
                        }
                        System.out.println("가게 URL:::" + driver.getCurrentUrl());
                        // 상세 페이지 처리
                        // 가게명
                        String storeName = driver.getTitle().trim();
                        System.out.println("가게명: " + storeName);

                        // 가게 운영시간
                        try {
                            StringBuilder storeHours = new StringBuilder();
                            List<WebElement> hoursElements = driver.findElements(By.cssSelector(".list_operation > li"));
                            for (WebElement element : hoursElements) {
                                storeHours.append(element.getText()).append(" ");
                            }
                            System.out.println("운영시간: " + storeHours.toString().trim());
                        } catch (Exception e) {
                            System.out.println("운영시간: 정보 없음");
                        }

                        // 가게 주소
                        String storeAddress = driver.findElement(By.cssSelector(".txt_address")).getText();
                        System.out.println("주소: " + storeAddress);

                        // 가게 전화번호
                        try {
                            String storePhone = driver.findElement(By.cssSelector(".txt_contact")).getText();
                            System.out.println("전화번호: " + storePhone);
                        } catch (Exception e) {
                            System.out.println("전화번호: 정보 없음");
                        }

                        // 가게 이미지 정보 4장 가져오기
                        try {
                            // 메인 이미지
                            WebElement mainImageElement = driver.findElement(By.cssSelector(".link_photo[data-pidx='0']"));
                            String mainImageUrl = mainImageElement.getAttribute("style");
                            mainImageUrl = mainImageUrl.substring(mainImageUrl.indexOf("url(") + 4, mainImageUrl.length() - 2);
                            System.out.println("메인 이미지 URL: " + mainImageUrl);

                            // 나머지 3장 이미지
                            for (int i = 1; i <= 3; i++) {
                                try {
                                    WebElement imageElement = driver.findElement(By.cssSelector(".link_photo[data-pidx='" + i + "']"));
                                    String imageUrl = imageElement.getAttribute("style");
                                    imageUrl = imageUrl.substring(imageUrl.indexOf("url(") + 4, imageUrl.length() - 2);
                                    System.out.println("이미지 URL: " + imageUrl);
                                } catch (Exception e) {
                                    System.out.println("이미지 URL (data-pidx=" + i + "): 정보 없음");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("이미지: 정보 없음");
                        }

                        // 상세 조회 후 탭 닫기
                        driver.close();

                        // 원래 창으로 돌아가기
                        driver.switchTo().window(originalWindow);
                        Thread.sleep(2000);

                    } catch (Exception e) {
                        log.info(e.getMessage());
                        // 상세 조회 버튼 클릭 실패 시 다시 목록 페이지로 돌아가기
                        driver.navigate().back();
                        Thread.sleep(2000);
                    }
                }

                // 다음 페이지로 이동
                pageNo++;
                System.out.println("pageNumber:::" + pageNo);
                // 페이지 넘기기
                String xPath = "//*[@id=\"info.search.page.no" + pageNo + "\"]";
                WebElement pageElement = new WebDriverWait(driver, Duration.ofSeconds(10L)).until(
                        ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
                pageElement.sendKeys(Keys.ENTER);
                Thread.sleep(2000);

            } catch (Exception e) {
                // 다음 페이지가 없는 경우 반복 종료
                hasNextPage = false;
            }
        }
    }

    @Test
    @DisplayName("가게정보를 수집 + 페이징 전부(5이상) ")
    public void test7() throws InterruptedException {

        // 장소 탭 클릭 방지.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('dimmedLayer').style.display='none';");
        TimeUnit.SECONDS.sleep(2);

        // 장소 탭 누르기.
        WebElement option2 = driver.findElement(By.xpath("//*[@id=\"info.main.options\"]/li[2]/a"));
        option2.click();
        TimeUnit.SECONDS.sleep(2);

        // 목록에 있는 더보기 클릭하기.
        WebElement btn = driver.findElement(By.cssSelector(".more"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        boolean hasNextPage = true;

        while (hasNextPage) {
            for (int pageNo = 1; pageNo <= 5; pageNo++) {
                try {
                    // 페이지 넘기기
                    String xPath = "//*[@id=\"info.search.page.no" + pageNo + "\"]";
                    WebElement pageElement = new WebDriverWait(driver, Duration.ofSeconds(40L)).until(
                            ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
                    pageElement.sendKeys(Keys.ENTER);
                    Thread.sleep(2000);

                    // 가게 목록 가져오기
                    List<WebElement> storeList = driver.findElements(By.cssSelector(".PlaceItem"));

                    // 현재 창 핸들 저장
                    String originalWindow = driver.getWindowHandle();

                    // 각 가게의 상세 조회 버튼 클릭
                    for (WebElement store : storeList) {
                        try {
                            // 상세 조회 버튼 다시 찾기
                            WebElement detailButton = store.findElement(By.cssSelector(".moreview"));
                            String detailUrl = detailButton.getAttribute("href");

                            // 새로운 탭에서 상세 조회 페이지 열기
                            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", detailUrl);
                            Thread.sleep(4000);

                            // 새 탭으로 전환
                            Set<String> allWindows = driver.getWindowHandles();
                            for (String window : allWindows) {
                                if (!window.equals(originalWindow)) {
                                    driver.switchTo().window(window);
                                    break;
                                }
                            }
                            // 상세 페이지 처리
                            // 가게명
                            String storeName = driver.getTitle().trim();
                            storeName = storeName.replace(" | 카카오맵", "");
                            System.out.println("가게명: " + storeName);

                            // 가게 운영시간
                            try {
                                StringBuilder storeHours = new StringBuilder();
                                List<WebElement> hoursElements = driver.findElements(By.cssSelector(".list_operation > li"));
                                for (WebElement element : hoursElements) {
                                    storeHours.append(element.getText()).append(" ");
                                }
                                System.out.println("운영시간: " + storeHours.toString().trim());
                            } catch (Exception e) {
                                System.out.println("운영시간: 정보 없음");
                            }

                            // 가게 주소
                            String storeAddress = driver.findElement(By.cssSelector(".txt_address")).getText();
                            System.out.println("주소: " + storeAddress);

                            // 가게 전화번호
                            try {
                                String storePhone = driver.findElement(By.cssSelector(".txt_contact")).getText();
                                System.out.println("전화번호: " + storePhone);
                            } catch (Exception e) {
                                System.out.println("전화번호: 정보 없음");
                            }

                            // 가게 이미지 정보 4장 가져오기
                            try {
                                // 메인 이미지
                                WebElement mainImageElement = new WebDriverWait(driver, Duration.ofSeconds(40L)).until(
                                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='0']")));
                                String mainImageUrl = extractUrlFromStyle(mainImageElement.getAttribute("style"));
                                System.out.println("메인 이미지 URL: " + mainImageUrl);

                                // 나머지 3장 이미지
                                for (int i = 1; i <= 3; i++) {
                                    try {
                                        WebElement imageElement = new WebDriverWait(driver, Duration.ofSeconds(40L)).until(
                                                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='" + i + "']")));
                                        String imageUrl = extractUrlFromStyle(imageElement.getAttribute("style"));
                                        System.out.println("이미지 URL: " + imageUrl);
                                    } catch (TimeoutException e) {
                                        System.out.println("이미지 URL (data-pidx=" + i + "): 정보 없음");
                                    }
                                }
                            } catch (TimeoutException e) {
                                System.out.println("이미지: 정보 없음");
                            }

                            // 상세 조회 후 탭 닫기
                            driver.close();

                            // 원래 창으로 돌아가기
                            driver.switchTo().window(originalWindow);
                            Thread.sleep(4000);

                        } catch (Exception e) {
                            log.info(e.getMessage());
                            // 상세 조회 버튼 클릭 실패 시 다시 목록 페이지로 돌아가기
                            driver.navigate().back();
                            Thread.sleep(4000);
                        }
                    }

                } catch (Exception e) {
                    log.info(e.getMessage());
                    hasNextPage = false;
                    log.info("lastPage::" + hasNextPage);
                    break;
                }
            }

            // 다음 페이지 버튼 클릭
            try {
                WebElement nextPageBtn = driver.findElement(By.id("info.search.page.next"));
                if (nextPageBtn.isDisplayed() && nextPageBtn.isEnabled()) {
                    // 스크롤해서 다음 페이지 버튼이 보이도록 함
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextPageBtn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageBtn);
                    Thread.sleep(4000);
                } else {
                    hasNextPage = false;
                    log.info("다음 페이지 버튼을 찾을 수 없음 또는 클릭할 수 없음");
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                hasNextPage = false;
                log.info("?????????????::::" + hasNextPage);
            }
        }
    }

    //
    private String extractUrlFromStyle(String style) {
        int start = style.indexOf("url(") + 4;
        int end = style.indexOf(")", start);
        if (start > 3 && end > start) {
            return style.substring(start, end - 1);
        } else {
            return "";
        }
    }

}

