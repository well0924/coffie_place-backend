package com.example.coffies_vol_02.testPlace;

import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.factory.PlaceFactory;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceImageResponseDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
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
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private RedisService redisService;

    @MockBean
    private PlaceService placeService;

    Member member;

    Place place;

    PlaceImage placeImage;

    List<PlaceImage> placeImages = new ArrayList<>();

    PlaceImageRequestDto placeImageRequestDto;

    PlaceImageResponseDto placeImageResponseDto;

    PlaceRequestDto placeRequestDto;

    PlaceResponseDto placeResponseDto;

    CustomUserDetails customUserDetails;

    final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = MemberFactory.memberDto();
        place = PlaceFactory.place();
        placeImage = PlaceFactory.placeImage();
        placeImages.add(placeImage);
        placeImageRequestDto = PlaceFactory.placeImageRequestDto();
        placeImageResponseDto = PlaceFactory.placeImageResponseDto();
        placeRequestDto = PlaceFactory.placeRequestDto();
        placeResponseDto = PlaceFactory.placeResponseDto();
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
       // given(redisService.getSearchList(place.getPlaceName())).willReturn(listName);
        given(placeService.listCafePlace(pageRequest,place.getPlaceName(),customUserDetails.getMember())).willReturn(sliceDto);

        //when
        when(placeService.listCafePlace(pageRequest,place.getPlaceName(),customUserDetails.getMember())).thenReturn(sliceDto);

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

        when(placeService.findCafePlaceById(placeResponseDto.getId())).thenReturn(placeResponseDto);

        mvc.perform(get("/api/place/detail/"+place.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print());

        verify(placeService).findCafePlaceById(placeResponseDto.getId());
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

        when(placeService.searchCafePlace(SearchType.p,searchKeyword,pageRequest,customUserDetails.getMember())).thenReturn(placeList);

        mvc.perform(get("/api/place/search")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user(customUserDetails))
                    .param("searchType", String.valueOf(SearchType.p))
                    .param("placeKeyword",searchKeyword))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print());

        verify(placeService).searchCafePlace(SearchType.p,searchKeyword,pageRequest,customUserDetails.getMember());
    }
}
