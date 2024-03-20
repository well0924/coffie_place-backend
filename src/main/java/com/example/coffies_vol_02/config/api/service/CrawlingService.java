package com.example.coffies_vol_02.config.api.service;

import com.example.coffies_vol_02.config.api.dto.KakaoApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.KakaoPlaceApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.PlaceCrawlingDto;
import com.example.coffies_vol_02.config.api.dto.PlaceDocumentDto;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.service.MemberService;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.service.PlaceService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.util.Streams;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CrawlingService {
    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUriBuilderService;
    private final MemberService memberService;
    private final PlaceService placeService;
    //TODO 파일 리사이징 크기
    private final int resizeImageWidth = 300;
    private final int resizeImageHeight = 300;

    private final String filePath = "crawling.csv"; //TODO 파일 저장 경로 나중에 수정해주면 됨 (이렇게 작성하면 프로젝트 최상단 디렉토리에 csv 파일 생성)
    private final String imageFilePath = System.getProperty("user.dir"); //TODO 파일 경로 수정해주면 됨 -> 이 경로는 프로젝트 최상단 디렉토리
    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    /**
     * kakao map api 검색
     * @return KakaoApiResponseDto
     **/
    @Retryable(
            value = {RuntimeException.class},//api가 호출이 되지 않은 경우에 runtimeException을 실행
            maxAttempts = 3,//재시도 횟수
            backoff = @Backoff(delay = 2000)//재시도 전에 딜레이 시간을 설정(ms)
    )
    //@Scheduled(cron="0 0 3 * * 6") //매일 새벽 3시에 스케줄러 적용
    @Scheduled(cron = "0/2 * * * * ?",zone = "Asia/Seoul")
    public void crawlingByMember() throws Exception{

        Set<URI> uris = new HashSet<>();
        PageRequest pageable = PageRequest.of(0,5, Sort.by("id").descending());
        Page<MemberResponse> members = memberService.findAll(pageable);
        log.info(members.get().collect(Collectors.toList()));
        for(MemberResponse member : members){
            if(ObjectUtils.isEmpty(member.memberLat()) || ObjectUtils.isEmpty(member.memberLng())) continue;
            uris.add(kakaoUriBuilderService.buildUriByCategorySearch(String.valueOf(member.memberLng()), String.valueOf(member.memberLat()), 1)); //중복 없이 모든 회원의 주소값 받아오기
        }
        HttpHeaders headers = new HttpHeaders();

        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

        HttpEntity httpEntity = new HttpEntity<>(headers);

        List<KakaoPlaceApiResponseDto> results = new ArrayList<>();

        for(URI uri : uris) {
            KakaoPlaceApiResponseDto result = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                    KakaoPlaceApiResponseDto.class).getBody();
            results.add(result);
            int maxPage = result.getMetaDto().getPageableCount()/15;
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri.toString());
            for(int i=2; i<=maxPage; i++){
                builder.replaceQueryParam("page", i); //다음 페이지에 대해서 요청 보내야함
                results.add(restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, httpEntity,
                        KakaoPlaceApiResponseDto.class).getBody());
            }
        }
        //csv파일 저장하기.
        saveCSV(results);
    }

    public void firstCheckByNewMember(String longitude, String latitude) throws Exception{
        if(ObjectUtils.isEmpty(longitude) || ObjectUtils.isEmpty(latitude)) return;

        log.info("firstCheckByNewMember");
        URI uri = kakaoUriBuilderService.buildUriByCategorySearch(longitude, latitude,1);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity httpEntity = new HttpEntity<>(headers);
        List<KakaoPlaceApiResponseDto> results = new ArrayList<>();
        KakaoPlaceApiResponseDto result = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                KakaoPlaceApiResponseDto.class).getBody();
        results.add(result);
        int maxPage = result.getMetaDto().getPageableCount()/15;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri.toString());
        for(int i=2; i<=maxPage; i++){
            builder.replaceQueryParam("page", i); //다음 페이지에 대해서 요청 보내야함
            results.add(restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, httpEntity,
                    KakaoPlaceApiResponseDto.class).getBody());
            log.info("resultpage : {}", i);
        }
        saveCSV(results);

    }
    private void saveCSV(List<KakaoPlaceApiResponseDto> results) throws Exception{
        boolean fileExists = Files.exists(Path.of(filePath));
        if (!fileExists) {
            Files.createFile(Paths.get(filePath));
        }
        try (
                CSVReader reader = new CSVReader(new FileReader(filePath));
                CSVWriter writer = new CSVWriter(new FileWriter(filePath, true));) {

            if (!fileExists) {
                String[] header = {"ID", "placeName", "phone", "addressName", "roadAddressName", "x", "y", "place_url",
                        "distance", "start", "close"};
                writer.writeNext(header);
            }
            // 기존 파일에서 중복 체크를 위한 Set 또는 List 초기화
            List<String> existingIds = new ArrayList<>();

            // 기존 파일의 데이터 읽기
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // 각 행의 첫 번째 열(ID)을 중복 체크를 위한 데이터로 저장
                existingIds.add(nextLine[0]);
            }

            for (KakaoPlaceApiResponseDto res : results) {
                // 새로운 데이터 중에서 중복을 제외하고 파일에 추가
                for (PlaceDocumentDto result : res.getDocumentList()) {
                    if (!existingIds.contains(String.valueOf(result.getId()))) {
                        // 중복되지 않는 경우에만 추가
                        PlaceCrawlingDto linkCrawlingResult = crawlingLink(result.getPlaceUrl());
                        String[] data = {result.getId(), result.getPlaceName(), result.getPhone(),
                                result.getAddressName(), result.getRoadAddressName(),
                                String.valueOf(result.getLongitude()), String.valueOf(result.getLatitude()),
                                result.getPlaceUrl(), String.valueOf(result.getDistance()),
                                linkCrawlingResult.getPlaceStart(),
                                linkCrawlingResult.getPlaceClose()};
                        writer.writeNext(data);
                        log.info("save csv : {}", data);
                        updateDB(result, linkCrawlingResult);
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private PlaceCrawlingDto crawlingLink(String url) throws Exception{
        //C:\Users\well4\OneDrive\바탕 화면\chromedriver_win32\chromedriver.exe
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\well4\\OneDrive\\바탕 화면\\chromedriver_win32\\chromedriver.exe"); //TODO chromedriver 수정
        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);

        driver.get(url);//document 받아오기

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            //TODO 최대 10초 대기 -> 알아서 수정, 대기 없으면 이미지 로딩 안 됐을때 못 받아옴
            By locator = By.cssSelector(".photo_area * .link_photo"); // 대기할 엘리먼트의 식별자로 수정
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        }catch(TimeoutException e){
            //pass
        }

        List<WebElement> openhour = driver.findElements(By.cssSelector(".location_detail.openhour_wrap .location_present * span.time_operation"));
        //시간 정보들을 갖고있는 태그를 모두 받아옴
        PlaceCrawlingDto placeCrawlingDto = new PlaceCrawlingDto();
        if(!openhour.isEmpty()){
            //Dto 생성
            String[] placeTime = openhour.get(0).getText().split(" ~ ");
            placeCrawlingDto.addPlaceTime(placeTime[0], placeTime[1]);
        }

        List<WebElement> images = driver.findElements(By.cssSelector(".photo_area * .link_photo"));
        if(!images.isEmpty()){
            //Dto 생성
            for(WebElement element: images){
                MultipartFile placeImage = photoUrlCrawling(element.getAttribute("style"));
                placeCrawlingDto.addPlaceImage(placeImage);
            }
        }
        return placeCrawlingDto;
    }

    private MultipartFile photoUrlCrawling(String tag) throws IOException {
        String pattern = "background-image:\\s*url\\(['\"]?([^'\"]*)['\"]?\\)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(tag);

        if(matcher.find()){
            String thumbnailUrl = "https:"+matcher.group(1);
            String originalUrl = thumbnailUrl.replaceFirst("C320x320.q50", "R0x420.q50");
            return getImageUrl(originalUrl);
        }else{
            return null;
        }
    }

    private MultipartFile getImageUrl(String imageUrl) throws IOException{
        BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
        BufferedImage resizedImage = Thumbnails.of(originalImage).size(resizeImageWidth,resizeImageHeight).asBufferedImage();
        //TODO 리사이징 값 알아서 수정해주면 될듯
        String fileName = "image"+ UUID.randomUUID()+".png"; //TODO 파일명은 알아서 수정해주면 될 듯

        File outputFile = new File(imageFilePath, fileName);
        ImageIO.write(resizedImage, "png", outputFile); //파일 생성

        File repository = new File(imageFilePath);
        // 파일 객체 생성
        DiskFileItem resource = new DiskFileItem(
                "file",
                "image/png",
                false,
                fileName,
                10240,
                repository
        ); //TODO 사이즈 최대용량은 알아서 수정해주면 될 듯함

        try (FileInputStream fileInputStream = new FileInputStream(outputFile)) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Streams.copy(fileInputStream, outputStream, true);
                Streams.copy(new ByteArrayInputStream(outputStream.toByteArray()), resource.getOutputStream(), true);
            }
        }

        return new CommonsMultipartFile(resource);
    }
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

        PlaceImageRequestDto placeImageRequestDto = PlaceImageRequestDto.builder().images(placeCrawlingDto.getPlaceImage()).build();

        placeService.placeRegister(placeRequestDto, placeImageRequestDto);
    }

    @Recover
    public KakaoApiResponseDto recover(String address, CustomExceptionHandler customExceptionHandler){
        log.error("All the retries failed. address: {}, error : {}", address, customExceptionHandler.getErrorCode().getMessage());
        return null;
    }
}
