package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.repository.MemberRepository;
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
    private MemberRepository memberRepository;
    @Mock
    private PlaceRepository placeRepository;
    @MockBean
    private PlaceService placeService;

    private CustomUserDetails customUserDetails;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

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
        mvc.perform(get("/page/place/detail/"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("가게 등록 화면")
    public void placeRegisterPageTest(){

    }

    @Test
    @DisplayName("가게수정 및 삭제 화면")
    public void placeUpdateDeleteTest(){

    }

}
