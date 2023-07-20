package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.constant.Role;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.example.coffies_vol_02.place.service.PlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaceApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private RedisService redisService;

    @MockBean
    private PlaceService placeService;

    private Member member;

    private Place place;

    private PlaceImage placeImage;

    List<PlaceImage> placeImages = new ArrayList<>();

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
        placeImages.add(placeImage);
        placeResponseDto = placeResponseDto();
        customUserDetails = (CustomUserDetails)testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("가게 목록-성공")
    public void placeListTest()throws Exception{
        //given
        List<String>listName = new ArrayList<>();
        listName.add(place.getPlaceName());

        List<PlaceResponseDto>placelist = new ArrayList<>();
        placelist.add(placeResponseDto);

        PageRequest pageRequest =  PageRequest.of(0,5, Sort.by("id").descending());
        Slice<PlaceResponseDto> sliceDto = new SliceImpl<>(placelist,pageRequest,false);

        given(memberRepository.findByUserId(member.getUserId())).willReturn(Optional.of(member));
        given(placeRepository.placeList(pageRequest,place.getPlaceName())).willReturn(sliceDto);
        given(redisService.getSearchList(place.getPlaceName())).willReturn(listName);
        given(placeService.placeSlideList(pageRequest,place.getPlaceName(),customUserDetails.getMember())).willReturn(sliceDto);

        //when
        when(placeService.placeSlideList(pageRequest,place.getPlaceName(),customUserDetails.getMember())).thenReturn(sliceDto);

        mvc.perform(get("/api/place/list")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(customUserDetails))
                        .param("keyword",place.getPlaceName()))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("가게 조회-성공")
    public void placeDetailTest()throws Exception{
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));

        when(placeService.placeDetail(placeResponseDto().getId())).thenReturn(placeResponseDto);

        mvc.perform(get("/api/place/detail/"+place.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print());

        verify(placeService).placeDetail(placeResponseDto().getId());
    }

    @Test
    @DisplayName("가게 검색-성공")
    public void placeSearchTest()throws Exception{

        String searchKeyword = place.getPlaceName();

        List<PlaceResponseDto>placelist = new ArrayList<>();
        placelist.add(placeResponseDto);

        PageRequest pageRequest =  PageRequest.of(0,5, Sort.by("id").descending());

        Page<PlaceResponseDto>placeList = new PageImpl<>(placelist,pageRequest,1);

        given(placeRepository.placeListSearch(SearchType.p,searchKeyword,pageRequest)).willReturn(placeList);

        when(placeService.placeListAll(SearchType.p,searchKeyword,pageRequest,customUserDetails.getMember())).thenReturn(placeList);

        mvc.perform(get("/api/place/search")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user(customUserDetails))
                    .param("searchType", String.valueOf(SearchType.p))
                    .param("placeKeyword",searchKeyword))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print());

        verify(placeService).placeListAll(SearchType.p,searchKeyword,pageRequest,customUserDetails.getMember());
    }

    @Test
    @DisplayName("가게 등록 테스트")
    public void placeRegisterTest(){

    }

    @Test
    @DisplayName("가게 수정 테스트")
    public void placeUpdateTest(){

    }

    @Test
    @DisplayName("가게 삭제 테스트")
    public void placeDeleteTest(){

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
