package com.example.coffies_vol_02.config;

import com.example.coffies_vol_02.config.api.service.KakaoUriBuilderService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KakaoMapApiTest {
    @Autowired
    private KakaoUriBuilderService kakaoUriBuilderService;

    @BeforeEach
    public void init(){
        this.kakaoUriBuilderService = new KakaoUriBuilderService();
    }

    @Test
    @DisplayName("api 호출 테스트")
    public void kakoApiTest(){

        String address = "서울 성북구";
        Charset charset = StandardCharsets.UTF_8;

        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);
        String decodeURL = URLDecoder.decode(uri.toString(),charset);

        assertThat(decodeURL).isEqualTo("https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구");
    }
}
