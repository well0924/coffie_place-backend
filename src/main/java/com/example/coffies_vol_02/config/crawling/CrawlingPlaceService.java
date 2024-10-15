package com.example.coffies_vol_02.config.crawling;

import com.example.coffies_vol_02.config.crawling.dto.PlaceCache;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class CrawlingPlaceService {

    private final PlaceRepository placeRepository;

    private final PlaceImageRepository placeImageRepository;

    private final CrawlingCacheService cacheService;

    private final FileHandler fileHandler;

    private static final String DEFAULT_IMAGE_PATH = "C:/spring_work/workspace/CoffiesVol.02/default_image.png";

    /**
     * 디비에 저장 (가게를 확인후 있으면 수정, 없으면 추가)
     * Redis를 사용해서 캐싱을 사용(가게정보는 PLACE:가게번호 , 가게이미지URL은 PLACEIMAGE:가게번호 로 redis에 저장)
     * @param csvLine csv파일에 구분항목
     **/
    @Transactional
    public void saveOrUpdatePlaceAndImages(String[] csvLine) throws Exception {
        // 가게 번호, 이름, 이미지 URL
        String placeName = csvLine[1];

        // 가게 여부 확인
        Place existingPlace = placeRepository.findByPlaceName(placeName);
        Place place;

        // 가게가 있으면 수정, 없으면 추가
        if (existingPlace != null) {
            place = existingPlace;
            PlaceRequestDto placeRequestDto = PlaceRequestDto.fromCsv(csvLine);
            place.placeUpdate(placeRequestDto);
        } else {
            place = createPlace(csvLine);
            placeRepository.save(place);
        }

        // 가게 정보를 캐싱
        PlaceCache placeCache = buildPlaceCache(place);
        log.info("Caching place: {}", placeCache);
        cacheService.cachePlace(placeCache);

        // 이미지 생성 및 리사이징
        List<PlaceImage> placeImages = createPlaceImages(csvLine, place);

        // 기존 이미지를 비우고 새 이미지를 추가
        place.getPlaceImageList().clear();  // 기존 이미지 리스트 비우기
        place.getPlaceImageList().addAll(placeImages);  // 새 이미지 추가

        // 가게 이미지 일괄 저장
        for (PlaceImage placeImage : placeImages) {
            savePlaceImage(place, placeImage);
        }
    }

    public void savePlaceImage(Place place, PlaceImage placeImage) {
        // 가게와 이미지의 조합을 검사하여 중복 여부 확인
        String storedNameInDb = placeImage.getStoredName();

        // ?fname= 뒤에 있는 부분만 비교하도록 처리
        String dbFnamePart = extractFnamePart(storedNameInDb);

        boolean imageExists = placeImageRepository.existsByPlaceAndStoredName(place, dbFnamePart);
        log.info("thumbnailImageDuplicated:::" + imageExists);

        if (!imageExists) {
            try {
                placeImage.setPlace(place);
                placeImageRepository.save(placeImage);
                cacheService.cacheImage(placeImage);
                log.info("Cached image: {}", placeImage);
            } catch (Exception e) {
                log.error("Error saving image: {}", e.getMessage());
            }
        } else {
            log.warn("Duplicate image found for place {}, not saving: {}", place.getId(), placeImage.getThumbFileImagePath());
        }
    }

    /**
     * 이미지 생성(+리사이징)
     * @param csvLine csv파일에 이미지 관련 열(메인이미지 URL, 서브이미지 URL)
     * @return List<PlaceImage> 가게 이미지 리스트
     **/
    @Transactional
    public List<PlaceImage> createPlaceImages(String[] csvLine, Place place) throws Exception {
        List<PlaceImage> imagesToSave = new ArrayList<>();

        // 메인 이미지 처리 로직
        String mainImageUrl = ensureProtocol(csvLine[6]);
        log.info("Main Image URL: {}", mainImageUrl);

        // URL 처리 및 다운로드
        List<MultipartFile> mainImages = downloadImagesFromUrls(Collections.singletonList(mainImageUrl));

        // 메인 이미지 저장 로직
        if (!mainImages.isEmpty()) {
            if (!placeImageRepository.existsByPlaceAndStoredName(place, extractFnamePart(mainImageUrl))) {
                List<PlaceImage> mainPlaceImages = fileHandler.placeImagesUpload(mainImages);

                for (PlaceImage image : mainPlaceImages) {
                    image.setIsTitle("Y");
                    image.setPlace(place);
                    String resizedImagePath = fileHandler.ResizeImage(image, 360, 360);
                    image.setThumbFileImagePath(resizedImagePath);
                    imagesToSave.add(image);
                }
            } else {
                log.warn("Main image already exists: {}", mainImageUrl);
            }
        } else {
            log.warn("Invalid Main Image URL: {}", mainImageUrl);
        }

        // 서브 이미지 처리 로직
        List<String> subImageUrls = Arrays.asList(
                ensureProtocol(csvLine[7]),
                ensureProtocol(csvLine[8]),
                ensureProtocol(csvLine[9])
        );

        // 서브 이미지 유효성 및 중복 체크
        List<String> validSubImageUrls = new ArrayList<>();
        for (String subImageUrl : subImageUrls) {
            if (!subImageUrl.equalsIgnoreCase(ensureProtocol(DEFAULT_IMAGE_PATH)) &&
                    !placeImageRepository.existsByPlaceAndStoredName(place, extractFnamePart(subImageUrl))) {
                validSubImageUrls.add(subImageUrl);
            } else {
                log.info("Sub image is default or already exists, skipping: {}", subImageUrl);
            }
        }

        // 부족한 서브 이미지를 기본 이미지로 채우기
        while (validSubImageUrls.size() < 3) {
            validSubImageUrls.add(ensureProtocol(DEFAULT_IMAGE_PATH));
        }

        // 서브 이미지 다운로드 및 처리
        for (String subImageUrl : validSubImageUrls) {
            List<MultipartFile> subImages = downloadImagesFromUrls(Collections.singletonList(subImageUrl));

            if (!subImages.isEmpty()) {
                List<PlaceImage> subPlaceImages = fileHandler.placeImagesUpload(subImages);
                for (PlaceImage image : subPlaceImages) {
                    String resizedImagePath = fileHandler.ResizeImage(image, 120, 120);
                    image.setThumbFileImagePath(resizedImagePath);
                    image.setPlace(place);
                    imagesToSave.add(image);
                }
            } else {
                log.warn("Invalid Sub Image URL: {}", subImageUrl);
            }
        }

        return imagesToSave;
    }

    /**
     * 이미지 URL을 MultipartFile로 전환
     * @param urls 이미지 URL (List)
     * @return images (List<MultipartFile>)
     **/
    public List<MultipartFile> downloadImagesFromUrls(List<String> urls) throws Exception {
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
    public String ensureProtocol(String url) {
        // 경로 구분자를 통일
        String normalizedUrl = url.replace("\\", "/");
        String normalizedDefaultImagePath = DEFAULT_IMAGE_PATH.replace("\\", "/");

        log.info("Normalized URL: " + normalizedUrl);
        log.info("Normalized Default Image Path: " + normalizedDefaultImagePath);

        // 기본 이미지 경로를 파일 URL로 변환
        if (normalizedUrl.equalsIgnoreCase(normalizedDefaultImagePath)) {
            log.info("기본 이미지 경로 사용.");
            return "file:///" + normalizedDefaultImagePath.replace(" ", "%20");
        }

        // URL이 //로 시작하면 http://를 추가
        if (normalizedUrl.startsWith("//")) {
            return "http:" + normalizedUrl;
        }

        // HTTP 프로토콜이 없는 경우 추가 (기본 이미지가 아닌 경우)
        if (!normalizedUrl.startsWith("http://") &&
                !normalizedUrl.startsWith("https://") &&
                !normalizedUrl.startsWith("file:///")) {
            log.info("HTTP 프로토콜이 추가되었습니다.");
            return "http://" + normalizedUrl;
        }

        // URL이 이미 http:// 또는 https://로 시작하는 경우 그대로 반환
        return normalizedUrl;
    }

    /**
     * 디비에서 ?fname= 추출
     * @param url csv파일에 있는 url경로
     **/
    public String extractFnamePart(String url) {
        int fnameIndex = url.indexOf("?fname=");
        if (fnameIndex != -1) {
            return url.substring(fnameIndex);
        }
        return url; // ?fname=이 없으면 전체 URL 반환
    }

    /**
     * CSS 스타일 문자열에서 URL을 추출
     * @param style 스타일 문자열
     **/
    public String extractUrlFromStyle(String style) {
        int start = style.indexOf("url(") + 4;
        int end = style.indexOf(")", start);

        if (start > 3 && end > start) {
            return style.substring(start, end).replace("\"", "").trim();
        } else {
            return ensureProtocol(DEFAULT_IMAGE_PATH);  // 기본 이미지 경로 사용
        }
    }

    /**
     * 가게 시간 추출(정규식을 사용)
     * @param hours 가게 시간
     **/
    public String extractTimeFromHours(String hours) {
        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2}");
        Matcher matcher = pattern.matcher(hours);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    public String generateFileGroupId(){
        String uuid = UUID.randomUUID().toString();
        String key = "place_"+uuid.substring(0,uuid.indexOf("-"));
        return key;
    }

    public PlaceImage createDefaultImage(Place place) {
        String uuid = UUID.randomUUID().toString();
        String fileGroupId = "place_" + uuid.substring(0, uuid.indexOf("-"));

        // 기본 이미지 경로를 웹 경로로 변환
        String webPath = "/istatic/images/coffieplace/images/";
        String defaultImageName = "default_image.png";  // 기본 이미지 파일명

        PlaceImage defaultImage = PlaceImage.builder()
                .fileGroupId(fileGroupId)
                .fileType("images")
                .imgGroup("coffieplace")
                .imgUploader("well4149")
                .originName(defaultImageName)
                .storedName(defaultImageName)
                .thumbFilePath(webPath + "thumb/" + "file_" + uuid + "_thumb.png")  // 썸네일 경로
                .build();

        defaultImage.setPlace(place);
        defaultImage.setIsTitle("N");

        // 썸네일 이미지 경로 리사이즈 적용
        String resizedImagePath = webPath + "thumb/file_" + uuid + "_thumb.png";
        defaultImage.setThumbFileImagePath(resizedImagePath);  // 웹 경로로 썸네일 설정

        return defaultImage;
    }

    /**
     * 가게저장 dto
     * @param csvLine CSV파일의 각 열
     * @return Place
     **/
    public Place createPlace(String[] csvLine) {
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

    /**
     * 가게정보 캐싱
     * @param place 가게 객체
     * @return PlaceCache
     **/
    public PlaceCache buildPlaceCache(Place place) {
        return PlaceCache.builder()
                .placeId(place.getId().toString())
                .placeName(place.getPlaceName())
                .placeAddr(place.getPlaceAddr())
                .placeStart(place.getPlaceStart())
                .placeClose(place.getPlaceClose())
                .placePhone(place.getPlacePhone())
                .placeAuthor(place.getPlaceAuthor())
                .build();
    }
}
