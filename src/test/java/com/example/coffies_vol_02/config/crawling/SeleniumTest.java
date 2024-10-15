package com.example.coffies_vol_02.config.crawling;

import com.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@SpringBootTest
public class SeleniumTest {

    private WebDriver driver;

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";

    public static String WEB_DRIVER_PATH = "C:\\Users\\well4\\OneDrive\\바탕 화면\\chromedriver-win32 (1)\\chromedriver-win32\\chromedriver.exe";

    private static final String DEFAULT_IMAGE_PATH = "C:\\spring_work\\workspace\\CoffiesVol.02\\default_image.png"; // 대체 이미지 URL

    private static final int MAX_ATTEMPTS = 3;

    private static final int WAIT_TIME = 20; // 20초 대기

    private static int storeNumber = 1; // 가게 번호 초기화

    @Autowired
    private CrawlingService crawlingService;

    @BeforeEach
    public void init() throws Exception {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        // 2. WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless"); // 창 숨기는 옵션 추가
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
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
    @Disabled
    @DisplayName("가게정보 수집 + csv 파일로 저장하기.(이미지 포함)")
    public void test8() throws InterruptedException{

        List<List<String>> dataLines = new ArrayList<>();
        dataLines.add(List.of("번호", "가게이름", "가게주소", "가게시작시간", "가게종료시간", "전화번호", "메인이미지URL", "서브이미지1URL", "서브이미지2URL", "서브이미지3URL"));


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

        boolean hasNextPage = true;

        initializeCSV();

        while (hasNextPage) {
            for (int pageNo = 1; pageNo <= 5; pageNo++) {

                int attempt = 0;
                boolean success = false;

                while(!success&attempt <MAX_ATTEMPTS){
                    try {
                        // 페이지 넘기기
                        String xPath = "//*[@id=\"info.search.page.no" + pageNo + "\"]";
                        WebElement pageElement = new WebDriverWait(driver, Duration.ofSeconds(30L)).until(
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
                                Thread.sleep(2000);

                                // 새 탭으로 전환
                                Set<String> allWindows = driver.getWindowHandles();
                                for (String window : allWindows) {
                                    if (!window.equals(originalWindow)) {
                                        driver.switchTo().window(window);
                                        break;
                                    }
                                }

                                // 가게 정보를 저장할 리스트 생성
                                List<String> storeInfo = new ArrayList<>();
                                storeInfo.add(String.valueOf(storeNumber++));  // 번호
                                System.out.println(storeNumber);
                                // 상세 페이지 처리
                                // 가게명
                                String storeName = driver.getTitle().trim();
                                // "| 카카오맵" 부분 제거
                                storeName = storeName.replace(" | 카카오맵", "");
                                System.out.println("가게명:"+storeName);
                                storeInfo.add(storeName);

                                // 가게 주소
                                try {
                                    String storeAddress = driver.findElement(By.cssSelector(".txt_address")).getText();
                                    System.out.println("가게주소::"+storeAddress);
                                    storeInfo.add(storeAddress);
                                } catch (Exception e) {
                                    storeInfo.add("");
                                }

                                // 가게 운영시간
                                try {
                                    StringBuilder storeHours = new StringBuilder();
                                    List<WebElement> hoursElements = driver.findElements(By.cssSelector(".list_operation > li"));
                                    for (WebElement element : hoursElements) {
                                        storeHours.append(element.getText()).append(" ");
                                    }
                                    String[] hours = storeHours.toString().trim().split("~");
                                    String storeStartTime = hours.length > 0 ? hours[0].trim() : "";
                                    String storeEndTime = hours.length > 1 ? hours[1].trim() : "";

                                    System.out.println("시작시간::"+extractTimeFromHours(storeStartTime));
                                    System.out.println("종료 시간::"+extractTimeFromHours(storeEndTime));
                                    storeInfo.add(extractTimeFromHours(storeStartTime));  // 시작시간
                                    storeInfo.add(extractTimeFromHours(storeEndTime));  // 종료시간
                                } catch (Exception e) {
                                    storeInfo.add("");
                                    storeInfo.add("");
                                }

                                // 가게 전화번호
                                try {
                                    String storePhone = driver.findElement(By.cssSelector(".txt_contact")).getText();
                                    storeInfo.add(storePhone);
                                    System.out.println("전화번호::"+storePhone);
                                } catch (Exception e) {
                                    storeInfo.add("");
                                }

                                // 가게 이미지 정보 가져오기
                                try {
                                    // 메인 이미지
                                    WebElement mainImageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                                            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='0']")));
                                    String mainImageUrl = extractUrlFromStyle(mainImageElement.getAttribute("style"))
                                            .replace("\"", "");
                                    System.out.println("메인 이미지 URL: " + mainImageUrl);
                                    //메인 이미지 추가
                                    storeInfo.add(mainImageUrl);
                                } catch (TimeoutException e) {
                                    System.out.println("이미지: 정보 없음");
                                    storeInfo.add(DEFAULT_IMAGE_PATH);
                                }

                                // 나머지 3장 이미지
                                for (int i = 1; i <= 3; i++) {
                                    try {
                                        WebElement imageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                                                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='" + i + "']")));
                                        String imageUrl = extractUrlFromStyle(imageElement.getAttribute("style"))
                                                .replace("\"", "");
                                        System.out.println("이미지 URL: " + imageUrl);
                                        //서브 이미지 추가
                                        storeInfo.add(imageUrl);
                                    } catch (TimeoutException e) {
                                        //없는 경우 지정된 기본 이미지 저장하기.
                                        System.out.println("이미지 URL (data-pidx=" + i + "): 정보 없음");
                                        storeInfo.add(DEFAULT_IMAGE_PATH);
                                    }
                                }
                                //csv 파일 저장하기.
                                writeToCSV(storeInfo);
                                // 상세 조회 후 탭 닫기
                                driver.close();
                                // 원래 창으로 돌아가기
                                driver.switchTo().window(originalWindow);
                                Thread.sleep(4000);
                                success =true;
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                // 상세 조회 버튼 클릭 실패 시 다시 목록 페이지로 돌아가기
                                driver.navigate().back();
                                Thread.sleep(4000);
                            }
                        }

                    } catch (Exception e) {
                        attempt++;
                        System.out.println(e.getMessage());
                    }
                }

                if (attempt >= MAX_ATTEMPTS) {
                    System.out.println("최대 재시도 횟수 초과: 페이지 넘김 실패");
                    hasNextPage = false;
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
                    System.out.println("다음 페이지 버튼을 찾을 수 없음 또는 클릭할 수 없음");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                hasNextPage = false;
                System.out.println("?????????????::::" + hasNextPage);
            }

        }

    }

    @Test
    public void crawlingServiceTest(){
        crawlingService.runCrawlingAndSaveToCSV();
    }

    // CSS 스타일 문자열에서 URL을 추출
    private String extractUrlFromStyle(String style) {
        int start = style.indexOf("url(") + 4;
        int end = style.indexOf(")", start);
        if (start > 3 && end > start) {
            return style.substring(start, end - 1);
        } else {
            return DEFAULT_IMAGE_PATH;
        }
    }

    //시작시간 정규식 제거
    private String extractTimeFromHours(String hours) {
        // 예시: "월,화,수 10:00"
        // 시간 정보 추출하는 정규표현식 사용
        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2}");
        Matcher matcher = pattern.matcher(hours);
        if (matcher.find()) {
            return matcher.group(); // 시간 정보만 반환
        } else {
            return ""; // 시간 정보가 없을 경우 처리
        }
    }

    //csv 파일 작성
    private static void writeToCSV(List<String> storeInfo) {
        String filePath = "store_info.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            String[] record = storeInfo.toArray(new String[0]);
            writer.writeNext(record);
            System.out.println("CSV 파일에 저장: " + String.join(",", record));
        } catch (IOException e) {
            System.err.println("CSV 파일 저장 실패: " + e.getMessage());
        }
    }

    //csv파일 초기화
    private static void initializeCSV() {
        String filePath = "store_info.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"번호", "가게명", "가게주소", "가게시작시간", "가게종료시간", "가게전화번호", "메인이미지URL", "서브이미지1URL", "서브이미지2URL", "서브이미지3URL"};
            writer.writeNext(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

