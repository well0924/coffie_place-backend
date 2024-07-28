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

    //가게 크롤링
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";

    public static String WEB_DRIVER_PATH = "C:\\Users\\well4\\OneDrive\\바탕 화면\\chromedriver-win32 (1)\\chromedriver-win32\\chromedriver.exe";

    private static final String DEFAULT_IMAGE_PATH = "C:/spring_work/workspace/CoffiesVol.02/default_image.png";

    private static final int MAX_ATTEMPTS = 3;

    private static final int WAIT_TIME = 20; // 20초 대기

    private static int storeNumber = 1; // 가게 번호 초기화

    /**
     * 가게 정보 크롤링후 + csv 파일 저장
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

            collectStoreInfo(driver);

        } catch (Exception e) {
            log.error("Error occurred during crawling and CSV generation: {}", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void collectStoreInfo(WebDriver driver) throws InterruptedException {

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
                                    String mainImageUrl = extractUrlFromStyle(mainImageElement.getAttribute("style")).replace("\"", "");
                                    storeInfo.add(mainImageUrl);
                                } catch (TimeoutException e) {
                                    for (int i = 0; i < 4; i++) {
                                        storeInfo.add(ensureProtocol(DEFAULT_IMAGE_PATH));
                                    }
                                }

                                for (int i = 1; i <= 3; i++) {
                                    try {
                                        WebElement imageElement = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME))
                                                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".link_photo[data-pidx='" + i + "']")));
                                        String imageUrl = extractUrlFromStyle(imageElement.getAttribute("style")).replace("\"", "");
                                        storeInfo.add(imageUrl);
                                    } catch (TimeoutException e) {
                                        storeInfo.add(ensureProtocol(DEFAULT_IMAGE_PATH));
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
        //크롤링 후 디비에 저장
        processCsvAndSaveToDatabase("store_info.csv");
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

    //csv파일을 읽고 디비에 저장하기.
    @Transactional
    public void processCsvAndSaveToDatabase(String csvFilePath) {

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            String[] values;

            csvReader.readNext(); // 헤더 스킵

            while ((values = csvReader.readNext()) != null) {
                saveOrUpdatePlaceAndImages(values);
            }
        } catch (IOException e) {
            log.error("Error occurred while reading CSV file: {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //디비에 저장(가게명을 확인하면서 가게저장하기.
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

        //이미지 처리.
        List<PlaceImage> placeImages = createPlaceImages(csvLine);

        for (PlaceImage placeImage : placeImages) {
            placeImage.setPlace(place);
            placeImageRepository.save(placeImage);
        }
    }

    private List<PlaceImage> createPlaceImages(String[] csvLine) throws Exception {
        List<PlaceImage> placeImages = new ArrayList<>();

        // 메인이미지 URL 처리
        String mainImageUrl = ensureProtocol(csvLine[6]);
        log.info("mainUrl::"+mainImageUrl);
        List<MultipartFile> mainImages = downloadImagesFromUrls(Collections.singletonList(mainImageUrl));
        log.info("이미지처리::"+mainImages);
        List<PlaceImage> mainPlaceImages = fileHandler.placeImagesUpload(mainImages);
        log.info("이미지 업로드??:"+mainPlaceImages);
        // 리사이징 적용 (메인이미지: 360x360)
        for (PlaceImage image : mainPlaceImages) {
            String resizedImagePath = fileHandler.ResizeImage(image, 360, 360);
            log.info("resizing:::"+resizedImagePath);
            image.setThumbFileImagePath(resizedImagePath);
        }

        placeImages.addAll(mainPlaceImages);

        // 서브이미지 URL 처리
        List<String> subImageUrls = Arrays.asList(ensureProtocol(csvLine[7]), ensureProtocol(csvLine[8]), ensureProtocol(csvLine[9]));
        log.info("subimages::"+subImageUrls);
        List<MultipartFile> subImages = downloadImagesFromUrls(subImageUrls);

        List<PlaceImage> subPlaceImages = fileHandler.placeImagesUpload(subImages);
        log.info(subPlaceImages);
        // 리사이징 적용 (서브이미지: 120x120)
        for (PlaceImage image : subPlaceImages) {
            String resizedImagePath = fileHandler.ResizeImage(image, 120, 120);
            log.info("resizeing:::"+resizedImagePath);
            image.setThumbFileImagePath(resizedImagePath);
        }
        placeImages.addAll(subPlaceImages);

        return placeImages;
    }

    private Place createPlace(String[] csvLine) {
        List<PlaceImage> placeImages = new ArrayList<>();
        return Place.builder()
                .placeName(csvLine[1])
                .placeAddr1(csvLine[2])
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
                .placeAddr1(csvLine[2])
                .placeStart(csvLine[3])
                .placeClose(csvLine[4])
                .placePhone(csvLine[5])
                .build();
    }

    /*private List<MultipartFile> downloadImagesFromUrls(List<String> imageUrls) throws Exception {
        List<MultipartFile> imageFiles = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            URL url = new URL(imageUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
            }

            byte[] imageBytes = baos.toByteArray();

            String fileName = UUID.randomUUID() + ".jpg";

            String contentType = connection.getContentType();

            DiskFileItem fileItem = new DiskFileItem(fileName, contentType, false, fileName, imageBytes.length, new File(System.getProperty("java.io.tmpdir")));

            try (InputStream input = new ByteArrayInputStream(imageBytes)) {
                OutputStream os = fileItem.getOutputStream();
                IOUtils.copy(input, os);
            }

            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            imageFiles.add(multipartFile);
        }
        return imageFiles;
    }*/

    private List<MultipartFile> downloadImagesFromUrls(List<String> urls) throws Exception {
        List<MultipartFile> images = new ArrayList<>();

        for (String urlString : urls) {
            URL url = new URL(urlString);
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
        }

        return images;
    }

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
                normalizedUrl.equalsIgnoreCase(normalizedDefaultImagePath)&&
                !DEFAULT_IMAGE_PATH.startsWith("//")) {
            log.info("이미지.");
            return "http:" + normalizedUrl;
        }else{
            return "file:///" + normalizedDefaultImagePath.replace(" ", "%20");
        }
    }
}
