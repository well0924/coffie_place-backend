package com.example.coffies_vol_02.config.crawling;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final CrawlingPlaceService crawlingPlaceService;

    private final CrawlingStoreService crawlingStoreService;

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";

    public static String WEB_DRIVER_PATH = "C:\\Users\\well4\\OneDrive\\바탕 화면\\chromedriver-win32 (1)\\chromedriver-win32\\chromedriver.exe";

    private static final String DEFAULT_IMAGE_PATH = "C:/spring_work/workspace/CoffiesVol.02/default_image.png";

    private static final int MAX_ATTEMPTS = 3;

    private static final int WAIT_TIME = 20; // 20초 대기

    private static int storeNumber = 1; // 가게 번호 초기화

    /**
     * 가게 정보 크롤링후 + csv 파일 저장 + 디비 저장
     * 스케줄러 (1달에 한번 자정에 실행)
     **/
    @Scheduled(cron = "0 0 0 1 * ?")
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
            //가게 정보 수집 + csv 파일 + 디비 저장
            collectStoreInfo(driver);

        } catch (Exception e) {
            log.error("Error occurred during crawling and CSV generation: {}", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    /**
     * 가게 정보 수집 (크롤링 수집)
     * @param driver 셀레니움 드라이버
     **/
    private void collectStoreInfo(WebDriver driver) throws InterruptedException {

        List<List<String>> dataLines = new ArrayList<>();
        //csv 파일에서 필요한 구분
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
        //csv 파일 초기화
        initializeCSV();

        while (hasNextPage) {
            for (int pageNo = 1; pageNo <= 5; pageNo++) {
                int attempt = 0;
                boolean success = false;

                while (!success & attempt < MAX_ATTEMPTS) {
                    try {
                        clickAndScrap(driver, pageNo);
                        success = true; // 성공적으로 클릭 및 스크랩이 완료되면 true로 설정
                    } catch (Exception e) {
                        attempt++;
                        System.out.println("Attempt " + attempt + " failed for page: " + pageNo);
                        if (attempt >= MAX_ATTEMPTS) {
                            System.out.println("Max attempts reached for page: " + pageNo);
                            hasNextPage = false;
                            break;
                        }
                        TimeUnit.SECONDS.sleep(2000); // 재시도 간 대기 시간
                    }
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
        //크롤링+csv파일 작성후 디비에 저장
        crawlingStoreService.processCsvAndSaveToDatabase("store_info.csv");
    }

    /**
     * 특정 페이지의 가게 정보를 수집하고 저장하는 메서드.
     * @param driver 셀레니움 WebDriver
     * @param pageNo 가게 정보가 있는 페이지 번호
     * @throws InterruptedException 스레드 인터럽트 예외
     * 주어진 메서드가 TimeoutException 또는 NoSuchElementException 발생 시 재시도하도록 설정합니다.
     * 최대 재시도 횟수는 MAX_ATTEMPTS로 설정되며,
     * 각 재시도 사이에 2000밀리초의 대기 시간을 갖습니다.
     */
    @Retryable(value = {TimeoutException.class, NoSuchElementException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = 2000))
    private void clickAndScrap(WebDriver driver, int pageNo) throws InterruptedException {
        String xPath = "//*[@id=\"info.search.page.no" + pageNo + "\"]";
        WebElement pageElement = new WebDriverWait(driver, Duration.ofSeconds(30L))
                .until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));

        pageElement.sendKeys(Keys.ENTER);
        Thread.sleep(2000);

        List<WebElement> storeList = driver.findElements(By.cssSelector(".PlaceItem"));
        String originalWindow = driver.getWindowHandle();

        for(WebElement store  : storeList) {
            scrapeStoreInfo(driver,store,originalWindow);
        }
    }

    /**
     * 가게 정보를 수집하는 메서드.
     * @param driver 셀레니움 WebDriver
     * @param store 가게 정보를 담고 있는 WebElement
     * @param originalWindow 원래 창의 핸들
     * @throws InterruptedException 스레드 인터럽트 예외
     * 주어진 메서드가 TimeoutException 또는 NoSuchElementException 발생 시 재시도하도록 설정합니다.
     * 최대 재시도 횟수는 MAX_ATTEMPTS로 설정되며,
     * 각 재시도 사이에 2000밀리초의 대기 시간을 갖습니다.
     */
    @Retryable(value = {TimeoutException.class, NoSuchElementException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = 2000))
    private void scrapeStoreInfo(WebDriver driver, WebElement store, String originalWindow) throws InterruptedException {
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
                //가게 운영 시작시간
                String storeStartTime = hours.length > 0 ? hours[0].trim() : "";
                //가게 운영 종료시간
                String storeEndTime = hours.length > 1 ? hours[1].trim() : "";
                //시간 저장
                storeInfo.add(crawlingPlaceService.extractTimeFromHours(storeStartTime));
                storeInfo.add(crawlingPlaceService.extractTimeFromHours(storeEndTime));
            } catch (Exception e) {
                storeInfo.add("");
                storeInfo.add("");
            }

            try {
                //가게 전화번호
                String storePhone = driver.findElement(By.cssSelector(".txt_contact")).getText();
                //가게 전화번호 저장
                storeInfo.add(storePhone);
            } catch (Exception e) {
                storeInfo.add("");
            }

            try {
                WebElement mainImageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='0']")));
                //메인 이미지 추출 & 저장
                String mainImageUrl = crawlingPlaceService.extractUrlFromStyle(mainImageElement.getAttribute("style"));
                storeInfo.add(mainImageUrl);
            } catch (TimeoutException e) {//이미지가 없는 경우 기본 이미지를 사용
                for (int i = 0; i < 4; i++) {
                    storeInfo.add(crawlingPlaceService.ensureProtocol(DEFAULT_IMAGE_PATH));
                }
            }
            //나머지 이미지 3장
            for (int i = 1; i <= 3; i++) {
                try {
                    WebElement imageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='" + i + "']")));
                    //나머지 이미지 추출 & 저장
                    String imageUrl = crawlingPlaceService.extractUrlFromStyle(imageElement.getAttribute("style"));
                    storeInfo.add(imageUrl);
                } catch (TimeoutException e) {//이미지가 없는 경우 기본 이미지 사용
                    storeInfo.add(crawlingPlaceService.ensureProtocol(DEFAULT_IMAGE_PATH));
                }
            }
            //csv 파일 작성
            writeToCSV(storeInfo);
            driver.close();

            driver.switchTo().window(originalWindow);

            Thread.sleep(4000);

        } catch (Exception e) {
            driver.navigate().back();
            Thread.sleep(4000);
        }
    }

    /**
     * csv 파일 작성
     * @param storeInfo 크롤링으로 모인 가게정보
     **/
    private void writeToCSV(List<String> storeInfo) throws IOException {
        String filePath = "store_info.csv";

        // 5개의 비이미지 정보(번호, 가게이름, 가게주소, 가게시작시간, 가게종료시간) 뒤의 이미지를 4개로 제한 (메인이미지 1개, 서브이미지 3개)
        final int nonImageInfoCount = 5;
        final int maxImageCount = 4; // 메인이미지 1개, 서브이미지 3개
        int totalImageCount = storeInfo.size() - nonImageInfoCount;

        // 이미지가 4개 이상인 경우 초과된 이미지를 제거
        if (totalImageCount > maxImageCount) {
            storeInfo = new ArrayList<>(storeInfo.subList(0, nonImageInfoCount + maxImageCount));
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            String[] record = storeInfo.toArray(new String[0]);
            writer.writeNext(record);
            log.info("CSV 파일에 저장: {}", String.join(",", record));
        } catch (IOException e) {
            log.error("CSV 파일 저장 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * csv 파일 초기화 
     **/
    private void initializeCSV() {
        String filePath = "store_info.csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"번호", "가게명", "가게주소", "가게시작시간", "가게종료시간", "가게전화번호", "메인이미지URL", "서브이미지1URL", "서브이미지2URL", "서브이미지3URL"};
            writer.writeNext(header);
        } catch (IOException e) {
            log.error("CSV 파일 초기화 실패: {}", e.getMessage());
        }
    }

}
