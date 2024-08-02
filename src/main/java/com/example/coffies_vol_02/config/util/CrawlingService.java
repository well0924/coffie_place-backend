package com.example.coffies_vol_02.config.util;


import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final PlaceRepository placeRepository;

    private final PlaceImageRepository placeImageRepository;

    private final FileHandler fileHandler;
    
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
                                    //가게 운영 시작시간
                                    String storeStartTime = hours.length > 0 ? hours[0].trim() : "";
                                    //가게 운영 종료시간
                                    String storeEndTime = hours.length > 1 ? hours[1].trim() : "";
                                    //시간 저장
                                    storeInfo.add(extractTimeFromHours(storeStartTime));
                                    storeInfo.add(extractTimeFromHours(storeEndTime));
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
                                    String mainImageUrl = extractUrlFromStyle(mainImageElement.getAttribute("style")).replace("\"", "");
                                    storeInfo.add(mainImageUrl);
                                } catch (TimeoutException e) {//이미지가 없는 경우 기본 이미지를 사용
                                    for (int i = 0; i < 4; i++) {
                                        storeInfo.add(ensureProtocol(DEFAULT_IMAGE_PATH));
                                    }
                                }
                                //나머지 이미지 3장
                                for (int i = 1; i <= 3; i++) {
                                    try {
                                        WebElement imageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                                                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='" + i + "']")));
                                        //나머지 이미지 추출 & 저장
                                        String imageUrl = extractUrlFromStyle(imageElement.getAttribute("style")).replace("\"", "");
                                        storeInfo.add(imageUrl);
                                    } catch (TimeoutException e) {//이미지가 없는 경우 기본 이미지 사용
                                        storeInfo.add(ensureProtocol(DEFAULT_IMAGE_PATH));
                                    }
                                }
                                //csv 파일 작성
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
        //크롤링 후 디비에 저장
        processCsvAndSaveToDatabase("store_info.csv");
    }

    /**
     * CSS 스타일 문자열에서 URL을 추출
     * @param style 스타일 문자열
     **/
    private String extractUrlFromStyle(String style) {
        int start = style.indexOf("url(") + 4;
        int end = style.indexOf(")", start);

        if (start > 3 && end > start) {
            return style.substring(start, end - 1);
        } else {
            return DEFAULT_IMAGE_PATH;
        }
    }

    /**
     * 가게 시간 추출(정규식을 사용)
     * @param hours 가게 시간
     **/
    private String extractTimeFromHours(String hours) {
        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2}");
        Matcher matcher = pattern.matcher(hours);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }
    
    /**
     * csv 파일 작성
     * @param storeInfo 크롤링으로 모인 가게정보
     **/
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

    /**
     * csv파일을 읽고 디비에 저장하기.
     * @param csvFilePath csv 파일 경로
     **/
    @Transactional
    public void processCsvAndSaveToDatabase(String csvFilePath) {

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            String[] values;

            csvReader.readNext(); // 헤더 스킵

            while ((values = csvReader.readNext()) != null) {
                //csv파일을 읽으면서 디비 저장
                saveOrUpdatePlaceAndImages(values);
            }
        } catch (IOException e) {
            log.error("Error occurred while reading CSV file: {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 디비에 저장 (가게를 확인후 있으면 수정, 없으면 추가)
     * @param csvLine csv파일에 구분항목
     **/
    @Transactional
    public void saveOrUpdatePlaceAndImages(String[] csvLine) throws Exception {
        String placeName = csvLine[1];

        Place existingPlace = placeRepository.findByPlaceName(placeName);

        Place place;

        //가게가 존재하면 수정 아니면 추가.
        if (existingPlace != null) {
            log.info("수정");
            place = existingPlace;
            PlaceRequestDto placeRequestDto = createPlaceRequestDto(csvLine);
            place.placeUpdate(placeRequestDto);
            placeRepository.save(place);
        } else {
            log.info("추가.");
            // 가게가 존재하지 않으면 추가
            place = createPlace(csvLine);
            placeRepository.save(place);
        }

        //이미지 저장
        List<PlaceImage> placeImages = createPlaceImages(csvLine);

        for (PlaceImage placeImage : placeImages) {
            placeImage.setPlace(place);
            placeImageRepository.save(placeImage);
        }
    }
    
    /**
     * 이미지 생성(+리사이징)
     * @param csvLine csv파일에 이미지 관련 열(메인이미지 URL,서브이미지 URL)
     * @return List<PlaceImage> 가게이미지들
     **/
    private List<PlaceImage> createPlaceImages(String[] csvLine) throws Exception {
        List<PlaceImage> placeImages = new ArrayList<>();

        // 메인이미지 URL 처리
        String mainImageUrl = ensureProtocol(csvLine[6]);
        log.info("mainUrl::"+mainImageUrl);

        //URL을 MultipartFile로 전환
        List<MultipartFile> mainImages = downloadImagesFromUrls(Collections.singletonList(mainImageUrl));
        log.info("이미지처리::"+mainImages);

        if(!mainImages.isEmpty()){
            //이미지 업로드
            List<PlaceImage> mainPlaceImages = fileHandler.placeImagesUpload(mainImages);
            log.info("이미지 업로드??:"+mainPlaceImages);

            // 리사이징 적용 (메인이미지: 360x360)
            for (PlaceImage image : mainPlaceImages) {
                image.setIsTitle("Y");
                String resizedImagePath = fileHandler.ResizeImage(image, 360, 360);
                log.info("resizing:::"+resizedImagePath);
                image.setThumbFileImagePath(resizedImagePath);
            }
            placeImages.addAll(mainPlaceImages);
        }else {
            log.warn("Main image URL is invalid: {}", mainImageUrl);
        }

        //서브이미지 URL 처리
        List<String> subImageUrls = Arrays.asList(ensureProtocol(csvLine[7]), ensureProtocol(csvLine[8]), ensureProtocol(csvLine[9]));
        log.info("subimages::"+subImageUrls);

        //서브이미지 URL에서 MultipartFile로 전환
        List<MultipartFile> subImages = downloadImagesFromUrls(subImageUrls);

        if(!subImageUrls.isEmpty()) {
            //이미지 업로드
            List<PlaceImage> subPlaceImages = fileHandler.placeImagesUpload(subImages);
            log.info(subPlaceImages);
            // 리사이징 적용 (서브이미지: 120x120)
            for (PlaceImage image : subPlaceImages) {
                String resizedImagePath = fileHandler.ResizeImage(image, 120, 120);
                log.info("resizeing:::"+resizedImagePath);
                image.setThumbFileImagePath(resizedImagePath);
            }
            placeImages.addAll(subPlaceImages);
        }else {
            log.warn("Sub image URL is invalid: {}", mainImageUrl);
        }

        return placeImages;
    }

    private Place createPlace(String[] csvLine) {
        List<PlaceImage> placeImages = new ArrayList<>();
        return Place.builder()
                .placeName(csvLine[1])
                .placeAddr(csvLine[2])
                .placeStart(csvLine[3])
                .placeClose(csvLine[4])
                .placePhone(csvLine[5])
                .placeAuthor("well4149")
                .placeImages(placeImages)
                .build();
    }

    private PlaceRequestDto createPlaceRequestDto(String[] csvLine) {
        return PlaceRequestDto.builder()
                .placeName(csvLine[1])
                .placeAddr(csvLine[2])
                .placeStart(csvLine[3])
                .placeClose(csvLine[4])
                .placePhone(csvLine[5])
                .build();
    }

    /**
     * 이미지 URL을 MultipartFile로 전환
     * @param urls 이미지 URL (List)
     * @return images (List<MultipartFile>)
     **/
    private List<MultipartFile> downloadImagesFromUrls(List<String> urls) throws Exception {
        List<MultipartFile> images = new ArrayList<>();

        for (String urlString : urls) {

            URL url;

            try{

                url = new URL(urlString);

                if (url.getProtocol().equals("file")) {
                    // 파일 URL 처리
                    File file = new File(url.toURI());
                    MultipartFile multipartFile = convertFileToMultipartFile(file);
                    images.add(multipartFile);
                } else {
                    // HTTP URL 처리
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    connection.connect();

                    try (InputStream input = connection.getInputStream();
                         ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[4096];
                        int n;
                        while ((n = input.read(buffer)) != -1) {
                            output.write(buffer, 0, n);
                        }
                        MultipartFile multipartFile = convertByteArrayToMultipartFile(output.toByteArray(), url.getFile(), connection.getContentType());
                        images.add(multipartFile);
                    }
                }
            }catch (Exception e) {
                // 오류 발생 시 기본 이미지 추가
                File defaultImageFile = new File("C:/spring_work/workspace/CoffiesVol.02/default_image.png");
                MultipartFile defaultMultipartFile = convertFileToMultipartFile(defaultImageFile);
                images.add(defaultMultipartFile);
            }
        }

        return images;
    }

    /**
     * File을 MultipartFile로 변환
     * @param file 파일
     * @return MultipartFile
     **/
    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {

        DiskFileItem fileItem = new DiskFileItem(
                "file",
                Files.probeContentType(file.toPath()),
                false,
                file.getName(),
                (int) file.length(),
                file.getParentFile()
        );

        try (FileInputStream input = new FileInputStream(file)) {
            IOUtils.copy(input, fileItem.getOutputStream());
        }

        return new CommonsMultipartFile(fileItem);
    }

    /**
     * ByteArray를 MultipartFile로 변환
     * @param bytes ㅇ
     * @param contentType 컨텐츠 타입
     * @param fileName 파일명
     * @return MultipartFile
     **/
    public static MultipartFile convertByteArrayToMultipartFile(byte[] bytes, String fileName, String contentType) throws IOException {

        DiskFileItem fileItem = new DiskFileItem(
                "file",
                contentType,
                false,
                fileName,
                bytes.length,
                null
        );

        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            IOUtils.copy(input, fileItem.getOutputStream());
        }

        return new CommonsMultipartFile(fileItem);
    }

    /**
     * csv 파일에 있는 이미지 URL 추출
     * @param url 이미지 URL
     **/
    private String ensureProtocol(String url) {
        // 경로 구분자를 통일
        String normalizedUrl = url.replace("\\", "/");
        String normalizedDefaultImagePath = DEFAULT_IMAGE_PATH.replace("\\", "/");

        log.info("Normalized URL: " + normalizedUrl);
        log.info("Normalized Default Image Path: " + normalizedDefaultImagePath);

        // 기본 이미지 경로를 파일 URL로 변환
        if (normalizedUrl.equalsIgnoreCase(normalizedDefaultImagePath)) {
            log.info("기본 이미지.");
            return "file:///" + normalizedDefaultImagePath.replace(" ", "%20");
        }

        // //가 포함된 URL에서 //를 제거
        if (normalizedUrl.startsWith("//")) {
            return "http:" + normalizedUrl;
        }

        // HTTP 프로토콜이 없는 경우 추가
        if (!normalizedUrl.startsWith("http://") &&
                !normalizedUrl.startsWith("https://") &&
                !normalizedUrl.startsWith("file:///C") &&
                !normalizedUrl.equalsIgnoreCase(normalizedDefaultImagePath)&&
                normalizedUrl.equalsIgnoreCase(normalizedDefaultImagePath)) {
            log.info("이미지.");
            return "http:" + normalizedUrl;
        }else{
            return "file:///" + normalizedDefaultImagePath.replace(" ", "%20");
        }
    }
}
