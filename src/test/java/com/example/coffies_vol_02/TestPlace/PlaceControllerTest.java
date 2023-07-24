package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.constant.Role;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.example.coffies_vol_02.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class PlaceControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mvc;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private PlaceImageRepository placeImageRepository;
    @MockBean
    private PlaceService placeService;

    private Place place;

    private Member member;

    List<PlaceImage> placeImages = new ArrayList<>();

    private PlaceImage placeImage;

    private PlaceResponseDto placeResponseDto;

    private CustomUserDetails customUserDetails;

    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        place = place();
        placeImage = placeImage();
        placeImages.add(placeImage());
        placeResponseDto = placeResponseDto();
    }

    @Test
    @DisplayName("가게 목록화면")
    public void placeListTest()throws Exception{
        mvc.perform(get("/page/place/list")
                        .contentType(MediaType.TEXT_HTML)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("가게 조회화면")
    public void placeDetailTest()throws Exception{
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(placeImageRepository.findPlaceImagePlace(place().getId())).willReturn(placeImages);

        when(placeService.placeDetail(place.getId())).thenReturn(placeResponseDto);

        mvc.perform(get("/page/place/detail/"+place.getId())
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(placeService).placeDetail(place.getId());
    }

    @Test
    @DisplayName("가게 등록 화면")
    public void placeRegisterPageTest(){

    }

    @Test
    @DisplayName("가게수정 및 삭제 화면")
    public void placeUpdateDeleteTest(){

    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!!")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .role(Role.ROLE_ADMIN)
                .build();
    }

    private PlaceImage placeImage(){
        return PlaceImage
                .builder()
                .fileGroupId("place_ereg34593")
                .thumbFilePath("C:\\\\UploadFile\\\\coffieplace\\images\\thumb\\file_1320441223849700_thumb.jpg")
                .thumbFileImagePath("/istatic/images/coffieplace/images/thumb/1320441218420200_thumb.jpg")
                .imgPath("C:\\\\UploadFile\\\\coffieplace\\images\\1320441218420200.jpg")
                .storedName("다운로드 (1).jpg")
                .originName("1320441218420200.jpg")
                .imgUploader(member.getUserId())
                .imgGroup("coffieplace")
                .isTitle("1")
                .build();
    }
    private Place place(){
        return Place
                .builder()
                .id(1)
                .placeLng(123.3443)
                .placeLat(23.34322)
                .placeAddr1("xxxx시 xx구")
                .placeAddr2("ㅁㄴㅇㄹ")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(0.0)
                .fileGroupId("place_fre353")
                .placeName("릴렉스")
                .placeImages(placeImages)
                .build();
    }

    private PlaceResponseDto placeResponseDto(){
        return new PlaceResponseDto(
                place.getId(),
                place.getPlaceLng(),
                place.getPlaceLat(),
                place.getReviewRate(),
                place.getPlaceName(),
                place.getPlaceAddr1(),
                place.getPlaceAddr2(),
                place.getPlacePhone(),
                place.getPlaceAuthor(),
                place.getPlaceStart(),
                place.getPlaceClose(),
                place.getFileGroupId(),
                place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getIsTitle(),
                place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getImgPath(),
                place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getThumbFileImagePath());
    }
}
